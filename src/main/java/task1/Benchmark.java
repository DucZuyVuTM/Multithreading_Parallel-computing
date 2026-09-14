package task1;

import java.util.concurrent.ExecutionException;

public final class Benchmark {

    private static final int WARMUP_RUNS = 3;
    private static final int MEASURED_RUNS = 5;

    private Benchmark() { }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int arraySize = 50_000_000;
        long seed = 42L;
        int numThreads = Runtime.getRuntime().availableProcessors();
        int threshold = 100_000;

        System.out.println("=== Task 1 -- CPU-bound: Find max ===");
        System.out.println("Array size: " + arraySize);
        System.out.println("CPU Threads: " + numThreads);
        System.out.println("ForkJoin threshold: " + threshold);
        System.out.println();

        int[] arr = ArrayGenerator.generate(arraySize, seed);

        int seqResult = SequentialMax.findMax(arr);
        int execResult = ExecutorMax.findMax(arr, numThreads);
        int fjResult = ForkJoinMax.findMax(arr, threshold);

        if (seqResult != execResult || seqResult != fjResult) {
            System.err.println("Wrong answer:");
            System.err.println("Sequential: " + seqResult);
            System.err.println("Executor:   " + execResult);
            System.err.println("ForkJoin:   " + fjResult);
            return;
        }
        System.out.println("[OK] All 3 Results: " + seqResult);
        System.out.println();

        System.out.println("Warm-up " + WARMUP_RUNS + " times...");
        for (int i = 0; i < WARMUP_RUNS; i++) {
            SequentialMax.findMax(arr);
            ExecutorMax.findMax(arr, numThreads);
            ForkJoinMax.findMax(arr, threshold);
        }
        System.out.println("[OK] Warm-up completed.\n");

        long[] seqTimes = new long[MEASURED_RUNS];
        long[] execTimes = new long[MEASURED_RUNS];
        long[] fjTimes = new long[MEASURED_RUNS];

        for (int i = 0; i < MEASURED_RUNS; i++) {
            seqTimes[i]  = measureSequential(arr);
            execTimes[i] = measureExecutor(arr, numThreads);
            fjTimes[i]   = measureForkJoin(arr, threshold);
            System.out.printf("Round %d/%d completed%n", i + 1, MEASURED_RUNS);
        }

        System.out.println("\n=== Result (ms) ===");
        System.out.printf("%-20s", "Version");
        for (int i = 1; i <= MEASURED_RUNS; i++) {
            System.out.printf("%10s", "Round " + i);
        }
        System.out.printf("%15s%15s%n", "Average", "Speed Up");

        printRow("Sequential", seqTimes, seqTimes);
        printRow("ExecutorService", execTimes, seqTimes);
        printRow("ForkJoinPool", fjTimes, seqTimes);
    }

    private static void printRow(String name, long[] times, long[] baseline) {
        System.out.printf("%-20s", name);

        double sum = 0;
        for (long t : times) {
            System.out.printf("%10d", t);
            sum += t;
        }

        double avg = sum / times.length;
        double baselineAvg = average(baseline);
        double speedup = baselineAvg / avg;

        System.out.printf("%15.1f%15.2f%n", avg, speedup);
    }

    private static double average(long[] arr) {
        double sum = 0;
        for (long v : arr) sum += v;
        return sum / arr.length;
    }

    private static long measureSequential(int[] arr) {
        long start = System.nanoTime();
        SequentialMax.findMax(arr);
        return (System.nanoTime() - start) / 1_000_000;
    }

    private static long measureExecutor(int[] arr, int numThreads)
            throws InterruptedException, ExecutionException {
        long start = System.nanoTime();
        ExecutorMax.findMax(arr, numThreads);
        return (System.nanoTime() - start) / 1_000_000;
    }

    private static long measureForkJoin(int[] arr, int threshold) {
        long start = System.nanoTime();
        ForkJoinMax.findMax(arr, threshold);
        return (System.nanoTime() - start) / 1_000_000;
    }
}
