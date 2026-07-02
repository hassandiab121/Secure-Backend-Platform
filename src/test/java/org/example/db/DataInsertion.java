package org.example.db;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

@SpringBootTest
public class DataInsertion {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private Flyway flyway;

    private Connection connection;

    @BeforeEach
    public void setup() throws SQLException {
        // 1. Force Flyway to safely cycle the database schema before each test run
        flyway.clean();
        flyway.migrate();

        // 2. Open a direct channel out of the pre-configured data source factory
        this.connection = dataSource.getConnection();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    public void test_EP_UniqueEmailConstraint() throws SQLException {
        System.out.println("Testing the unique email constraint on the user table...");
        String insertUserSql = "INSERT INTO `user` (`id`, `email`, `status`) VALUES (UUID_TO_BIN(UUID()), ?, 'ACTIVE')";
        String targetEmail = "test-engineer@platform.com";

        try (PreparedStatement stmt1 = connection.prepareStatement(insertUserSql)) {
            stmt1.setString(1, targetEmail);
            Assertions.assertDoesNotThrow(() -> stmt1.executeUpdate());
        }

        try (PreparedStatement stmt2 = connection.prepareStatement(insertUserSql)) {
           stmt2.setString(1, targetEmail);

         Assertions.assertThrows(SQLException.class, () -> stmt2.executeUpdate(), "Expected a unique constraint violation on the email column");
        }
    }

    @Test
    public void test_UnableToAddNewUser_AuditTable(){
        System.out.println("Testing the audit table...");
        String insertUserSql = "INSERT INTO `user` (`id`, `email`, `status`) VALUES (UUID_TO_BIN(UUID()), ?, 'ACTIVE')";
        String targetEmail = "test-audit@platform.com";


        try (PreparedStatement stmt1 = connection.prepareStatement(insertUserSql)) {
            stmt1.setString(1, targetEmail);
            Assertions.assertDoesNotThrow(() -> stmt1.executeUpdate());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert user into the database: " + e.getMessage(), e);
        }

        String insertAuditSql = "INSERT INTO `audit` (`user_id`, `failed_login_attempts`, `last_login_at`) VALUES (UUID_TO_BIN(UUID()), ?, NOW())";
        try (PreparedStatement stmt = connection.prepareStatement(insertAuditSql)) {
            stmt.setInt(1, 0);
            Assertions.assertThrows(SQLException.class,() -> stmt.executeUpdate());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert audit record into the database: " + e.getMessage(), e);
        }
    }


    @Test
    public void test_AddAuditsForPresentedUSer() {
        String userId = UUID.randomUUID().toString();
        String email = "test-audit@platform.com";
        String insertUserSql = "INSERT INTO `user` (`id`, `email`, `status`) VALUES (UUID_TO_BIN(?), ?, 'ACTIVE')";

        try (PreparedStatement stmt1 = connection.prepareStatement(insertUserSql)) {
            stmt1.setString(1, userId);
            stmt1.setString(2, email);
            Assertions.assertDoesNotThrow(() -> stmt1.executeUpdate());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert user into the database: " + e.getMessage(), e);
        }

        String insertAuditSql = "INSERT INTO `audit` (`user_id`, `failed_login_attempts`, `last_login_at`) VALUES (UUID_TO_BIN(?), ?, NOW())";
        try (PreparedStatement stmt = connection.prepareStatement(insertAuditSql)) {
            stmt.setString(1, userId);
            stmt.setInt(2, 0);
            Assertions.assertDoesNotThrow(() -> stmt.executeUpdate());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert audit record into the database: " + e.getMessage(), e);
        }

    }



}
