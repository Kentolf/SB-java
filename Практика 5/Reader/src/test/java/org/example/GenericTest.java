package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GenericCollectionTest {

    @Test
    void testAddAndGet() {
        GenericCollection<String> collection = new GenericCollection<>();
        collection.add("A");
        collection.add("B");
        collection.add("C");

        assertEquals("A", collection.get(0));
        assertEquals("B", collection.get(1));
        assertEquals("C", collection.get(2));
    }

    @Test
    void testRemove() {
        GenericCollection<String> collection = new GenericCollection<>();
        collection.add("A");
        collection.add("B");
        collection.add("C");

        collection.remove(1);

        assertEquals("A", collection.get(0));
        assertEquals("C", collection.get(1));
        assertEquals(2, collection.size());
    }

    @Test
    void testRemoveFirstElement() {
        GenericCollection<String> collection = new GenericCollection<>();
        collection.add("A");
        collection.add("B");

        collection.remove(0);

        assertEquals("B", collection.get(0));
        assertEquals(1, collection.size());
    }

    @Test
    void testRemoveLastElement() {
        GenericCollection<String> collection = new GenericCollection<>();
        collection.add("A");
        collection.add("B");

        collection.remove(1);

        assertEquals("A", collection.get(0));
        assertEquals(1, collection.size());
    }

    @Test
    void testRemoveInvalidIndex() {
        GenericCollection<String> collection = new GenericCollection<>();
        collection.add("A");

        collection.remove(5);

        assertEquals(1, collection.size());
        assertEquals("A", collection.get(0));
    }

    @Test
    void testGetInvalidIndex() {
        GenericCollection<String> collection = new GenericCollection<>();
        collection.add("A");

        assertNull(collection.get(5));
    }

    @Test
    void testSize() {
        GenericCollection<String> collection = new GenericCollection<>();
        assertEquals(0, collection.size());

        collection.add("A");
        assertEquals(1, collection.size());

        collection.add("B");
        assertEquals(2, collection.size());

        collection.remove(0);
        assertEquals(1, collection.size());

        collection.remove(0);
        assertEquals(0, collection.size());
    }

    @Test
    void testPrintAll() {
        GenericCollection<String> collection = new GenericCollection<>();
        collection.add("A");
        collection.add("B");
        collection.add("C");

        collection.printAll();
    }

    @Test
    void testGenericWithIntegers() {
        GenericCollection<Integer> collection = new GenericCollection<>();
        collection.add(10);
        collection.add(20);
        collection.add(30);

        assertEquals(10, collection.get(0));
        assertEquals(20, collection.get(1));
        assertEquals(30, collection.get(2));
    }

    @Test
    void testGenericWithPerson() {
        GenericCollection<Person> collection = new GenericCollection<>();
        collection.add(new Person("Alice", 30));
        collection.add(new Person("Bob", 25));
        collection.add(new Person("Charlie", 35));

        assertEquals("Person{name='Alice', age=30}", collection.get(0).toString());
        assertEquals("Person{name='Bob', age=25}", collection.get(1).toString());
        assertEquals("Person{name='Charlie', age=35}", collection.get(2).toString());
    }
}