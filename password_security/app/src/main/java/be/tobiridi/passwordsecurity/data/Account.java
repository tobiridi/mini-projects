package be.tobiridi.passwordsecurity.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.StringJoiner;

@Entity(tableName = "accounts")
public class Account implements Serializable {
    private static final long serialVersionUID = 42263247523547L;
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
    private LocalDateTime created;

    @NonNull
    private LocalDateTime updated;

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

    public LocalDateTime getCreated() {
        return this.created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return this.updated;
    }

    public void setUpdated(LocalDateTime updated) {
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
        LocalDateTime n = LocalDateTime.now();
        this.created = n;
        this.updated = n;
        this.state = EncryptionState.ENCRYPTED;
    }

    @Ignore
    public Account(@NonNull String name, @NonNull String email, @NonNull String password, @NonNull LocalDateTime created, @NonNull LocalDateTime updated) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.created = created;
        this.updated = updated;
        this.state = EncryptionState.DECRYPTED;
        //used to encryption/decryption one String (better performance)
        this.packAccountData();
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
        return this.id == account.id || (this.name.equalsIgnoreCase(account.name) && this.email.equalsIgnoreCase(account.email));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name, this.email, this.created, this.updated);
    }

    /**
     * Restore the values of the account once the account has been decrypted.
     * <br/>
     * If the account state is not {@link EncryptionState#DECRYPTED}, call this method will produce nothing.
     * @param compactAccount The compacted and decrypted account data.
     * @see Account#packAccountData()
     */
    public void unPackAccountData(String compactAccount) {
        if (this.state.equals(EncryptionState.DECRYPTED)) {
            String[] values = compactAccount.split(ACCOUNT_SEPARATOR);
            //respect the same order when pack the account data
            this.name = values[0];
            this.email = values[1];
            this.password = values[2];
            this.compactAccount = compactAccount;
        }
    }

    /**
     * Update the compact account data with its new values.
     * <br/>
     * Should be call only if the data about this account has been updated.
     * <br/>
     * If the account state is not {@link EncryptionState#DECRYPTED}, call this method will produce nothing.
     * @see Account#unPackAccountData(String)
     */
    public void packAccountData() {
        if (this.state.equals(EncryptionState.DECRYPTED)) {
            StringJoiner joiner = new StringJoiner(ACCOUNT_SEPARATOR);
            //the order used to compact the account, ORDER IMPORTANT
            joiner.add(this.name);
            joiner.add(this.email);
            joiner.add(this.password);
            this.compactAccount = joiner.toString();
        }
    }
}
