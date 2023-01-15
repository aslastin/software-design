package ru.aslastin.graph;

public interface Graph extends Iterable<Edge> {
    void addEdge(int from, int to);
    void removeEdge(int from, int to);
    int getVerticesCount();
    int getEdgesCount();
}
