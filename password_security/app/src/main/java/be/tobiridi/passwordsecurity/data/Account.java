package be.tobiridi.passwordsecurity.data;

import java.io.Serializable;
import java.time.LocalDate;

public class Account implements Serializable {
    private int id;
    private String name;
    private String email;
    private String password;
    private LocalDate created;
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

    public Account(int id, String name, String email, String password, LocalDate created, LocalDate updated) {
        this.id = id;
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
