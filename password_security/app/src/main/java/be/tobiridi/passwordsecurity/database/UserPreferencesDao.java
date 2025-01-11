package be.tobiridi.passwordsecurity.database;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface UserPreferencesDao {
    @Transaction
    @Query("SELECT master_password FROM user_preferences")
    String getMasterPassword();
}
