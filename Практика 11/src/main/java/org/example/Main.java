package org.example;

public class Main {
    private static final int[] THREADS_TO_TEST = {1, 2, 3, 4, 5, 6};
    private static final int DEFAULT_THREADS = 4;
    private static final int TOP_RESULTS_TO_SHOW = 15;

    public static void main(String[] args) {

        String folderPath = args[0];
        runPerformanceTests(folderPath);
        runFinalAnalysis(folderPath);
    }

    private static void runPerformanceTests(String folderPath) {
        System.out.println("Тесты с разным числом потоков:");

        for (int i = 0; i < THREADS_TO_TEST.length; i++) {
            int threads = THREADS_TO_TEST[i];
            {
                try {
                    Analyzer analyzer = new Analyzer(folderPath, threads);
                    long startTime = System.nanoTime();

                    analyzer.analyzeFiles();

                    long durationMs = (System.nanoTime() - startTime) / 1_000_000;
                    printTestResult(threads, durationMs, analyzer.getUniqueWordsCount());
                } catch (Exception e) {
                    System.err.printf("Ошибка при тесте с %d потоками: %s%n", threads, e.getMessage());
                }
            }
        }
    }

    private static void printTestResult(int threads, long timeMs, int uniqueWords) {
        System.out.printf("Потоков: %d | Время: %d мс | Уникальных слов: %,d%n",
                threads, timeMs, uniqueWords);
    }

    private static void runFinalAnalysis(String folderPath) {
        System.out.printf("%nФинальный анализ (Количество потоков: %d):%n", DEFAULT_THREADS);

        try {
            Analyzer analyzer = new Analyzer(folderPath, DEFAULT_THREADS);
            long startTime = System.nanoTime();

            analyzer.analyzeFiles();

            long durationMs = (System.nanoTime() - startTime) / 1_000_000;
            System.out.printf("Общее время анализа: %d мс%n", durationMs);

            analyzer.displayTopWords(TOP_RESULTS_TO_SHOW);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}