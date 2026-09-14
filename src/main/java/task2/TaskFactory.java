package task2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class TaskFactory {

    private TaskFactory() { }

    public static List<TaskParams> createTasks(
            int count, long seed, long minDelayMs, long maxDelayMs) {

        Random random = new Random(seed);
        List<TaskParams> tasks = new ArrayList<>(count);

        for (int id = 0; id < count; id++) {
            long delay = minDelayMs + (long) (random.nextDouble() * (maxDelayMs - minDelayMs));
            int workUnits = 1_000 + random.nextInt(9_000); // 1000..9999
            tasks.add(new TaskParams(id, delay, workUnits));
        }

        return tasks;
    }
}
