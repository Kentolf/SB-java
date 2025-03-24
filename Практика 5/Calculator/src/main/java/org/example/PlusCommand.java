package org.example;

@Command.Operation(symbol = "+", priority = 1) // аннотация для поиска
public class PlusCommand implements Command {
    @Override
    public double execute(double a, double b) {
        return a + b;
    }
}