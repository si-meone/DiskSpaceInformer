
class TreeFile extends java.io.File {
    public TreeFile(java.io.File f) {
        super(f.getAbsolutePath());
    }

    public TreeFile(String s) {
        super(s);
    }

    @Override
    public String toString() {
        return getName();
    }
}