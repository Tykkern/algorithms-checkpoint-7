package kt7;

/**
 * Задача 3. Два числа, которые встречаются по одному разу.
 */
public final class TwoSingles {

    private TwoSingles() {
    }

    /**
     * XOR всех чисел равен x ^ y, где x и y - искомые: парные числа
     * взаимно уничтожаются. x и y различны, значит в x ^ y есть единичный бит,
     * в котором они расходятся. Берём младший такой бит и делим массив на две
     * группы по нему: x и y попадут в разные группы, а оба экземпляра любой
     * пары - в одну. XOR первой группы даёт одно из чисел, второе получается
     * как (x ^ y) ^ первое.
     *
     * O(n) по времени, O(1) по памяти.
     *
     * @return два числа по возрастанию
     */
    public static int[] find(int[] numbers) {
        int xor = 0;
        for (int number : numbers)
            xor ^= number;

        int bit = xor & -xor;
        int first = 0;
        for (int number : numbers)
            if ((number & bit) != 0)
                first ^= number;
        int second = xor ^ first;
        return first < second ? new int[] {first, second} : new int[] {second, first};
    }
}
