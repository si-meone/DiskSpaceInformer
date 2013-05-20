package dsi;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

class FindFileAndFolderSizes extends SwingWorker<Void, Void> {

    private String[] pathsToIgnore;
    private String filter;
    private JTextArea textArea;
    private JProgressBar progressBar;
    private File file;
    private static Logger log = Logger.getLogger(FindFileAndFolderSizes.class.getName());

    public static class Builder{
        //req params
        private File file;
        //opt params
        private String[] pathsToIgnore =  new String[0];
        private String filter = "Size";
        private JTextArea textArea = new JTextArea();
        private JProgressBar progressBar = new JProgressBar();


        public Builder(File file){
              this.file = file;
        }

        public Builder pathstoIgnore(String[] val)
            {pathsToIgnore = val; return this;}
        public Builder filter(String val)
            {filter = val; return this;}
        public Builder textArea(JTextArea val)
            {textArea = val; return this;}
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
        progressBar = builder.progressBar;
    }

    @Override
    public Void doInBackground() throws Exception {
        setProgress(0);
        if (file.isFile()) {
            textArea.setText(new TextFormatter().format(file) + "\n" + textArea.getText() + "\n");
            return null;
        }

        progressBar.setString("Processing Selection...");
        long startTime = System.currentTimeMillis();
        Path root = Paths.get(String.valueOf(file.getPath()));

        List<Path> foldersToIgnore = new ArrayList<Path>();
        for (String path : pathsToIgnore) {
            foldersToIgnore.add(new File(path).toPath());
        }
            FileSystemVisitor visitor = new FileSystemVisitor(root, foldersToIgnore, progressBar);
        try {
            Files.walkFileTree(root, visitor);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (filter.equals("Size")){
            SizeComparator vc = new SizeComparator(visitor.getFoldersSizes());
            textArea.setText(visitor.getTreeView(vc) + "\n" + textArea.getText());
        }else{
            textArea.setText(visitor.getTreeView(null) + "\n" + textArea.getText());
        }
        long endTime = System.currentTimeMillis();
        log.info("The scan for [" + file.getAbsolutePath() + "] took " + (endTime - startTime) + " milliseconds");
        return null;
    }

    public String checkSpaceAvailable() {
        StringBuffer sb = new StringBuffer();
        File[] roots = File.listRoots();
        for (File root : roots) {
            long totalSpace = root.getTotalSpace();
            long freeSpace = root.getFreeSpace();
            long usedSpace = totalSpace - freeSpace;
            sb.append(new TextFormatter().format(root.getPath(), totalSpace, usedSpace, freeSpace));
        }
        return sb.toString();
    }


    @Override
    public void done() {
        progressBar.setString("Task Complete...");
    }

}
