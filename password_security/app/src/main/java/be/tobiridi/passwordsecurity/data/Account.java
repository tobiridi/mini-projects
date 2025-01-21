package be.tobiridi.passwordsecurity.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.Objects;

@Entity(tableName = "accounts")
public class Account {
    public enum EncryptionState {
        ENCRYPTED,
        DECRYPTED
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "encrypted_account")
    private String compactAccount;

    @Ignore
    private String name;

    @Ignore
    private String email;

    @Ignore
    private String password;

    @NonNull
    private LocalDate created;

    @NonNull
    private LocalDate updated;

    @Ignore
    private EncryptionState state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompactAccount() {
        return this.compactAccount;
    }

    public void setCompactAccount(String compactAccount) {
        this.compactAccount = compactAccount;
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

    public EncryptionState getState() {
        return this.state;
    }

    public void setState(EncryptionState state) {
        this.state = state;
    }

    private static final String ACCOUNT_SEPARATOR = ",";

    public Account() {
        LocalDate n = LocalDate.now();
        this.created = n;
        this.updated = n;
        this.state = EncryptionState.ENCRYPTED;
    }

    @Ignore
    public Account(@NonNull String name, @NonNull String email, @NonNull String password, @NonNull LocalDate created, @NonNull LocalDate updated) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.created = created;
        this.updated = updated;
        //used to encryption/decryption one String (better performance)
        this.compactAccount = this.name + Account.ACCOUNT_SEPARATOR + this.email + Account.ACCOUNT_SEPARATOR + this.password;
        this.state = EncryptionState.DECRYPTED;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                ", state=" + state +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return this.name.equalsIgnoreCase(account.name) && this.email.equalsIgnoreCase(account.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name, this.email, this.created, this.updated);
    }

    /**
     * Restore the values of the account once the account has been decrypt.
     * If the account state is not {@link EncryptionState#DECRYPTED}, call this method will produce nothing.
     * @param compactAccount The compacted and decrypted account data.
     */
    public void unPackAccountData(String compactAccount) {
        if (this.state.equals(EncryptionState.DECRYPTED)) {
            String[] values = compactAccount.split(Account.ACCOUNT_SEPARATOR);
            //respect the same order as creating of the object
            this.name = values[0];
            this.email = values[1];
            this.password = values[2];
        }
    }
}
