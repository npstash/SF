package graph.models;

import graph.Graph;
import graph.Vertex;

import java.util.Random;

import proc.DefaultStepProcess;
import proc.param.StepProcessParameter;

/**
 * @author Nestor
 */
public class GraphGenerationModelB_NearestVertices extends DefaultStepProcess {

    public static final String PARAM_T = "Czas generacji - iloœæ krawêdzi (musi byæ wiêksze ni¿ N)";

    public static final String PARAM_N = "Wielkoœæ sieci N (iloœæ wierzcho³ków)";

    public static final String PARAM_RND = "Ziarno generatora losowego";

    private Graph graph = null;

    private Random rand = new Random();

    public GraphGenerationModelB_NearestVertices(Graph graph) {
        this.graph = graph;
        addParameter(new StepProcessParameter(PARAM_T, new Integer(200),
                new Integer(1), null));
        addParameter(new StepProcessParameter(PARAM_N, new Integer(100),
                new Integer(1), null));
        addParameter(new StepProcessParameter(PARAM_RND, new Integer(-1), null,
                null));
        getParameter(DefaultStepProcess.PARAM_DELAY).setValue("200");
    }

    public Object getContent() {
        return graph;
    }

    public synchronized void initProcess() {
        super.initProcess();

        int rnd = ((Integer) getParameter(PARAM_RND).getValue()).intValue();
        if (rnd != -1) {
            rand.setSeed(rnd);
        }

        graph.clearGraph();

        // tworzy N wierzcholkow
        for (int i = 0; i < ((Integer) getParameter(PARAM_N).getValue())
                .intValue(); i++) {
            graph.createVertex(rand.nextInt(Vertex.MAX_X), rand
                    .nextInt(Vertex.MAX_Y));
        }
    }

    public synchronized void stepProcess() {

        // tylko do okreslonej wielkosci grafu
        if (graph.getEdgeCount() < ((Integer) getParameter(PARAM_T).getValue())
                .intValue()) {

            // wybieram losowo wierzcholek
            Vertex vx = (Vertex) graph.getVertexCollection().toArray()[rand
                    .nextInt(graph.getVertexCount())];

            // rozklad stopni
            int[] distrib = graph.getConnectivityDistribution();
            int maxConn = graph.getMaxConnectivity();

            // jesli istnieja wierzcholki o stopniu zerowym -
            // maja one pierwszenstwo
            int chosenConnectivity = 0;

            if (distrib[0] == 0) {
                // brak wierzcholkow o stopniu zerowym - budowanie ruletki
                int[] bounds = new int[maxConn + 1];
                bounds[0] = 0; // == distrib[0] * 0;
                for (int b = 1; b < maxConn + 1; b++) {
                    bounds[b] = bounds[b - 1] + distrib[b] * b;
                }

                // losowanie
                int dice;
                dice = rand.nextInt(bounds[maxConn]);

                // odnalezienie stopnia
                for (int s = 0; s < graph.getMaxConnectivity(); s++) {
                    if ((dice >= bounds[s]) && (dice < bounds[s + 1])) {
                        chosenConnectivity = s + 1;
                        break;
                    }
                }
            }

            // wiemy juz, jakie stopnie zostaly wybrane. nalezy znalezc
            // odpowiadajacy wierzcholek
            Vertex nearest = graph.getNearestVertexNumByConnectivity(vx,
                    chosenConnectivity, null);

            if (nearest == null) {
                throw new Error("AAAA! %#@^&%$");
            }

            // tworzenie krawedzi
            graph.createEdge(vx, nearest);

            super.stepProcess();
        } else {
            // wazne aby powiadomic o zakonczeniu procesu, w przeciwnym
            // razie watki run i complete nie zakoncza sie
            stopProcess();
        }
    }

}