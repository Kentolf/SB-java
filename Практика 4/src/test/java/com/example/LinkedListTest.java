package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
        list.add(1, "B"); // Вставляем "B" на позицию 1

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

        list.remove(1); // Удаляем "B"

        assertEquals("A", list.get(0));
        assertEquals("C", list.get(1));
        assertEquals(2, list.size());
    }

    @Test
    void testRemoveFirstElement() {
        LinkedList list = new LinkedList();
        list.add("A");
        list.add("B");

        list.remove(0); // Удаляем "A"

        assertEquals("B", list.get(0));
        assertEquals(1, list.size());
    }

    @Test
    void testRemoveLastElement() {
        LinkedList list = new LinkedList();
        list.add("A");
        list.add("B");

        list.remove(1); // Удаляем "B"

        assertEquals("A", list.get(0));
        assertEquals(1, list.size());
    }

    @Test
    void testRemoveInvalidIndex() {
        LinkedList list = new LinkedList();
        list.add("A");

        list.remove(5); // Попытка удалить несуществующий элемент

        assertEquals(1, list.size());
        assertEquals("A", list.get(0));
    }

    @Test
    void testGetInvalidIndex() {
        LinkedList list = new LinkedList();
        list.add("A");

        assertNull(list.get(5)); // Попытка получить несуществующий элемент
    }

    @Test
    void testSize() {
        LinkedList list = new LinkedList();
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
        LinkedList list = new LinkedList();
        list.add("A");
        list.add("B");
        list.add("C");

        list.printList(); // Должно вывести: "A -> B -> C -> null"
    }
}
