package org.example;

import java.util.Map;
import java.util.Scanner;

public class Shell {
    private final Map<String, Command> commands;

    public Shell() {
        commands = SearchClasses.loadCommands("org.example");
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            if (input.endsWith("/?")) { // вывод подсказки
                String commandName = input.substring(0, input.length() - 2).trim().toLowerCase();
                Command command = commands.get(commandName);
                if (command != null) {
                    System.out.println(command.description());
                } else {
                    System.out.println("Command not found: " + commandName);
                }
                continue;
            }

            Command command = commands.get(input.toLowerCase());
            if (command != null) {
                command.execute();
            } else {
                System.out.println("Invalid command!");
            }
        }

        System.out.println("The program has completed its work");
    }

    public static void main(String[] args) {
        new Shell().run();
    }
}