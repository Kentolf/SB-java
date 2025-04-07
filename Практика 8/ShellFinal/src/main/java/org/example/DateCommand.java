package org.example;

import java.time.LocalDate;

@CommandInfo(name = "date", description = "Выводит текущую дату")
public class DateCommand implements Command {
    @Override
    public void exec(String[] args) throws CommandExecutionException {
        System.out.println(LocalDate.now());
    }
}
