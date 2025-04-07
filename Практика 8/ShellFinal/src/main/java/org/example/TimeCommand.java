package org.example;

import java.time.LocalTime;

@CommandInfo(name = "time", description = "Выводит текущее время")
public class TimeCommand implements Command {
    @Override
    public void exec(String[] args) throws CommandExecutionException {
        System.out.println(LocalTime.now().withNano(0));
    }
}
