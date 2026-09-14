package task2;

import java.util.List;
import java.util.concurrent.ExecutionException;

public final class Benchmark {

    private static final int TASK_COUNT = 1000;
    private static final long SEED = 42L;
    private static final long MIN_DELAY_MS = 100;
    private static final long MAX_DELAY_MS = 500;
    private static final int POOL_SIZE = 200;
    private static final int WARMUP_RUNS = 1;
    private static final int MEASURED_RUNS = 3;

    private Benchmark() { }

    public static void main(String[] args)
            throws InterruptedException, ExecutionException {

        System.out.println("=== Task 2 -- Blocking: Platform vs Virtual Threads ===");
        System.out.println("Task count: " + TASK_COUNT);
        System.out.println("Pool size (platform): " + POOL_SIZE);
        System.out.println("Delay range: " + MIN_DELAY_MS + "–" + MAX_DELAY_MS + " мс");
        System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors());
        System.out.println();

        List<TaskParams> tasks = TaskFactory.createTasks(
                TASK_COUNT, SEED, MIN_DELAY_MS, MAX_DELAY_MS);

        List<BlockingTaskResult> platformCheck = PlatformThreadRunner.run(tasks, POOL_SIZE);
        List<BlockingTaskResult> virtualCheck = VirtualThreadRunner.run(tasks);
        if (platformCheck.size() != TASK_COUNT || virtualCheck.size() != TASK_COUNT) {
            System.err.println("Not all tasks returned a result.");
            return;
        }
        System.out.println("[OK] Both implementations returned " + TASK_COUNT + " results");
        System.out.println();

        // ==== Warm-up ====
        for (int i = 0; i < WARMUP_RUNS; i++) {
            PlatformThreadRunner.run(tasks, POOL_SIZE);
            VirtualThreadRunner.run(tasks);
        }
        System.out.println("Warm-up completed.");
        System.out.println();

        // ==== Измерения ====
        long[] platformTimes = new long[MEASURED_RUNS];
        long[] virtualTimes = new long[MEASURED_RUNS];

        for (int i = 0; i < MEASURED_RUNS; i++) {
            platformTimes[i] = measurePlatform(tasks);
            virtualTimes[i] = measureVirtual(tasks);
            System.out.printf("Round %d/%d completed%n", i + 1, MEASURED_RUNS);
        }

        // ==== Результаты ====
        System.out.println("\n=== Result (ms) ===");
        System.out.printf("%-25s", "Executor");
        for (int i = 1; i <= MEASURED_RUNS; i++) {
            System.out.printf("%10s", "Round " + i);
        }
        System.out.printf("%15s%n", "Average");

        printRow("FixedThreadPool", platformTimes);
        printRow("VirtualThreadPerTask", virtualTimes);
    }

    private static void printRow(String name, long[] times) {
        System.out.printf("%-25s", name);
        double sum = 0;
        for (long t : times) {
            System.out.printf("%10d", t);
            sum += t;
        }
        System.out.printf("%15.1f%n", sum / times.length);
    }

    private static long measurePlatform(List<TaskParams> tasks)
            throws InterruptedException, ExecutionException {
        long start = System.nanoTime();
        PlatformThreadRunner.run(tasks, POOL_SIZE);
        return (System.nanoTime() - start) / 1_000_000;
    }

    private static long measureVirtual(List<TaskParams> tasks)
            throws InterruptedException, ExecutionException {
        long start = System.nanoTime();
        VirtualThreadRunner.run(tasks);
        return (System.nanoTime() - start) / 1_000_000;
    }
}
