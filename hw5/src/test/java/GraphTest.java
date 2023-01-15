import org.junit.jupiter.api.Test;
import ru.aslastin.graph.AdjacencyMatrixGraph;
import ru.aslastin.graph.EdgesListGraph;
import ru.aslastin.graph.Graph;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphTest {
    static void assertGraphsEqual(EdgesListGraph edgesListGraph, AdjacencyMatrixGraph adjacencyMatrixGraph) {
        assertEquals(edgesListGraph.getVerticesCount(), adjacencyMatrixGraph.getVerticesCount());
        assertEquals(edgesListGraph.getEdgesCount(), adjacencyMatrixGraph.getEdgesCount());
        assertThat(edgesListGraph.iterator())
                .containsOnlyElementsOf(adjacencyMatrixGraph);
    }

    @Test
    void first() {
        EdgesListGraph edgesListGraph = new EdgesListGraph();
        AdjacencyMatrixGraph adjacencyMatrixGraph = new AdjacencyMatrixGraph(7);

        for (Graph graph : List.of(edgesListGraph, adjacencyMatrixGraph)) {
            graph.addEdge(0, 5);
            graph.addEdge(1, 2);
            graph.addEdge(1, 3);
            graph.addEdge(4, 6);
            graph.addEdge(2, 3);
        }

        assertGraphsEqual(edgesListGraph, adjacencyMatrixGraph);
    }
}
