package kt7;

/**
 * Задача 4. Число расстановок n ферзей на доске с запрещёнными клетками.
 */
public final class Queens {

    private Queens() {
    }

    /**
     * Перебор с возвратом по строкам: в каждую строку ставится ровно один
     * ферзь. Занятые столбцы и диагонали хранятся битовыми масками, поэтому
     * свободные клетки строки считаются одной операцией:
     * ~(столбцы | диагонали | запрещённые). При переходе на следующую строку
     * маска диагоналей «\» сдвигается на 1 в сторону больших столбцов, маска
     * диагоналей «/» в сторону меньших: та же диагональ строкой ниже проходит
     * через соседний столбец.
     *
     * Без запрещённых клеток при n = 14 получается 365 596 расстановок,
     * перебор на масках укладывается в доли секунды.
     *
     * @param forbidden пары {row, column}, нумерация с 1
     */
    public static long count(int n, int[][] forbidden) {
        int[] blocked = new int[n];
        for (int[] cell : forbidden)
            blocked[cell[0] - 1] |= 1 << (cell[1] - 1);
        return place(0, n, 0, 0, 0, blocked);
    }

    private static long place(int row, int n, int columns, int downRight, int downLeft, int[] blocked) {
        if (row == n)
            return 1;

        int full = (1 << n) - 1;
        int free = full & ~(columns | downRight | downLeft | blocked[row]);
        long count = 0;
        while (free != 0) {
            int bit = free & -free;
            free -= bit;
            count += place(row + 1, n, columns | bit, ((downRight | bit) << 1) & full, (downLeft | bit) >> 1, blocked);
        }
        return count;
    }
}
