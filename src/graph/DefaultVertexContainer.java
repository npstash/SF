package graph;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Nestor
 * 
 * zwykla nakladka na liste. preferowana forma kontenera - Vector
 */
public class DefaultVertexContainer implements VertexContainer {

    private Collection vertexList = null;

    public DefaultVertexContainer(Collection vertexList) {
        super();
        if (vertexList == null) {
            throw new Error("null pointer error");
        }
        this.vertexList = vertexList;
    }

    public void addVertex(Vertex v) {
        vertexList.add(v);
    }

    public void clearVertices() {
        vertexList.clear();
    }

    public int getVertexCount() {
        return vertexList.size();
    }

    public Iterator getVertexIterator() {
        return vertexList.iterator();
    }

    public Collection getVertexCollection() {
        return vertexList;
    }

    public boolean containsVertex(Vertex v) {
        return vertexList.contains(v);
    }

    public void removeVertex(Vertex v) {
        vertexList.remove(v);
    }
}