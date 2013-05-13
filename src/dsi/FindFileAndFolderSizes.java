package dsi;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static dsi.PrettyPrint.prettyPrint;

class FindFileAndFolderSizes extends SwingWorker<Void, Void> {

    private String[] pathsToIgnore;
    private JTextArea textArea;
    private JProgressBar progressBar;
    private File file;
    private boolean debug;

    FindFileAndFolderSizes(File file, String[] pathsToIgnore, JTextArea textArea, JProgressBar progressBar, boolean debug) {
        this(file, debug);
        this.pathsToIgnore = pathsToIgnore;
        this.textArea = textArea;
        this.progressBar = progressBar;
    }

    FindFileAndFolderSizes(File file, boolean debug) {
        this.file = file;
        this.debug = debug;
    }

    FindFileAndFolderSizes() {
    }

    @Override
    public Void doInBackground() throws Exception {
        if (file.isFile()) {
            textArea.setText(prettyPrint(file) + "\n" + textArea.getText() + "\n");
            return null;
        }

        progressBar.setString("Processing Selection...");
        Map<String, Long> foldersSizes = null;
        Path root = Paths.get(String.valueOf(file.getPath()));

        List<Path> foldersToIgnore = new ArrayList<Path>();
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
        if (debug) {
            textArea.setText(prettyPrint(file, visitor.getGrandTotal(), sortedMap, extraInfo) + "\n" + textArea.getText() + "\n");
        } else {
            textArea.setText(prettyPrint(file, visitor.getGrandTotal(), sortedMap, !extraInfo.isEmpty()) + "\n" + textArea.getText() + "\n");
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
            sb.append(prettyPrint(root.getPath(), totalSpace, usedSpace, freeSpace));
        }
        return sb.toString();
    }

    @Override
    public void done() {
        progressBar.setString("Task Complete...");
    }

}
