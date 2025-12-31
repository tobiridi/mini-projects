package be.tobiridi.passwordsecurity.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import be.tobiridi.passwordsecurity.data.Account;
import be.tobiridi.passwordsecurity.data.UserPreferences;
import be.tobiridi.passwordsecurity.database.converters.DateTimeConverters;

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
     * Close the database connection and set the instance references to {@code null}.
     */
    @Override
    public void close() {
        super.close();
        INSTANCE = null;
    }

    //DAO class
    public abstract AccountDao getAccountDao();
    public abstract UserPreferencesDao getUserPreferencesDao();
}
