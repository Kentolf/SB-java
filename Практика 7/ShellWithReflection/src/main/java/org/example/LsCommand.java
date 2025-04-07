package org.example;

import java.io.File;

public class LsCommand implements Command {

    @Override
    public String getName() {
        return "ls";
    }

    @Override
    public void execute(String[] args) {
        File currentDir = new File(".");
        File[] files = currentDir.listFiles();

        if (files == null || files.length == 0) {
            System.out.println("Папка пуста.");
            return;
        }

        for (File file : files) {
            System.out.println((file.isDirectory() ? "[DIR] " : "      ") + file.getName());
        }
    }

    @Override
    public String getHelp() {
        return "Выводит содержимое текущей директории";
    }
}
