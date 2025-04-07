package org.example;

import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

public class Shell {
    private final Map<String, Command> commands;

    public Shell() {
        commands = SearchClasses.loadCommands();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите команду (или 'help' для справки, 'exit' для выхода):");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;

            String[] parts = input.split("\\s+");
            String commandName = parts[0].toLowerCase();
            String[] args = Arrays.copyOfRange(parts, 1, parts.length);

            if (commandName.equals("exit")) {
                System.out.println("The program has completed its work");
                break;
            }

            if (commandName.equals("help")) {
                helpCommand(args);
                continue;
            }

            Command command = commands.get(commandName);

            if (command != null) {
                try {
                    command.exec(args);
                } catch (CommandExecutionException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("Invalid command: " + commandName);
            }
        }
    }

    private void helpCommand(String[] args) {
        if (args.length == 0) {
            printHelpAll();
        } else {
            String targetCommand = args[0].toLowerCase();
            Command command = commands.get(targetCommand);
            if (command != null) {
                printHelpCommand(command);
            } else {
                System.out.println("Команда не найдена: " + targetCommand);
            }
        }
    }

    private void printHelpAll() {
        System.out.println("Доступные команды:");
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            CommandInfo info = entry.getValue().getClass().getAnnotation(CommandInfo.class);
            if (info != null) {
                System.out.printf(" - %-10s : %s%n", info.name(), info.description());
            }
        }
        System.out.println("Для подробной справки используйте: help <имя_команды>");
    }
    private void printHelpCommand(Command command) {
        CommandInfo info = command.getClass().getAnnotation(CommandInfo.class);
        if (info != null) {
            System.out.println(info.description());
        }
    }

    public static void main(String[] args) {
        new Shell().run();
    }
}
