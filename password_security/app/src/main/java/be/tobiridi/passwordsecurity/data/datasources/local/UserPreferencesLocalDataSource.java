package be.tobiridi.passwordsecurity.data.datasources.local;

import be.tobiridi.passwordsecurity.data.database.AppDatabase;
import be.tobiridi.passwordsecurity.data.database.Dao.UserPreferencesDao;
import be.tobiridi.passwordsecurity.data.entities.UserPreferences;

/**
 * The local data source class for {@link UserPreferences} entity.
 */
public final class UserPreferencesLocalDataSource implements LocalDataSource {
    private UserPreferencesDao userPreferencesDao;

    public UserPreferencesLocalDataSource(AppDatabase appDatabase) {
        this.userPreferencesDao = appDatabase.getUserPreferencesDao();
    }

    public UserPreferences getUserPreferences() {
        return this.userPreferencesDao.getUserPreferences();
    }

    public int updateUserPreferences(UserPreferences userPrefs) {
        return this.userPreferencesDao.updateUserPreferences(userPrefs);
    }
}
