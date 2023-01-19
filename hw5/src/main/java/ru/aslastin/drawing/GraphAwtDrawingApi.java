package ru.aslastin.drawing;

import ru.aslastin.graph.DrawableGraph;

import java.awt.*;

public class GraphAwtDrawingApi extends Frame implements DrawingApi {
    private final DrawableGraph drawableGraph;
    private Graphics2D graphics2D;

    public GraphAwtDrawingApi(DrawableGraph drawableGraph) {
        this.drawableGraph = drawableGraph;
    }

    @Override
    public void paint(Graphics g) {
        graphics2D = (Graphics2D) g;

        graphics2D.setPaint(Color.BLACK);
        graphics2D.setBackground(Color.WHITE);

        drawableGraph.drawGraph(this);
    }

    @Override
    public int getDrawingAreaWidth() {
        return getWidth();
    }

    @Override
    public int getDrawingAreaHeight() {
        return getHeight();
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
