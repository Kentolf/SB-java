package com.example;

public class SortingAlgorithms {

    public static void bubbleSort(int[] arr) {

        if (arr == null) {
            return;
        }

        int n = arr.length;

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    public static void quickSort(int[] arr, int low, int high) {

        if (arr == null || low >= high) {
            return;
        }

        int pivot = findPivot(arr, low, high);

        quickSort(arr, low, pivot - 1);

        quickSort(arr, pivot + 1, high);
    }

    private static int findPivot(int[] arr, int low, int high) {

        int pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        int temp = arr[i + 1];

        arr[i + 1] = arr[high];
        arr[high] = temp;

        return i + 1;
    }
}