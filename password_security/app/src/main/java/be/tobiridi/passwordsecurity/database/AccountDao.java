package be.tobiridi.passwordsecurity.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import be.tobiridi.passwordsecurity.data.Account;

@Dao
public interface AccountDao {
    @Insert
    void insertAccount(Account... accounts);

    @Update
    void updateAccount(Account account);

    @Delete
    void deleteAccount(Account account);

    @Transaction
    @Query("SELECT id, name, email, password, created, updated FROM accounts ORDER BY name ASC")
    LiveData<List<Account>> getAllAccounts();
}
