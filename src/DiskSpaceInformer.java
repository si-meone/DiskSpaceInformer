import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

import static java.util.ResourceBundle.*;


public class DiskSpaceInformer extends JPanel
        implements ActionListener {

    private static JTextArea log;
    private JTree tree;
    private final JButton checkButton;
    private JScrollPane treeScrollPane;
    protected FindFileAndFolderSizes task;
    protected JProgressBar progressBar;

    private static String version = "Disk Space Informer v0.1i";
    static private final String newline = "\n";
    private final JComboBox drives;

    public static boolean debug = false;
    private String[] pathsToIgnore;

    public DiskSpaceInformer(File[] files, String path) {
        super(new BorderLayout());
        pathsToIgnore = new String[0];
        log = new JTextArea(30, 40);
        log.setName("log");
        log.setMargin(new Insets(5, 5, 5, 5));
        log.setEditable(true);

        try{
            pathsToIgnore = new Config("config.properties").getItems("folders.to.ignore");
            StringBuffer pathBuffer = new StringBuffer();
            pathBuffer.append("config.properties\n");
            for(String pathToIgnore : pathsToIgnore) {
                pathBuffer.append(String.format("path Ignored: %s\n", pathToIgnore));
            }
            logTop(pathBuffer);

        }  catch (MissingResourceException e){
            logTop(new StringBuffer(String.format("Error: %s File: %s Key Missing: %s", e.getMessage() , e.getClassName(),e.getKey())));

        }


        log.append(new FindFileAndFolderSizes().checkSpaceAvailable());
        JScrollPane logScrollPane = new JScrollPane(log);
        logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        logScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        checkButton = new JButton("Check Space");
        checkButton.setName("Check Space");
        checkButton.addActionListener(this);

        drives = new JComboBox(files);
        if (path != "") drives.addItem(path);
        drives.setSelectedItem(path);
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
        tree.setName("tree");
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

    protected class LeftClickMouseListener extends MouseAdapter {
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
    }

    ;

    private static void setupAndShowUI(File[] files, String path) {
        JFrame frame = new JFrame(version);
        frame.setName("DiskSpaceInformer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new DiskSpaceInformer(files, path));
        frame.pack();
        frame.setVisible(true);
    }


    protected static void logTop(StringBuffer currentLog) {
        StringBuffer oldLog = new StringBuffer();
        oldLog.append(log.getText());
        log.setText("");
        log.append(currentLog + newline +  oldLog);
    }


    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                if (args.length == 0) {
                    setupAndShowUI(File.listRoots(), System.getProperty("user.home"));
                } else {
                    setupAndShowUI(new File[]{new File("")}, args[0]);
                }
            }
        });
    }


    protected class FindFileAndFolderSizes extends SwingWorker<Void, Void> {

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
        public Void doInBackground() throws Exception {
            if (file.isFile()) {
                logTop(PrettyPrint.prettyPrint(file));
                return null;
            }

            progressBar.setString("Processing Selection...");
            Map<String, Long> foldersSizes = null;
            Path root = Paths.get(String.valueOf(file.getPath()));

            List<Path> foldersToIgnore =  new ArrayList<Path>();
            for (String path : pathsToIgnore) {
                foldersToIgnore.add(new File(path).toPath());
            }

            FileSystemVisitor visitor = new FileSystemVisitor(root, foldersToIgnore, progressBar);
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
                logTop(PrettyPrint.prettyPrint(file, visitor.getGrandTotal()));
            } else if (debug) {
                logTop(PrettyPrint.prettyPrint(file, visitor.getGrandTotal(), sortedMap, extraInfo));
            } else {
                logTop(PrettyPrint.prettyPrint(file, visitor.getGrandTotal(), sortedMap, !extraInfo.isEmpty() ));
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
                sb.append(PrettyPrint.prettyPrint(root.getPath(), totalSpace, usedSpace, freeSpace));
            }
            return sb.toString();
        }

        @Override
        public void done() {
            progressBar.setString("Task Complete...");
        }

    }

}