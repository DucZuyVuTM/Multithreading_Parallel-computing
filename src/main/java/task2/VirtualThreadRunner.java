package task2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class VirtualThreadRunner {

    private VirtualThreadRunner() { }

    public static List<BlockingTaskResult> run(List<TaskParams> tasks)
            throws InterruptedException, ExecutionException {

        // Mỗi task được cấp 1 virtual thread riêng — không giới hạn pool
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        try {
            List<Future<BlockingTaskResult>> futures = new ArrayList<>(tasks.size());

            for (TaskParams params : tasks) {
                Callable<BlockingTaskResult> task = () -> BlockingTask.execute(params);
                futures.add(executor.submit(task));
            }

            List<BlockingTaskResult> results = new ArrayList<>(tasks.size());
            for (Future<BlockingTaskResult> f : futures) {
                results.add(f.get());
            }
            return results;

        } finally {
            executor.shutdown();
        }
    }
}
