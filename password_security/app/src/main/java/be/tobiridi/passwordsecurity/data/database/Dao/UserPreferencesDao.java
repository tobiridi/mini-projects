package be.tobiridi.passwordsecurity.data.database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import be.tobiridi.passwordsecurity.data.entities.UserPreferences;

@Dao
public interface UserPreferencesDao {
    /*
     * Can only have one row in this table !
     * Always use primary key = 1
     */

    @Query("SELECT * FROM user_preferences WHERE user_pref_id = 1")
    UserPreferences getUserPreferences();

    @Query("SELECT master_password FROM user_preferences WHERE user_pref_id = 1")
    String getMasterPassword();

    /**
     * Update any columns based on entity fields.
     * @param preferences The updated {@link UserPreferences}.
     * @return Number of rows affected.
     */
    @Update
    int updateUserPreferences(UserPreferences preferences);

    /**
     * Insert/Replace the user preferences.
     * <br/>
     * Should be used only one time at the start of the app.
     * <br/>
     * <b>be caution! If you insert a new {@link UserPreferences} the previous one will be replaced.</b>
     * @param preferences Set the first {@link UserPreferences}.
     * @return the rowId.
     * @see #updateUserPreferences(UserPreferences)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUserPreferences(UserPreferences preferences);

}
