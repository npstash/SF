package graph.infect;

import graph.Edge;
import graph.Vertex;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;


/**
 * @author Nestor
 */
public class InfectibleEdge extends Edge {

    private Vertex infectDirection = null;

    private boolean infected = false;

    public InfectibleEdge(Edge e) {
        super(e);
    }

    public InfectibleEdge(Vertex start, Vertex end) {
        super(start, end);
    }

    public void doPaint(Graphics g, Rectangle delims) {
        super.doPaint(g, delims);
        int x1, y1, x2, y2;
        x1 = getStart().getX() * delims.width / Vertex.MAX_X;
        y1 = getStart().getY() * delims.height / Vertex.MAX_Y;
        x2 = getEnd().getX() * delims.width / Vertex.MAX_X;
        y2 = getEnd().getY() * delims.height / Vertex.MAX_Y;
        Color currCol = g.getColor();
        if (infected) {
            g.setColor(new Color(255, 0, 0));
        } else {
            int gr = 255 - getValue() * 255 / MAX_VALUE;
            g.setColor(new Color(0, gr, 0));
        }
        g.drawLine(x1, y1, x2, y2);
        g.setColor(currCol);
    }

    public Vertex getInfectDirection() {
        return infectDirection;
    }

    public boolean isInfected() {
        return infected;
    }

    public void setInfectDirection(Vertex infectDirection) {
        this.infectDirection = infectDirection;
    }

    public void setInfected(boolean infected) {
        this.infected = infected;
    }
}