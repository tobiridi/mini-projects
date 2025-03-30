package be.tobiridi.passwordsecurity.database;

import android.content.Context;
import android.database.Cursor;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import be.tobiridi.passwordsecurity.data.Account;
import be.tobiridi.passwordsecurity.data.UserPreferences;
import be.tobiridi.passwordsecurity.database.converters.DateConverters;

@Database(version = 1,
        entities = {
            Account.class,
            UserPreferences.class,
        },
        exportSchema = true
)
@TypeConverters({DateConverters.class})
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
     * Make a checkpoint for SQLite {@code .wal} file and apply all modifications in the database file.
     * </br>
     * Use the {@code PRAGMA wal_checkpoint(TRUNCATE);} MySQL statement.
     */
    public void MakeWalCheckpoint() {
        Cursor cursor = INSTANCE.getOpenHelper().getWritableDatabase().query("PRAGMA wal_checkpoint(TRUNCATE);");
        cursor.moveToNext();
        cursor.close();
    }

    /**
     * Close the database connection and set the instance references to {@code null}.
     */
    public static void closeDatabase() {
        INSTANCE.close();
        INSTANCE = null;
    }

    //Dao class
    public abstract AccountDao getAccountDao();
    public abstract UserPreferencesDao getUserPreferencesDao();
}
