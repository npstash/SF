package graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

/**
 * @author Nestor
 * 
 * nie wspiera wielokrotnych krawedzi; zbior krawedzi od danego wierzcholka.
 */
public class VertexNeighbourhoodList implements EdgeContainer {

    private Set edges = Collections.synchronizedSet(new HashSet());

    private DefaultVertexContainer ends = new DefaultVertexContainer(
            Collections.synchronizedList(new LinkedList()));

    private Vertex vertex = null;

    public VertexNeighbourhoodList(Vertex v) {
        super();
        vertex = v;
    }

    public void addEdge(Edge e) {
        if (edges.contains(e)) {
            return;
        }
        if (containsEdgeFromTo(e.getStart(), e.getEnd())) {
            return;
        }
        Vertex vx = null;
        if (vertex == e.getStart()) {
            vx = e.getEnd();
        } else if (vertex == e.getEnd()) {
            vx = e.getStart();
        }
        if (vx == null) {
            return;
        }
        if (!ends.containsVertex(vx)) {
            ends.addVertex(vx);
            edges.add(e);
        }
    }

    public void clearEdges() {
        ends.clearVertices();
        edges.clear();
    }

    public boolean containsEdge(Edge e) {
        return edges.contains(e);
    }

    public boolean containsEdgeFromTo(Vertex v1, Vertex v2) {
        if (v1 == vertex) {
            return ends.containsVertex(v2);
        } else if (v2 == vertex) {
            return ends.containsVertex(v1);
        }
        return false;
    }

    public Collection getEdgeCollection() {
        return edges;
    }

    public int getEdgeCount() {
        return edges.size();
    }

    public Iterator getEdgeIterator() {
        return edges.iterator();
    }

    public Collection getEdgesEndingAt(Vertex v) {
        Vector res = new Vector();
        for (Iterator iter = edges.iterator(); iter.hasNext();) {
            Edge element = (Edge) iter.next();
            if (element.getEnd() == v) {
                res.add(element);
            }
        }
        return res;
    }

    public Collection getEdgesIncidentWith(Vertex v) {
        if (v == vertex) {
            return getEdgesStartingAt(vertex);
        }
        return getEdgesEndingAt(v);
    }

    public Collection getEdgesStartingAt(Vertex v) {
        Vector res = new Vector();
        if (v == vertex) {
            res.addAll(edges);
        }
        return res;
    }

    public int getVertexConnectivity(Vertex v) {
        if (v == vertex) {
            return edges.size();
        }
        int res = 0;
        for (Iterator iter = ends.getVertexIterator(); iter.hasNext();) {
            Vertex element = (Vertex) iter.next();
            if (element == v) {
                res++;
            }
        }
        return res;
    }

    public Collection getVerticesConnectedTo(Vertex v) {
        Vector res = new Vector();
        if (v == vertex) {
            return ends.getVertexCollection();
        }
        for (Iterator iter = ends.getVertexIterator(); iter.hasNext();) {
            Vertex element = (Vertex) iter.next();
            if (element == v) {
                res.add(this);
            }
        }
        return res;
    }

    public void removeEdge(Edge e) {
        edges.remove(e);
        ends.removeVertex(e.getEnd());
    }

}