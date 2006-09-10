package graph.infect;

import graph.Vertex;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * @author Nestor
 */
public class InfectibleVertex extends Vertex {

    private boolean infected = false;

    public InfectibleVertex(int x, int y) {
        super(x, y);
    }

    public InfectibleVertex(Vertex v) {
        super(v);
    }

    public void doPaint(Graphics g, Rectangle delims) {
        super.doPaint(g, delims);
        if (infected) {
            int x1, y1, w, h;
            x1 = getX() * delims.width / MAX_X;
            y1 = getY() * delims.height / MAX_Y;
            Color currCol = g.getColor();
            g.setColor(new Color(255, 0, 0));
            g.drawOval(x1 - Vertex.VERTEX_SIZE / 2,
                    y1 - Vertex.VERTEX_SIZE / 2, Vertex.VERTEX_SIZE,
                    Vertex.VERTEX_SIZE);
            g.setColor(currCol);
        }
    }

    public boolean isInfected() {
        return infected;
    }

    public void setInfected(boolean infected) {
        this.infected = infected;
    }
}