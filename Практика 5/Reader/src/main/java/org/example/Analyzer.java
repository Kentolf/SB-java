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
        Map<String, Integer> wordFrequencyMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                for (String word : line.replaceAll("[^a-zA-Zа-яА-Я\\s]", "").toLowerCase().split("\\s+")) {
                    if (!word.isEmpty()) {
                        wordFrequencyMap.put(word, wordFrequencyMap.getOrDefault(word, 0) + 1);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
            return;
        }

        wordFrequencyMap.forEach((word, count) -> System.out.println(word + ": " + count));
    }
}