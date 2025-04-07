package org.example;

public interface Command {
    String getName();

    void execute(String[] args);
    String getHelp();
}