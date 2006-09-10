package graph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import proc.content.Paintable;
import util.HashRegistry;

public class Vertex implements Paintable, Comparable {

    public static int MAX_X = 10000;

    public static int MAX_Y = 10000;

    public static int VERTEX_SIZE = 10;

    private Object maxValueKey = null;

    private int value = 0;

    private int x = 0;

    private int y = 0;

    public Vertex(int x, int y) {
        super();
        this.x = x;
        this.y = y;
    }

    public Vertex(Vertex v) {
        super();
        this.x = v.getX();
        this.y = v.getY();
        this.value = v.getValue();
    }

    public int compareTo(Object o) {
        if (o instanceof Vertex) {
            Vertex v = (Vertex) o;
            return value - v.value;
        }
        return 0;
    }

    public int computeDistanceTo(Vertex to) {
        long diffX = getX() - to.getX();
        long diffY = getY() - to.getY();
        long r = diffX * diffX + diffY * diffY;
        int res = (int) Math.sqrt((double) r);
        return res;
    }

    public void doPaint(Graphics g, Rectangle delims) {
        int x1, y1;
        x1 = getX() * delims.width / MAX_X;
        y1 = getY() * delims.height / MAX_Y;
        Color currCol = g.getColor();
        Integer maxValue = null;
        if (maxValueKey != null) {
            maxValue = (Integer) HashRegistry.getObject(maxValueKey);
        }
        int gr = 100;
        if (maxValue != null) {
            gr = 255 - getValue() * 255 / maxValue.intValue();
            gr = (gr > 255) ? 255 : (gr < 0) ? 0 : gr;
        }
        g.setColor(new Color(0, gr, 0));
        g.fillOval(x1 - VERTEX_SIZE / 2, y1 - VERTEX_SIZE / 2, VERTEX_SIZE,
                VERTEX_SIZE);
        g.setColor(currCol);
        if (maxValue == null) {
            g.drawString("" + getValue(), x1 + VERTEX_SIZE / 2, y1
                    + VERTEX_SIZE / 2);
        }
    }

    public int getValue() {
        return value;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setMaxValueKey(Object maxValueKey) {
        this.maxValueKey = maxValueKey;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}