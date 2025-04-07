package org.example;

import java.io.File;

@CommandInfo(name = "ls", description = "Выводит список файлов в директории (по умолчанию текущая)")
public class LsCommand implements Command {
    @Override
    public void exec(String[] args) throws CommandExecutionException {
        File dir;

        if (args.length > 0) {
            dir = new File(args[0]);
        } else {
            dir = new File(System.getProperty("user.dir")); // текущая директория JVM
        }

        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("(Пусто)");
            return;
        }

        for (File file : files) {
            System.out.println(file.getName());
        }
    }
}
