package entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "passwords")
public class Password {

    @Id
    @Column(name = "user_id")
    private UUID user_id;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(
            name = "user_id",
            foreignKey = @ForeignKey(
                    name = "fk_passwords_user",
                    foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE"
            )
    )
    private User user;

    public Password() {
    }

    public Password(String passwordHash, User user) {
        this.passwordHash = passwordHash;
        this.user = user;
    }

    // Getters and Setters
    public UUID getId() {
        return user_id;
    }

    public void setId(UUID id) {
        this.user_id = id;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}