package demo;

import com.serializer.JsonField;
import java.util.List;

public class Person {
    @JsonField
    private String name;

    @JsonField(name = "age_years")
    private int age;

    @JsonField
    private List<String> hobbies;
    
    private String password;

    public Person(String name, int age, List<String> hobbies) {
        this.name = name;
        this.age = age;
        this.hobbies = hobbies;
    }
}