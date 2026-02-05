package be.tobiridi.passwordsecurity.data.database;

import android.content.Context;
import android.database.Cursor;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import be.tobiridi.passwordsecurity.data.entities.Account;
import be.tobiridi.passwordsecurity.data.entities.UserPreferences;
import be.tobiridi.passwordsecurity.data.database.Dao.AccountDao;
import be.tobiridi.passwordsecurity.data.database.Dao.UserPreferencesDao;
import be.tobiridi.passwordsecurity.data.database.converters.DateTimeConverters;

@Database(version = 1,
        entities = {
            Account.class,
            UserPreferences.class,
        },
        exportSchema = true
)
@TypeConverters({DateTimeConverters.class})
public abstract class AppDatabase extends RoomDatabase {
    public static final String DB_NAME = "appDatabase.db";
    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, DB_NAME)
                    .build();
        }
        return INSTANCE;
    }

    /**
     * Close the database connection if not already closed and set the instance references to {@code null}.
     */
    public static boolean closeDatabase() {
        if(INSTANCE != null) {
            INSTANCE.close();
            INSTANCE = null;
        }
        return true;
    }

    public static boolean clearAllTablesFromDatabase() {
        if(INSTANCE != null) {
            INSTANCE.clearAllTables();
            return true;
        }
        return false;
    }

    /**
     * Make a checkpoint for SQLite {@code .wal} file and apply all modifications in the database file.
     * </br>
     * Use the {@code PRAGMA wal_checkpoint(TRUNCATE);} SQLite statement.
     */
    public static boolean makeWalCheckpoint() {
        if (INSTANCE != null) {
            Cursor cursor = INSTANCE.getOpenHelper().getWritableDatabase().query("PRAGMA wal_checkpoint(TRUNCATE);");
            cursor.moveToNext();
            cursor.close();
            return true;
        }
        return false;
    }

    //DAO class
    public abstract AccountDao getAccountDao();
    public abstract UserPreferencesDao getUserPreferencesDao();
}
