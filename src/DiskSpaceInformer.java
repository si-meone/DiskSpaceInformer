import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;


public class DiskSpaceInformer extends JPanel
        implements ActionListener {

    private static JTextArea log;
    private JFileChooser fileChooser;
    private JTree tree;
    private JScrollPane treeScrollPane;
    protected FindFileAndFolderSizes task;
    protected JProgressBar progressBar;

    private static String version = "Disk Space Informer v0.1f";
    private static final String newline = "\n";
    private final JComboBox drives;

    public DiskSpaceInformer() {
        super(new BorderLayout());
        log = new JTextArea(30, 40);
        log.setMargin(new Insets(5, 5, 5, 5));
        log.setEditable(true);
        log.append(new FindFileAndFolderSizes().checkSpaceAvailable());
        JScrollPane logScrollPane = new JScrollPane(log);
        logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        logScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        //Create a file chooser
        fileChooser = new JFileChooser();

        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        drives = new JComboBox(File.listRoots());
        drives.setSelectedIndex(0);
        drives.addActionListener(this);

        // flow layout
        JPanel controlPanel = new JPanel();
        controlPanel.add(drives);

        progressBar = new JProgressBar();
        JPanel progressPanel = new JPanel();
        progressPanel.add(progressBar);

        // Create a TreeModel object to represent our tree of files
        String root = drives.getSelectedItem().toString();
        // Create a JTree and tell it to display our model
        tree = new JTree();
        tree.setModel(new FileSystemTreeModel(root));
        tree.addMouseListener(new LeftClickMouseListener());

        // The JTree can get big, so allow it to scroll
        treeScrollPane = new JScrollPane(tree);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                treeScrollPane, logScrollPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(250);

        //Add bits to the panel.
        add(controlPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(progressPanel, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == drives) { //open
                // Create a JTree and tell it to display our model
            tree = new JTree();
            tree.setModel(new FileSystemTreeModel(new TreeFile(drives.getSelectedItem().toString())));
            tree.addMouseListener(new LeftClickMouseListener());

            // The JTree can get big, so allow it to scroll
            treeScrollPane.setViewportView(tree);
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
                if(Arrays.asList(File.listRoots()).contains(lastPathComponent)){
                    return;
                }
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

        //Add content to the window.
        frame.add(new DiskSpaceInformer());
        //Display the window.
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


            Map<String, Long> foldersSizes = null;
            Path root = Paths.get(String.valueOf(file.getPath()));
            FileSystemVisitor visitor = new FileSystemVisitor(root, progressBar);
            try {
                Files.walkFileTree(root, visitor);
                foldersSizes = visitor.getFoldersSizes();
            } catch (IOException e) {
                e.printStackTrace();
            }

            progressBar.setString("Sorting Listing...");
            SizeComparator vc = new SizeComparator(foldersSizes);
            Map<String, Long> sortedMap = new TreeMap<String, Long>(vc);
            sortedMap.putAll(foldersSizes);
            progressBar.setIndeterminate(false);
            if (summary) {
                logTop(Utils.prettyPrint(file, visitor.getGrandTotal()));
            } else {
                logTop(Utils.prettyPrint(file, visitor.getGrandTotal(), sortedMap));
            }
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

