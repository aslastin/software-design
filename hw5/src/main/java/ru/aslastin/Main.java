package ru.aslastin;

import ru.aslastin.drawing.GraphAwtDrawingApi;
import ru.aslastin.graph.AdjacencyMatrixGraph;
import ru.aslastin.graph.DrawableGraph;
import ru.aslastin.graph.Edge;
import ru.aslastin.graph.EdgesListGraph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;

public class Main {
    private Main() {
        // just main method
    }

    static void check(boolean condition, String msg) {
        if (!condition) {
            throw new RuntimeException(msg);
        }
    }

    static final String EDGES_GRAPH_TYPE = "edges";
    static final String MATRIX_GRAPH_TYPE = "matrix";

    static final String AWT_DRAWING_API = "awt";
    static final String JAVAFX_DRAWING_API = "javafx";

    static Edge readEdge(BufferedReader reader, int verticesCount) throws IOException {
        String line = reader.readLine();
        int wsIndex = line.indexOf(" ");

        int from = Integer.parseInt(line.substring(0, wsIndex));
        check(0 <= from, "from < 0");
        check(from < verticesCount, "from > vertices count");

        int to = Integer.parseInt(line.substring(wsIndex + 1));
        check(0 <= to, "to < 0");
        check(to < verticesCount, "to > vertices count");

        return new Edge(from, to);
    }

    static DrawableGraph readEdgesGraph(BufferedReader reader, int verticesCount, int edgesCount) throws IOException {
        EdgesListGraph edgesListGraph = new EdgesListGraph();

        while (edgesListGraph.getEdgesCount() != edgesCount) {
            var edge = readEdge(reader, verticesCount);

            if (edgesListGraph.hasEdge(edge.getFrom(), edge.getTo())) {
                System.out.println("graph already has this edge: please reenter");
            } else {
                edgesListGraph.addEdge(edge.getFrom(), edge.getTo());
            }
        }

        return edgesListGraph;
    }

    static DrawableGraph readMatrixGraph(BufferedReader reader, int verticesCount, int edgesCount) throws IOException {
        AdjacencyMatrixGraph adjacencyMatrixGraph = new AdjacencyMatrixGraph(verticesCount);

        int edgeNumber = 0;
        while (edgeNumber < edgesCount) {
            var edge = readEdge(reader, verticesCount);

            if (adjacencyMatrixGraph.hasEdge(edge.getFrom(), edge.getTo())) {
                System.out.println("graph already has this edge: please reenter");
            } else {
                adjacencyMatrixGraph.addEdge(edge.getFrom(), edge.getTo());
                ++edgeNumber;
            }
        }

        return adjacencyMatrixGraph;
    }

    static DrawableGraph readDrawableGraph(String graphType) throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Enter graph vertices count:");

            int verticesCount = Integer.parseInt(reader.readLine());
            check(0 <= verticesCount, "graph must have non-negative number of vertices");

            System.out.println("Enter graph edges count:");
            System.out.println("(loops and multiple edges between vertices not supported)");

            int edgesCount = Integer.parseInt(reader.readLine());
            check(0 <= edgesCount, "edges count > 0");
            check(edgesCount <= verticesCount * (verticesCount - 1) / 2,
                    "edges count > max available edges count");

            System.out.println("Enter edges in format 'from to' in separate lines");

            return switch (graphType) {
                case EDGES_GRAPH_TYPE -> readEdgesGraph(reader, verticesCount, edgesCount);
                case MATRIX_GRAPH_TYPE -> readMatrixGraph(reader, verticesCount, edgesCount);
                default -> throw new RuntimeException("unknown graph type");
            };
        }
    }

    static boolean validateGraphType(String graphType) {
        return EDGES_GRAPH_TYPE.equals(graphType) || MATRIX_GRAPH_TYPE.equals(graphType);
    }

    static boolean validateDrawingApi(String drawingApi) {
        return AWT_DRAWING_API.equals(drawingApi) || JAVAFX_DRAWING_API.equals(drawingApi);
    }

    private static void doMain(String graphType, String drawingApi) throws IOException {
        DrawableGraph drawableGraph = readDrawableGraph(graphType);

        switch (drawingApi) {
            case AWT_DRAWING_API -> {
                GraphAwtDrawingApi graphAwtDrawing =
            }
            case JAVAFX_DRAWING_API -> {

            }
            default -> throw new RemoteException("unknown drawing api");
        }
    }

    public static void main(String[] args) {
        boolean isHelp = args != null && args.length == 1 && ("-h".equals(args[0]) || "--help".equals(args[0]));
        boolean isIncorrectLaunch = isHelp || args == null || args.length != 2
                || !validateGraphType(args[0]) || !validateDrawingApi(args[1]);

        if (isHelp || isIncorrectLaunch) {
            String helpMsg = """
                Program accepts 2 arguments passed via command line:
                1) graph type: '%s' or '%s'
                2) drawing api: '%s' or '%s'
                """;

            System.out.printf(helpMsg + "%n", EDGES_GRAPH_TYPE, MATRIX_GRAPH_TYPE, AWT_DRAWING_API, JAVAFX_DRAWING_API);

            return;
        }

        try {
            doMain(args[0], args[1]);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
