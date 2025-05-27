package org.example;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class ShellTest {

    @Test
    public void testDateCommand() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        Command dateCommand = new DateCommand();
        dateCommand.execute();

        String expectedOutput = LocalDate.now().toString() + System.lineSeparator();
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void testTimeCommand() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        Command timeCommand = new TimeCommand();
        timeCommand.execute();

        String actualOutput = outputStream.toString().trim();
        String actualTime = actualOutput.split("\\.")[0];

        String expectedTime = LocalTime.now().toString().split("\\.")[0];

        assertEquals(expectedTime, actualTime);
    }

    @Test
    public void testPwdCommand() {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        Command pwdCommand = new PwdCommand();
        pwdCommand.execute();

        String expectedOutput = System.getProperty("user.dir") + System.lineSeparator();
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void testHelpCommand() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        Command helpCommand = new HelpCommand();
        helpCommand.execute();

        String actualOutput = outputStream.toString();

        String expectedOutput =
                "Доступные команды:\n" +
                        "date – выводит текущую дату\n" +
                        "time – выводит текущее время\n" +
                        "pwd – выводит текущий рабочий каталог\n" +
                        "exit – завершает работу приложения\n" +
                        "help – выводит список доступных команд с их описанием\n";


        String normalizedActualOutput = actualOutput.replaceAll("\\r\\n", "\n").trim();
        String normalizedExpectedOutput = expectedOutput.replaceAll("\\r\\n", "\n").trim();

        String[] actualLines = normalizedActualOutput.split("\n");
        String[] expectedLines = normalizedExpectedOutput.split("\n");

        assertEquals(expectedLines.length, actualLines.length, "The number of lines does not match");

        for (int i = 0; i < expectedLines.length; i++) {
            assertEquals(expectedLines[i].trim(), actualLines[i].trim(), "Line " + (i + 1) + " does not match");
        }
    }

    @Test
    public void testExitCommand() {
        Command exitCommand = new ExitCommand();

        assertThrows(UnsupportedOperationException.class, () -> {
            System.setSecurityManager(new SecurityManager() {
                @Override
                public void checkExit(int status) {
                    throw new SecurityException("System.exit() called");
                }
            });

            exitCommand.execute();
        });
    }
}