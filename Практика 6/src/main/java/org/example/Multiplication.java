package org.example;

public final class Multiplication {

    public static int multiplication(int a, int b) throws MyException {

        if (a % 2 != 0 && b % 2 != 0) {
            throw new MyException("Both numbers are odd: " + a + " and " + b);

        } else if (a % 2 != 0) {
            throw new MyException("The first number is odd: " + a);

        } else if (b % 2 != 0) {
            throw new MyException("The second number is odd: " + b);
        }

        return a * b;
    }
}