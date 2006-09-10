package graph.models;

import graph.Graph;
import graph.Vertex;

import java.util.Iterator;
import java.util.Random;

import proc.DefaultStepProcess;
import proc.param.StepProcessParameter;

/**
 * @author Nestor
 */
public class GraphGenerationModelER extends DefaultStepProcess {

    public static final String PARAM_N = "Wielkoœæ sieci (iloœæ wierzcho³ków)";

    public static final String PARAM_P = "Prawdopodobieñstwo, ¿e dwa wierzcho³ki bêd¹ po³¹czone (od 0 do 100)";

    public static final String PARAM_RND = "Ziarno generatora losowego";

    private Graph graph = null;

    private Random rand = new Random();

    public GraphGenerationModelER(Graph graph) {
        this.graph = graph;
        addParameter(new StepProcessParameter(PARAM_P, new Integer(5),
                new Integer(0), new Integer(100)));
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
        graph.createVertex(rand.nextInt(Vertex.MAX_X), rand
                .nextInt(Vertex.MAX_Y));
    }

    public synchronized void stepProcess() {
        if (graph.getVertexCount() < ((Integer) getParameter(PARAM_N)
                .getValue()).intValue()) {
            int prob = ((Integer) getParameter(PARAM_P).getValue()).intValue();
            Vertex vx = graph.createVertex(rand.nextInt(Vertex.MAX_X), rand
                    .nextInt(Vertex.MAX_Y));
            for (Iterator iter = graph.getVertexIterator(); iter.hasNext();) {
                Vertex v = (Vertex) iter.next();
                if (rand.nextInt(100) < prob) {
                    graph.createEdge(vx, v);
                }
            }
            super.stepProcess();
        } else {
            stopProcess();
        }
    }

}