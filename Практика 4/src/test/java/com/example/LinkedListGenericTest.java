package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class LinkedListGenericTest {

    @Test
    void testAddAndGet() {
        LinkedListGeneric<String> list = new LinkedListGeneric<>();
        list.add("A");
        list.add("B");
        list.add("C");

        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        assertEquals("C", list.get(2));
    }

    @Test
    void testAddAtIndex() {
        LinkedListGeneric<String> list = new LinkedListGeneric<>();
        list.add("A");
        list.add("C");
        list.add(1, "B");

        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        assertEquals("C", list.get(2));
    }

    @Test
    void testRemove() {
        LinkedListGeneric<String> list = new LinkedListGeneric<>();
        list.add("A");
        list.add("B");
        list.add("C");

        list.remove(1);

        assertEquals("A", list.get(0));
        assertEquals("C", list.get(1));
        assertEquals(2, list.size());
    }

    @Test
    void testRemoveFirstElement() {
        LinkedListGeneric<String> list = new LinkedListGeneric<>();
        list.add("A");
        list.add("B");

        list.remove(0);

        assertEquals("B", list.get(0));
        assertEquals(1, list.size());
    }

    @Test
    void testRemoveLastElement() {
        LinkedListGeneric<String> list = new LinkedListGeneric<>();
        list.add("A");
        list.add("B");

        list.remove(1);

        assertEquals("A", list.get(0));
        assertEquals(1, list.size());
    }

    @Test
    void testRemoveInvalidIndex() {
        LinkedListGeneric<String> list = new LinkedListGeneric<>();
        list.add("A");

        list.remove(5);

        assertEquals(1, list.size());
        assertEquals("A", list.get(0));
    }

    @Test
    void testGetInvalidIndex() {
        LinkedListGeneric<String> list = new LinkedListGeneric<>();
        list.add("A");

        assertNull(list.get(5));
    }

    @Test
    void testSize() {
        LinkedListGeneric<String> list = new LinkedListGeneric<>();
        assertEquals(0, list.size());

        list.add("A");
        assertEquals(1, list.size());

        list.add("B");
        assertEquals(2, list.size());

        list.remove(0);
        assertEquals(1, list.size());

        list.remove(0);
        assertEquals(0, list.size());
    }

    @Test
    void testPrintList() {
        LinkedListGeneric<String> list = new LinkedListGeneric<>();
        list.add("A");
        list.add("B");
        list.add("C");

        list.printList();
    }

    @Test
    void testGenericWithIntegers() {
        LinkedListGeneric<Integer> list = new LinkedListGeneric<>();
        list.add(10);
        list.add(20);
        list.add(30);

        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1));
        assertEquals(30, list.get(2));
    }
}