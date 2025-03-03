package com.example.paint;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

public class PaintExampleTest {

    @Test

    public void testMain() {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PrintStream originalOut = System.out;

        System.setOut(new PrintStream(outputStream));

        PaintExample.main(new String[]{});

        System.setOut(originalOut);

        String output = outputStream.toString();
        
        assertTrue(output.contains("Нарисована фигура с координатами (10, 20)"));
    }
}