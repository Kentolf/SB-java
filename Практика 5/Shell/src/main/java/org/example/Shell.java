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
            String input = scanner.nextLine().trim().toLowerCase(); // читаем и преобразуем команду

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            Command command = commands.get(input);
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
