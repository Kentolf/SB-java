package org.example;

public interface Command {
    void exec(String[] args) throws CommandExecutionException;
}
