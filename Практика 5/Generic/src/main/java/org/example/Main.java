package org.example;

public class Main {
    public static void main(String[] args) {
        GenericCollection<Person> personCollection = new GenericCollection<>();

        personCollection.add(new Person("Alice", 30));
        personCollection.add(new Person("Bob", 25));
        personCollection.add(new Person("Charlie", 35));

        System.out.println("All persons:");
        personCollection.printAll();

        personCollection.remove(1);

        System.out.println("Person at index 1:");
        System.out.println(personCollection.get(1));

        System.out.println("Collection size: " + personCollection.size());

        System.out.println("All persons after removal:");
        personCollection.printAll();
    }
}