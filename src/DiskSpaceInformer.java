import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

/*
 * DiskSpaceInformer.java
 */
public class DiskSpaceInformer extends JPanel
        implements ActionListener, PropertyChangeListener {
    static private final String newline = "\n";
    private static String version = "Directory Sizer v0.1c";;
    private final JButton checkButton;
    JButton openButton, clearButton;
    JTextArea log;
    JFileChooser fileChooser;
    JProgressBar jProgressBar;
    private FindFileAndFolderSizes task;

    private ProgressMonitor progressMonitor;
    private long filesFolderCount = 0;
    private static float increment = 0.0f;
    private static float progress = 0.0f;

    class FindFileAndFolderSizes extends SwingWorker<Void, Void> {

        private File dir;

        FindFileAndFolderSizes(File dir) {
            this.dir = dir;
        }

        @Override
        public Void doInBackground() {

            File[] files = null;
            Map<String, Long> dirListing = new HashMap<String, Long>();

            jProgressBar.setStringPainted(true);
            jProgressBar.setString("Determining files to scan");
            jProgressBar.setVisible(true);
            jProgressBar.setIndeterminate(true);

            try {
                files = dir.listFiles();
            } catch (SecurityException se) {
                jProgressBar.setIndeterminate(false);
                jProgressBar.setString("Security problem: " + se);
                throw new SecurityException("Security problem: " + se);
            }
            if (null == files) {
                jProgressBar.setString("Unable to retrieve folder information check permissions");
                dirListing = new HashMap<String, Long>();
            }

            for (File file : files) {
                jProgressBar.setString(file.toString());
                //log.append("Processing: " + file);
                boolean onlyCount = true;
                DiskUsage diskUsage = new DiskUsage(onlyCount);
                diskUsage.accept(file);
                filesFolderCount += diskUsage.getCount();
            }
            //log.append("count: " + filesFolderCount);

            if (filesFolderCount != 0) {
                increment = 100.0f / filesFolderCount;
            }
            jProgressBar.setVisible(false);

            setProgress(0);
            //log.append("increment will be: " + increment);
            for (File file : files) {
                //log.append("Processing: " + file);
                DiskUsage diskUsage = new DiskUsage();
                diskUsage.accept(file);
                long size = diskUsage.getSize();
                dirListing.put(file.getName(), size);
            }
            ValueComparator bvc = new ValueComparator(dirListing);
            Map<String, Long> sortedMap = new TreeMap<String, Long>(bvc);
            sortedMap.putAll(dirListing);
            PrettyPrint(dir, sortedMap);
            return null;
        }

        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            progressMonitor.close();
        }

        class DiskUsage implements FileFilter {

            public DiskUsage() {
            }

            ;

            public DiskUsage(boolean count) {
                this.count = count;
            }

            ;
            private long fileCount = 0;
            private long size = 0;
            private boolean count = false;

            public boolean accept(File file) {
                if (file.isFile()) {
                    if (count) {
                        fileCount++;
                    } else {
                        size += file.length();
                        progress += increment;
                        //log.append("progress: " + progress);
                        if ((int) Math.round(progress) % 10 == 0) {
                            setProgress(Math.min((int) Math.round(progress), 100));
                        }
                    }
                } else if (file.isDirectory() && !isSymlink(file) && !isVirtualFileSystem(file)) {
                    String[] contents = file.list();
                    if (contents != null) {  //take care of empty folders
                        file.listFiles(this);
                    }
                } else {
                    size += 0;
                }

                return false;
            }

            /*
                Folders to avoid they give strange readings
             */
            private boolean isVirtualFileSystem(File file) {
                boolean isVfs = false;
                String absPath = file.getAbsolutePath();
                if (absPath.equalsIgnoreCase("/proc")
                        || absPath.equalsIgnoreCase("/dev")) {
                    isVfs = true;
                }
                return isVfs;  //To change body of created methods use File | Settings | File Templates.
            }

            private boolean isSymlink(File file) {
                File canon = null;
                boolean isLink = false;
                try {
                    if (file.getParent() == null) {
                        canon = file;
                    } else {
                        File canonDir = file.getParentFile().getCanonicalFile();
                        canon = new File(canonDir, file.getName());
                        isLink = !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
                    }
                } catch (IOException e) {
                    log.append(e.toString());
                }

                return isLink;
            }

            public long getSize() {
                return size;
            }

            public long getCount() {
                return fileCount;
            }
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
        for(File root : roots){
            long totalSpace = root.getTotalSpace();
            long freeSpace = root.getFreeSpace();
            long usedSpace = totalSpace - freeSpace;

            String title =  "Checking: [ " + root + " ]";
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
                    log.append("Task canceled.\n");
                    task.cancel(true);
                } else {
                    log.append("Task completed.\n");
                }
            }
        }
    }

    private void PrettyPrint(File file, Map<String, Long> sortedFileFolderSizes) {
        Long total = 0L;
        for (Long value : sortedFileFolderSizes.values()) {
            total = total + value; // Can also be done by total += value;
        }
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

class ValueComparator implements Comparator<String> {

    Map<String, Long> base;

    public ValueComparator(Map<String, Long> base) {
        this.base = base;
    }

    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // take care of 0  merge keys ?
    }

}