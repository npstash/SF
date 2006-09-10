package proj.gis.proc;

import graph.Graph;
import graph.GraphWithNeighbourhoodLists;
import graph.infect.InfectibleEdge;
import graph.infect.InfectibleVertex;
import graph.infect.InfectionSimulation_SIS;
import graph.infect.ui.CenterClassificator;
import graph.infect.ui.RozkladIlosciInfekcji;
import graph.infect.ui.WykresIlosciInfekcji;
import graph.models.GraphGenerationModelER;
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
public class GraphModelER implements StepProcessInitializator {

    public static class KlasyfikatorDlaModeluER extends
            CenterClassificator {

        public static final String PARAM_P = "Prawdopodobieñstwo istnienia ³¹cza miêdzy wêz³ami";

        public KlasyfikatorDlaModeluER() {
            super();
            StepProcessParameter param2 = new StepProcessParameter(PARAM_P,
                    new Integer(5), new Integer(0), new Integer(100));
            addParameter(param2);
            param2.addContentListener(this);
        }

        protected double computeExpectedCenterPercent(int graphSize) {
            double suma = 0;
            int min = ((Integer) getParameter(PARAM_MINK).getValue())
                    .intValue();
            int p = ((Integer) getParameter(PARAM_P).getValue()).intValue();
            double dp = ((double) p) / 100;

            // z rozkladu Bernoulliego: P(k) = (k po n) * p^k * (1-p)^(n-k)
            for (int i = min; i < graphSize; i++) {
                double el = pK(i, graphSize, dp);
                suma += el;
            }
            return suma;
        }

        private double pK(int k, int n, double p) {
            // n! / ( k! * (n-k)! )
            // = ( (k+1) * ... * n ) / ( 1 * ... * (n-k) )
            //   ^--- jest n-k ^--- jest n-k
            if (n == k) {
                return StrictMath.pow(p, (double) n);
            }

            double iloczyn = 1;
            for (int i = 1; i <= n - k; i++) {
                double czynnik = ((double) (i + k)) / ((double) i) * (1 - p);
                if (i <= k)
                    czynnik *= p;
                iloczyn *= czynnik;
            }
            for (int i = n - k; i <= k; i++) {
                iloczyn *= p;
            }
            return iloczyn;
        }
    }

    public GraphModelER() {
        super();
    }

    public String getName() {
        return "Graf losowy (model ER)";
    }

    public Collection getToolsClasses() {
        Vector t = new Vector();
        t.add(PaintablePanel.class);
        t.add(RozkladStopniWierzcholkow.class);
        t.add(WykresIlosciInfekcji.class);
        t.add(RozkladIlosciInfekcji.class);
        t.add(KlasyfikatorDlaModeluER.class);
        return t;
    }

    public String[] getToolsNames() {
        return new String[] {
                "Podgl¹d grafu",
                "Rozk³ad stopni wierzcho³ków w grafie",
                "Wykres iloœci infekcji w porównaniu do rozk³adu stopni wierzcho³ków",
                "Rozk³ad iloœci infekcji w wierzcho³kach",
                "Klasyfikacja \"centrów\" i podgl¹d wyników" };
    }

    public StepProcess newInstance() {
        Vector ps = new Vector();
        Graph graph = new GraphWithNeighbourhoodLists(InfectibleVertex.class,
                InfectibleEdge.class);
        GraphGenerationModelER graphModel = new GraphGenerationModelER(graph);
        ps.add(graphModel);
        ps.add(new InfectionSimulation_SIS(graph));
        StepProcessChain sf = new StepProcessChain(ps);
        return sf;
    }

}