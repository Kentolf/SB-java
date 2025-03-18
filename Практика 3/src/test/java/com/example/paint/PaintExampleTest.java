package com.example.paint;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;

public class PaintExampleTest {

    @Test
    public void testMain() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        PaintExample.main(new String[] {});

        System.setOut(originalOut);
        String output = outputStream.toString();

        assertTrue(output.contains("A shape with coordinates has been drawn (10, 20)"));
    }
}