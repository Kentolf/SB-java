package org.example;

@CommandInfo(name = "exit", description = "Завершает выполнение программы")
public class ExitCommand implements Command {
    @Override
    public void exec(String[] args) throws CommandExecutionException {
        System.out.println("Завершение работы...");
        System.exit(0);
    }
}
