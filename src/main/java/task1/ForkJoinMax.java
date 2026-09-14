package task1;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public final class ForkJoinMax {

    private ForkJoinMax() { }

    public static final class MaxTask extends RecursiveTask<Integer> {

        private final int[] arr;
        private final int from;
        private final int to;
        private final int threshold;

        public MaxTask(int[] arr, int from, int to, int threshold) {
            this.arr = arr;
            this.from = from;
            this.to = to;
            this.threshold = threshold;
        }

        @Override
        protected Integer compute() {
            if (to - from <= threshold) {
                int localMax = arr[from];

                for (int i = from + 1; i < to; i++) {
                    if (arr[i] > localMax) {
                        localMax = arr[i];
                    }
                }

                return localMax;
            }

            int mid = (from + to) >>> 1;
            MaxTask left = new MaxTask(arr, from, mid, threshold);
            MaxTask right = new MaxTask(arr, mid, to, threshold);

            left.fork();
            int rightResult = right.compute();
            int leftResult = left.join();

            return Math.max(leftResult, rightResult);
        }
    }

    public static int findMax(int[] arr, int threshold) {
        if (arr == null || arr.length == 0) {
            throw new IllegalArgumentException("Mảng rỗng");
        }

        ForkJoinPool pool = new ForkJoinPool();

        try {
            return pool.invoke(new MaxTask(arr, 0, arr.length, threshold));
        } finally {
            pool.shutdown();
        }
    }
}
