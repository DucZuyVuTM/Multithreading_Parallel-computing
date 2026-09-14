package task4;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * Потокобезопасная статистика обработки.
 * Использует AtomicInteger и ConcurrentHashMap вместо обычных счётчиков.
 */
public final class TaskStats {

    private final LongAdder totalProcessed = new LongAdder();
    private final ConcurrentHashMap<FileType, LongAdder> perType = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Boolean> processedIds = new ConcurrentHashMap<>();

    public TaskStats() {
        for (FileType t : FileType.values()) {
            perType.put(t, new LongAdder());
        }
    }

    /** Отметить задачу как обработанную. */
    public void record(FileTask task) {
        totalProcessed.increment();
        perType.get(task.type()).increment();

        // ConcurrentHashMap.putIfAbsent возвращает null, если ключа не было
        // Если вернул не null — задача с таким ID уже обрабатывалась!
        Boolean previous = processedIds.putIfAbsent(task.id(), Boolean.TRUE);
        if (previous != null) {
            throw new IllegalStateException(
                    "Duplicate processing detected: id=" + task.id());
        }
    }

    public long total() {
        return totalProcessed.sum();
    }

    public long countByType(FileType type) {
        return perType.get(type).sum();
    }

    public int uniqueIdsCount() {
        return processedIds.size();
    }
}
