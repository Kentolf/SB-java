package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Analyzer {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please specify the file path as an argument");
            return;
        }

        String filePath = args[0];
        Map<String, Integer> wordMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.toLowerCase();
                StringBuilder wordBuilder = new StringBuilder();

                for (int i = 0; i < line.length(); i++) {
                    char ch = line.charAt(i);

                    if (Character.isDigit(ch) || Character.isLetter(ch)) {
                        wordBuilder.append(ch);
                    }

                    else if (wordBuilder.length() > 0) {
                        String word = wordBuilder.toString();
                        wordMap.put(word, wordMap.getOrDefault(word, 0) + 1);
                        wordBuilder.setLength(0);
                    }
                }

                if (wordBuilder.length() > 0) {
                    String word = wordBuilder.toString();
                    wordMap.put(word, wordMap.getOrDefault(word, 0) + 1);
                }
            }
        }

        catch (IOException e) {
            System.out.println(e.getMessage());
        }

        for (Map.Entry<String, Integer> entry : wordMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
