package com.example.paint;

public class FigureUtil {

    private FigureUtil() {}

    public static double area(Figure figure) {
        return figure.area();
    }

    public static double perimeter(Figure figure) {
        return figure.perimeter();
    }

    public static void draw(Figure figure) {
        System.out.println(
                "A shape with coordinates has been drawn ("
                        + figure.getPosition().getX()
                        + ", "
                        + figure.getPosition().getY()
                        + ") black color");
    }

    public static void draw(Figure figure, Color color) {
        System.out.println(
                "A shape with coordinates has been drawn ("
                        + figure.getPosition().getX()
                        + ", "
                        + figure.getPosition().getY()
                        + ") color"
                        + color);
    }
}