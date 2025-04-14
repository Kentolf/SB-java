package org.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Date;

public class ShowCommand extends Command {
    public ShowCommand(Connection conn) { super(conn); }

    @Override
    public String getName() { return "show"; }

    @Override
    public void execute(String[] args) {
        String sortBy = "time";
        if (args.length > 0) {
            sortBy = args[0].toLowerCase();
            if (!sortBy.equals("time") && !sortBy.equals("importance") && !sortBy.equals("due")) {
                System.out.println("Неверная опция сортировки. Используйте time, importance или due.");
                return;
            }
        }
        try {
            String orderBy;
            if ("importance".equals(sortBy)) {
                orderBy = "importance";
            }
            else if ("due".equals(sortBy)) {
                orderBy = "due_date";
            }
            else {
                orderBy = "added_at"; // По умолчанию
            }

            String sql = "SELECT * FROM tasks ORDER BY " + orderBy;
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {

                    int uid = rs.getInt("uid");
                    String name = rs.getString("name");
                    String status = rs.getString("status");
                    Timestamp addedAt = rs.getTimestamp("added_at");
                    int importance = rs.getInt("importance");
                    Date dueDate = rs.getDate("due_date");
                    System.out.printf("UID: %d, Название: %s, Статус: %s, Добавлено: %s, Важность: %d, Срок: %s%n",
                            uid, name, status, addedAt.toString(), importance, dueDate != null ? dueDate.toString() : "null");
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getHelp() {
        return "show [time|importance|due] - Показать все задачи, отсортированные по времени добавления, важности или дате выполнения";
    }
}