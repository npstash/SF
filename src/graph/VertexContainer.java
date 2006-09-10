package graph;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Nestor
 */
public interface VertexContainer {

    public void addVertex(Vertex v);

    public void clearVertices();

    public boolean containsVertex(Vertex v);

    public Collection getVertexCollection();

    public int getVertexCount();

    public Iterator getVertexIterator();

    public void removeVertex(Vertex v);
}
