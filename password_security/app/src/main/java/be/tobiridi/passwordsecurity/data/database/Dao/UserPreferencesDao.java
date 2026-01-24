<<<<<<<< HEAD:password_security/app/src/main/java/be/tobiridi/passwordsecurity/database/Dao/UserPreferencesDao.java
package be.tobiridi.passwordsecurity.database.Dao;
========
package be.tobiridi.passwordsecurity.data.database.Dao;
>>>>>>>> 7777d5d ([FIX] reorganize project folders):password_security/app/src/main/java/be/tobiridi/passwordsecurity/data/database/Dao/UserPreferencesDao.java

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

<<<<<<<< HEAD:password_security/app/src/main/java/be/tobiridi/passwordsecurity/database/Dao/UserPreferencesDao.java
import be.tobiridi.passwordsecurity.entities.UserPreferences;
========
import be.tobiridi.passwordsecurity.data.entities.UserPreferences;
>>>>>>>> 7777d5d ([FIX] reorganize project folders):password_security/app/src/main/java/be/tobiridi/passwordsecurity/data/database/Dao/UserPreferencesDao.java

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
