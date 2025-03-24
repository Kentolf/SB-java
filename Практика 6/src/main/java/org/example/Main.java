package org.example;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        try {
            System.out.print("Enter the first number: ");
            int firstNumber = input.nextInt();

            System.out.print("Enter the second number: ");
            int secondNumber = input.nextInt();

            int result = Multiplication.multiplication(firstNumber, secondNumber);
            System.out.println("Result: " + result);

        } catch (MyException e) {
            System.out.println(e.getMessage());

        } catch (Exception e) {
            System.out.println("Error: Invalid input. Please enter integers only.");

        } finally {
            System.out.println("The program is completed");
        }
    }
}