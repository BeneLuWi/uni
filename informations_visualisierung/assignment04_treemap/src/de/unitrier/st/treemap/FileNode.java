package de.unitrier.st.treemap;

import java.awt.*;
import java.io.File;
import java.nio.file.Paths;

class FileNode extends Node {
    enum ScalingApproaches {
        linear, logarithmic, squareRoot, square
    }
    static ScalingApproaches selectedScalingApproach = ScalingApproaches.linear;
    private static final long now = System.currentTimeMillis();

    private final long age;

    FileNode(File file) {
        setName(file.getName());
        setPath(Paths.get(file.getAbsolutePath()).getParent().toString());
        setSize(file.length());
        age = now - file.lastModified();
    }

    @Override
    void setSize(long size) {
        super.setSize(size);
        setScaledSize(scale(size));
    }

    private double scale(long value) {
        return scale((double)value);
    }

    private double scale(double value) {
        switch (selectedScalingApproach) {
            case linear:
                return value;
            case logarithmic:
                return Math.log(1 + value); // prevent negative values
            case squareRoot:
                return Math.sqrt(value);
            case square:
                return value * value;
            default:
                throw new IllegalArgumentException("Unknown scaling approach.");
        }
    }

    @Override
    void paint(Graphics g, int outerWidth, int outerHeight) {
        paint(g, outerWidth, outerHeight, false);
    }

    void paint(Graphics graphics, int outerWidth, int outerHeight, boolean highlight) {
        drawRect(graphics, outerWidth, outerHeight, highlight);
    }

    private void drawRect(Graphics g, int outerWidth, int outerHeight, boolean highlight) {
        // TODO: draw file node as rectangle (fill color should be determined using getAgeColor(), border should be Color.gray for highlighted nodes, otherwise Color.black)
        // Draw rectangle filled with certain color
        g.setColor(getAgeColor());
        g.fillRect((int)getXPos(),(int)getYPos(), outerWidth, outerHeight);

        // Draw the border on top of the rectangle
        g.setColor(highlight? Color.gray : Color.black);
        g.drawRect((int)getXPos(),(int)getYPos(), outerWidth, outerHeight);
    }

    private Color getAgeColor() {
        // TODO: find suitable mapping



        return LinearOptimalColorScale.map((int) (Math.random() * (double)255));
    }

    @Override
    FileNode getSelectedNode(double x, double y) {
        return intersects(x, y) ? this : null;
    }

    private boolean intersects(double x, double y) {
        return x >= getXPos() && x < getXPos() + getWidth() && y >= getYPos() && y < getYPos() + getHeight();
    }
}
