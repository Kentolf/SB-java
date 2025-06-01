package org.example;

import java.io.*;
import java.util.regex.*;
import java.nio.file.Files;

public class Main {

    public static void main(String[] args) {
        PatternSearcher searcher = new PatternSearcher();
        searcher.run(args);
    }
}

class PatternSearcher {

    private boolean ignoreCase;
    private String patternStr;
    private String pathStr;
    private Pattern pattern;

    public void run(String[] args) {

        if (!parseArgs(args)) {
            return;
        }

        File path = new File(pathStr);
        if (!isPath(path)) {
            return;
        }

        if (!Pattern()) {
            return;
        }
        processPath(path);
    }

    private boolean parseArgs(String[] args) {

        if (args.length < 2 || args.length > 3) {
            System.out.println("Usage: java Analyzer [-i] <pattern> <file_or_directory>");
            return false;
        }

        if (args.length == 2) {
            patternStr = args[0];
            pathStr = args[1];
            ignoreCase = false;
        }
        else {
            if (args[0].equals("-i")) {
                ignoreCase = true;
                patternStr = args[1];
                pathStr = args[2];
            }
            else {
                System.out.println("Unknown option: " + args[0]);
                return false;
            }
        }
        return true;
    }

    private boolean isPath(File path) {

        if (!path.exists()) {
            System.out.println("Такого пути не существует");
            return false;
        }

        if (!path.isFile() && !path.isDirectory()) {
            System.out.println("Такого файла/директории не существует");
            return false;
        }
        return true;
    }

    private boolean Pattern() {

        try {
            if (ignoreCase) {
                pattern = Pattern.compile(patternStr, Pattern.UNICODE_CASE);
            }
            else {
                pattern = Pattern.compile(patternStr);
            }

            return true;
        }
        catch (PatternSyntaxException e) {
            System.out.println("Неправильный паттерн");
            return false;
        }
    }

    private void processPath(File path) {

        if (path.isFile()) {
            searchInFile(path, false);
        }

        else if (path.isDirectory()) {
            processDirectory(path);
        }
    }

    private void processDirectory(File directory) {

        File[] files = directory.listFiles();

        if (files == null) {
            System.out.println("Директория пуста");
            return;
        }

        for (File file : files) {
            if (file.isFile()) {
                searchInFile(file, true);
            }
        }
    }

    private void searchInFile(File file, boolean showFileName) {

        if (isBinary(file)) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);

                if (matcher.find()) {
                    Match(file, showFileName, lineNumber, line);
                }
                lineNumber++;
            }
        }
        catch (IOException e) {
            System.out.println("Ошибка с чтением файла: " + file.getName());
        }
    }

    private void Match(File file, boolean showFileName, int lineNumber, String line) {

        String prefix;

        if (showFileName) {
            prefix = file.getName() + ":";
        }
        else {
            prefix = "";
        }
        System.out.println(prefix + lineNumber + ": " + line);
    }

    private boolean isBinary(File file) {

        try (InputStream in = Files.newInputStream(file.toPath())) {

            byte[] buffer = new byte[512];
            int bytesRead = in.read(buffer);

            for (int i = 0; i < bytesRead; i++) {
                if (buffer[i] == 0) {
                    return true;
                }
            }

            return false;
        }
        catch (IOException e) {
            System.out.println("Ошибка с чтением файла: " + file.getName());
            return true;
        }
    }
}