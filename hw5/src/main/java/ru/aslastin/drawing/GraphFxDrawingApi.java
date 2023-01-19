package ru.aslastin.drawing;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ru.aslastin.graph.DrawableGraph;

public class GraphFxDrawingApi extends Application implements  DrawingApi {
    private final DrawableGraph drawableGraph;
    private int width;
    private int height;
    private GraphicsContext graphicsContext;

    public GraphFxDrawingApi(DrawableGraph drawableGraph) {
        this.drawableGraph = drawableGraph;
    }

    void setSize(int width, int height) {
        setSize(width, height);;
    }

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        Canvas canvas = new Canvas(width, height);

        graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setFill(Color.BLACK);

        drawableGraph.drawGraph(this);

        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Override
    public int getDrawingAreaWidth() {
        return 0;
    }

    @Override
    public int getDrawingAreaHeight() {
        return 0;
    }

    @Override
    public void drawCircle(double x, double y, double r) {

    }

    @Override
    public void drawFilledCircle(double x, double y, double r, String text) {

    }

    @Override
    public void drawLine(double x1, double y1, double x2, double y2) {

    }

    @Override
    public void drawArrow(double x1, double y1, double x2, double y2, double arrowLength) {

    }
}
