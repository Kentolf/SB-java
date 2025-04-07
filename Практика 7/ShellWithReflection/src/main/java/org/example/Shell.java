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
            if (input.isEmpty()) continue;

            String[] parts = input.split("\\s+");
            String commandName = parts[0].toLowerCase();
            String[] args = new String[parts.length - 1];
            System.arraycopy(parts, 1, args, 0, args.length);

            if (commandName.equals("exit")) {
                break;
            }

            if (commandName.equals("help")) {
                if (args.length == 0) {
                    System.out.println("Commands:");
                    for (Command cmd : commands.values()) {
                        System.out.printf("- %s%n", cmd.getName());
                    }
                } else {
                    Command cmd = commands.get(args[0].toLowerCase());
                    if (cmd != null) {
                        System.out.println(cmd.getHelp());
                    } else {
                        System.out.println("Command not found: " + args[0]);
                    }
                }
                continue;
            }

            Command command = commands.get(commandName);
            if (command != null) {
                try {
                    command.execute(args);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("Invalid command: " + commandName);
            }
        }

        System.out.println("The program has completed its work");
    }

    public static void main(String[] args) {
        new Shell().run();
    }
}
