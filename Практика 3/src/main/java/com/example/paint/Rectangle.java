package com.example.paint;

public class Rectangle extends Figure {

    private double width;
    private double height;

    public Rectangle(Point position, double width, double height) {
        super(position);
        this.width = width;
        this.height = height;
    }

    @Override
    public double area() {
        return width * height;
    }

    @Override
    public double perimeter() {
        return 2 * (width + height);
    }
}