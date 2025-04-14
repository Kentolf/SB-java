package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ToDoList {

    private static final String DB_URL = "jdbc:h2:file:./tododb";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            createTable(conn);
            UI ui = new UI(conn);
            ui.run();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createTable(Connection conn) throws SQLException {

        String sql = "CREATE TABLE IF NOT EXISTS tasks (" +
                "uid INT PRIMARY KEY AUTO_INCREMENT," +
                "name VARCHAR(255)," +
                "status VARCHAR(50)," +
                "added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "importance INT," +
                "due_date DATE" +
                ")";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
}