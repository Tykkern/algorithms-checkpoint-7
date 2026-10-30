package kt7;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Быстрое чтение чисел и слов из потока. Scanner на миллионе чисел работает
 * заметно медленнее, поэтому читаем байты блоками и разбираем их вручную.
 */
final class FastReader {
    private final InputStream in;
    private final byte[] buffer = new byte[1 << 16];
    private int length;
    private int position;

    FastReader(InputStream in) {
        this.in = in;
    }

    long nextLong() throws IOException {
        int c = skipSpaces();
        boolean negative = c == '-';
        if (negative)
            c = read();
        if (c < '0' || c > '9')
            throw new IOException("Ожидалось число, а встретился символ '" + (char) c + "'.");

        long result = 0;
        while (c >= '0' && c <= '9') {
            result = result * 10 + (c - '0');
            c = read();
        }
        return negative ? -result : result;
    }

    int nextInt() throws IOException {
        return Math.toIntExact(nextLong());
    }

    int[] nextInts(int count) throws IOException {
        int[] values = new int[count];
        for (int i = 0; i < count; i++)
            values[i] = nextInt();
        return values;
    }

    // Следующее слово до пробела или перевода строки.
    String next() throws IOException {
        int c = skipSpaces();
        StringBuilder word = new StringBuilder();
        while (c > ' ') {
            word.append((char) c);
            c = read();
        }
        return word.toString();
    }

    private int skipSpaces() throws IOException {
        int c = read();
        while (c != -1 && c <= ' ')
            c = read();
        if (c == -1)
            throw new EOFException("Входные данные закончились раньше, чем ожидалось.");
        return c;
    }

    private int read() throws IOException {
        if (position == length) {
            length = in.read(buffer, 0, buffer.length);
            position = 0;
            if (length <= 0) {
                length = 0;
                return -1;
            }
        }
        return buffer[position++];
    }
}
