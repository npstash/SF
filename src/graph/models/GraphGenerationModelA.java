package graph.models;

import graph.Graph;
import graph.Vertex;

import java.util.Random;

import proc.DefaultStepProcess;
import proc.param.StepProcessParameter;

/**
 * @author Nestor
 */
public class GraphGenerationModelA extends DefaultStepProcess {

    public static final String PARAM_M = "Iloœæ tworzonych po³¹czeñ ka¿dej iteracji";

    public static final String PARAM_M0 = "Iloœæ punktów startowych";

    public static final String PARAM_N = "Wielkoœæ sieci (iloœæ wierzcho³ków)";

    public static final String PARAM_RND = "Ziarno generatora losowego";

    private Graph graph = null;

    private Random rand = new Random();

    public GraphGenerationModelA(Graph graph) {
        this.graph = graph;
        addParameter(new StepProcessParameter(PARAM_M0, new Integer(3),
                new Integer(1), null));
        addParameter(new StepProcessParameter(PARAM_M, new Integer(2),
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

        // tworzy m0 poczatkowych wierzcholkow
        for (int i = 0; i < ((Integer) getParameter(PARAM_M0).getValue())
                .intValue(); i++) {
            graph.createVertex(rand.nextInt(Vertex.MAX_X), rand
                    .nextInt(Vertex.MAX_Y));
        }
    }

    public synchronized void stepProcess() {

        // tylko do okreslonej wielkosci grafu
        if (graph.getVertexCount() < ((Integer) getParameter(PARAM_N)
                .getValue()).intValue()) {

            // utworzyc nowy wierzcholek
            Vertex vx = graph.createVertex(rand.nextInt(Vertex.MAX_X), rand
                    .nextInt(Vertex.MAX_Y));

            Object[] allVertices = graph.getVertexCollection().toArray();
            int m = ((Integer) getParameter(PARAM_M).getValue()).intValue();
            for (int j = 0; j < m; j++) {
                // wybiera losowo wierzcholek do krorego ma dodac
                // krawedz od nowo utworzonego. (nie moga byc juz polaczone)

                Vertex v = null;
                while (true) {
                    int vNum = rand.nextInt(allVertices.length - 1);
                    v = (Vertex) allVertices[vNum];
                    if (v == vx)
                        continue;
                    if (graph.containsEdgeFromTo(vx, v))
                        continue;
                    break;
                }

                // tworzy krawedz
                graph.createEdge(vx, v);
            }
            super.stepProcess();
        } else {
            // wazne aby powiadomic o zakonczeniu procesu, w przeciwnym
            // razie watki run i complete nie zakoncza sie
            stopProcess();
        }
    }

}