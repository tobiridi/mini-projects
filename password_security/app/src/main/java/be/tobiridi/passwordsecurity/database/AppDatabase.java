package be.tobiridi.passwordsecurity.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import be.tobiridi.passwordsecurity.data.Account;
import be.tobiridi.passwordsecurity.database.converters.DateConverters;

@Database(version = 1,
        entities = {Account.class},
        exportSchema = true
)
@TypeConverters({DateConverters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "appDatabase.db")
                    .build();
        }

        return INSTANCE;
    }

    //Dao class
    public abstract AccountDao getAccountDao();
}
