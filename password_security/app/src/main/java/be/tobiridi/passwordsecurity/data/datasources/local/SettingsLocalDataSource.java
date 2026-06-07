package be.tobiridi.passwordsecurity.data.datasources.local;

import android.content.SharedPreferences;

import be.tobiridi.passwordsecurity.ui.fragments.settings.SettingsPreferenceKey;

/**
 * The local data source class for manipulate the settings of the app.
 */
public final class SettingsLocalDataSource implements LocalDataSource {
    private final SharedPreferences preferences;

    public SettingsLocalDataSource(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public boolean isAutomationEnable() {
        return this.preferences.getBoolean(SettingsPreferenceKey.EN_AUTOMATION, false);
    }

    public int getAutoDisconnectDuration() {
        return this.preferences.getInt(SettingsPreferenceKey.AUTO_DISCONNECT, 60);
    }

    public boolean isNotificationEnable() {
        return this.preferences.getBoolean(SettingsPreferenceKey.EN_NOTIF, false);
    }

    public int getNotifBackupDuration() {
        return this.preferences.getInt(SettingsPreferenceKey.NOTIF_BACKUP, 30);
    }

}
