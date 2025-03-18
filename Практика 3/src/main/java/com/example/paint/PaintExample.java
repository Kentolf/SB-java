package com.example.paint;

public class PaintExample {

    public static void main(String[] args) {
        Point point = new Point(10, 20);

        Circle circle = new Circle(point, 5);
        Rectangle rectangle = new Rectangle(point, 10, 20);
        Square square = new Square(point, 15);
        Triangle triangle = new Triangle(point, 3, 4, 5);

        FigureUtil.draw(circle);
        FigureUtil.draw(rectangle, Color.RED);
        FigureUtil.draw(square, Color.GREEN);
        FigureUtil.draw(triangle, Color.BLUE);
    }
}