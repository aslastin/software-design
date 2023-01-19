package ru.aslastin.graph;

import ru.aslastin.drawing.DrawingApi;

import java.util.*;

public class EdgesListGraph implements Graph, DrawableGraph {
    private final Set<Edge> edges;

    public EdgesListGraph() {
        this.edges = new HashSet<>();
    }

    @Override
    public boolean hasEdge(int from, int to) {
        return edges.contains(new Edge(from, to));
    }

    @Override
    public void addEdge(int from, int to) {
        edges.add(new Edge(from, to));
    }

    @Override
    public void removeEdge(int from, int to) {
        edges.remove(new Edge(from, to));
    }

    @Override
    public int getVerticesCount() {
        Set<Integer> vertices = new HashSet<>();
        for (var edge : edges) {
            vertices.add(edge.getFrom());
            vertices.add(edge.getTo());
        }
        return vertices.size();
    }

    @Override
    public int getEdgesCount() {
        return edges.size();
    }

    @Override
    public Iterator<Edge> iterator() {
        return edges.iterator();
    }

    @Override
    public void drawGraph(DrawingApi drawingApi) {
        // TODO
    }

}
