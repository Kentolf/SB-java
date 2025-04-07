package org.example;

@CommandInfo(name = "pwd", description = "Выводит текущую директорию")
public class PwdCommand implements Command {
    @Override
    public void exec(String[] args) {
        System.out.println(System.getProperty("user.dir"));
    }
}
