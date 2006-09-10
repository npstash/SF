package proj.gis.proc;

import graph.Graph;
import graph.GraphWithNeighbourhoodLists;
import graph.infect.InfectibleEdge;
import graph.infect.InfectibleVertex;
import graph.infect.InfectionSimulation_SIS;
import graph.infect.ui.CenterClassificator;
import graph.infect.ui.RozkladIlosciInfekcji;
import graph.infect.ui.WykresIlosciInfekcji;
import graph.models.GraphGenerationModelA;
import graph.ui.RozkladStopniWierzcholkow;

import java.util.Collection;
import java.util.Vector;

import proc.StepProcess;
import proc.StepProcessChain;
import proc.StepProcessInitializator;
import proc.content.ui.PaintablePanel;
import proc.param.StepProcessParameter;

/**
 * @author Nestor
 */
public class GraphModelA implements StepProcessInitializator {

    public static class KlasyfikatorDlaModeluA extends CenterClassificator {

        public static final String PARAM_M = "Parametr M";

        public KlasyfikatorDlaModeluA() {
            super();
            StepProcessParameter param2 = new StepProcessParameter(PARAM_M,
                    new Integer(2), new Integer(1), null);
            addParameter(param2);
            param2.addContentListener(this);
        }

        protected double computeExpectedCenterPercent(int graphSize) {
            double suma = 0;
            int min = ((Integer) getParameter(PARAM_MINK).getValue())
                    .intValue();
            int m = ((Integer) getParameter(PARAM_M).getValue()).intValue();

            // dla modelu A: P(k) = e/m * exp(-k / m)
            for (int i = min; i < graphSize; i++) {
                double p1 = StrictMath.E / ((double) m);
                double p2 = StrictMath.exp(((double) -i) / ((double) m));
                suma += p1 * p2;
            }
            return suma;
        }
    }

    public GraphModelA() {
        super();
    }

    public String getName() {
        return "Sieæ z równomiernym tworzeniem po³¹czeñ (model A)";
    }

    public Collection getToolsClasses() {
        Vector t = new Vector();
        t.add(PaintablePanel.class);
        t.add(RozkladStopniWierzcholkow.class);
        t.add(WykresIlosciInfekcji.class);
        t.add(RozkladIlosciInfekcji.class);
        t.add(KlasyfikatorDlaModeluA.class);
        return t;
    }

    public String[] getToolsNames() {
        return new String[] {
                "Podgl¹d grafu",
                "Rozk³ad stopni wierzcho³ków w grafie",
                "Wykres iloœci infekcji w porównaniu do rozk³adu stopni wierzcho³ków",
                "Rozk³ad iloœci infekcji w wierzcho³kach",
                "Klasyfikacja centrów i podgl¹d wyników" };
    }

    public StepProcess newInstance() {
        Vector ps = new Vector();
        Graph graph = new GraphWithNeighbourhoodLists(InfectibleVertex.class,
                InfectibleEdge.class);
        GraphGenerationModelA graphModel = new GraphGenerationModelA(graph);
        ps.add(graphModel);
        ps.add(new InfectionSimulation_SIS(graph));
        StepProcessChain sf = new StepProcessChain(ps);
        return sf;
    }
}