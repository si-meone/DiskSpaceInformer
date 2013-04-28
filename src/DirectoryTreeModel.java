import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.util.Arrays;

public class DirectoryTreeModel implements TreeModel {
    protected TreeFile topDirectory;
    private EventListenerList listeners = new EventListenerList();

    public DirectoryTreeModel(TreeFile directory) {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("not a directory");
        }
        this.topDirectory = directory;
    }

    public DirectoryTreeModel(String dirName) {
        this (new TreeFile(dirName));
    }
    public DirectoryTreeModel() {
        this ("/");
    }

    public Object getRoot() {
        return topDirectory;
    }



    public boolean isLeaf(Object node) {
        assert (node instanceof TreeFile);
        return !((File) node).isDirectory();
    }

    public int getChildCount(Object parent) {
        assert (parent instanceof File);
        if (null == ((File) parent).list()){
         return 0;
        }
        return ((File)parent).listFiles().length;
    }

    public Object getChild(Object parent, int index) {
        assert (parent instanceof TreeFile);
        TreeFile treeFile = new TreeFile((File) parent);
        return (new TreeFile(treeFile.listFiles()[index]));
    }

    public int getIndexOfChild(Object parent, Object child) {
        assert (parent instanceof File);
        assert (child instanceof File);
        assert ((File) parent).isDirectory();
        return Arrays.asList(((File) parent).listFiles()).indexOf(child);
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        // blank implementation, we do not allow and expect changes to data
    }

    public void addTreeModelListener(TreeModelListener listener) {
        listeners.add(TreeModelListener.class, listener);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        //To change body of implemented methods use TreeFile | Settings | TreeFile Templates.
    }


}


