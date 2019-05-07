package de.unitrier.st.treemap;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

class DirectoryNode extends Node {
    private final List<Node> childNodes = new ArrayList<>();

    private DirectoryNode(File file) {
        setName(file.getName());
        setPath(file.getAbsolutePath());
    }

    private void addChildNode(Node childNode) {
        childNodes.add(childNode);
        setSize(getSize() + childNode.getSize());
        setScaledSize(getScaledSize() + childNode.getScaledSize());
    }

    static DirectoryNode retrieveDirectoryTree(File file) {
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("Not a directory.");
        }

        DirectoryNode root = new DirectoryNode(file);
        File[] containedFiles = file.listFiles();
        containedFiles = containedFiles == null ? new File[0] : containedFiles;

        for (File child : containedFiles) {
            if (child.isHidden()) {
                continue;
            }
            if (child.isDirectory()) {
                root.addChildNode(DirectoryNode.retrieveDirectoryTree(child));
            } else {
                root.addChildNode(new FileNode(child));
            }
        }

        return root;
    }

    @Override
    void paint(Graphics g, int outerWidth, int outerHeight) {
        paint(g, outerWidth, outerHeight, true);
    }

    private void paint(Graphics g, int outerWidth, int outerHeight, boolean splitHorizontal) {
        // TODO: recursively draw child nodes

        // if height > width, the current rect has a vertical orientation
        // => split it horizontally

        splitHorizontal = outerHeight > outerWidth;

        for (Node child: childNodes) {
            int width =     splitHorizontal ? outerWidth : (int) (((double) child.getSize() / (double) getSize()) * (double) outerWidth);
            int height =    splitHorizontal ? (int) (((double) child.getSize() / (double) getSize()) * (double) outerHeight) : outerHeight;
            child.paint(g, width, height);
        }


    }

    @Override
    FileNode getSelectedNode(double x, double y) {
        for (Node child : childNodes) {
            FileNode selectedNode = child.getSelectedNode(x, y);
            if (selectedNode != null)
                return selectedNode;
        }
        return null;
    }
}
