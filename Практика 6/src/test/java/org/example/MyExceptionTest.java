package org.example;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class MyExceptionTest {

    @Test
    public void multiplication_withEvenNumbers_returnsProduct() throws MyException {
        int result = Multiplication.multiplication(4, 6);
        assertEquals(result, 24);
    }

    @Test(
            expectedExceptions = MyException.class,
            expectedExceptionsMessageRegExp = "The first number is odd: 3")
    public void multiplication_withFirstOddNumber_throwsException() throws MyException {
        Multiplication.multiplication(3, 4);
    }

    @Test(
            expectedExceptions = MyException.class,
            expectedExceptionsMessageRegExp = "The second number is odd: 5")
    public void multiplication_withSecondOddNumber_throwsException() throws MyException {
        Multiplication.multiplication(4, 5);
    }

    @Test(
            expectedExceptions = MyException.class,
            expectedExceptionsMessageRegExp = "Both numbers are odd: 7 and 9")
    public void multiplication_withBothOddNumbers_throwsException() throws MyException {
        Multiplication.multiplication(7, 9);
    }
}