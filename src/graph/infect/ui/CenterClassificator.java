package graph.infect.ui;

import graph.Graph;
import graph.Vertex;
import graph.infect.InfectibleVertex;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import proc.content.ContentEvent;
import proc.content.ContentListener;
import proc.param.DefaultParameterContainer;
import proc.param.ParameterContainer;
import proc.param.StepProcessParameter;

/**
 * @author Nestor
 */
public abstract class CenterClassificator extends JPanel implements
        ParameterContainer, ContentListener {

    public static final String PARAM_MINK = "Minimalny stopieñ centrum";

    private static double computeTrueCenterPrecent(int[] rozklad, int min) {
        int suma = 0;
        int sumaC = 0;
        for (int i = 0; i < rozklad.length; i++) {
            suma += rozklad[i];
            if (i >= min) {
                sumaC += rozklad[i];
            }
        }
        if (suma == 0) {
            return 0;
        }
        return (double) sumaC / (double) suma;
    }

    private boolean bClassificationDone = false;

    private double dCentersCorrectlyClassifiedPercent = 0;

    private double dClassificatorAccurancy = 0;

    private double dCorrectlyClassifiedPercent = 0;

    private double dExpectedCenterPercent = 0;

    private double dTrueCenterPercent = 0;

    private Graph graph = null;

    private int iClassifiedAsCenters = 0;

    private int iCorrectlyClassified = 0;

    private JLabel jlCentersCorrectlyClassifiedPercent = new JLabel();

    private JLabel jlClassificatorAccurancy = new JLabel();

    private JLabel jlClassifiedAsCenters = new JLabel();

    private JLabel jlCorrectlyClassified = new JLabel();

    private JLabel jlExpectedCenterPercent = new JLabel();

    private JLabel jlTrueCenterPrecent = new JLabel();

    private JPanel jpVertices = null;

    private int minInfectionsToClassifyAsCenter = 0;

    private ParameterContainer parameters = new DefaultParameterContainer();

    public CenterClassificator() {
        super();
        StepProcessParameter param1 = new StepProcessParameter(PARAM_MINK,
                new Integer(12), new Integer(1), null);
        addParameter(param1);
        param1.addContentListener(this);
        JPanel infos = new JPanel();
        infos.setLayout(new GridLayout(6, 1));
        infos.add(jlTrueCenterPrecent);
        infos.add(jlExpectedCenterPercent);
        infos.add(jlClassifiedAsCenters);
        infos.add(jlCorrectlyClassified);
        infos.add(jlCentersCorrectlyClassifiedPercent);
        infos.add(jlClassificatorAccurancy);
        setLayout(new BorderLayout());
        add(infos, BorderLayout.NORTH);
        jpVertices = new JPanel() {
            public void paint(Graphics g) {
                super.paint(g);
                paintPanel(g, getBounds());
            }
        };
        jpVertices.setBorder(BorderFactory.createEtchedBorder());
        add(jpVertices, BorderLayout.CENTER);
    }

    public void addParameter(StepProcessParameter param) {
        parameters.addParameter(param);
    }

    protected abstract double computeExpectedCenterPercent(int graphSize);

    public void contentsChanged(ContentEvent e) {
        if (e.getContents() instanceof Graph) {
            graph = (Graph) e.getContents();
        }
        doComputations();
        refreshStats();
        jpVertices.repaint();
    }

    private void doComputations() {
        bClassificationDone = false;
        if (graph != null) {
            int[] distr = graph.getConnectivityDistribution();
            int min = ((Integer) getParameter(PARAM_MINK).getValue())
                    .intValue();
            double f = computeTrueCenterPrecent(distr, min);
            double t = computeExpectedCenterPercent(graph.getVertexCount());
            dTrueCenterPercent = StrictMath.rint(f * 10000) / 100;
            dExpectedCenterPercent = StrictMath.rint(t * 10000) / 100;

            if (graph.getVertexCount() == 0) {
                return;
            }
            Vector vertices = new Vector(graph.getVertexCount());
            vertices.addAll(graph.getVertexCollection());
            Collections.sort(vertices);
            int split = vertices.size()
                    - (int) StrictMath.ceil((dExpectedCenterPercent / 100)
                            * (double) vertices.size());
            if ((split >= vertices.size()) || (split < 0)) {
                return;
            }
            minInfectionsToClassifyAsCenter = ((Vertex) vertices.get(split))
                    .getValue();
            if (minInfectionsToClassifyAsCenter == 0) {
                return;
            }
            bClassificationDone = true;
            iClassifiedAsCenters = 0;
            iCorrectlyClassified = 0;
            int iCentersNotClassified = 0;
            for (Iterator iter = vertices.iterator(); iter.hasNext();) {
                InfectibleVertex v = (InfectibleVertex) iter.next();
                int stopien = graph.getVertexConnectivity(v);
                if (stopien >= min) {
                    if (v.getValue() < minInfectionsToClassifyAsCenter) {
                        iCentersNotClassified++;
                    }
                }
                if (v.getValue() >= minInfectionsToClassifyAsCenter) {
                    iClassifiedAsCenters++;
                    if (stopien >= min) {
                        iCorrectlyClassified++;
                    }
                }
            }//for
            dCorrectlyClassifiedPercent = (double) iCorrectlyClassified
                    / (double) iClassifiedAsCenters;
            dCentersCorrectlyClassifiedPercent = (double) iCorrectlyClassified
                    / (double) (iClassifiedAsCenters + iCentersNotClassified);
            dClassificatorAccurancy = (double) (iCorrectlyClassified
                    + vertices.size() - iClassifiedAsCenters - iCentersNotClassified)
                    / (double) (vertices.size());

            dCorrectlyClassifiedPercent = StrictMath
                    .rint(dCorrectlyClassifiedPercent * 10000) / 100;
            dCentersCorrectlyClassifiedPercent = StrictMath
                    .rint(dCentersCorrectlyClassifiedPercent * 10000) / 100;
            dClassificatorAccurancy = StrictMath
                    .rint(dClassificatorAccurancy * 10000) / 100;
        } // if graph
    }

    public StepProcessParameter getParameter(String name) {
        return parameters.getParameter(name);
    }

    public int getParameterCount() {
        return parameters.getParameterCount();
    }

    public Collection getParameters() {
        return parameters.getParameters();
    }

    public void paintPanel(Graphics g, Rectangle delims) {
        if (!bClassificationDone) {
            return;
        }
        if (graph != null) {
            int vSize = 8;
            int min = ((Integer) getParameter(PARAM_MINK).getValue())
                    .intValue();

            for (Iterator iter = graph.getVertexIterator(); iter.hasNext();) {
                InfectibleVertex v = (InfectibleVertex) iter.next();
                int x = v.getX() * delims.width / Vertex.MAX_X;
                int y = v.getY() * delims.height / Vertex.MAX_Y;

                boolean cF = (graph.getVertexConnectivity(v) >= min);
                boolean cT = (v.getValue() >= minInfectionsToClassifyAsCenter);
                if (cT) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(Color.GRAY);
                }
                g.fillOval(x - vSize / 2, y - vSize / 2, vSize, vSize);

                if (cF && !cT) {
                    g.setColor(Color.RED);
                    g.drawOval(x - vSize / 2, y - vSize / 2, vSize, vSize);
                }
                if (cT && !cF) {
                    g.setColor(Color.RED);
                    g.drawLine(x - vSize / 2, y - vSize / 2, x + vSize / 2, y
                            + vSize / 2);
                    g.drawLine(x + vSize / 2, y - vSize / 2, x - vSize / 2, y
                            + vSize / 2);
                }
            } // for
        } // if
    }

    private void refreshStats() {
        int min = ((Integer) getParameter(PARAM_MINK).getValue()).intValue();
        jlTrueCenterPrecent.setText("Faktyczny procent wêz³ów o stopinu >= "
                + min + " = " + dTrueCenterPercent + "%");
        jlExpectedCenterPercent
                .setText("Oczekiwany procent wêz³ów o stopinu >= " + min
                        + " = " + dExpectedCenterPercent + "%");
        if (bClassificationDone) {
            jlClassifiedAsCenters
                    .setText("Iloœæ wêz³ów zaklasyfikowanych jako centra: "
                            + iClassifiedAsCenters);
            jlCorrectlyClassified.setText("W tym poprawnie "
                    + dCorrectlyClassifiedPercent + "%");
            jlCentersCorrectlyClassifiedPercent
                    .setText("Procent poprawnie zaklasyfikowanych centrów: "
                            + dCentersCorrectlyClassifiedPercent + "%");
            jlClassificatorAccurancy.setText("Ca³kowita celnoœæ klasyfikacji "
                    + dClassificatorAccurancy + "%");
        } else {
            jlClassifiedAsCenters.setText("-");
            jlCorrectlyClassified.setText("(Klasyfikacja niedostêpna)");
            jlCentersCorrectlyClassifiedPercent.setText("-");
            jlClassificatorAccurancy.setText("-");
        }
    }
}