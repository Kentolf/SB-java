package org.example;

import java.time.LocalDate;

public class DateCommand implements Command {

    @Override
    public void execute() {
        System.out.println(LocalDate.now());
    }
}