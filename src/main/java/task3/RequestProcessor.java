package task3;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public final class RequestProcessor {

    private RequestProcessor() { }

    public static CompletableFuture<Result> process(
            Request request, ExecutorService executor) {

        return CompletableFuture
                // ─── Stage 1: Validation (async, run in executor) ───
                .supplyAsync(() -> validate(request), executor)

                // ─── Stage 2: Calculation (new async → need thenCompose) ───
                .thenCompose(validated -> calculateAsync(validated, executor))

                // ─── Stage 3: Transformation (sync → thenApply) ───
                .thenApply(value -> Result.success(request.id(), value))

                // ─── Timeout for all pipeline ───
                .orTimeout(2, TimeUnit.SECONDS)

                // ─── Error handling ───
                .exceptionally(error -> Result.failure(request.id(), error.getMessage()));
    }

    // ──────────────────────────────────────────────────────────────
    // Stage 1: Validation — check input data
    // ──────────────────────────────────────────────────────────────
    private static Request validate(Request r) {
        if (r.operandB() == 0 && "DIV".equals(r.operation())) {
            throw new IllegalArgumentException("Division by zero");
        }
        if (!"ADD".equals(r.operation())
                && !"MUL".equals(r.operation())
                && !"DIV".equals(r.operation())) {
            throw new IllegalArgumentException("Unknown operation: " + r.operation());
        }
        return r;
    }

    // ──────────────────────────────────────────────────────────────
    // Stage 2: Calculation — simulate async I/O operation
    // ──────────────────────────────────────────────────────────────
    private static CompletableFuture<Double> calculateAsync(
            Request r, ExecutorService executor) {

        return CompletableFuture.supplyAsync(() -> {
            // Mô phỏng I/O (chờ tài nguyên ngoài)
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted during calculation", e);
            }

            return switch (r.operation()) {
                case "ADD" -> r.operandA() + r.operandB();
                case "MUL" -> r.operandA() * r.operandB();
                case "DIV" -> r.operandA() / r.operandB();
                default -> throw new IllegalArgumentException("Unknown op");
            };
        }, executor);
    }
}
