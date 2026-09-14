package task3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Send multiple request at once.
 * Main thread does not wait each request.
 */
public final class Benchmark {

    private static final int REQUEST_COUNT = 20;

    private Benchmark() { }

    public static void main(String[] args) throws Exception {

        System.out.println("=== Task 3 -- Async request processing ===");
        System.out.println("Request amount: " + REQUEST_COUNT);
        System.out.println();

        ExecutorService executor = Executors.newFixedThreadPool(4);

        try {
            List<Request> requests = createRequests();

            long start = System.nanoTime();

            List<CompletableFuture<Result>> futures = new ArrayList<>();
            for (Request r : requests) {
                futures.add(RequestProcessor.process(r, executor));
            }

            System.out.println("[OK] All " + REQUEST_COUNT + " request sent for "
                    + (System.nanoTime() - start) / 1_000_000 + " ms");
            System.out.println("Main thread is free and could do another work.");
            System.out.println();

            // ─── Wait all pipelines till completed (only once) ───
            CompletableFuture<Void> all = CompletableFuture.allOf(
                    futures.toArray(new CompletableFuture[0]));
            all.join();

            long elapsed = (System.nanoTime() - start) / 1_000_000;

            System.out.println("=== Result ===");
            int successCount = 0;
            int failureCount = 0;
            for (CompletableFuture<Result> f : futures) {
                Result r = f.get();
                System.out.println("  " + r.formatted());
                if (r.success()) successCount++;
                else failureCount++;
            }

            System.out.println();
            System.out.println("Success: " + successCount);
            System.out.println("Error: " + failureCount);
            System.out.println("Total time: " + elapsed + " ms");

        } finally {
            // ─── Graceful shutdown ───
            executor.shutdown();
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
            System.out.println("Executor shut down correctly.");
        }
    }

    /**
     * Create request list, included successful and error request
     * for exception handling.
     */
    private static List<Request> createRequests() {
        List<Request> list = new ArrayList<>();
        for (int i = 1; i <= REQUEST_COUNT; i++) {
            double a = 10 + i;
            double b = 2 + (i % 5);
            String op;
            if (i % 3 == 0) op = "MUL";
            else if (i % 3 == 1) op = "ADD";
            else op = "DIV";

            if (i == 7) {
                list.add(Request.of(i, a, 0, "DIV"));      // Division by 0
            } else if (i == 13) {
                list.add(Request.of(i, a, b, "UNKNOWN"));  // unknown op
            } else {
                list.add(Request.of(i, a, b, op));
            }
        }
        return list;
    }
}
