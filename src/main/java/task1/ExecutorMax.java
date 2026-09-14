package task1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class ExecutorMax {

    private ExecutorMax() { }

    public static int findMax(int[] arr, int numThreads) throws InterruptedException, ExecutionException {
        if (arr == null || arr.length == 0) {
            throw new IllegalArgumentException("Empty array");
        }

        int length = arr.length;
        int chunkSize = (length + numThreads - 1) / numThreads;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        try {
            List<Future<Integer>> futures = new ArrayList<>();

            for (int i = 0; i < numThreads; i++) {
                int from = i * chunkSize;
                int to = Math.min(from + chunkSize, length);

                if (from >= to) break;

                Callable<Integer> task = () -> {
                    int localMax = arr[from];

                    for (int j = from + 1; j < to; j++) {
                        if (arr[j] > localMax) {
                            localMax = arr[j];
                        }
                    }

                    return localMax;
                };

                futures.add(executor.submit(task));
            }

            int globalMax = Integer.MIN_VALUE;

            for (Future<Integer> f : futures) {
                int localMax = f.get();

                if (localMax > globalMax) {
                    globalMax = localMax;
                }
            }

            return globalMax;

        } finally {
            executor.shutdown();
        }
    }
}
