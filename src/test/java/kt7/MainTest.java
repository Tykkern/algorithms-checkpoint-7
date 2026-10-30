package kt7;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Проверка чтения входных данных и формата вывода для каждой задачи.
 */
class MainTest {

    private static String run(int task, String input) throws IOException {
        StringWriter output = new StringWriter();
        PrintWriter out = new PrintWriter(output);
        FastReader in = new FastReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        Main.solve(task, in, out);
        out.flush();
        return output.toString().replace("\r\n", "\n");
    }

    @Test
    void task1() throws IOException {
        assertEquals("(())\n()()\n", run(1, "2\n"));
    }

    @Test
    void task2() throws IOException {
        assertEquals("2\n1\n0\n", run(2, "6\nADD apple\nADD app\nCOUNT ap\nREMOVE app\nCOUNT app\nCOUNT b\n"));
    }

    @Test
    void task3() throws IOException {
        assertEquals("3 5\n", run(3, "6\n1 2 1 3 2 5\n"));
    }

    @Test
    void task4() throws IOException {
        assertEquals("2\n", run(4, "4 0\n"));
        assertEquals("1\n", run(4, "4 1\n1 2\n"));
    }

    @Test
    void task5() throws IOException {
        String input = "7\nADD ab\nADD a\nADD abc\nKTH a 2\nKTH a 4\nREMOVE ab\nKTH ab 1\n";
        assertEquals("ab\nNONE\nabc\n", run(5, input));
    }

    @Test
    void task6() throws IOException {
        assertEquals("7\n", run(6, "4\n1 2 3 4\n"));
    }

    @Test
    void windowsLineEndings() throws IOException {
        assertEquals("1\n", run(2, "2\r\nADD word\r\nCOUNT wo\r\n"));
    }

    @Test
    void unknownOperationThrows() {
        assertThrows(IOException.class, () -> run(2, "1\nDELETE word\n"));
        assertThrows(IOException.class, () -> run(5, "1\nCOUNT word\n"));
    }

    @Test
    void truncatedInputThrows() {
        assertThrows(EOFException.class, () -> run(3, "4\n1 1 2\n"));
    }

    @Test
    void unknownTaskThrows() {
        assertThrows(IllegalArgumentException.class, () -> run(7, ""));
    }
}
