package org.example;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;

public class DoneCommand extends Command {
    public DoneCommand(Connection conn) {
        super(conn);
    }

    @Override
    public String getName() {
        return "done";
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 1) {
            System.out.println("Неверный формат. Используйте: done <UID>");
            return;
        }
        int uid;
        try {
            uid = Integer.parseInt(args[0].trim());
        }
        catch (NumberFormatException e) {
            System.out.println("Неверный UID");
            return;
        }
        try {
            String sql = "UPDATE tasks SET status = 'completed' WHERE uid = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, uid);
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("Задача выполнена");
                } else {
                    System.out.println("Задача не найдена");
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getHelp() {
        return "done <UID> - Пометить задачу с указанным UID как выполненную";
    }
}