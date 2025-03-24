package org.example;

@Command.Operation(symbol = "*", priority = 2)
public class MultiplicationCommand implements Command {
    @Override
    public double execute(double a, double b) {
        return a * b;
    }
}