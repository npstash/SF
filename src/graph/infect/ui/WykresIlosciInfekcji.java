package graph.infect.ui;

import graph.Graph;
import graph.Vertex;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JPanel;

import proc.content.ContentEvent;
import proc.content.ContentListener;
import proc.param.DefaultParameterContainer;
import proc.param.ParameterContainer;
import proc.param.StepProcessParameter;

public class WykresIlosciInfekcji extends JPanel implements ContentListener,
        ParameterContainer {

    public static final String PARAM_GRID = "Iloœæ poziomych linii siatki";

    private Graph graph = null;

    private DefaultParameterContainer params = new DefaultParameterContainer();

    public WykresIlosciInfekcji() {
        super();
        params.addParameter(new StepProcessParameter(PARAM_GRID,
                new Integer(5), new Integer(0), null));
    }

    public void contentsChanged(ContentEvent e) {
        Object p = e.getContents();
        if (p instanceof Graph) {
            graph = (Graph) p;
            repaint();
        }
    }

    private void rysujWykres(Graphics g) {
        if (graph == null)
            return;
        // trzeba posortowac wierzcholki od najczesciej infekowanych
        // a potem utworzyc histogram
        Vector sortedVertices = new Vector(graph.getVertexCollection());
        if (sortedVertices.isEmpty()) {
            return;
        }
        Collections.sort(sortedVertices);

        int[] rozklad = new int[sortedVertices.size()];
        int[] rozkladStopni = graph.getConnectivityDistribution();
        for (int i = 0; i < rozklad.length; i++) {
            rozklad[i] = ((Vertex) sortedVertices.get(i)).getValue();
        }
        int minInf = rozklad[0];
        int maxInf = rozklad[rozklad.length - 1];
        if (maxInf == 0)
            return;

        FontMetrics fm = g.getFontMetrics();
        int offsetY = 10;
        int offsetX = 10;

        int wysokosc = getHeight() - 2 * offsetY;
        int szerokosc = getWidth() - 2 * offsetX;
        int startWykresu = offsetY;
        int startRozkladu = offsetY + 5 * wysokosc / 10;
        int koniecWykresu = offsetY + 4 * wysokosc / 10;
        int koniecRozkladu = offsetY + 9 * wysokosc / 10;
        int wysokoscWykresu = koniecWykresu - startWykresu;
        int wysokoscRozkladu = koniecRozkladu - startRozkladu;

        double pixelsPerInfection = (double) (wysokoscWykresu)
                / (double) maxInf;
        double pixelsPerGrade = (double) (wysokoscRozkladu)
                / (double) rozkladStopni.length;
        double pixelsPerVertex = (double) (szerokosc)
                / (double) (rozklad.length - 1);
        g.setColor(Color.WHITE);
        g.fillRect(offsetX, startWykresu, szerokosc, wysokoscWykresu);
        g.fillRect(offsetX, startRozkladu, szerokosc, wysokoscRozkladu);
        g.setColor(Color.BLACK);
        g.drawString("Iloœæ infekcji w kolejnych wierzcho³kach (posortowane)",
                offsetX, koniecWykresu + fm.getHeight());
        g.drawString("Stopnie odpowiadaj¹cych wierzcho³ków",
                offsetX, koniecRozkladu + fm.getHeight());

        for (int n = 0; n < rozklad.length; n++) {
            int startX = (int) ((double) n * pixelsPerVertex) + offsetX;
            int endX = (int) ((double) (n + 1) * pixelsPerVertex) + offsetX;

            // wykres
            int value = rozklad[n];
            int startY = wysokoscWykresu
                    - (int) ((double) value * pixelsPerInfection)
                    + startWykresu;

            g.setColor(Color.BLACK);
            g.drawLine(startX, startY, startX, wysokoscWykresu + startWykresu);

//            g.setColor(Color.GRAY);
//            g.drawLine(startX, startRozkladu, startX, koniecRozkladu);

            // rozklad
            if (n < rozklad.length - 1) {
                Vertex startVertex = (Vertex) sortedVertices.get(n);
                int startValue2 = graph.getVertexConnectivity(startVertex);
                int startY2 = (wysokoscRozkladu)
                        - (int) ((double) startValue2 * pixelsPerGrade)
                        + startRozkladu;

                Vertex endVertex = (Vertex) sortedVertices.get(n + 1);
                int endValue2 = graph.getVertexConnectivity(endVertex);
                int endY2 = (wysokoscRozkladu)
                        - (int) ((double) endValue2 * pixelsPerGrade)
                        + startRozkladu;
                g.setColor(Color.RED);
                g.drawLine(startX, startY2, endX, endY2);
                g.drawLine(startX, startY2, startX, koniecRozkladu);
            }
        } // for

        // linie siatki
        int linie = ((Integer) getParameter(PARAM_GRID).getValue()).intValue();
        if (linie > 0) {
            if (maxInf - minInf < linie) {
                linie = maxInf - minInf;
            }
            double pixelsPerLine = ((double) wysokoscWykresu / ((double) linie));
            double infPerLine = ((double) (maxInf - minInf) / (double) linie);
            g.setColor(Color.BLUE);
            for (int i = 0; i < linie; i++) {
                int Y = (int) ((double) i * pixelsPerLine) + startWykresu;
                g.drawLine(offsetX, Y, szerokosc + offsetX, Y);

                int inf = maxInf - (int) ((double) i * infPerLine);
                g.drawString("" + inf, offsetX, Y);
            }
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        rysujWykres(g);
    }

    public void addParameter(StepProcessParameter param) {
        params.addParameter(param);
    }

    public StepProcessParameter getParameter(String name) {
        return params.getParameter(name);
    }

    public int getParameterCount() {
        return params.getParameterCount();
    }

    public Collection getParameters() {
        return params.getParameters();
    }
}