package graph;

import java.util.Collection;

public interface Graph extends VertexContainer, EdgeContainer {

    public void clearGraph();

    public Edge createEdge(Vertex v1, Vertex v2);

    public Vertex createVertex(int x, int y);

    public int[] getConnectivityDistribution();

    public int getMaxConnectivity();

    public Vertex getNearestVertexNumByConnectivity(Vertex near, int con,
            Collection except);

    public int getTotalConnectivity();

    public int getVertexConnectivity(Vertex v);

}
