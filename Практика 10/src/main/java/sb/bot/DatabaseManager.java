package sb.bot;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;

public class DatabaseManager {
    private final Connection conn;

    public DatabaseManager(Connection conn) {
        this.conn = conn;
        initDatabase();
    }

    private void initDatabase() {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS events (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            name VARCHAR(255) NOT NULL,
                            creator_id VARCHAR(255),
                            start_time TIMESTAMP NULL
                        );
                    """);

            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS participants (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            event_id INT NOT NULL,
                            chat_id BIGINT NOT NULL,
                            FOREIGN KEY (event_id) REFERENCES events(id)
                        );
                    """);

            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS timeslots (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            participant_id INT NOT NULL,
                            start_time TIMESTAMP NOT NULL,
                            end_time TIMESTAMP NOT NULL,
                            FOREIGN KEY (participant_id) REFERENCES participants(id)
                        );
                    """);

            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS availabilities (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            event_id INT NOT NULL,
                            user_id BIGINT NOT NULL,
                            start_time TIMESTAMP NOT NULL,
                            end_time TIMESTAMP NOT NULL,
                            FOREIGN KEY (event_id) REFERENCES events(id)
                        );
                    """);

            stmt.executeUpdate("""
                        ALTER TABLE events ADD COLUMN IF NOT EXISTS invite_code VARCHAR(10) UNIQUE;
                    """);

            System.out.println("База данных успешно инициализирована.");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int createEvent(String name, String creatorId) {
        String sql = "INSERT INTO events (name, creator_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setString(2, creatorId);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void joinEvent(int eventId, long chatId) {
        String sql = "INSERT INTO participants (event_id, chat_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            pstmt.setLong(2, chatId);
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveAvailability(int eventId, long userId, LocalDateTime start, LocalDateTime end) {
        String sql = "INSERT INTO availabilities (event_id, user_id, start_time, end_time) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            pstmt.setLong(2, userId);
            pstmt.setTimestamp(3, Timestamp.valueOf(start));
            pstmt.setTimestamp(4, Timestamp.valueOf(end));
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, List<TimeSlot>> getAvailabilitiesByEvent(int eventId) {
        Map<String, List<TimeSlot>> result = new HashMap<>();
        String sql = "SELECT user_id, start_time, end_time FROM availabilities WHERE event_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String userId = String.valueOf(rs.getLong("user_id"));
                LocalDateTime start = rs.getTimestamp("start_time").toLocalDateTime();
                LocalDateTime end = rs.getTimestamp("end_time").toLocalDateTime();
                result.computeIfAbsent(userId, k -> new ArrayList<>()).add(new TimeSlot(start, end));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Long> getParticipants(int eventId) {
        List<Long> participants = new ArrayList<>();
        String sql = "SELECT chat_id FROM participants WHERE event_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                participants.add(rs.getLong("chat_id"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return participants;
    }

    public boolean deleteAvailability(int eventId, long userId, LocalDateTime start, LocalDateTime end) {
        String sql = "DELETE FROM availabilities WHERE event_id = ? AND user_id = ? AND start_time = ? AND end_time = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            pstmt.setLong(2, userId);
            pstmt.setTimestamp(3, Timestamp.valueOf(start));
            pstmt.setTimestamp(4, Timestamp.valueOf(end));
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAllAvailabilityForUser(int eventId, long userId) {
        String sql = "DELETE FROM availabilities WHERE event_id = ? AND user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            pstmt.setLong(2, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAvailabilityForDateRange(int eventId, long userId, LocalDateTime start, LocalDateTime end) {
        String sql = "DELETE FROM availabilities WHERE event_id = ? AND user_id = ? " +
                "AND start_time >= ? AND end_time <= ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            pstmt.setLong(2, userId);
            pstmt.setTimestamp(3, Timestamp.valueOf(start));
            pstmt.setTimestamp(4, Timestamp.valueOf(end));
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAvailabilityForDate(int eventId, long userId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return deleteAvailabilityForDateRange(eventId, userId, startOfDay, endOfDay);
    }

    public String getCreatorIdByEventId(int eventId) {
        String sql = "SELECT creator_id FROM events WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("creator_id");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getEventName(int eventId) {
        String sql = "SELECT name FROM events WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setInviteCode(int eventId, String code) {
        String sql = "UPDATE events SET invite_code = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            pstmt.setInt(2, eventId);
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Integer getEventIdByCode(String code) {
        String sql = "SELECT id FROM events WHERE invite_code = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}