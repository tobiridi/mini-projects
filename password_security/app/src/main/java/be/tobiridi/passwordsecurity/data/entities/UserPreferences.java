package be.tobiridi.passwordsecurity.data.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.Objects;

@Entity(tableName = "user_preferences")
public class UserPreferences {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "user_pref_id")
    private int userPrefId;

    @ColumnInfo(name = "master_password")
    @NonNull
    private String masterPassword;

    @ColumnInfo(name = "last_backup")
    @Nullable
    private LocalDate lastBackup;

    public int getUserPrefId() {
        return userPrefId;
    }

    public void setUserPrefId(int userPrefId) {
        if (userPrefId != 1)
            userPrefId = 1;
        this.userPrefId = userPrefId;
    }

    @NonNull
    public String getMasterPassword() {
        return masterPassword;
    }

    public void setMasterPassword(@NonNull String masterPassword) {
        this.masterPassword = masterPassword;
    }

    public LocalDate getLastBackup() {
        return this.lastBackup;
    }

    public void setLastBackup(LocalDate lastBackup) {
        this.lastBackup = lastBackup;
    }

    public UserPreferences() {
        //Always use the "primary key = 1" to oblige to have only one row in the table
        this.userPrefId = 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPreferences)) return false;
        UserPreferences that = (UserPreferences) o;
        return Objects.equals(this.masterPassword, that.masterPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.userPrefId, this.masterPassword);
    }
}
