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

class FindFileAndFolderSizes extends SwingWorker<Void, Void> {

    private String[] pathsToIgnore;
    private JTextArea textArea;
    private JProgressBar progressBar;
    private File file;
    private boolean debug;
    private Formatter formatter = new TextFormatter();

    public static class Builder{
        //req params
        private File file;
        //opt params
        private String[] pathsToIgnore =  new String[0];
        private JTextArea textArea = new JTextArea();
        private Formatter formatter = new TextFormatter();
        private boolean debug = false;
        private JProgressBar progressBar = new JProgressBar();

        public Builder(File file){
              this.file = file;
        }

        public Builder pathstoIgnore(String[] val)
            {pathsToIgnore = val; return this;}
        public Builder textArea(JTextArea val)
            {textArea = val; return this;}
        public Builder formatter(Formatter val)
            {formatter = val; return this;}
        public Builder debug(boolean val)
            {debug = val; return this;}
        public Builder progressBar(JProgressBar val)
            {progressBar = val; return this;}

        public FindFileAndFolderSizes build(){
            return new FindFileAndFolderSizes(this);
        }
    }

    private FindFileAndFolderSizes(Builder builder){
        file = builder.file;
        pathsToIgnore = builder.pathsToIgnore;
        textArea = builder.textArea;
        formatter = builder.formatter;
        debug = builder.debug;
        progressBar = builder.progressBar;
    }

    @Override
    public Void doInBackground() throws Exception {
        if (file.isFile()) {
            textArea.setText(formatter.format(file) + "\n" + textArea.getText() + "\n");
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
            Formatter debugFormatter = new TextDebugFormatter();
            textArea.setText(debugFormatter.format(file, visitor.getGrandTotal(), sortedMap, extraInfo) + "\n" + textArea.getText() + "\n");
        } else {
            String status = extraInfo.length() > 0 ? "  Error(s): turn on debug checkbox" : "";
            textArea.setText(formatter.format(file, visitor.getGrandTotal(), sortedMap, status) + "\n" + textArea.getText() + "\n");
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
            sb.append(formatter.format(root.getPath(), totalSpace, usedSpace, freeSpace));
        }
        return sb.toString();
    }

    @Override
    public void done() {
        progressBar.setString("Task Complete...");
    }

}
