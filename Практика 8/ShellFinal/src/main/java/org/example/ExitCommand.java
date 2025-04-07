package org.example;

@CommandInfo(name = "exit", description = "Выход из программы")
public class ExitCommand implements Command {
    @Override
    public void exec(String[] args) throws CommandExecutionException {
        System.out.println("The program has completed its work");
        System.exit(0);
    }
}
