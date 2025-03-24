package org.example;

import java.lang.annotation.*;

public interface Command {
    double execute(double a, double b);

    @Retention(RetentionPolicy.RUNTIME) // рефлексия
    @Target(ElementType.TYPE) // обозначения для поиска

    @interface Operation {
        String symbol(); // символ
        int priority() default 1; // приоритет
    }
}