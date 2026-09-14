package task4;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public final class Benchmark {

    private static final int TASK_COUNT = 100;
    private static final int QUEUE_CAPACITY = 20;
    private static final int CONSUMER_COUNT = 3;
    private static final long SEED = 42L;

    private Benchmark() { }

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=== Task 4 -- Producer / Consumer ===");
        System.out.println("Task count: " + TASK_COUNT);
        System.out.println("Queue capacity: " + QUEUE_CAPACITY);
        System.out.println("Consumer count: " + CONSUMER_COUNT);
        System.out.println();

        // ─── Общие структуры ───
        BlockingQueue<FileTask> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        TaskStats stats = new TaskStats();
        AtomicInteger remainingTasks = new AtomicInteger(TASK_COUNT);

        // 2 latch: producer + все consumers
        CountDownLatch producerDone = new CountDownLatch(1);
        CountDownLatch consumersDone = new CountDownLatch(CONSUMER_COUNT);

        // ─── Producer ───
        Thread producer = new Thread(
                new Producer(queue, TASK_COUNT, SEED, producerDone),
                "producer");

        // ─── Consumers ───
        Thread[] consumers = new Thread[CONSUMER_COUNT];
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            consumers[i] = new Thread(
                    new Consumer("consumer-" + (i + 1),
                                 queue, stats, remainingTasks, consumersDone),
                    "consumer-" + (i + 1));
        }

        long start = System.nanoTime();

        // Стартуем consumers первыми, чтобы они ждали задачи
        for (Thread c : consumers) c.start();
        producer.start();

        // ─── Ожидание завершения ───
        producerDone.await();
        consumersDone.await();

        long elapsed = (System.nanoTime() - start) / 1_000_000;

        // ─── Проверка инвариантов ───
        System.out.println();
        System.out.println("=== Stats ===");
        System.out.println("Total task:         " + TASK_COUNT);
        System.out.println("Total task handled: " + stats.total());
        System.out.println("Unique ID:          " + stats.uniqueIdsCount());
        System.out.println("  XML:              " + stats.countByType(FileType.XML));
        System.out.println("  JSON:             " + stats.countByType(FileType.JSON));
        System.out.println("  XLS:              " + stats.countByType(FileType.XLS));
        System.out.println("Total time:         " + elapsed + " мс");
        System.out.println();

        // ─── Инварианты ───
        boolean generatedEqualsProcessed = (stats.total() == TASK_COUNT);
        boolean processedEqualsTypes =
                (stats.countByType(FileType.XML)
                        + stats.countByType(FileType.JSON)
                        + stats.countByType(FileType.XLS)) == stats.total();
        boolean allIdsUnique = (stats.uniqueIdsCount() == stats.total());

        System.out.println("=== Invariant checking ===");
        System.out.println("Generated = Processed:    " + generatedEqualsProcessed);
        System.out.println("Processed = XML+JSON+XLS: " + processedEqualsTypes);
        System.out.println("AlL IDs that are unique:  " + allIdsUnique);

        if (generatedEqualsProcessed && processedEqualsTypes && allIdsUnique) {
            System.out.println("\n[OK] All invariants completed!");
        } else {
            System.out.println("\n[ERROR] Violation of invariants!");
        }
    }
}
