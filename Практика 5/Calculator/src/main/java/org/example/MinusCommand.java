package org.example;

@Command.Operation(symbol = "-", priority = 1)
public class MinusCommand implements Command {

    @Override
    public double execute(double a, double b) {

        return a - b;
    }
}
