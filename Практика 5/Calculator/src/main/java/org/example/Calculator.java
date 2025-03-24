package org.example;

import java.util.Scanner;

public class Calculator {
    private final Parser parser;

    public Calculator() {
        parser = new Parser();
    }

    public void run() {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the expression:");

        while (true) {

            System.out.print("> ");
            String input = scanner.nextLine(); // считываем выражение

            if (input.equalsIgnoreCase("exit")) { // exit для выхода
                break;
            }

            try {
                double result = parser.evaluate(input); // обрабатывает выражение с помощью парсера
                System.out.println("= " + result);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println("The calculator has completed its work");
    }

    public static void main(String[] args) {

        Calculator calculator = new Calculator();
        calculator.run();
    }
}