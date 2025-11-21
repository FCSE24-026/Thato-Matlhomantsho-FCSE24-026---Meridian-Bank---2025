package com.banking.persistence;

import com.banking.model.AuditLog;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuditDAO {
    private Connection connection;

    public AuditDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public boolean create(AuditLog log) {
        String sql = "INSERT INTO AUDIT_LOG (ID, TIMESTAMP, ACTOR_ID, ACTOR_EMAIL, ACTION_TYPE, TARGET_TYPE, TARGET_ID, DETAILS, STATUS) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, log.getId());
            pstmt.setString(2, log.getTimestamp().toString());
            pstmt.setString(3, log.getActorId());
            pstmt.setString(4, log.getActorEmail());
            pstmt.setString(5, log.getActionType());
            pstmt.setString(6, log.getTargetType());
            pstmt.setString(7, log.getTargetId());
            pstmt.setString(8, log.getDetails());
            pstmt.setString(9, log.getStatus());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException ex) {
            System.out.println("✗ Error inserting audit log: " + ex.getMessage());
            return false;
        }
    }

    public List<AuditLog> readAll() {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM AUDIT_LOG ORDER BY TIMESTAMP DESC";
        try (var stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                AuditLog l = new AuditLog();
                l.setId(rs.getString("ID"));
                try { l.setTimestamp(LocalDateTime.parse(rs.getString("TIMESTAMP"))); } catch (Exception e) { l.setTimestamp(LocalDateTime.now()); }
                l.setActorId(rs.getString("ACTOR_ID"));
                l.setActorEmail(rs.getString("ACTOR_EMAIL"));
                l.setActionType(rs.getString("ACTION_TYPE"));
                l.setTargetType(rs.getString("TARGET_TYPE"));
                l.setTargetId(rs.getString("TARGET_ID"));
                l.setDetails(rs.getString("DETAILS"));
                l.setStatus(rs.getString("STATUS"));
                logs.add(l);
            }
        } catch (SQLException ex) {
            System.out.println("✗ Error reading audit logs: " + ex.getMessage());
        }
        return logs;
    }
}
