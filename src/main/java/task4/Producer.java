package task4;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public final class Producer implements Runnable {

    private final BlockingQueue<FileTask> queue;
    private final int taskCount;
    private final long seed;
    private final CountDownLatch doneSignal;

    public Producer(BlockingQueue<FileTask> queue, int taskCount, long seed,
                    CountDownLatch doneSignal) {
        this.queue = queue;
        this.taskCount = taskCount;
        this.seed = seed;
        this.doneSignal = doneSignal;
    }

    @Override
    public void run() {
        try {
            Random random = new Random(seed);
            FileType[] types = FileType.values();

            for (int i = 1; i <= taskCount; i++) {
                FileType type = types[random.nextInt(types.length)];
                int size = 10 + random.nextInt(91);  // 10..100
                FileTask task = new FileTask(i, type, size);

                // put() блокируется, если очередь заполнена → backpressure
                queue.put(task);

                // Случайный интервал между задачами
                int pause = random.nextInt(5);
                Thread.sleep(pause);
            }

            System.out.println("[Producer] Created " + taskCount + " task.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[Producer] Interrupted.");
        } finally {
            doneSignal.countDown();   // Сообщаем main: producer закончил
        }
    }
}
