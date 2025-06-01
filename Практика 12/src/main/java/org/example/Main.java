package org.example;

import java.io.*;
import java.util.regex.*;

public class Main {

    public static void main(String[] args) {
        if (args.length < 2 || args.length > 3) {
            System.err.println("Usage: java Analyzer [-i] <pattern> <file_or_directory>");
            return;
        }

        boolean ignoreCase = false;  // с учетом / без учета регистра (по умолчанию без)
        String patternStr;  // сам паттерн (пока только как строка)
        String pathStr;  // путь (тоже пока что строка)

        if (args.length == 2) {  // нет -i, значит регистр учитывается
            patternStr = args[0];
            pathStr = args[1];
        }
        else {
            if (!args[0].equals("-i")) {
                System.err.println("Unknown option: " + args[0]);
                return;
            }
            ignoreCase = true;
            patternStr = args[1];
            pathStr = args[2];
        }

        File path = new File(pathStr);  // получаем путь из строки
        if (!path.exists()) {
            System.err.println("Error: path does not exist.");
            return;
        }

        if (path.isFile()) {  // если путь - это файл
            if (ignoreCase) { // если игнорируем регистр, переходим к созданию паттерна без регистра
                processFileIgnoreCase(path, patternStr, false); // не показываем название файла, если он один
            } else {  // не игнорируем
                processFileBaseCase(path, patternStr, false);
            }
        } else if (path.isDirectory()) { // то же самое, но для файлов из директории
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        if (ignoreCase) {
                            processFileIgnoreCase(file, patternStr, true);
                        } else {
                            processFileBaseCase(file, patternStr, true);
                        }
                    }
                }
            } else {
                System.err.println("Error: Unable to list files in directory.");
            }
        } else {
            System.err.println("Error: Not a file or directory.");
        }
    }

    public static void processFileBaseCase(File file, String patternStr, boolean showFileName) {
        Pattern pattern = Pattern.compile(patternStr);  // создаем паттерн учитывая регистр
        processFileWithPattern(file, pattern, showFileName);
    }

    public static void processFileIgnoreCase(File file, String patternStr, boolean showFileName) {
        Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);  // создаем паттерн не учитывая регистр
        processFileWithPattern(file, pattern, showFileName);
    }

    private static void processFileWithPattern(File file, Pattern pattern, boolean showFileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {  // обрабатываем файл в цикле
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {  // находим совпадение с паттерном - выводим строку и файл
                    String prefix;
                    if (showFileName) {  // если работаем с несколькими файлами, выводим название текущего
                        prefix = file.getName() + ":";
                    }
                    else {
                        prefix = "";  // если с одним - не выводим
                    }
                    System.out.println(prefix + lineNumber + ": " + line);
                }
                lineNumber++;
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + file.getName());
        }
    }
}
