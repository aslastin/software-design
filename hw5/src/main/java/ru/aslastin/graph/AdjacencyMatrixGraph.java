package ru.aslastin.graph;

import ru.aslastin.drawing.DrawingApi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AdjacencyMatrixGraph implements Graph, DrawableGraph {
    private final boolean[][] matrix;

    public AdjacencyMatrixGraph(int verticesCount) {
        matrix = new boolean[verticesCount][verticesCount];
    }

    private void checkFromTo(int from, int to) {
        assert 0 <= from && from < matrix.length : "from < 0 or from >= verticesCount";
        assert 0 <= to && to < matrix.length : "to < 0 or to >= verticesCount";
    }

    @Override
    public boolean hasEdge(int from, int to) {
        checkFromTo(from, to);

        return matrix[from][to];
    }

    @Override
    public void addEdge(int from, int to) {
        checkFromTo(from, to);

        matrix[from][to] = true;
    }

    @Override
    public void removeEdge(int from, int to) {
        checkFromTo(from, to);

        matrix[from][to] = false;
    }

    @Override
    public int getVerticesCount() {
        return matrix.length;
    }

    @Override
    public int getEdgesCount() {
        int edgesCount = 0;
        for (int from = 0; from < matrix.length; ++from) {
            for (int to = 0; to < matrix.length; ++to) {
                if (matrix[from][to]) {
                    ++edgesCount;
                }
            }
        }
        return edgesCount;
    }

    @Override
    public Iterator<Edge> iterator() {
        List<Edge> edges = new ArrayList<>();
        for (int from = 0; from < matrix.length; ++from) {
            for (int to = 0; to < matrix.length; ++to) {
                if (matrix[from][to]) {
                    edges.add(new Edge(from, to));
                }
            }
        }
        return edges.iterator();
    }

    @Override
    public void drawGraph(DrawingApi drawingApi) {
        // TODO
    }
}
