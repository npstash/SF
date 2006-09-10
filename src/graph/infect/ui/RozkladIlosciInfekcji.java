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

public class RozkladIlosciInfekcji extends JPanel implements ContentListener,
        ParameterContainer {

    public static final String PARAM_BAGS = "Iloœæ koszy";

    public static final String PARAM_VIEW = "Sposób wyœwietlania: 0 - linie, 1 - s³upki";

    private Graph graph = null;

    private DefaultParameterContainer params = new DefaultParameterContainer();

    public RozkladIlosciInfekcji() {
        super();
        addParameter(new StepProcessParameter(PARAM_BAGS, new Integer(15),
                new Integer(2), null));
        addParameter(new StepProcessParameter(PARAM_VIEW, new Integer(1),
                new Integer(0), new Integer(1)));
    }

    public void contentsChanged(ContentEvent e) {
        Object p = e.getContents();
        if (p instanceof Graph) {
            graph = (Graph) p;
            repaint();
        }
    }

    private void rysujRozklad(Graphics g) {
        if (graph == null)
            return;
        // trzeba posortowac wierzcholki od najczesciej infekowanych
        // a potem utworzyc histogram
        Vector sortedVertices = new Vector(graph.getVertexCollection());
        if (sortedVertices.isEmpty()) {
            return;
        }
        Collections.sort(sortedVertices);

        int[] infs = new int[sortedVertices.size()];
        for (int i = 0; i < infs.length; i++) {
            infs[i] = ((Vertex) sortedVertices.get(i)).getValue();
        }
        int minInf = infs[0];
        int maxInf = infs[infs.length - 1];
        int spread = maxInf - minInf + 1;
        if (maxInf == 0)
            return;

        int bags = ((Integer) getParameter(PARAM_BAGS).getValue()).intValue();
        if (bags > spread) {
            bags = spread;
        }
        int rozklad[] = new int[bags];
        for (int i = 0; i < infs.length; i++) {
            int temp = infs[i] - minInf + 1;
            int toBag = (int) ((double) temp / ((double) spread / (double) bags));
            if (toBag > 0)
                toBag--;
            rozklad[toBag]++;
        }

        int maxBag = 0;
        for (int b = 0; b < bags; b++) {
            if (maxBag < rozklad[b]) {
                maxBag = rozklad[b];
            }
        }

        FontMetrics fm = g.getFontMetrics();
        int offset1 = 30;
        int offset2 = 5;
        int offset3 = 5;
        int usedHeight = getHeight() - offset1 - 2 * fm.getHeight();
        int usedWidth = 9 * getWidth() / 10;

        double pixelsPerInfection = (double) (usedHeight) / (double) maxBag;
        double pixelsPerGrade = (double) (usedWidth)
                / (double) (rozklad.length - 1);

        int view = ((Integer) getParameter(PARAM_VIEW).getValue()).intValue();
        int lastX = 0;
        int lastY = 0;
        int previousX = 0;
        int previousY = usedHeight;
        for (int n = 0; n < rozklad.length; n++) {
            int startX = (int) ((double) n * pixelsPerGrade);

            int value = rozklad[n];
            int barHeight = (int) ((double) value * pixelsPerInfection);
            int startY = usedHeight - barHeight + offset1;

            g.setColor(Color.BLACK);
            switch (view) {
            case 0:
                g.drawLine(previousX, previousY, startX, startY);
                break;
            case 1:
                g.drawRect(startX, usedHeight + offset1 - barHeight,
                        (int) pixelsPerGrade, barHeight);
                break;
            }
            int circleSize = 6;
            g.fillOval(startX - circleSize / 2, startY - circleSize / 2,
                    circleSize, circleSize);

            // dodanie opisu i sprawdzenie czy zmiesci sie nastepny
            boolean describe = false;
            if ((startX > lastX) || (startY < lastY - 20 - offset3)
                    || (startY - 20 - offset3 > lastY)) {
                describe = true;
            }

            if ((n == rozklad.length - 1) || (n == 0))
                describe = true;

            if ((rozklad[n] > 0) && (describe)) {
                int loB = (n - 1) * spread / bags + minInf;
                int hiB = n * spread / bags + minInf;
                String s1 = loB + "< i <=" + hiB;
                String s2 = "N=" + rozklad[n];
                g.setColor(Color.MAGENTA);
                g.drawString(s1, startX + offset2, startY - 10 - offset3);
                g.drawString(s2, startX + offset2, startY - offset3);
                int s1len = fm.stringWidth(s1);
                int s2len = fm.stringWidth(s2);
                int slen = offset2 + ((s1len > s2len) ? s1len : s2len);
                lastX = startX + slen;
                lastY = startY;
            }
            previousX = startX;
            previousY = startY;
        } // for
        //legenda
        String s1 = "i - iloœæ infekcji w wierzcho³ku (poziomo)";
        String s2 = "N - iloœæ wierzcho³ków (pionowo)";
        g.setColor(Color.BLACK);
        g.drawString(s2, 0, offset1 + usedHeight + fm.getHeight());
        g.drawString(s1, getWidth() - fm.stringWidth(s1), offset1 + usedHeight
                + 2 * fm.getHeight());
    }

    public void paint(Graphics g) {
        super.paint(g);
        rysujRozklad(g);
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