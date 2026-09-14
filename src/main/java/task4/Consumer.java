package task4;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public final class Consumer implements Runnable {

    private final String name;
    private final BlockingQueue<FileTask> queue;
    private final TaskStats stats;
    private final AtomicInteger remainingTasks;
    private final CountDownLatch doneSignal;

    public Consumer(String name,
                    BlockingQueue<FileTask> queue,
                    TaskStats stats,
                    AtomicInteger remainingTasks,
                    CountDownLatch doneSignal) {
        this.name = name;
        this.queue = queue;
        this.stats = stats;
        this.remainingTasks = remainingTasks;
        this.doneSignal = doneSignal;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Если задач больше нет — выходим
                if (remainingTasks.get() <= 0) break;

                // take() блокируется, пока нет элемента — никакого busy waiting
                FileTask task = queue.poll(100, java.util.concurrent.TimeUnit.MILLISECONDS);
                if (task == null) continue;  // проверим remainingTasks ещё раз

                // Обработка
                FileTaskProcessor.process(task);

                // Записываем статистику (thread-safe)
                stats.record(task);

                // Уменьшаем счётчик оставшихся задач
                int left = remainingTasks.decrementAndGet();
                System.out.printf("[%s] Handled task id=%d type=%s size=%d (%d remaining)%n",
                        name, task.id(), task.type(), task.size(), left);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[" + name + "] Interrupted.");
        } finally {
            doneSignal.countDown();
        }
    }
}
