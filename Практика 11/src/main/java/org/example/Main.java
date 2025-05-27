package org.example;

public class Main {
    private static final int[] Threads = {1, 2, 3, 4, 5, 6};
    private static final int Default_Theads = 4;
    private static final int Top = 15;

    public static void main(String[] args) {

        String folderPath = args[0];
        runPerformanceTests(folderPath);
        runFinalAnalysis(folderPath);
    }

    private static void runPerformanceTests(String folderPath) {
        System.out.println("Тесты с разным числом потоков:");

        for (int i = 0; i < Threads.length; i++) {
            int threads = Threads[i]; {
            try {
                Analyzer analyzer = new Analyzer(folderPath, threads);
                long startTime = System.nanoTime();

                analyzer.analyzeFiles();

                long durationMs = (System.nanoTime() - startTime) / 1_000_000;
                printTestResult(threads, durationMs, analyzer.getUniqueWordsCount());
            }
            catch (Exception e) {
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
        System.out.printf("%nФинальный анализ (Количество потоков: %d):%n", Default_Theads);

        try {
            Analyzer analyzer = new Analyzer(folderPath, Default_Theads);
            long startTime = System.nanoTime();

            analyzer.analyzeFiles();

            long durationMs = (System.nanoTime() - startTime) / 1_000_000;
            System.out.printf("Общее время анализа: %d мс%n", durationMs);

            analyzer.displayTopWords(Top);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}