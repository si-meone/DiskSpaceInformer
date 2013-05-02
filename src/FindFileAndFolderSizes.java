import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.TreeMap;

class FindFileAndFolderSizes extends SwingWorker<Void, Void> {

    private File file;
    private JTextArea log;
    private JProgressBar progressBar;
    private boolean summary = false;

    FindFileAndFolderSizes(File file, JTextArea log, JProgressBar progressBar, boolean summary) {
        this(file, log, progressBar);
        this.summary = summary;
    }

    FindFileAndFolderSizes(File file, JTextArea log, JProgressBar progressBar) {
        this.file = file;
        this.log = log;
        this.progressBar = progressBar;
    }

    @Override
    public Void doInBackground() {
        if (file.isFile()) {
            Utils.prettyPrint(file, log);
            return null;
        }

        progressBar.setString("Determining files to scan");
        progressBar.setStringPainted(true);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);

        Map<String, Long> foldersSizes = null;
        Path root = Paths.get(String.valueOf(file.getPath()));
        CalculateFileSizeVisitor visitor = new CalculateFileSizeVisitor(root ,progressBar);
        try {
            Files.walkFileTree(root, visitor);
            foldersSizes = visitor.getFoldersSizes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        progressBar.setString("Sorting Listing...");
        ValueComparator vc = new ValueComparator(foldersSizes);
        Map<String, Long> sortedMap = new TreeMap<String, Long>(vc);
        sortedMap.putAll(foldersSizes);
        progressBar.setIndeterminate(false);
        if (summary) {
            int lastDoc = log.getDocument().getLength();
            Utils.prettyPrint(file, visitor.getGrandTotal(), log);
            log.setCaretPosition(lastDoc);
        } else {
            int lastDoc = log.getDocument().getLength();
            Utils.prettyPrint(file, visitor.getGrandTotal(), sortedMap, log);
            log.setCaretPosition(lastDoc);
        }
        return null;
    }

    @Override
    public void done() {
        Toolkit.getDefaultToolkit().beep();
    }

}
