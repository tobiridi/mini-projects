package be.tobiridi.passwordsecurity.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.time.LocalDate;

@Entity(tableName = "accounts")
public class Account implements Serializable {
    private static final long serialVersionUID = 2765142817316875747L;

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String name;

    @NonNull
    private String email;

    @NonNull
    private String password;

    @NonNull
    private LocalDate created;

    @NonNull
    private LocalDate updated;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getCreated() {
        return this.created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public LocalDate getUpdated() {
        return this.updated;
    }

    public void setUpdated(LocalDate updated) {
        this.updated = updated;
    }

    public Account() {
        this.created = LocalDate.now();
        this.updated = LocalDate.now();
    }

    @Ignore
    public Account(@NonNull String name, @NonNull String email, @NonNull String password, @NonNull LocalDate created, @NonNull LocalDate updated) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.created = created;
        this.updated = updated;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }

}
