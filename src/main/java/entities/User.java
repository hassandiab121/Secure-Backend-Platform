package entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "`user`")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "`id`", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "`email`", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "`status`", nullable = false, length = 50)
    private String status;

    @OneToOne(mappedBy = "user",fetch = FetchType.LAZY, optional = false)
    private Password password;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, optional = false)
    private Audit audit;

    public User(String email, String status) {
        this.email = email;
        this.status = status;
    }


    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Password getPassword() {
        return password;
    }

    public Audit getAudit() {
        return audit;
    }
}