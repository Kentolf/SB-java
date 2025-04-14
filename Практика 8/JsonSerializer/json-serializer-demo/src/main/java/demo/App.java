package demo;

import com.serializer.JsonSerializer;
import com.serializer.JsonSerializationException;
import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        Person person = new Person("Alice", 30, Arrays.asList("Reading", "Hiking"));
        JsonSerializer serializer = new JsonSerializer();
        try {
            String json = serializer.serialize(person);
            System.out.println(json);
        } catch (JsonSerializationException e) {
            e.printStackTrace();
        }
    }
}