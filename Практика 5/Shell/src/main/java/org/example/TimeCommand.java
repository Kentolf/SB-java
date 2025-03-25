package org.example;

import java.time.LocalTime;

public class TimeCommand implements Command {

    @Override
    public void execute() {
        System.out.println(LocalTime.now());
    }

    @Override
    public String description() {
        return "Выводит текущее время";
    }
}