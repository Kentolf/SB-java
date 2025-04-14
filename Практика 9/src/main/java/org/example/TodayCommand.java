package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Date;
import java.sql.Connection;

public class TodayCommand extends Command {
    public TodayCommand(Connection conn) {
        super(conn);
    }

    @Override
    public String getName() {
        return "today";
    }

    @Override
    public void execute(String[] args) {
        try {
            String sql = "SELECT * FROM tasks WHERE due_date = CURRENT_DATE() AND status = 'pending'";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getHelp() {
        return "today - Показать задачи, которые нужно сделать сегодня";
    }
}