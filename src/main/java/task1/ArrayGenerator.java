package task1;

import java.util.Random;

public final class ArrayGenerator {

    private ArrayGenerator() { }

    public static int[] generate(int size, long seed) {
        Random random = new Random(seed);
        int[] arr = new int[size];

        for (int i = 0; i < size; i++) {
            arr[i] = random.nextInt();
        }

        return arr;
    }
}
