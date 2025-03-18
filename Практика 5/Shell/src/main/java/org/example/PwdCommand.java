package org.example;

public class PwdCommand implements Command {
    @Override
    public void execute() {
        System.out.println(System.getProperty("user.dir"));
    }
}