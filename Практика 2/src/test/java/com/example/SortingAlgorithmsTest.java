package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SortingAlgorithmsTest {

    @Test
    public void testBubbleSort() {
        int[] arr = {64, 34, 25, 12, 22, 11, 90};
        int[] expected = {11, 12, 22, 25, 34, 64, 90};
        SortingAlgorithms.bubbleSort(arr);
        assertArrayEquals(expected, arr);
    }

    @Test
    public void testQuickSort() {
        int[] arr = {64, 34, 25, 12, 22, 11, 90};
        int[] expected = {11, 12, 22, 25, 34, 64, 90};
        SortingAlgorithms.quickSort(arr, 0, arr.length - 1);
        assertArrayEquals(expected, arr);
    }
}