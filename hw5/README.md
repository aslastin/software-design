# HW5: Graph Visualizer

## Objective

To gain practical experience in using the bridge structural pattern.

## Description

It is necessary to implement a simple graph visualizer using two different graphical APIs.
You can choose the way to visualize the graph yourself (for example, draw vertices in a circle).
The application must support two graph implementations: on edge lists and an adjacency matrix.

Class drafts:

```java
public abstract class Graph {
    /**
     * Bridge to drawing api
     */
    private DrawingApi drawingApi;

    public Graph(DrawingApi drawingApi) {
        this.drawingApi = drawingApi;
    }

    public abstract void drawGraph();
}

public interface DrawingApi {
    long getDrawingAreaWidth();
    long getDrawingAreaHeight();
    void drawCircle(...);
    void drawLine(...);
}
```

Notes:
- The choice of API and graph implementations should be set via command line arguments when launching the application;
- The drafts of classes can be changed (add new fields/methods, method parameters, etc.);
- As an api for drawing, you can use java.awt and javax ([examples](https://github.com/akirakozov/software-design/tree/master/java/graphics));
- You can use any language and any api for drawing (the main requirement is that they are fundamentally different).
