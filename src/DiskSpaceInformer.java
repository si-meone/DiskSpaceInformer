import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileFilter;
import java.text.DecimalFormat;
import java.util.*;

/*
 * DiskSpaceInformer.java
 */
public class DiskSpaceInformer extends JPanel
        implements ActionListener, PropertyChangeListener {
    static private final String newline = "\n";
    private static String version = "Directory Sizer v0.1c";
    private final JButton checkButton;
    JButton openButton, clearButton;
    JTextArea log;
    JFileChooser fileChooser;
    JProgressBar jProgressBar;

    private FindFileAndFolderSizes task;
    private ProgressMonitor progressMonitor;

    private int fileCount = 0;
    private long folderSize = 0;


    class FindFileAndFolderSizes extends SwingWorker<Void, Void> {

        private File dir;

        FindFileAndFolderSizes(File dir) {
            this.dir = dir;
        }

        @Override
        public Void doInBackground() {
            Map<String, Long> listing = new HashMap<String, Long>();

            jProgressBar.setStringPainted(true);
            jProgressBar.setString("Determining files to scan");
            jProgressBar.setVisible(true);
            jProgressBar.setIndeterminate(true);

            fileCount = 0;

            setProgress(0);
            long totalSize = 0L;

            for (File file : dir.listFiles(new IgnoreFilter())) {
                if (file.isFile()) {
                    long size = file.length();
                    totalSize += size;
                    listing.put(file.getName(), size);
                } else {
                    folderSize = 0;
                    getFolderSize(file);
                    totalSize += folderSize;
                    listing.put(file.getName(), folderSize);
                }
            }

            jProgressBar.setString("Sorting Listing...");
            ValueComparator vc = new ValueComparator(listing);
            Map<String, Long> sortedMap = new TreeMap<String, Long>(vc);
            sortedMap.putAll(listing);
            jProgressBar.setIndeterminate(false);
            jProgressBar.setVisible(false);
            PrettyPrint(dir, totalSize, sortedMap);

            return null;
        }

        private long getFolderSize(File file) {
            if (file.isFile()) {
                folderSize += file.length();
            } else {
                //log.append("\nProcessing size: " + file);
                String[] contents = file.list();
                if (contents != null) {  //take care of empty folders
                    for (File f : file.listFiles(new IgnoreFilter())) {
                        jProgressBar.setString(file.toString());
                        getFolderSize(f);
                    }
                }
            }
            return folderSize;
        }

        public void setProgressBar(int i) {
            super.setProgress(i);

        }

        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            progressMonitor.close();
        }

    }


    public DiskSpaceInformer() {
        super(new BorderLayout());
        JFrame f = new JFrame();
        log = new JTextArea(30, 35);
        log.setMargin(new Insets(5, 5, 5, 5));
        log.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(log);
        logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        logScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        //Create a file chooser
        String os = System.getProperty("os.name");
        fileChooser = new JFileChooser();

        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        openButton = new JButton("Choose Folder...");
        openButton.addActionListener(this);

        checkButton = new JButton("Check Space...");
        checkButton.addActionListener(this);

        clearButton = new JButton("Clear Log...");
        clearButton.addActionListener(this);

        //flow layout
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(checkButton);

        jProgressBar = new JProgressBar(0, 100);
        JPanel progressPanel = new JPanel();
        progressPanel.setSize(100, 100);
        progressPanel.setMinimumSize(new Dimension(200, 200));
        progressPanel.add(jProgressBar);

        //Add bits to the panel.
        add(buttonPanel, BorderLayout.PAGE_START);
        add(progressPanel, BorderLayout.CENTER);
        add(logScrollPane, BorderLayout.PAGE_END);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openButton) { //open
            int returnVal = fileChooser.showOpenDialog(DiskSpaceInformer.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File dir = fileChooser.getSelectedFile();

                progressMonitor = new ProgressMonitor(DiskSpaceInformer.this,
                        "Getting Folder sizes",
                        "", 0, 100);
                progressMonitor.setProgress(0);
                task = new FindFileAndFolderSizes(dir);
                task.addPropertyChangeListener(this);
                task.execute();
            } else {
                log.append("Open command cancelled by user." + newline);
            }
            log.setCaretPosition(log.getDocument().getLength());

        } else if (e.getSource() == clearButton) {
            log.setText("");  //clear
        } else if (e.getSource() == checkButton) {
            log.append(checkSpaceAvailable());  //check
            log.setCaretPosition(log.getDocument().getLength());
        }
    }

    private String checkSpaceAvailable() {
        StringBuffer sb = new StringBuffer();
        File[] roots = File.listRoots();
        for (File root : roots) {
            long totalSpace = root.getTotalSpace();
            long freeSpace = root.getFreeSpace();
            long usedSpace = totalSpace - freeSpace;

            String title = "Checking: [ " + root + " ]";
            String underline = String.format(String.format("%%0%dd", title.length()), 0).replace("0", "=");

            sb.append(underline + "\n" + title + "\n" + underline);
            sb.append(String.format("\nTotal Space is: [%s]\nUsed space is: [%s] \nFree space is: [%s] \n\n",
                    readableFileSize(totalSpace),
                    readableFileSize(usedSpace),
                    readableFileSize(freeSpace))
            );
        }
        return sb.toString();
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("ProgressLevel" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressMonitor.setProgress(progress);
            String message =
                    String.format("Completed %d%%.\n", progress);
            progressMonitor.setNote(message);
            //log.append(message); // see it print increments
            if (progressMonitor.isCanceled() || task.isDone()) {
                Toolkit.getDefaultToolkit().beep();
                if (progressMonitor.isCanceled()) {
                    log.append("Task canceled.\n");
                    task.cancel(true);
                } else {
                    log.append("Task completed.\n");
                }
            }
        }
    }

    private void PrettyPrint(File file, long total, Map<String, Long> sortedFileFolderSizes) {
        String title = file.getName() + ": [" + readableFileSize(total) + "]";
        String underline = String.format(String.format("%%0%dd", title.length()), 0).replace("0", "=");
        log.append(underline + newline);
        log.append(title + newline);
        log.append(underline + newline);
        for (Map.Entry<String, Long> entry : sortedFileFolderSizes.entrySet()) {
            log.append("[ " + readableFileSize(entry.getValue()) + " ]");
            log.append(" --> " + entry.getKey() + "\n");
        }
        log.append(newline + newline);
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

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
