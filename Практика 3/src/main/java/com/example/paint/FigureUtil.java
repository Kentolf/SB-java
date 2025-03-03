package com.example.paint;

public class FigureUtil {

    private FigureUtil() {

    }

    public static double area(Figure figure) {

        return figure.area();
    }

    public static double perimeter(Figure figure) {

        return figure.perimeter();
    }

    public static void draw(Figure figure) {

        System.out.println("Нарисована фигура с координатами (" + figure.getPosition().getX() + ", " + figure.getPosition().getY() + ") черным цветом");
    }

    public static void draw(Figure figure, Color color) {

        System.out.println("Нарисована фигура с координатами (" + figure.getPosition().getX() + ", " + figure.getPosition().getY() + ") цветом " + color);
    }
}