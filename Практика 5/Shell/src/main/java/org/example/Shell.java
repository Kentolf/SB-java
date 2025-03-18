package org.example;

import java.util.Scanner;

public class Shell {

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "date":
                    new DateCommand().execute();
                    break;
                case "time":
                    new TimeCommand().execute();
                    break;
                case "pwd":
                    new PwdCommand().execute();
                    break;
                case "exit":
                    new ExitCommand().execute();
                    return;
                case "help":
                    new HelpCommand().execute();
                    break;
                default:
                    System.out.println("Invalid syntax!");
                    break;
            }
        }
    }

    public static void main(String[] args) {
        new Shell().run();
    }
}