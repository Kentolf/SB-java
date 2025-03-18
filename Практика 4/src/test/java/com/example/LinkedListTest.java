package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class LinkedListTest {

    @Test
    void testAddAndGet() {
        LinkedList list = new LinkedList();
        list.add("A");
        list.add("B");
        list.add("C");

        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        assertEquals("C", list.get(2));
    }

    @Test
    void testAddAtIndex() {
        LinkedList list = new LinkedList();
        list.add("A");
        list.add("C");
        list.add(1, "B");

        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        assertEquals("C", list.get(2));
    }

    @Test
    void testRemove() {
        LinkedList list = new LinkedList();
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
        LinkedList list = new LinkedList();
        list.add("A");
        list.add("B");

        list.remove(0);

        assertEquals("B", list.get(0));
        assertEquals(1, list.size());
    }

    @Test
    void testRemoveLastElement() {
        LinkedList list = new LinkedList();
        list.add("A");
        list.add("B");

        list.remove(1);

        assertEquals("A", list.get(0));
        assertEquals(1, list.size());
    }

    @Test
    void testRemoveInvalidIndex() {
        LinkedList list = new LinkedList();
        list.add("A");

        list.remove(5);

        assertEquals(1, list.size());
        assertEquals("A", list.get(0));
    }

    @Test
    void testGetInvalidIndex() {
        LinkedList list = new LinkedList();
        list.add("A");

        assertNull(list.get(5));
    }

