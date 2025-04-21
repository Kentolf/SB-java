package org.example;

public class GenericCollection<T> {

    private Object[] elements;
    private int size;

    public GenericCollection() {
        
        elements = new Object[10];
        size = 0;
    }

    public void add(T element) {

        capacity();
        elements[size++] = element;
    }

    public void remove(int index) {

        if (index < 0 || index >= size) {
            System.err.println("Invalid index: " + index);
            return;
        }

        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }

        elements[--size] = null;
    }

    public T get(int index) {

        if (index < 0 || index >= size) {
            System.err.println("Invalid index: " + index);
            return null;
        }

        return (T) elements[index];
    }

    public int size() {
        return size;
    }

    public void printAll() {

        for (int i = 0; i < size; i++) {
            System.out.println(elements[i]);
        }
    }

    private void capacity() {

        if (size == elements.length) {
            Object[] newArray = new Object[elements.length + 1];

            for (int i = 0; i < elements.length; i++) {
                newArray[i] = elements[i];
            }

            elements = newArray;
        }
    }
}
