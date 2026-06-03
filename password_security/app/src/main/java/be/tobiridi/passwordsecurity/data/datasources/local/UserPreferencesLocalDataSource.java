package be.tobiridi.passwordsecurity.data.datasources.local;

import androidx.annotation.NonNull;

import java.time.LocalDate;

import be.tobiridi.passwordsecurity.data.database.AppDatabase;
import be.tobiridi.passwordsecurity.data.database.Dao.UserPreferencesDao;
import be.tobiridi.passwordsecurity.data.entities.UserPreferences;

/**
 * The local data source class for {@link UserPreferences} entity.
 */
public final class UserPreferencesLocalDataSource implements LocalDataSource {
    private UserPreferencesDao userPreferencesDao;
    private UserPreferences userPrefs;

    public UserPreferencesLocalDataSource(AppDatabase appDatabase) {
        this.userPreferencesDao = appDatabase.getUserPreferencesDao();
    }

    /**
     * Check if the data source has reference to the entity before manipulate its fields,
     * prevent {@link NullPointerException} exception.
     */
    private void checkUserPreferencesInstance() {
        if (this.userPrefs == null)
            this.userPrefs = this.userPreferencesDao.getUserPreferences();
    }

    public LocalDate getLastBackup() {
        this.checkUserPreferencesInstance();
        return this.userPrefs.getLastBackup();
    }

    /**
     * Update the last backup date field of {@link UserPreferences} entity.
     * @param lastBackupDate The last date when backup is made.
     * @return {@code true} if updated {@code false} otherwise.
     */
    public boolean updateLastBackup(@NonNull LocalDate lastBackupDate) {
        this.checkUserPreferencesInstance();
        this.userPrefs.setLastBackup(lastBackupDate);
        return this.userPreferencesDao.updateUserPreferences(this.userPrefs) > 0;
    }
}
