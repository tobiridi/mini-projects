package be.tobiridi.passwordsecurity.data.repositories;

import be.tobiridi.passwordsecurity.data.datasources.local.SettingsLocalDataSource;

/**
 * Centralize for the ui layer how interact with the settings of the app.
 */
public class SettingsRepository {
    private static SettingsLocalDataSource settingsDataSource;

    public SettingsRepository(SettingsLocalDataSource dataSource) {
        if (settingsDataSource == null) {
            settingsDataSource = dataSource;
        }
    }

    public boolean isAutomationEnable() {
        return settingsDataSource.isAutomationEnable();
    }

    public int getAutoDisconnectDuration() {
        return settingsDataSource.getAutoDisconnectDuration();
    }

    public boolean isNotificationEnable() {
        return settingsDataSource.isNotificationEnable();
    }

    public int getNotifBackupDuration() {
        return settingsDataSource.getNotifBackupDuration();
    }

}
