package org.example;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class Analyzer {
    private final ConcurrentHashMap<String, LongAdder> wordFrequency = new ConcurrentHashMap<>();
    private final Path targetFolder;
    private final int maxThreads;

    public Analyzer(String folderPath, int maxThreads) {
        this.targetFolder = Paths.get(folderPath);
        this.maxThreads = maxThreads;
    }

    public void analyzeFiles() throws IOException, InterruptedException {
        ExecutorService processingPool = createProcessingPool();

        try (Stream<Path> filePaths = Files.walk(targetFolder)) {
            processFiles(filePaths, processingPool);
        }
        finally {
            shutdownProcessingPool(processingPool);
        }
    }

    private ExecutorService createProcessingPool() {
        return Executors.newFixedThreadPool(maxThreads);
    }

    private void processFiles(Stream<Path> filePaths, ExecutorService pool) {
        filePaths.filter(this::isTextFile).forEach(file -> pool.submit(() -> processSingleFile(file)));
    }

    private boolean isTextFile(Path file) {
        return Files.isRegularFile(file) && file.getFileName().toString().toLowerCase().endsWith(".txt");
    }

    private void shutdownProcessingPool(ExecutorService pool) throws InterruptedException {
        pool.shutdown();
        if (!pool.awaitTermination(1, TimeUnit.MINUTES)) {
            System.err.println("Ошибка во время выполнения");
        }
    }

    private void processSingleFile(Path file) {
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            Map<String, Integer> localWordCounts = countWordsInFile(reader);
            mergeResults(localWordCounts);
        }
        catch (IOException e) {
            System.err.printf("Ошибка обработки файла %s: %s%n", file, e.getMessage());
        }
    }

    private Map<String, Integer> countWordsInFile(BufferedReader reader) {
        Map<String, Integer> counts = new HashMap<>();
        reader.lines()
                .flatMap(line -> splitLineToWords(line))
                .filter(word -> !word.isEmpty())
                .forEach(word -> counts.merge(word, 1, Integer::sum));
        return counts;
    }

    public void displayTopWords(int topN) {
        System.out.printf("%nТоп %d самых частых слов:%n", topN);

        wordFrequency.entrySet().stream()
                .filter(entry -> isValidWord(entry.getKey()))
                .sorted(createFrequencyComparator())
                .limit(topN)
                .forEach(this::printWordEntry);
    }

    private Stream<String> splitLineToWords(String line) {
        return Arrays.stream(line.toLowerCase().split("[^\\p{L}]+"));
    }

    private void mergeResults(Map<String, Integer> localCounts) {
        localCounts.forEach((word, count) -> wordFrequency.computeIfAbsent(word, k -> new LongAdder()).add(count));
    }

    private Comparator<Map.Entry<String, LongAdder>> createFrequencyComparator() {
        return Comparator.comparingLong((Map.Entry<String, LongAdder> e) -> e.getValue().sum()).reversed().thenComparing(Map.Entry::getKey);
    }

    private void printWordEntry(Map.Entry<String, LongAdder> entry) {
        System.out.printf("Слово: %-15s Частота: %,d %n", entry.getKey(), entry.getValue().sum());
    }

    public int getUniqueWordsCount() {
        return wordFrequency.size();
    }

    private boolean isValidWord(String word) {
        return word.length() >= 3;
    }
}