package org.example;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchClasses {
    public static List<Class<?>> getClasses(String packageName)
            throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/'); // преобразует пакет в путь

        URL resource = classLoader.getResource(path);
        if (resource == null) // проверка
            throw new Exception("Package not found: " + packageName);

        File dir = new File(resource.toURI());
        for (File file : dir.listFiles()) { // ищет все классы в директории
            if (file.isDirectory()) {
                classes.addAll(getClasses(packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().replace(".class", "");
                classes.add(Class.forName(className));
            }
        }
        return classes;
    }
}