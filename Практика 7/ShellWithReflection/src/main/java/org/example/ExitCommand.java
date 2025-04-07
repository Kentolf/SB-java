package org.example;

public class ExitCommand implements Command {

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public void execute(String[] args) {
        System.exit(0);
    }

    @Override
    public String getHelp() {
        return "Выход из программы";
    }
}
