package org.example;

public interface Command {
    void execute();
    String description(); // описание команды
}