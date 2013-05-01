import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;


public class DiskSpaceInformer extends JPanel
        implements ActionListener, PropertyChangeListener {

    static private final String newline = "\n";
    private static String version = "Disk Space Informer v0.1d";
    private final JButton checkButton;
    JButton openButton, summaryButton, clearButton;
    JTextArea log;
    JFileChooser fileChooser;
    JTree tree;
    JScrollPane treeScrollPane;
    protected FindFileAndFolderSizes task;
    protected ProgressMonitor progressMonitor;
    protected JProgressBar progressBar;

    public DiskSpaceInformer() {
        super(new BorderLayout());
        log = new JTextArea(30, 40);
        log.setMargin(new Insets(5, 5, 5, 5));
        log.setEditable(false);
        log.append(Utils.printInstructions());
        JScrollPane logScrollPane = new JScrollPane(log);
        logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        logScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        //Create a file chooser
        String os = System.getProperty("os.name");
        fileChooser = new JFileChooser();

        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        openButton = new JButton("Choose Folder");
        openButton.addActionListener(this);

        checkButton = new JButton("Check Space");
        checkButton.addActionListener(this);

        summaryButton = new JButton("Storage Info");
        summaryButton.addActionListener(this);

        clearButton = new JButton("Clear Log...");
        clearButton.addActionListener(this);

        // flow layout
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openButton);
        buttonPanel.add(checkButton);
        buttonPanel.add(summaryButton);
        buttonPanel.add(clearButton);

        progressBar = new JProgressBar();
        JPanel progressPanel = new JPanel();
        progressPanel.add(progressBar);

        // Create a TreeModel object to represent our tree of files
        String root =System.getProperty("user.home");
        // Create a JTree and tell it to display our model
        tree = new JTree();
        tree.setModel(new DirectoryTreeModel(root));
        tree.addMouseListener(new LeftClickMouseListener());
        tree.addMouseListener(new RightClickMouseListener());

        // The JTree can get big, so allow it to scroll
        treeScrollPane = new JScrollPane(tree);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                treeScrollPane, logScrollPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(250);

        //Add bits to the panel.
        add(buttonPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(progressPanel, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openButton) { //open
            int returnVal = fileChooser.showOpenDialog(DiskSpaceInformer.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File dir = fileChooser.getSelectedFile();

                // Create a JTree and tell it to display our model
                tree = new JTree();
                tree.setModel(new DirectoryTreeModel(new TreeFile(dir)));
                tree.addMouseListener(new RightClickMouseListener());
                // The JTree can get big, so allow it to scroll
                treeScrollPane.setViewportView(tree);
            } else {
                log.append("Open command cancelled by user." + newline);
            }

        } else if (e.getSource() == checkButton) {
            progressMonitor = new ProgressMonitor(DiskSpaceInformer.this,
                    "Getting Folder sizes",
                    "", 0, 100);
            progressMonitor.setProgress(0);
            TreePath[] selectionPaths = tree.getSelectionPaths();

            if (null == selectionPaths) {  //more than one thing
                log.append(newline + "Error: Please select a file or folder in the tree" + newline);
            }

            for (TreePath path : selectionPaths) {
                if (selectionPaths.length > 1) {  //more than one thing
                    boolean summary = true;
                    task = new FindFileAndFolderSizes((File) path.getLastPathComponent(), log, progressBar, progressMonitor, summary);
                } else {
                    task = new FindFileAndFolderSizes((File) path.getLastPathComponent(), log, progressBar, progressMonitor);
                }
                task.addPropertyChangeListener(this);
                task.execute();
            }

        } else if (e.getSource() == summaryButton) {
            log.append(Utils.checkSpaceAvailable());  //check
            log.setCaretPosition(log.getDocument().getLength());

        } else if (e.getSource() == clearButton) {
            log.setText("");  //clear
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressMonitor.setProgress(progress);
            String message =
                    String.format("Completed %d%%.\n", progress);
            progressMonitor.setNote(message);
            //log.append(message); // see it print increments
            if (progressMonitor.isCanceled() || task.isDone()) {
                Toolkit.getDefaultToolkit().beep();
                if (progressMonitor.isCanceled()) {
                    progressBar.setString("Task cancelled");
                    log.append("Task canceled.\n");
                    task.cancel(true);
                } else {
                    progressBar.setString("Task completed");
                    // log.append("Task completed.\n");
                }
            }
        }
    }

    class LeftClickMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                if (e.getClickCount() == 2) {
                    int rowLocation = tree.getRowForLocation(e.getX(), e.getY());
                    File lastPathComponent = (File) tree.getPathForRow(rowLocation).getLastPathComponent();
                    if (lastPathComponent.isFile()) {
                        task = new FindFileAndFolderSizes(lastPathComponent,log, progressBar,progressMonitor);
                        task.addPropertyChangeListener(DiskSpaceInformer.this);
                        task.execute();
                    }
                }


            }
        }
    };

    class RightClickMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {

            JTree tree;

            if (SwingUtilities.isRightMouseButton(e)) {
                tree = (JTree) e.getSource();
                TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                Rectangle pathBounds = tree.getUI().getPathBounds(tree, path);
                if (pathBounds != null && pathBounds.contains(e.getX(), e.getY())) {
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem jMenuItem = new JMenuItem("check space");
                    jMenuItem.addActionListener(new MenuActionListener());
                    menu.add(jMenuItem);
                    menu.show(tree, pathBounds.x, pathBounds.y + pathBounds.height);


                }
            }
        }
    };

    class MenuActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object selection = tree.getLastSelectedPathComponent();
            if (selection.equals("listings:")) return;

            progressMonitor = new ProgressMonitor(DiskSpaceInformer.this,
                    "Getting Folder sizes",
                    "", 0, 100);
            progressMonitor.setProgress(0);
            TreePath[] selectionPaths = tree.getSelectionPaths();
            for (TreePath path : selectionPaths) {
                FindFileAndFolderSizes task;
                if (selectionPaths.length > 1) {  //more than one thing
                    boolean summary = true;
                    task = new FindFileAndFolderSizes((File) path.getLastPathComponent(), log, progressBar,progressMonitor, summary);
                } else {
                    task = new FindFileAndFolderSizes((File) path.getLastPathComponent(),log, progressBar,progressMonitor);
                }
                task.addPropertyChangeListener(DiskSpaceInformer.this);
                task.execute();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                setupAndShowUI();
            }
        });
    }



}

