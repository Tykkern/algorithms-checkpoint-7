package kt7;

/**
 * Задача 6. Максимальный XOR непрерывного фрагмента.
 */
public final class MaxXorSegment {
    private static final int BITS = 30;

    private MaxXorSegment() {
    }

    /**
     * XOR фрагмента a[l..r] равен p[r + 1] ^ p[l], где p - префиксные XOR
     * (p[0] = 0). Значит, ответ - максимум p[i] ^ p[j] по парам префиксов.
     *
     * Префиксы складываются в двоичный бор по битам от старшего к младшему.
     * Для очередного префикса идём по бору и на каждом бите стараемся свернуть
     * в противоположную сторону: единица в старшем бите важнее всех младших
     * вместе, поэтому жадный выбор даёт максимум. Затем префикс сам
     * добавляется в бор.
     *
     * O(30 n) по времени, бор до 30 (n + 1) + 1 узлов, около 48 МБ при n = 200 000.
     */
    public static int maxXor(int[] numbers) {
        // Потомки узла v: child[2v] по биту 0 и child[2v + 1] по биту 1, 0 значит «нет».
        int[] child = new int[2 * (BITS * (numbers.length + 1) + 1)];
        int nodes = 1;
        nodes = insert(child, nodes, 0);

        int prefix = 0;
        int best = 0;
        for (int number : numbers) {
            prefix ^= number;
            int node = 0;
            int xor = 0;
            for (int b = BITS - 1; b >= 0; b--) {
                int bit = (prefix >> b) & 1;
                if (child[2 * node + (bit ^ 1)] != 0) {
                    xor |= 1 << b;
                    node = child[2 * node + (bit ^ 1)];
                } else {
                    node = child[2 * node + bit];
                }
            }
            best = Math.max(best, xor);
            nodes = insert(child, nodes, prefix);
        }
        return best;
    }

    // Добавляет value в бор и возвращает новое число узлов.
    private static int insert(int[] child, int nodes, int value) {
        int node = 0;
        for (int b = BITS - 1; b >= 0; b--) {
            int slot = 2 * node + ((value >> b) & 1);
            if (child[slot] == 0)
                child[slot] = nodes++;
            node = child[slot];
        }
        return nodes;
    }
}
