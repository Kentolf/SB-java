package org.example;

import java.io.File;

@CommandInfo(name = "cd", description = "Меняет текущую директорию")
public class CdCommand implements Command {
    private static File currentDir = new File(System.getProperty("user.dir"));

    @Override
    public void exec(String[] args) throws CommandExecutionException {
        if (args.length == 0) {
            System.out.println(currentDir.getAbsolutePath());
            return;
        }

        File newDir = new File(currentDir, args[0]);
        if (!newDir.isDirectory()) {
            throw new CommandExecutionException("cd", "Директория не существует: " + args[0]);
        }

        currentDir = newDir;
    }
}