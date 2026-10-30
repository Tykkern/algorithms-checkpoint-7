package kt7;

import java.util.ArrayList;
import java.util.List;

/**
 * Задача 1. Все правильные скобочные последовательности из n пар.
 */
public final class Brackets {

    private Brackets() {
    }

    /**
     * Перебор с возвратом: строка собирается слева направо, открывающую скобку
     * можно поставить, пока их меньше n, закрывающую - пока закрытых меньше
     * открытых. Так в любом префиксе закрывающих не больше открывающих, и
     * тупиковые ветки не строятся вообще. Открывающая пробуется первой,
     * а '(' меньше ')', поэтому строки получаются сразу в лексикографическом
     * порядке.
     *
     * Ответов столько, сколько число Каталана C(n), для n = 10 это 16 796.
     * Время O(C(n) * n), глубина рекурсии 2n.
     */
    public static List<String> generate(int n) {
        List<String> result = new ArrayList<>();
        build(new char[2 * n], 0, 0, 0, n, result);
        return result;
    }

    private static void build(char[] line, int position, int open, int closed, int n, List<String> result) {
        if (position == line.length) {
            result.add(new String(line));
            return;
        }
        if (open < n) {
            line[position] = '(';
            build(line, position + 1, open + 1, closed, n, result);
        }
        if (closed < open) {
            line[position] = ')';
            build(line, position + 1, open, closed + 1, n, result);
        }
    }
}
