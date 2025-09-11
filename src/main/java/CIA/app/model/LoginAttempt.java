package CIA.app.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "loginAttempts")
public class LoginAttempt {
    @Id 
    private String email;
    @Column
    private int attempts;
    @Column
    private LocalDateTime lastAttempt;
    @Column
    private boolean locked;
    @Column
    private LocalDateTime lockTime;

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public int getAttempts() {
        return attempts;
    }
    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }
    public LocalDateTime getLastAttempt() {
        return lastAttempt;
    }
    public void setLastAttempt(LocalDateTime lastAttempt) {
        this.lastAttempt = lastAttempt;
    }
    public boolean isLocked() {
        return locked;
    }
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    public LocalDateTime getLockTime() {
        return lockTime;
    }
    public void setLockTime(LocalDateTime lockTime) {
        this.lockTime = lockTime;
    }
}
