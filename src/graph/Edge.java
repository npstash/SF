package graph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import proc.content.Paintable;


/**
 * @author Nestor
 */

public class Edge implements Paintable {

    private Vertex end = null;

    private Vertex start = null;
    
    public static int MAX_VALUE = 255;

    private int value = 0;
    
    public Edge(Edge e) {
        super();
        this.start = e.getStart();
        this.end = e.getEnd();
        this.value = e.getValue();
    }

    public Edge(Vertex start, Vertex end) {
        super();
        this.start = start;
        this.end = end;
    }

    public void doPaint(Graphics g, Rectangle delims) {
        int x1, y1, x2, y2;
        x1 = getStart().getX() * delims.width / Vertex.MAX_X;
        y1 = getStart().getY() * delims.height / Vertex.MAX_Y;
        x2 = getEnd().getX() * delims.width / Vertex.MAX_X;
        y2 = getEnd().getY() * delims.height / Vertex.MAX_Y;
        Color currCol = g.getColor();
        int gr = 255 - getValue() * 255 / MAX_VALUE;
        gr = (gr > 255) ? 255 : (gr < 0) ? 0 : gr;
        g.setColor(new Color(gr, gr, gr));
        g.drawLine(x1, y1, x2, y2);
        g.setColor(currCol);
    }

    public Vertex getEnd() {
        return end;
    }

    public Vertex getStart() {
        return start;
    }

    public int getValue() {
        return value;
    }

    protected void setEnd(Vertex end) {
        this.end = end;
    }

    protected void setStart(Vertex start) {
        this.start = start;
    }

    public void setValue(int value) {
        this.value = value;
    }
}