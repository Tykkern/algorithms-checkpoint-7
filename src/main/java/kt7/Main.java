package kt7;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Запуск решения по номеру задачи: входные данные читаются из stdin
 * в формате из задания, ответ печатается в stdout.
 *
 * Пример: gradlew run -q --args="2" < input.txt
 */
public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Укажите номер задачи от 1 до 6, входные данные подаются в stdin.");
            System.exit(1);
        }

        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
        solve(Integer.parseInt(args[0]), new FastReader(System.in), out);
        out.flush();
    }

    static void solve(int task, FastReader in, PrintWriter out) throws IOException {
        switch (task) {
            case 1 -> Brackets.generate(in.nextInt()).forEach(out::println);
            case 2 -> {
                WordTrie dictionary = new WordTrie();
                int q = in.nextInt();
                for (int i = 0; i < q; i++) {
                    String operation = in.next();
                    String word = in.next();
                    switch (operation) {
                        case "ADD" -> dictionary.add(word);
                        case "REMOVE" -> dictionary.remove(word);
                        case "COUNT" -> out.println(dictionary.countPrefix(word));
                        default -> throw unknownOperation(operation);
                    }
                }
            }
            case 3 -> {
                int[] pair = TwoSingles.find(in.nextInts(in.nextInt()));
                out.println(pair[0] + " " + pair[1]);
            }
            case 4 -> {
                int n = in.nextInt();
                int[][] forbidden = new int[in.nextInt()][];
                for (int i = 0; i < forbidden.length; i++)
                    forbidden[i] = in.nextInts(2);
                out.println(Queens.count(n, forbidden));
            }
            case 5 -> {
                WordTrie dictionary = new WordTrie();
                int q = in.nextInt();
                for (int i = 0; i < q; i++) {
                    String operation = in.next();
                    String word = in.next();
                    switch (operation) {
                        case "ADD" -> dictionary.add(word);
                        case "REMOVE" -> dictionary.remove(word);
                        case "KTH" -> {
                            String found = dictionary.kth(word, in.nextInt());
                            out.println(found == null ? "NONE" : found);
                        }
                        default -> throw unknownOperation(operation);
                    }
                }
            }
            case 6 -> out.println(MaxXorSegment.maxXor(in.nextInts(in.nextInt())));
            default -> throw new IllegalArgumentException("Нет задачи с номером " + task + ", есть 1-6.");
        }
    }

    private static IOException unknownOperation(String operation) {
        return new IOException("Неизвестная операция: " + operation + ".");
    }
}
