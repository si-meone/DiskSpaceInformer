package dsi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TreeNode {

    final String name;
    final List<TreeNode> children;
    public static StringBuffer output = new StringBuffer();

    public TreeNode(String name, TreeNode... children) {
        this.name = name;
        this.children = java.util.Arrays.asList(children);
    }

    public void print() {
        print("", true);
    }

    private void print(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + name);
        output.append(prefix + (isTail ? "└── " : "├── ") + name + "\n");
        for (Iterator<TreeNode> iterator = children.iterator(); iterator.hasNext(); ) {
            iterator.next().print(prefix + (isTail ? "    " : "│   "), !iterator.hasNext());
        }
    }

    public static void main(String[] args) {
        TreeNode[] children = {new TreeNode("b"),new TreeNode("c")};
        System.out.print(new TreeNode("a", children));
    }
}