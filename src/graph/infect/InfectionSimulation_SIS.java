package graph.infect;

import graph.Graph;
import graph.Vertex;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import proc.DefaultStepProcess;
import proc.param.StepProcessParameter;
import util.HashRegistry;

/**
 * @author Nestor
 */
public class InfectionSimulation_SIS extends DefaultStepProcess {

    public static final String PARAM_PCURE = "Prawdopodobienstwo wyleczenia; skala od 0 do 100";

    public static final String PARAM_PINFECT = "Prawdopodobienstwo zainfekowania; skala od 0 do 100";

    public static final String PARAM_RND = "Ziarno generatora losowego";

    public static final String PARAM_INFECTIONCOUNTTYPE = "Rodzaj zliczania zara¿eñ: 0 - zlicza siê ile cykli wêze³ by³ zara¿ony; 1 - zlicza siê ile by³ zara¿any (mo¿e byæ zara¿any wielokrotnie w ci¹gu iteracji, z ró¿nych Ÿróde³)";

    private Graph g = null;

    // true jesli pokazane maja byc krawedzie
    // false jesli weirzcholki
    private boolean infectPhase;

    private int probBase = 100;

    private boolean infectionDead = true;

    private Random rand = new Random();

    public InfectionSimulation_SIS(Graph g) {
        super();
        this.g = g;
        addParameter(new StepProcessParameter(PARAM_PINFECT, new Integer(55),
                new Integer(0), new Integer(100)));
        addParameter(new StepProcessParameter(PARAM_PCURE, new Integer(100),
                new Integer(0), new Integer(100)));
        addParameter(new StepProcessParameter(PARAM_RND, new Integer(-1), null,
                null));
        addParameter(new StepProcessParameter(PARAM_INFECTIONCOUNTTYPE,
                new Integer(0), new Integer(0), new Integer(1)));
        getParameter(DefaultStepProcess.PARAM_DELAY).setValue("500");
    }

    public Graph getGraph() {
        return g;
    }

    public Object getContent() {
        return g;
    }

    private void infectAVertex(InfectibleVertex v) {
        v.setInfected(true);
        v.setValue(v.getValue() + 1);
        Integer iMax = (Integer) HashRegistry.getObject(this);
        int currentMax = (iMax == null) ? 0 : iMax.intValue();
        if (v.getValue() > currentMax) {
            HashRegistry.addKey(this, new Integer(v.getValue()));
        }
        infectionDead = false;
    }

    public synchronized void initProcess() {
        int rnd = ((Integer) getParameter(PARAM_RND).getValue()).intValue();
        if (rnd != -1) {
            rand.setSeed(rnd);
        }
        infectPhase = false;
        HashRegistry.addKey(this, new Integer(1));

        // czyszczenie wierzcholkow i krawedzi
        for (Iterator iter = g.getVertexIterator(); iter.hasNext();) {
            InfectibleVertex v = (InfectibleVertex) iter.next();
            v.setValue(0);
            v.setInfected(false);
            v.setMaxValueKey(this);
        }
        for (Iterator iter = g.getEdgeIterator(); iter.hasNext();) {
            InfectibleEdge e = (InfectibleEdge) iter.next();
            e.setInfected(false);
            e.setInfectDirection(null);
        }

        // wybierz losowo wierzcholek do zainfekowania
        // TODO - ma byc niskiego stopnia

        Object[] vxs = g.getVertexCollection().toArray();

        int wybrany = rand.nextInt(g.getVertexCount());
        InfectibleVertex v = (InfectibleVertex) vxs[wybrany];
        infectAVertex(v);
        super.initProcess();
    }

    public synchronized void stepProcess() {
        int probOfClean = ((Integer) getParameter(PARAM_PCURE).getValue())
                .intValue();
        int probOfInfect = ((Integer) getParameter(PARAM_PINFECT).getValue())
                .intValue();
        if (infectPhase) {
            // dla kazdego zainfekowanego wierzcholka - proba wyleczenia
            for (Iterator iter = g.getVertexIterator(); iter.hasNext();) {
                InfectibleVertex v = (InfectibleVertex) iter.next();
                if (v.isInfected()) {
                    if (rand.nextInt(probBase) < probOfClean) {
                        // leczenie wierzcholka
                        v.setInfected(false);
                    }
                }
            }

            // ta falga zostanie odpowiednio ustawiona jesli zaraza nie
            // wygasla
            infectionDead = true;

            // przenoszenie infekcji
            for (Iterator iter = g.getEdgeIterator(); iter.hasNext();) {
                InfectibleEdge e = (InfectibleEdge) iter.next();
                if (!e.isInfected()) {
                    continue;
                }
                InfectibleVertex toInfect = (InfectibleVertex) e
                        .getInfectDirection();
                int infectionCountType = ((Integer) getParameter(
                        PARAM_INFECTIONCOUNTTYPE).getValue()).intValue();
                if (infectionCountType == 0) {
                    if (toInfect.isInfected())
                        continue;
                }
                infectAVertex(toInfect);
            }
        } else {
            // czyszczenie znacznika infected z krawedzi
            for (Iterator iter = g.getEdgeIterator(); iter.hasNext();) {
                InfectibleEdge e = (InfectibleEdge) iter.next();
                e.setInfected(false);
                e.setInfectDirection(null);
            }

            // dla kazdego zainfekowanego wierzcholka - zarazanie krawedzi
            for (Iterator iter = g.getVertexIterator(); iter.hasNext();) {
                InfectibleVertex v = (InfectibleVertex) iter.next();
                if (!v.isInfected()) {
                    continue;
                }

                // pobranie listy krawedzi incydentnych
                Collection es = g.getEdgesIncidentWith(v);
                for (Iterator iter2 = es.iterator(); iter2.hasNext();) {
                    InfectibleEdge e = (InfectibleEdge) iter2.next();
                    if (rand.nextInt(probBase) >= probOfInfect) {
                        // nie zainfekowano
                        continue;
                    }
                    Vertex direction = null;
                    if (e.getStart() == v) {
                        direction = e.getEnd();
                    } else {
                        direction = e.getStart();
                    }
                    // zainfekowanie krawedzi
                    e.setInfected(true);
                    e.setInfectDirection(direction);
                } // for e
            } // for v
        } // else infect

        infectPhase = !infectPhase;
        super.stepProcess();
        if (infectionDead) {
            super.stopProcess();
        }
    }
}