package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddCommand extends Command {
    public AddCommand(Connection conn) { super(conn); }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 1) {
            System.out.println("Неверный формат. Используйте: add <имя> / <важность> / <дата_выполнения>");
            return;
        }
        String[] taskParts = args[0].split("/");

        if (taskParts.length != 3) {
            System.out.println("Неверный формат. Используйте: add <имя> / <важность> / <дата_выполнения>");
            return;
        }

        String name = taskParts[0].trim();
        int importance;

        try {
            importance = Integer.parseInt(taskParts[1].trim());
        }
        catch (NumberFormatException e) {
            System.out.println("Важность должна быть числом");
            return;
        }
        String due_date = taskParts[2].trim().replace(".", "-");
        try {
            java.sql.Date.valueOf(due_date);
        }
        catch (IllegalArgumentException e) {
            System.out.println("Неверный формат даты. Используйте ГГГГ-ММ-ДД или ГГГГ.ММ.ДД");
            return;
        }
        try {
            String sql = "INSERT INTO tasks (name, status, importance, due_date) VALUES (?, 'pending', ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setInt(2, importance);
                pstmt.setDate(3, java.sql.Date.valueOf(due_date));
                pstmt.executeUpdate();
                System.out.println("Задача добавлена");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getHelp() {
        return "add <имя> / <важность> / <дата_выполнения> - Добавить новую задачу";
    }
}