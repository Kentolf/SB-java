package org.example;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class SearchClasses {
    private static final String COMMAND_PACKAGE = "org.example";

    public static Map<String, Command> loadCommands() {
        Map<String, Command> commands = new HashMap<>();
        try {
            List<Class<?>> classes = getClasses(COMMAND_PACKAGE);

            for (Class<?> clazz : classes) {
                processClass(clazz, commands);
            }
        } catch (Exception e) {
            logError("Critical error during command loading: " + e.getMessage());
        }
        return commands;
    }

    private static void processClass(Class<?> clazz, Map<String, Command> commands) {
        try {
            if (!Command.class.isAssignableFrom(clazz) || clazz.isInterface()) {
                return;
            }

            CommandInfo info = clazz.getAnnotation(CommandInfo.class);
            if (info == null) {
                logWarning("Class " + clazz.getName() + " implements Command but has no @CommandInfo - skipped");
                return;
            }

            registerCommand(clazz, info, commands);
        } catch (Exception e) {
            logError("Failed to process class " + clazz.getName() + ": " + e.getMessage());
        }
    }

    private static void registerCommand(Class<?> clazz, CommandInfo info, Map<String, Command> commands) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);

            Command command = (Command) constructor.newInstance();
            String commandName = info.name().toLowerCase();

            commands.put(commandName, command);
            logInfo("Successfully registered command: " + commandName);

        } catch (ReflectiveOperationException e) {
            logError("Failed to register command " + clazz.getName() + ": " + e.getCause().getMessage());
        }
    }
    private static List<Class<?>> getClasses(String packageName) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');

        URL resource = classLoader.getResource(path);
        if (resource == null) {
            throw new Exception("Package not found: " + packageName);
        }

        try {
            File dir = new File(resource.toURI());
            scanDirectory(packageName, dir, classes);
        } catch (URISyntaxException | SecurityException e) {
            throw new Exception(e.getMessage());
        }
        return classes;
    }

    private static void scanDirectory(String packageName, File dir, List<Class<?>> classes) {
        for (File file : dir.listFiles()) {
            try {
                if (file.isDirectory()) {
                    scanDirectory(packageName + "." + file.getName(), file, classes);
                } else if (file.getName().endsWith(".class")) {
                    processClassFile(packageName, file, classes);
                }
            } catch (Exception e) {
                logError("Error processing file " + file.getName() + ": " + e.getMessage());
            }
        }
    }

    private static void processClassFile(String packageName, File file, List<Class<?>> classes) {
        String className = packageName + '.'
                + file.getName().replace(".class", "");

        try {
            classes.add(Class.forName(className));
        } catch (ClassNotFoundException e) {
            logError("Class not found: " + className);
        } catch (NoClassDefFoundError e) {
            logError("Missing dependency for class: " + className);
        } catch (ExceptionInInitializerError e) {
            logError("Class initialization failed for: " + className);
        }
    }

    private static void logInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    private static void logWarning(String message) {
        System.out.println("[WARN] " + message);
    }

    private static void logError(String message) {
        System.out.println("[ERROR] " + message);
    }
}