<<<<<<<< HEAD:password_security/app/src/main/java/be/tobiridi/passwordsecurity/entities/Account.java
package be.tobiridi.passwordsecurity.entities;
========
package be.tobiridi.passwordsecurity.data.entities;
>>>>>>>> 7777d5d ([FIX] reorganize project folders):password_security/app/src/main/java/be/tobiridi/passwordsecurity/data/entities/Account.java

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

import be.tobiridi.passwordsecurity.data.security.AESManager;

@Entity(tableName = "accounts")
public class Account implements Serializable {
    private static final long serialVersionUID = 42263247523547L;

    private enum EncryptionState {
        ENCRYPTED,
        DECRYPTED
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    /**
     * The data about this {@link Account} combined and stored in a {@code String}, used to encrypt and decrypt one field.
     * @see Account#encrypt(byte[])
     * @see Account#decrypt(byte[])
     * @see Account#packAccountData()  
     * @see Account#unPackAccountData()
     */
    @ColumnInfo(name = "encrypted_account")
    private String compactAccount;

    @Ignore
    private String name;

    @Ignore
    private String email;

    @Ignore
    private String password;

    @Ignore
    private String username;

    @Ignore
    private String note;

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

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public boolean isEncrypted() {
        return this.state.equals(EncryptionState.ENCRYPTED);
    }

    /**
     * Separator to combine all account data in one field.
     */
    private static final String ACCOUNT_DATA_SEPARATOR = "&SEP;";

    /**
     * Constructor for {@link androidx.room.RoomDatabase} only,
     * you should not used this constructor to create an {@code Account},
     * prefer to use another constructor.
     */
    public Account() {
        LocalDateTime n = LocalDateTime.now();
        this.created = n;
        this.updated = n;
        this.state = EncryptionState.ENCRYPTED;
    }

    @Ignore
    public Account(@NonNull String name, @NonNull String password, String email, String username, String note) {
        LocalDateTime now = LocalDateTime.now();
        this.name = name;
        this.password = password;
        this.created = now;
        this.updated = now;
        this.email = email;
        this.username = username;
        this.note = note;
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
                ", username='" + username + '\'' +
                ", note='" + note + '\'' +
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
        if (this.name.equalsIgnoreCase(account.name) && this.email.equalsIgnoreCase(account.email)) return true;
        if (this.name.equalsIgnoreCase(account.name) && this.username.equalsIgnoreCase(account.username)) return true;
        return this.id == account.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name, this.created, this.updated);
    }

<<<<<<<< HEAD:password_security/app/src/main/java/be/tobiridi/passwordsecurity/entities/Account.java
    // TODO: 19/01/2026 don't use the data source directly in view model, centralize in some "Service" where viewmodel use the service to interact with the database
========
    // TODO: 19/01/2026 don't use the data source directly in view model, centralize in some "Service" where viewmodel has "LiveData" to the data
>>>>>>>> 7777d5d ([FIX] reorganize project folders):password_security/app/src/main/java/be/tobiridi/passwordsecurity/data/entities/Account.java

    /**
     * @param encryptionKey The key used to encrypt the account.
     * @return {@code true} If the account has been encrypted, {@code false} otherwise.
     * @throws GeneralSecurityException If the {@code encryptionKey} is invalid for encryption.
     */
    public boolean encrypt(byte[] encryptionKey) throws GeneralSecurityException {
        if(!this.isEncrypted()) {
            this.compactAccount = AESManager.encryptToStringBase64(encryptionKey, this.compactAccount);
            this.state = EncryptionState.ENCRYPTED;
        }
        return this.isEncrypted();
    }

    /**
     * @param decryptionKey The key used to decrypt the account.
     * @return {@code true} If the account has been decrypted, {@code false} otherwise.
     * @throws GeneralSecurityException If the {@code decryptionKey} is invalid for decryption.
     */
    public boolean decrypt(byte[] decryptionKey) throws GeneralSecurityException {
        if(this.isEncrypted()) {
            this.compactAccount = AESManager.decryptToString(decryptionKey, this.compactAccount);
            this.state = EncryptionState.DECRYPTED;
            this.unPackAccountData();
        }
        return !this.isEncrypted();
    }

    /**
     * Reassign all fields about this {@link Account}, respecting the same order when {@link Account#packAccountData()}.
     * <br/>
     * If the account state is not {@link EncryptionState#DECRYPTED}, call this method will produce nothing.
     * @see Account#packAccountData()
     * @see Account#compactAccount
     */
    private void unPackAccountData() {
        if (!this.isEncrypted()) {
            // some member variables are optionals, see how the data are compacted
            // affect null reference and not a "null" string value
            String[] values = Arrays.stream(this.compactAccount.split(ACCOUNT_DATA_SEPARATOR))
                    .map(v -> v.equals("null") ? null : v)
                    .toArray(String[]::new);

            // respect the same order when pack the account data
            this.name = values[0];
            this.email = values[1];
            this.password = values[2];
            this.username = values[3];
            this.note = values[4];
        }
    }

    /**
     * Order and compact all fields of this {@link Account}.
     * <br/>
     * If the account state is not {@link EncryptionState#DECRYPTED}, call this method will produce nothing.
     * @see Account#unPackAccountData()
     * @see Account#compactAccount
     */
    public void packAccountData() {
        if (!this.isEncrypted()) {
            StringJoiner joiner = new StringJoiner(ACCOUNT_DATA_SEPARATOR);

            // the order used to compact the account, ORDER IMPORTANT
            joiner.add(this.name);
            joiner.add(this.email);
            joiner.add(this.password);
            joiner.add(this.username);
            joiner.add(this.note);
            this.compactAccount = joiner.toString();
        }
    }
}
