package graph;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Nestor
 */
public interface EdgeContainer {

    public void addEdge(Edge e);

    public void clearEdges();

    public boolean containsEdge(Edge e);

    public boolean containsEdgeFromTo(Vertex v1, Vertex v2);

    public Collection getEdgeCollection();

    public int getEdgeCount();

    public Iterator getEdgeIterator();

    public Collection getEdgesEndingAt(Vertex v);

    public Collection getEdgesIncidentWith(Vertex v);

    public Collection getEdgesStartingAt(Vertex v);

    public int getVertexConnectivity(Vertex v);

    public Collection getVerticesConnectedTo(Vertex v);

    public void removeEdge(Edge e);
}

