<<<<<<<< HEAD:password_security/app/src/main/java/be/tobiridi/passwordsecurity/database/Dao/AccountDao.java
package be.tobiridi.passwordsecurity.database.Dao;
========
package be.tobiridi.passwordsecurity.data.database.Dao;
>>>>>>>> 7777d5d ([FIX] reorganize project folders):password_security/app/src/main/java/be/tobiridi/passwordsecurity/data/database/Dao/AccountDao.java

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

<<<<<<<< HEAD:password_security/app/src/main/java/be/tobiridi/passwordsecurity/database/Dao/AccountDao.java
import be.tobiridi.passwordsecurity.entities.Account;
========
import be.tobiridi.passwordsecurity.data.entities.Account;
>>>>>>>> 7777d5d ([FIX] reorganize project folders):password_security/app/src/main/java/be/tobiridi/passwordsecurity/data/database/Dao/AccountDao.java

@Dao
public interface AccountDao {
    @Insert
    long[] insertAccount(Account... accounts);

    @Update
    int updateAccount(Account... accounts);

    @Delete
    int deleteAccount(Account account);

    @Query("DELETE FROM accounts")
    int deleteAllAccounts();

    @Transaction
    @Query("SELECT id, encrypted_account, created, updated FROM accounts")
    LiveData<List<Account>> getAllAccounts();
}
