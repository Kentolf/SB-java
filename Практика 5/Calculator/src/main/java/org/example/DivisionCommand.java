package org.example;

@Command.Operation(symbol = "/", priority = 2)
public class DivisionCommand implements Command {
    @Override
    public double execute(double a, double b) {
        if (b == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return a / b;
    }
}