package kt7;

import java.util.Arrays;

/**
 * Задачи 2 и 5. Префиксное дерево (бор) над словами из строчных латинских букв.
 *
 * Узлы хранятся в массивах, а не объектами: при суммарной длине слов
 * до миллиона узлов тоже до миллиона, и объект с массивом из 26 ссылок
 * на каждый из них занял бы в несколько раз больше памяти.
 */
public final class WordTrie {
    private static final int LETTERS = 26;

    // Потомок узла v по букве c лежит в next[v * 26 + c], 0 значит «нет» (0 - корень).
    private int[] next = new int[LETTERS * 1024];
    // Сколько слов словаря проходит через узел, включая оканчивающиеся в нём.
    private int[] passing = new int[1024];
    // Оканчивается ли в узле слово словаря.
    private boolean[] ending = new boolean[1024];
    private int nodes = 1;

    /**
     * Добавляет слово, которого ещё нет в словаре. O(длина слова).
     */
    public void add(String word) {
        int node = 0;
        passing[0]++;
        for (int i = 0; i < word.length(); i++) {
            int slot = node * LETTERS + letter(word.charAt(i));
            if (next[slot] == 0) {
                // Отдельной строкой: в next[slot] = newNode() ссылка на массив берётся
                // до вызова, и после расширения массива запись ушла бы в старую копию.
                int created = newNode();
                next[slot] = created;
            }
            node = next[slot];
            passing[node]++;
        }
        ending[node] = true;
    }

    /**
     * Удаляет слово, которое есть в словаре. Узлы остаются, у них только
     * уменьшаются счётчики, при повторном добавлении они пригодятся снова.
     * O(длина слова).
     */
    public void remove(String word) {
        int node = 0;
        passing[0]--;
        for (int i = 0; i < word.length(); i++) {
            node = next[node * LETTERS + letter(word.charAt(i))];
            passing[node]--;
        }
        ending[node] = false;
    }

    /**
     * Задача 2. Сколько слов начинается с prefix: счётчик узла, в который
     * ведёт префикс. O(длина префикса).
     */
    public int countPrefix(String prefix) {
        int node = find(prefix);
        return node < 0 ? 0 : passing[node];
    }

    /**
     * Задача 5. k-е по порядку слово среди начинающихся с prefix или null.
     *
     * От узла префикса спускаемся вниз. Слово, оканчивающееся в текущем узле,
     * меньше всех более длинных, поэтому оно идёт первым. Дальше потомки
     * перебираются по алфавиту: если в поддереве буквы слов меньше k, всё
     * поддерево пропускается целиком и k уменьшается на его счётчик, иначе
     * спускаемся в него. O(26 * длина слова) на запрос.
     */
    public String kth(String prefix, int k) {
        int node = find(prefix);
        if (node < 0 || passing[node] < k)
            return null;

        StringBuilder word = new StringBuilder(prefix);
        while (true) {
            if (ending[node]) {
                if (k == 1)
                    return word.toString();
                k--;
            }
            for (int c = 0; c < LETTERS; c++) {
                int child = next[node * LETTERS + c];
                if (child == 0)
                    continue;
                if (passing[child] >= k) {
                    word.append((char) ('a' + c));
                    node = child;
                    break;
                }
                k -= passing[child];
            }
        }
    }

    private int find(String prefix) {
        int node = 0;
        for (int i = 0; i < prefix.length() && node >= 0; i++) {
            int child = next[node * LETTERS + letter(prefix.charAt(i))];
            node = child == 0 ? -1 : child;
        }
        return node;
    }

    private int newNode() {
        if (nodes == passing.length) {
            next = Arrays.copyOf(next, next.length * 2);
            passing = Arrays.copyOf(passing, passing.length * 2);
            ending = Arrays.copyOf(ending, ending.length * 2);
        }
        return nodes++;
    }

    private static int letter(char c) {
        if (c < 'a' || c > 'z')
            throw new IllegalArgumentException("Допустимы только строчные латинские буквы, а встретился символ '" + c + "'.");
        return c - 'a';
    }
}
