package proj.gis.proc;

import graph.Graph;
import graph.GraphWithNeighbourhoodLists;
import graph.infect.InfectibleEdge;
import graph.infect.InfectibleVertex;
import graph.infect.InfectionSimulation_SIS;
import graph.infect.ui.CenterClassificator;
import graph.infect.ui.RozkladIlosciInfekcji;
import graph.infect.ui.WykresIlosciInfekcji;
import graph.models.SFGenerationModelBA_NearestVertices;
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
public class SFModelBA implements StepProcessInitializator {

    public static class KlasyfikatorDlaModeluBA extends CenterClassificator {

        public static final String PARAM_M = "Parametr M";

        public KlasyfikatorDlaModeluBA() {
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

            // dla tego modelu P(k) = 2 * m^2 * k^-3 * t / (t + m0)
            // skoro nie moge znac m0, przyjmuje ze m0 = m
            // (poniewaz m0 >= m)
            // oraz t = N - m0
            double m0 = m;
            double t = graphSize - m0;

            for (int i = min; i < graphSize; i++) {
                double di3 = StrictMath.pow((double) i, -3);
                double dm = (double) m;
                suma += 2 * dm * dm * di3 * t / (t + m0);
            }
            return suma;
        }
    }

    public SFModelBA() {
        super();
    }

    public String getName() {
        return "Sieæ bezskalowa, model BA";
    }

    public Collection getToolsClasses() {
        Vector t = new Vector();
        t.add(PaintablePanel.class);
        t.add(RozkladStopniWierzcholkow.class);
        t.add(WykresIlosciInfekcji.class);
        t.add(RozkladIlosciInfekcji.class);
        t.add(KlasyfikatorDlaModeluBA.class);
        return t;
    }

    public String[] getToolsNames() {
        return new String[] {
                "Podgl¹d sieci bezskalowej",
                "Rozk³ad stopni wierzcho³ków w sieci",
                "Wykres iloœci infekcji w porównaniu do rozk³adu stopni wierzcho³ków",
                "Rozk³ad iloœci infekcji w wierzcho³kach",
                "Klasyfikacja centrów i podgl¹d wyników" };
    }

    public StepProcess newInstance() {
        Vector ps = new Vector();
        Graph graph = new GraphWithNeighbourhoodLists(InfectibleVertex.class,
                InfectibleEdge.class);
        SFGenerationModelBA_NearestVertices graphModel = new SFGenerationModelBA_NearestVertices(
                graph);
        ps.add(graphModel);
        ps.add(new InfectionSimulation_SIS(graph));
        StepProcessChain sf = new StepProcessChain(ps);
        return sf;
    }

}