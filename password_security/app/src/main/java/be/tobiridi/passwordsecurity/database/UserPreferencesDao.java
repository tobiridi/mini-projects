package be.tobiridi.passwordsecurity.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import be.tobiridi.passwordsecurity.data.UserPreferences;

@Dao
public interface UserPreferencesDao {
    /*
     * Can only have one row in this table !
     * Always use primary key = 1
     */

    @Transaction
    @Query("SELECT master_password FROM user_preferences WHERE user_pref_id = 1")
    String getMasterPassword();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long saveMasterPassword(UserPreferences preferences);
}
