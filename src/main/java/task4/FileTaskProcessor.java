package task4;

/**
 * Обработка задачи в зависимости от типа.
 * Длительность зависит от size.
 */
public final class FileTaskProcessor {

    private FileTaskProcessor() { }

    public static void process(FileTask task) throws InterruptedException {
        // Время обработки зависит от размера
        long processingMs = task.size(); // 10..100 мс
        Thread.sleep(processingMs);
    }
}
