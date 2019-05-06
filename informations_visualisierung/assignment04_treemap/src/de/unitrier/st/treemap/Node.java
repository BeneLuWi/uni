package de.unitrier.st.treemap;

import java.awt.*;

abstract class Node {
    private String name;
    private String path;
    private long size;
    private double scaledSize;
    private double xPos;
    private double yPos;
    private double width = 1.0;
    private double height = 1.0;

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getPath() {
        return path;
    }

    void setPath(String path) {
        this.path = path;
    }

    long getSize() {
        return size;
    }

    void setSize(long size) {
        this.size = size;
    }

    String getSizeString() {
        int unit = 1000;
        if (size < unit) return size + " B";
        int exponent = (int) (Math.log(size) / Math.log(unit));
        String prefix = ("KMGTPE").charAt(exponent-1) + "i";
        return String.format("%.1f %sB", size / Math.pow(unit, exponent), prefix);
    }

    double getScaledSize() {
        return scaledSize;
    }

    void setScaledSize(double scaledSize) {
        this.scaledSize = scaledSize;
    }

    double getHeight() {
        return height;
    }

    void setHeight(double height) {
        this.height = height;
    }

    double getWidth() {
        return width;
    }

    void setWidth(double width) {
        this.width = width;
    }

    double getXPos() {
        return xPos;
    }

    void setxPos(double xPos) {
        this.xPos = xPos;
    }

    double getYPos() {
        return yPos;
    }

    void setyPos(double yPos) {
        this.yPos = yPos;
    }

    abstract void paint(Graphics g, int outerWidth, int outerHeight);

    abstract FileNode getSelectedNode(double x, double y);
}
