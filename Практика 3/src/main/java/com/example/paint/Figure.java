package com.example.paint;

public abstract class Figure {

    private Point position;

    public Figure(Point position) {
        this.position = position;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public abstract double area();

    public abstract double perimeter();
}