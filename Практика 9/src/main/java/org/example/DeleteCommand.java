package org.example;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;

public class DeleteCommand extends Command {
    public DeleteCommand(Connection conn) {
        super(conn);
    }

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 1) {
            System.out.println("Неверный формат. Используйте: delete <UID>");
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
            String sql = "DELETE FROM tasks WHERE uid = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, uid);
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("Задача удалена");
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
        return "delete <UID> - Удалить задачу с указанным UID";
    }
}