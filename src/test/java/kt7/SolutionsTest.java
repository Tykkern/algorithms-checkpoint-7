package kt7;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class SolutionsTest {

    private static final Random RANDOM = new Random(1);

    private static String randomWord(int maxLength, int letters) {
        StringBuilder word = new StringBuilder();
        int length = 1 + RANDOM.nextInt(maxLength);
        for (int i = 0; i < length; i++)
            word.append((char) ('a' + RANDOM.nextInt(letters)));
        return word.toString();
    }

    @Nested
    class BracketsTest {

        // Все строки из 2n скобок в лексикографическом порядке, из них правильные.
        private List<String> bruteForce(int n) {
            List<String> result = new ArrayList<>();
            for (int mask = 0; mask < 1 << (2 * n); mask++) {
                StringBuilder line = new StringBuilder();
                int balance = 0;
                for (int i = 2 * n - 1; i >= 0 && balance >= 0; i--) {
                    boolean close = (mask >> i & 1) == 1;
                    line.append(close ? ')' : '(');
                    balance += close ? -1 : 1;
                }
                if (balance == 0 && line.length() == 2 * n)
                    result.add(line.toString());
            }
            return result;
        }

        @Test
        void smallCases() {
            assertEquals(List.of("()"), Brackets.generate(1));
            assertEquals(List.of("((()))", "(()())", "(())()", "()(())", "()()()"), Brackets.generate(3));
        }

        @Test
        void matchesBruteForce() {
            for (int n = 1; n <= 8; n++)
                assertEquals(bruteForce(n), Brackets.generate(n));
        }

        @Test
        @Timeout(5)
        void maxSizeIsCatalanNumber() {
            List<String> all = Brackets.generate(10);
            assertEquals(16_796, all.size());
            assertEquals("(((((((((())))))))))", all.get(0));
            assertEquals("()()()()()()()()()()", all.get(all.size() - 1));
        }
    }

    @Nested
    class WordTrieTest {

        @Test
        void countAfterAddAndRemove() {
            WordTrie dictionary = new WordTrie();
            dictionary.add("apple");
            dictionary.add("app");
            dictionary.add("banana");
            assertEquals(2, dictionary.countPrefix("ap"));
            assertEquals(2, dictionary.countPrefix("app"));
            assertEquals(1, dictionary.countPrefix("appl"));
            assertEquals(0, dictionary.countPrefix("apples"));
            assertEquals(0, dictionary.countPrefix("c"));

            dictionary.remove("app");
            assertEquals(1, dictionary.countPrefix("app"));
            dictionary.add("app");
            assertEquals(2, dictionary.countPrefix("a"));
        }

        @Test
        void kthPutsShorterWordFirst() {
            WordTrie dictionary = new WordTrie();
            for (String word : new String[] {"ab", "a", "abc", "b", "aa"})
                dictionary.add(word);
            assertEquals("a", dictionary.kth("a", 1));
            assertEquals("aa", dictionary.kth("a", 2));
            assertEquals("ab", dictionary.kth("a", 3));
            assertEquals("abc", dictionary.kth("a", 4));
            assertNull(dictionary.kth("a", 5));
            assertEquals("abc", dictionary.kth("abc", 1));
            assertNull(dictionary.kth("abcd", 1));

            dictionary.remove("ab");
            assertEquals("abc", dictionary.kth("ab", 1));
            assertNull(dictionary.kth("ab", 2));
        }

        @Test
        void randomOperationsMatchTreeSet() {
            for (int test = 0; test < 200; test++) {
                WordTrie dictionary = new WordTrie();
                TreeSet<String> words = new TreeSet<>();
                for (int operation = 0; operation < 60; operation++) {
                    String word = randomWord(4, 3);
                    int kind = RANDOM.nextInt(4);
                    if (kind == 0 && !words.contains(word)) {
                        words.add(word);
                        dictionary.add(word);
                    } else if (kind == 1 && words.contains(word)) {
                        words.remove(word);
                        dictionary.remove(word);
                    } else if (kind == 2) {
                        long expected = words.stream().filter(w -> w.startsWith(word)).count();
                        assertEquals(expected, dictionary.countPrefix(word));
                    } else {
                        int k = 1 + RANDOM.nextInt(6);
                        List<String> matching = words.stream().filter(w -> w.startsWith(word)).toList();
                        String expected = k <= matching.size() ? matching.get(k - 1) : null;
                        assertEquals(expected, dictionary.kth(word, k));
                    }
                }
            }
        }

        @Test
        @Timeout(5)
        void maxSize() {
            // 20 000 слов по 50 букв: суммарная длина миллион, почти миллион узлов.
            WordTrie dictionary = new WordTrie();
            List<String> words = new ArrayList<>();
            Set<String> unique = new HashSet<>();
            while (words.size() < 20_000) {
                String word = randomFixedWord(50);
                if (unique.add(word)) {
                    words.add(word);
                    dictionary.add(word);
                }
            }
            Collections.sort(words);

            for (int query = 0; query < 180_000; query++) {
                String prefix = String.valueOf((char) ('a' + RANDOM.nextInt(26)));
                int first = lowerBound(words, prefix);
                int count = lowerBound(words, prefix + '{') - first;
                if (query % 2 == 0) {
                    assertEquals(count, dictionary.countPrefix(prefix));
                } else {
                    int k = 1 + RANDOM.nextInt(count + 1);
                    String expected = k <= count ? words.get(first + k - 1) : null;
                    assertEquals(expected, dictionary.kth(prefix, k));
                }
            }
        }

        private String randomFixedWord(int length) {
            StringBuilder word = new StringBuilder();
            for (int i = 0; i < length; i++)
                word.append((char) ('a' + RANDOM.nextInt(26)));
            return word.toString();
        }

        // Первый индекс в отсортированном списке, где слово не меньше key.
        private int lowerBound(List<String> sorted, String key) {
            int position = Collections.binarySearch(sorted, key);
            return position >= 0 ? position : -position - 1;
        }
    }

    @Nested
    class TwoSinglesTest {

        private int[] randomCase(int pairs) {
            Set<Integer> used = new HashSet<>();
            while (used.size() < pairs + 2)
                used.add(RANDOM.nextInt(1_000_000_001));
            List<Integer> values = new ArrayList<>(used);
            List<Integer> numbers = new ArrayList<>();
            for (int i = 0; i < values.size(); i++) {
                numbers.add(values.get(i));
                if (i >= 2)
                    numbers.add(values.get(i));
            }
            Collections.shuffle(numbers, RANDOM);
            return numbers.stream().mapToInt(Integer::intValue).toArray();
        }

        private int[] bruteForce(int[] numbers) {
            Map<Integer, Integer> counts = new HashMap<>();
            for (int number : numbers)
                counts.merge(number, 1, Integer::sum);
            return counts.entrySet().stream()
                    .filter(entry -> entry.getValue() == 1)
                    .mapToInt(Map.Entry::getKey)
                    .sorted()
                    .toArray();
        }

        @Test
        void basicCases() {
            assertArrayEquals(new int[] {3, 5}, TwoSingles.find(new int[] {1, 2, 1, 3, 2, 5}));
            assertArrayEquals(new int[] {0, 1_000_000_000}, TwoSingles.find(new int[] {1_000_000_000, 0}));
            assertArrayEquals(new int[] {0, 7}, TwoSingles.find(new int[] {7, 4, 0, 4}));
        }

        @Test
        void randomArraysMatchBruteForce() {
            for (int test = 0; test < 500; test++) {
                int[] numbers = randomCase(RANDOM.nextInt(20));
                assertArrayEquals(bruteForce(numbers), TwoSingles.find(numbers));
            }
        }

        @Test
        @Timeout(5)
        void maxSize() {
            int[] numbers = randomCase(99_999);
            assertEquals(200_000, numbers.length);
            assertArrayEquals(bruteForce(numbers), TwoSingles.find(numbers));
        }
    }

    @Nested
    class QueensTest {

        // Перебор всех перестановок: в строке r ферзь стоит в столбце column[r].
        private long bruteForce(int n, boolean[][] forbidden, int[] column, int row) {
            if (row == n)
                return 1;
            long count = 0;
            for (int c = 0; c < n; c++) {
                boolean ok = !forbidden[row][c];
                for (int r = 0; r < row && ok; r++)
                    ok = column[r] != c && Math.abs(column[r] - c) != row - r;
                if (ok) {
                    column[row] = c;
                    count += bruteForce(n, forbidden, column, row + 1);
                }
            }
            return count;
        }

        @Test
        void knownCountsWithoutForbiddenCells() {
            long[] expected = {1, 0, 0, 2, 10, 4, 40, 92};
            for (int n = 1; n <= 8; n++)
                assertEquals(expected[n - 1], Queens.count(n, new int[0][]));
        }

        @Test
        void forbiddenCellsRemoveSolutions() {
            // У доски 4 x 4 два решения: столбцы 2 4 1 3 и 3 1 4 2.
            assertEquals(1, Queens.count(4, new int[][] {{1, 2}}));
            assertEquals(0, Queens.count(4, new int[][] {{1, 2}, {1, 3}}));
            assertEquals(0, Queens.count(1, new int[][] {{1, 1}}));
        }

        @Test
        void randomBoardsMatchBruteForce() {
            for (int test = 0; test < 200; test++) {
                int n = 1 + RANDOM.nextInt(7);
                boolean[][] forbidden = new boolean[n][n];
                List<int[]> cells = new ArrayList<>();
                for (int r = 0; r < n; r++)
                    for (int c = 0; c < n; c++)
                        if (RANDOM.nextInt(6) == 0) {
                            forbidden[r][c] = true;
                            cells.add(new int[] {r + 1, c + 1});
                        }
                assertEquals(bruteForce(n, forbidden, new int[n], 0), Queens.count(n, cells.toArray(new int[0][])));
            }
        }

        @Test
        @Timeout(5)
        void maxBoard() {
            assertEquals(365_596, Queens.count(14, new int[0][]));
        }
    }

    @Nested
    class MaxXorSegmentTest {

        private int bruteForce(int[] numbers) {
            int best = 0;
            for (int left = 0; left < numbers.length; left++) {
                int xor = 0;
                for (int right = left; right < numbers.length; right++) {
                    xor ^= numbers[right];
                    best = Math.max(best, xor);
                }
            }
            return best;
        }

        // Другой способ: ответ набирается по битам от старшего, проверка через множество префиксов.
        private int byPrefixSets(int[] numbers) {
            int[] prefixes = new int[numbers.length + 1];
            for (int i = 0; i < numbers.length; i++)
                prefixes[i + 1] = prefixes[i] ^ numbers[i];

            int best = 0;
            int mask = 0;
            for (int b = 29; b >= 0; b--) {
                mask |= 1 << b;
                Set<Integer> seen = new HashSet<>();
                for (int prefix : prefixes)
                    seen.add(prefix & mask);
                int candidate = best | 1 << b;
                for (int prefix : seen)
                    if (seen.contains(prefix ^ candidate)) {
                        best = candidate;
                        break;
                    }
            }
            return best;
        }

        @Test
        void basicCases() {
            assertEquals(7, MaxXorSegment.maxXor(new int[] {1, 2, 3, 4}));
            assertEquals(0, MaxXorSegment.maxXor(new int[] {0}));
            assertEquals(5, MaxXorSegment.maxXor(new int[] {5}));
            assertEquals(6, MaxXorSegment.maxXor(new int[] {6, 6}));
        }

        @Test
        void randomArraysMatchBruteForce() {
            for (int test = 0; test < 500; test++) {
                int[] numbers = RANDOM.ints(1 + RANDOM.nextInt(40), 0, 64).toArray();
                assertEquals(bruteForce(numbers), MaxXorSegment.maxXor(numbers));
            }
        }

        @Test
        void largeValuesMatchBruteForce() {
            for (int test = 0; test < 100; test++) {
                int[] numbers = RANDOM.ints(1 + RANDOM.nextInt(40), 0, 1 << 30).toArray();
                assertEquals(bruteForce(numbers), MaxXorSegment.maxXor(numbers));
            }
        }

        @Test
        @Timeout(10)
        void maxSizeMatchesPrefixSets() {
            int[] numbers = RANDOM.ints(200_000, 0, 1 << 30).toArray();
            assertEquals(byPrefixSets(numbers), MaxXorSegment.maxXor(numbers));

            int[] zeros = new int[200_000];
            zeros[123_456] = (1 << 30) - 1;
            assertEquals((1 << 30) - 1, MaxXorSegment.maxXor(zeros));
        }
    }
}
