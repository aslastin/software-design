package ru.aslastin.drawing;

public interface DrawingApi {
    int getDrawingAreaWidth();
    int getDrawingAreaHeight();

    void drawCircle(double x, double y, double r);
    void drawFilledCircle(double x, double y, double r, String text);

    void drawLine(double x1, double y1, double x2, double y2);
    void drawArrow(double x1, double y1, double x2, double y2, double arrowLength);
}
