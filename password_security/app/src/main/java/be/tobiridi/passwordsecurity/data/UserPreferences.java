package be.tobiridi.passwordsecurity.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "user_preferences")
public class UserPreferences implements Serializable {
    private static final long serialVersionUID = 3415344675890978362L;

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "user_pref_id")
    private int userPrefId;

    @ColumnInfo(name = "master_password")
    @NonNull
    private String masterPassword;

    public int getUserPrefId() {
        return userPrefId;
    }

    public void setUserPrefId(int userPrefId) {
        this.userPrefId = userPrefId;
    }

    @NonNull
    public String getMasterPassword() {
        return masterPassword;
    }

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }

    public UserPreferences() {
        //Always use the "primary key = 1" to oblige to have only one row in the table
        this.userPrefId = 1;
    }

    public UserPreferences(@NonNull String masterPassword) {
        this();
        this.masterPassword = masterPassword;
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
