package org.example;

import java.sql.Connection;
import java.util.Map;
import java.util.Scanner;

public class UI {
    private final Map<String, Command> commands;

    public UI(Connection conn) throws Exception {

        commands = SearchClasses.loadCommands("org.example", conn);
    }

    public void run() {

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.print("Введите команду: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            String[] inputParts = input.split("\\s+", 2);
            String commandName = inputParts[0].toLowerCase();
            String argsStr = inputParts.length > 1 ? inputParts[1] : "";

            if (commandName.equals("exit")) {
                break;
            }
            else if (commandName.equals("help")) {

                if (argsStr.isEmpty()) {
                    for (Command cmd : commands.values()) {
                        System.out.println(cmd.getName() + ": " + cmd.getHelp());
                    }
                }
                else {
                    Command cmd = commands.get(argsStr.toLowerCase());
                    if (cmd != null) {
                        System.out.println(cmd.getHelp());
                    }
                    else {
                        System.out.println("Команда не найдена");
                    }
                }
            } else {
                Command cmd = commands.get(commandName);
                if (cmd != null) {
                    cmd.execute(new String[]{argsStr});
                }
                else {
                    System.out.println("Неизвестная команда. Введите help для списка команд.");
                }
            }
        }
    }
}