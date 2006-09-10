package proj.gis.proc;

import graph.Graph;
import graph.GraphWithNeighbourhoodLists;
import graph.infect.InfectibleEdge;
import graph.infect.InfectibleVertex;
import graph.infect.InfectionSimulation_SIS;
import graph.infect.ui.RozkladIlosciInfekcji;
import graph.infect.ui.WykresIlosciInfekcji;
import graph.models.GraphGenerationModelB_NearestVertices;
import graph.ui.RozkladStopniWierzcholkow;

import java.util.Collection;
import java.util.Vector;

import proc.StepProcess;
import proc.StepProcessChain;
import proc.StepProcessInitializator;
import proc.content.ui.PaintablePanel;

/**
 * @author Nestor
 */
public class GraphModelB implements StepProcessInitializator {

    public GraphModelB() {
        super();
    }

    public String getName() {
        return "Sieæ z preferencyjnym tworzeniem ³¹czeñ, o sta³ej wielkoœci (model B)";
    }

    public Collection getToolsClasses() {
        Vector t = new Vector();
        t.add(PaintablePanel.class);
        t.add(RozkladStopniWierzcholkow.class);
        t.add(WykresIlosciInfekcji.class);
        t.add(RozkladIlosciInfekcji.class);
        return t;
    }

    public String[] getToolsNames() {
        return new String[] {
                "Podgl¹d grafu",
                "Rozk³ad stopni wierzcho³ków w grafie",
                "Wykres iloœci infekcji w porównaniu do rozk³adu stopni wierzcho³ków",
                "Rozk³ad iloœci infekcji w wierzcho³kach" };
    }

    public StepProcess newInstance() {
        Vector ps = new Vector();
        Graph graph = new GraphWithNeighbourhoodLists(InfectibleVertex.class,
                InfectibleEdge.class);
        GraphGenerationModelB_NearestVertices graphModel = new GraphGenerationModelB_NearestVertices(
                graph);
        ps.add(graphModel);
        ps.add(new InfectionSimulation_SIS(graph));
        StepProcessChain sf = new StepProcessChain(ps);
        return sf;
    }

}