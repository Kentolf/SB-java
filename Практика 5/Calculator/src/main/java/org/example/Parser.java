package org.example;

import java.util.Stack;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class Parser {
    private final Map<String, Command> commands = new HashMap<>(); // символы для операций
    private final Map<String, Integer> precedence = new HashMap<>(); // приоритеты операций

    public Parser() {
        loadCommands();
    }

    private void loadCommands() { // загрузка операций (через рефлексию)
        try {
            List<Class<?>> classes = SearchClasses.getClasses("org.example");

            for (Class<?> clazz : classes) {
                if (clazz.isAnnotationPresent(Command.Operation.class)) {
                    Command.Operation annotation = clazz.getAnnotation(Command.Operation.class);
                    Command command = (Command) clazz.getDeclaredConstructor().newInstance();
                    commands.put(annotation.symbol(), command);
                    precedence.put(annotation.symbol(), annotation.priority());
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public double evaluate(String expression) {
        expression = expression.replaceAll("\\s+", ""); // удаляет пробелы
        Stack<Double> numbers = new Stack<>(); // стек для чисел
        Stack<String> operators = new Stack<>(); // стек для операций

        for (int i = 0; i < expression.length(); i++) { // обработка десятичных и многозначных чисел
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                StringBuilder numStr = new StringBuilder();
                while (i < expression.length() &&
                        (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    numStr.append(expression.charAt(i++));
                }
                i--;
                numbers.push(Double.parseDouble(numStr.toString()));
            } else if (c == '(') { // обработка выражения в скобках
                operators.push("(");
            } else if (c == ')') {
                while (!operators.peek().equals("(")) {
                    applyOperation(numbers, operators.pop());
                }
                operators.pop();
            } else if (commands.containsKey(String.valueOf(c))) { // обработка операций с учетом приоритетов
                String op = String.valueOf(c);
                while (!operators.isEmpty() &&
                        !operators.peek().equals("(") &&
                        precedence.getOrDefault(operators.peek(), 0) >= precedence.get(op)) {
                    applyOperation(numbers, operators.pop());
                }
                operators.push(op);
            }
        }

        while (!operators.isEmpty()) {
            applyOperation(numbers, operators.pop());
        }

        return numbers.pop();
    }

    private void applyOperation(Stack<Double> numbers, String op) { // обрабатывает два числа из стека
        double b = numbers.pop();
        double a = numbers.pop();
        numbers.push(commands.get(op).execute(a, b));
    }
}