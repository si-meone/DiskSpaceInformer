import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;


public class DiskSpaceInformer extends JPanel
        implements ActionListener {

    private static JTextArea log;
    private JTree tree;
    private final JButton checkButton;
    private JScrollPane treeScrollPane;
    protected FindFileAndFolderSizes task;
    protected JProgressBar progressBar;

    private static String version = "Disk Space Informer v0.1g";
    private final JComboBox drives;

    public static boolean debug = false;

    public DiskSpaceInformer() {
        super(new BorderLayout());
        log = new JTextArea(30, 40);
        log.setMargin(new Insets(5, 5, 5, 5));
        log.setEditable(true);
        log.append(new FindFileAndFolderSizes().checkSpaceAvailable());
        JScrollPane logScrollPane = new JScrollPane(log);
        logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        logScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        checkButton = new JButton("Check Space");
        checkButton.addActionListener(this);

        String userHome = System.getProperty("user.home");
        drives = new JComboBox(File.listRoots());
        drives.addItem(userHome);
        drives.setSelectedItem(userHome);
        drives.addActionListener(this);

        JCheckBox debugToggle = new JCheckBox();
        debugToggle.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                JCheckBox cb = (JCheckBox) e.getSource();
                debug = cb.isSelected() ? true : false;
            }
        });
        debugToggle.setToolTipText("Debugging");

        JPanel controlPanel = new JPanel();
        controlPanel.add(drives);
        controlPanel.add(checkButton);
        controlPanel.add(debugToggle);

        progressBar = new JProgressBar();
        Dimension prefSize = progressBar.getPreferredSize();
        prefSize.width = 700;
        progressBar.setPreferredSize(prefSize);
        JPanel progressPanel = new JPanel();
        progressPanel.add(progressBar);

        String root = drives.getSelectedItem().toString();
        tree = new JTree();
        tree.setModel(new FileSystemTreeModel(root));
        tree.addMouseListener(new LeftClickMouseListener());

        treeScrollPane = new JScrollPane(tree);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                treeScrollPane, logScrollPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(250);

        add(controlPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(progressPanel, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == drives) { //open
            tree = new JTree();
            tree.setModel(new FileSystemTreeModel(new TreeFile(drives.getSelectedItem().toString())));
            tree.addMouseListener(new LeftClickMouseListener());
            treeScrollPane.setViewportView(tree);
        } else if (e.getSource() == checkButton) {
            task = new FindFileAndFolderSizes(new TreeFile(drives.getSelectedItem().toString()));
            task.execute();
        }
    }

    private void showSpaceUsedByFolder() {
        Object selection = tree.getLastSelectedPathComponent();
        if (selection.equals("listings:")) return;
        TreePath[] selectionPaths = tree.getSelectionPaths();
        for (TreePath path : selectionPaths) {
            FindFileAndFolderSizes task;
            if (selectionPaths.length > 1) {  //more than one thing
                boolean summary = true;
                task = new FindFileAndFolderSizes((File) path.getLastPathComponent(), summary);
            } else {
                File lastPathComponent = (File) path.getLastPathComponent();
                task = new FindFileAndFolderSizes(lastPathComponent);
            }
            task.execute();
        }
    }

    private class LeftClickMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                if (e.getClickCount() == 2) {
                    int rowLocation = tree.getRowForLocation(e.getX(), e.getY());
                    File lastPathComponent = (File) tree.getPathForRow(rowLocation).getLastPathComponent();
                    if (lastPathComponent.isFile()) {
                        task = new FindFileAndFolderSizes(lastPathComponent);
                        task.execute();
                    }
                } else if (e.getClickCount() == 1) {
                    showSpaceUsedByFolder();
                }
            }
        }
    };

    private static void setupAndShowUI() {
        JFrame frame = new JFrame(version);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new DiskSpaceInformer());
        frame.pack();
        frame.setVisible(true);
    }

    protected static void logTop(String currentLog) {
        String lastLog = log.getText();
        log.setText("");
        log.append(currentLog + lastLog);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                setupAndShowUI();
            }
        });
    }


    class FindFileAndFolderSizes extends SwingWorker<Void, Void> {

        private boolean summary = false;
        private File file;

        FindFileAndFolderSizes(File file, boolean summary) {
            this(file);
            this.summary = summary;
        }

        FindFileAndFolderSizes(File file) {
            this.file = file;
        }

        FindFileAndFolderSizes() {
        }

        @Override
        public Void doInBackground() {
            if (file.isFile()) {
                logTop(Utils.prettyPrint(file));
                return null;
            }

            progressBar.setString("Processing Selection...");
            Map<String, Long> foldersSizes = null;
            Path root = Paths.get(String.valueOf(file.getPath()));
            FileSystemVisitor visitor = new FileSystemVisitor(root, progressBar);
            String extraInfo = "";
            try {
                Files.walkFileTree(root, visitor);
                foldersSizes = visitor.getFoldersSizes();
                extraInfo = visitor.getErrors();
            } catch (IOException e) {
                e.printStackTrace();
            }

            progressBar.setString("Sorting Listing...");
            SizeComparator vc = new SizeComparator(foldersSizes);
            Map<String, Long> sortedMap = new TreeMap<String, Long>(vc);
            sortedMap.putAll(foldersSizes);
            if (summary) {
                logTop(Utils.prettyPrint(file, visitor.getGrandTotal()));
            } else logTop(Utils.prettyPrint(file, visitor.getGrandTotal(), sortedMap, debug, extraInfo));
            return null;
        }

        public String checkSpaceAvailable() {
            StringBuffer sb = new StringBuffer();
            File[] roots = File.listRoots();
            for (File root : roots) {
                long totalSpace = root.getTotalSpace();
                long freeSpace = root.getFreeSpace();
                long usedSpace = totalSpace - freeSpace;
                sb.append(Utils.prettyPrint(root.getPath(), totalSpace, usedSpace, freeSpace));
            }
            return sb.toString();
        }

        @Override
        public void done() {
            progressBar.setString("Task Complete...");
        }

    }

}