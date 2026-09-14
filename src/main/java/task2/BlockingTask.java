package task2;

public final class BlockingTask {

    private BlockingTask() { }

    public static BlockingTaskResult execute(TaskParams params) {
        long start = System.nanoTime();

        // A little CPU-bound
        long checksum = 0;
        for (int i = 0; i < params.workUnits(); i++) {
            checksum += (long) i * params.id();
        }

        // I/O blocking
        try {
            Thread.sleep(params.delayMs());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new BlockingTaskResult(params.id(), 0, checksum);
        }

        long elapsed = (System.nanoTime() - start) / 1_000_000;
        return new BlockingTaskResult(params.id(), elapsed, checksum);
    }
}
