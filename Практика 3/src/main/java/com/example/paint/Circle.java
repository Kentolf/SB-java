package com.example.paint;

public class Circle extends Figure {

    private double radius;

    public Circle(Point position, double radius) {
        super(position); // используем super для вызова конструктора суперкласса Figure
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }

    @Override
    public double perimeter() {
        return 2 * Math.PI * radius;
    }
}