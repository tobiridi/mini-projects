package be.tobiridi.passwordsecurity.data.datasources.local;

import android.content.SharedPreferences;

/**
 * The local data source class for manipulate the settings of the app.
 */
public final class SettingsLocalDataSource implements LocalDataSource {
    private final SharedPreferences preferences;

    public SettingsLocalDataSource(SharedPreferences preferences) {
        this.preferences = preferences;
    }



}
