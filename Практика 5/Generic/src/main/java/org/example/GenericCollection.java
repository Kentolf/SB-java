package org.example;

import java.util.ArrayList;

public class GenericCollection<T> {
    private ArrayList<T> collection;

    public GenericCollection() {
        this.collection = new ArrayList<>();
    }

    public void add(T element) {
        collection.add(element);
    }

    public void remove(int index) {
        if (index < 0 || index >= collection.size()) {
            System.err.println("Invalid index: " + index);
            return;
        }
        collection.remove(index);
    }

    public T get(int index) {
        if (index < 0 || index >= collection.size()) {
            System.err.println("Invalid index: " + index);
            return null;
        }
        return collection.get(index);
    }

    public int size() {
        return collection.size();
    }

    public void printAll() {
        for (T element : collection) {
            System.out.println(element);
        }
    }
}