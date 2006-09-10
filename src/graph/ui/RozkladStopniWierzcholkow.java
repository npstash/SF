package graph.ui;

import graph.Graph;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Collection;

import javax.swing.JPanel;

import proc.content.ContentEvent;
import proc.content.ContentListener;
import proc.param.DefaultParameterContainer;
import proc.param.ParameterContainer;
import proc.param.StepProcessParameter;

public class RozkladStopniWierzcholkow extends JPanel implements
        ContentListener, ParameterContainer {

    public static final String PARAM_VIEW = "Sposób wyœwietlania: 0 - linie, 1 - s³upki";

    private Graph graph = null;

    private DefaultParameterContainer params = new DefaultParameterContainer();

    public RozkladStopniWierzcholkow() {
        super();
        addParameter(new StepProcessParameter(PARAM_VIEW, new Integer(0),
                new Integer(0), new Integer(1)));
    }

    public void addParameter(StepProcessParameter param) {
        params.addParameter(param);
    }

    public void contentsChanged(ContentEvent e) {
        Object p = e.getContents();
        if (p instanceof Graph) {
            graph = (Graph) p;
            repaint();
        }
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

    public void paint(Graphics g) {
        super.paint(g);
        rysujRozklad(g);
    }

    private void rysujRozklad(Graphics g) {
        if (graph == null)
            return;
        int[] distribution = graph.getConnectivityDistribution();
        int max = 0;
        for (int i = 0; i < distribution.length; i++) {
            if (distribution[i] > max) {
                max = distribution[i];
            }
        }
        if (max == 0)
            return;

        FontMetrics fm = g.getFontMetrics();
        int offset1 = 30;
        int offset2 = 5;
        int offset3 = 5;
        int usedHeight = getHeight() - offset1 - 2 * fm.getHeight();
        int usedWidth = 9 * getWidth() / 10;

        double pixelsPerUnit = (double) (usedHeight) / (double) max;
        double pixelsPerGrade = (double) (usedWidth)
                / (double) (distribution.length - 1);

        int view = ((Integer) getParameter(PARAM_VIEW).getValue()).intValue();
        int lastX = 0;
        int lastY = 0;
        int previousX = 0;
        int previousY = usedHeight + offset1;
        for (int n = 0; n < distribution.length; n++) {
            int startX = (int) ((double) n * pixelsPerGrade);

            int value = distribution[n];
            int barHeight = (int) ((double) value * pixelsPerUnit);
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

            if ((n == distribution.length - 1) || (n == 0))
                describe = true;

            if ((distribution[n] > 0) && (describe)) {
                String s1 = "D=" + n;
                String s2 = "N=" + distribution[n];
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
        String s1 = "D - stopieñ wierzcho³ka (w poziomie)";
        String s2 = "N - iloœæ wierzcho³ków (w pionie)";
        g.setColor(Color.BLACK);
        g.drawString(s1, 0, usedHeight + offset1 + fm.getHeight());
        g.drawString(s2, getWidth() - fm.stringWidth(s2), usedHeight + offset1
                + 2 * fm.getHeight());
    }
}