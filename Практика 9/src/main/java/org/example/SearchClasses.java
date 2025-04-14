package org.example;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.util.*;
import java.lang.reflect.Modifier;

public class SearchClasses {
    public static Map<String, Command> loadCommands(String packageName, Connection conn) throws Exception {
        Map<String, Command> commands = new HashMap<>();
        List<Class<?>> classes = getClasses(packageName);
        for (Class<?> clazz : classes) {
            if (Command.class.isAssignableFrom(clazz)
                    && !clazz.isInterface()
                    && !Modifier.isAbstract(clazz.getModifiers())) {

                Command command = (Command) clazz.getConstructor(Connection.class).newInstance(conn);
                commands.put(command.getName().toLowerCase(), command);
            }
        }
        return commands;
    }

    private static List<Class<?>> getClasses(String packageName) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        URL resource = classLoader.getResource(path);
        if (resource == null) {
            throw new Exception("Package not found: " + packageName);
        }
        File dir = new File(resource.toURI());
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().replace(".class", "");
                classes.add(Class.forName(className));
            }
        }
        return classes;
    }
}