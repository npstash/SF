package graph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Vector;

import proc.content.Paintable;

public class GraphWithNeighbourhoodLists extends DefaultVertexContainer
        implements Paintable, Graph {

    private Class eClass = null;

    private Set edges = Collections.synchronizedSet(new HashSet());

    private LinkedHashMap links = new LinkedHashMap();

    private int maxConnectivity = 0;

    private Class vClass = null;

    public GraphWithNeighbourhoodLists(Class vClass, Class eClass) {
        super(Collections.synchronizedList(new Vector()));
        this.vClass = vClass;
        this.eClass = eClass;
    }

    public final void addEdge(Edge e) {
        throw new Error("use createEdge");
    }

    public final void addVertex(Vertex v) {
        throw new Error("use createVertex");
    }

    public synchronized void clearEdges() {
        for (Iterator iter = super.getVertexIterator(); iter.hasNext();) {
            Vertex v = (Vertex) iter.next();
            VertexNeighbourhoodList vn = (VertexNeighbourhoodList) links.get(v);
            vn.clearEdges();
        }
        maxConnectivity = 0;
        edges.clear();
    }

    public synchronized void clearGraph() {
        clearEdges();
        clearVertices();
        links.clear();
    }

    public synchronized boolean containsEdge(Edge e) {
        return edges.contains(e);
    }

    public boolean containsEdgeFromTo(Vertex v1, Vertex v2) {
        if (super.containsVertex(v1) && super.containsVertex(v2)) {
            VertexNeighbourhoodList vn = (VertexNeighbourhoodList) links
                    .get(v1);
            return vn.containsEdgeFromTo(v1, v2);
        }
        return false;
    }

    public synchronized final Edge createEdge(Vertex v1, Vertex v2) {
        Edge e = null;
        try {
            if (containsVertex(v1) && containsVertex(v2)) {
                VertexNeighbourhoodList n1 = (VertexNeighbourhoodList) links
                        .get(v1);
                VertexNeighbourhoodList n2 = (VertexNeighbourhoodList) links
                        .get(v2);
                Constructor c = eClass.getConstructor(new Class[] {
                        Vertex.class, Vertex.class });
                e = (Edge) c.newInstance(new Object[] { v1, v2 });
                edges.add(e);
                n1.addEdge(e);
                n2.addEdge(e);
                int con1 = n1.getVertexConnectivity(v1);
                int con2 = n2.getVertexConnectivity(v2);
                maxConnectivity = (con1 > maxConnectivity) ? con1
                        : maxConnectivity;
                maxConnectivity = (con2 > maxConnectivity) ? con2
                        : maxConnectivity;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return e;
    }

    public synchronized final Vertex createVertex(int x, int y) {
        Vertex v = null;
        try {
            Constructor c = vClass.getConstructor(new Class[] { Integer.TYPE,
                    Integer.TYPE });
            v = (Vertex) c.newInstance(new Object[] { new Integer(x),
                    new Integer(y) });
            VertexNeighbourhoodList vnb = new VertexNeighbourhoodList(v);
            super.addVertex(v);
            links.put(v, vnb);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    public synchronized void doPaint(Graphics g, Rectangle delims) {
        for (Iterator iter = edges.iterator(); iter.hasNext();) {
            Edge e = (Edge) iter.next();
            e.doPaint(g, delims);
        }

        for (Iterator iter = super.getVertexIterator(); iter.hasNext();) {
            Vertex v = (Vertex) iter.next();
            v.doPaint(g, delims);
        }

        g.setColor(Color.BLUE);
        String s1 = "Vertices: " + super.getVertexCount();
        String s2 = "Edges: " + getEdgeCount();
        g.drawString(s1, delims.x, delims.y + 10);
        g.drawString(s2, delims.x, delims.y + 20);

    }

    public synchronized int[] getConnectivityDistribution() {
        int[] res = new int[maxConnectivity + 1];
        for (Iterator iter = super.getVertexIterator(); iter.hasNext();) {
            Vertex v = (Vertex) iter.next();
            VertexNeighbourhoodList vn = (VertexNeighbourhoodList) links.get(v);
            res[vn.getVertexConnectivity(v)]++;
        }
        return res;
    }

    public synchronized Collection getEdgeCollection() {
        return edges;
    }

    public synchronized int getEdgeCount() {
        return edges.size();
    }

    public synchronized Iterator getEdgeIterator() {
        return edges.iterator();
    }

    public synchronized Collection getEdgesEndingAt(Vertex v) {
        return getEdgesStartingAt(v);
    }

    public synchronized Collection getEdgesIncidentWith(Vertex v) {
        return getEdgesStartingAt(v);
    }

    public synchronized Collection getEdgesStartingAt(Vertex v) {
        if (!super.containsVertex(v)) {
            return new Vector();
        } else {
            VertexNeighbourhoodList vn = (VertexNeighbourhoodList) links.get(v);
            return vn.getEdgeCollection();
        }
    }

    public synchronized int getMaxConnectivity() {
        return maxConnectivity;
    }

    public synchronized Vertex getNearestVertexNumByConnectivity(Vertex near,
            int con, Collection except) {
        if (!super.containsVertex(near)) {
            return null;
        }
        Vertex res = null;
        long minDist = -1;
        for (Iterator iter = super.getVertexIterator(); iter.hasNext();) {
            Vertex v = (Vertex) iter.next();
            VertexNeighbourhoodList vn = (VertexNeighbourhoodList) links.get(v);
            if ((vn.getVertexConnectivity(v) == con)) {
                if (v != near) {
                    boolean excepted = false;
                    if (except != null) {
                        if (except.contains(v))
                            excepted = true;
                    }
                    if (!excepted) {
                        long currDist = v.computeDistanceTo(near);
                        if ((minDist == -1) || (currDist < minDist)) {
                            minDist = currDist;
                            res = v;
                        }
                    } // not contains
                } // != near
            } // connectivity
        }
        return res;
    }

    public synchronized int getTotalConnectivity() {
        return edges.size() * 2;
    }

    public synchronized int getVertexConnectivity(Vertex v) {
        if (!super.containsVertex(v)) {
            return 0;
        } else {
            VertexNeighbourhoodList vn = (VertexNeighbourhoodList) links.get(v);
            return vn.getVertexConnectivity(v);
        }
    }

    public synchronized Collection getVerticesConnectedTo(Vertex v) {
        if (!super.containsVertex(v)) {
            return new Vector();
        } else {
            VertexNeighbourhoodList vn = (VertexNeighbourhoodList) links.get(v);
            return vn.getVerticesConnectedTo(v);
        }
    }

    public synchronized void removeEdge(Edge e) {
        edges.remove(e);
    }
}