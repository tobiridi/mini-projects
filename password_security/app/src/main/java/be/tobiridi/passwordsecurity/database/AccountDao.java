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
    long[] insertAccount(Account... accounts);

    @Update
    int updateAccount(Account... accounts);

    @Delete
    int deleteAccount(Account account);

    @Transaction
    @Query("SELECT id, encrypted_account, created, updated FROM accounts")
    LiveData<List<Account>> getAllAccounts();
}
