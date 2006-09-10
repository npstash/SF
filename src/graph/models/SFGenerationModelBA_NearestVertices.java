package graph.models;

import graph.Graph;
import graph.Vertex;

import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import proc.DefaultStepProcess;
import proc.param.StepProcessParameter;

/**
 * @author Nestor
 */
public class SFGenerationModelBA_NearestVertices extends DefaultStepProcess {

    public static final String PARAM_M = "Iloœæ tworzonych po³¹czeñ ka¿dej iteracji";

    public static final String PARAM_M0 = "Iloœæ punktów startowych";

    public static final String PARAM_N = "Wielkoœæ sieci (iloœæ wierzcho³ków)";

    public static final String PARAM_RND = "Ziarno generatora losowego";

    private Graph graph = null;

    private Random rand = new Random();

    public SFGenerationModelBA_NearestVertices(Graph graph) {
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

            // tworze liste stopni wierzcholkow, do ktorych maja byc
            // dodane krawedzie
            int m = ((Integer) getParameter(PARAM_M).getValue()).intValue();
            int[] chosenGrades = new int[m];

            // rozklad stopni
            int[] distrib = graph.getConnectivityDistribution();
            int maxConn = graph.getMaxConnectivity();

            for (int j = 0; j < m; j++) {

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

                // stopien wirzcholka, do ktorego ma isc krawedz,
                // juz odnaleziono. odejmuje go od rozkladu aby
                // uniknac powtorzen
                distrib[chosenConnectivity]--;
                chosenGrades[j] = chosenConnectivity;
            }

            // tworzenie nowego wierzcholka
            Vertex vx = graph.createVertex(rand.nextInt(Vertex.MAX_X), rand
                    .nextInt(Vertex.MAX_Y));

            // wiemy juz, jakie stopnie zostaly wybrane. nalezy znalezc
            // odpowiadajace wierzcholki
            Vector vertices = new Vector();
            for (int j = 0; j < m; j++) {
                Vertex nearest = graph.getNearestVertexNumByConnectivity(vx,
                        chosenGrades[j], vertices);

                if (nearest != null) {
                    vertices.add(nearest);
                } else {
                    throw new Error("AAAA! %#@^&%$");
                }
            }

            // tworzenie krawedzi
            for (Iterator iter = vertices.iterator(); iter.hasNext();) {
                Vertex v = (Vertex) iter.next();
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