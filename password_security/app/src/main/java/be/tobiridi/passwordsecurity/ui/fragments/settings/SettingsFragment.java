package be.tobiridi.passwordsecurity.ui.fragments.settings;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.time.LocalDate;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.data.database.AppDatabase;

public class SettingsFragment extends PreferenceFragmentCompat {
    private SettingsViewModel settingsViewModel;
    private Preference importPreference, exportPreference;
    private SeekBarPreference attemptsPreference, autoDisconnectPreference, notifBackupPreference;
    private EditTextPreference masterPasswordPreference;
    private SwitchPreferenceCompat enableAutomationPreference, enableNotificationPreference;
    private ListPreference deleteAllAccountsPreference;
    private ActivityResultLauncher<String> exportFileLauncher, requestPermissionLauncher;
    private ActivityResultLauncher<String[]> importFileLauncher;

    public static SettingsFragment newInstance() { return new SettingsFragment(); }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        this.settingsViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(SettingsViewModel.initializer)).get(SettingsViewModel.class);

        //get preferences
        this.importPreference = findPreference(SettingsPreferenceKey.IMPORT_DB);
        this.exportPreference = findPreference(SettingsPreferenceKey.EXPORT_DB);
        this.attemptsPreference = findPreference(SettingsPreferenceKey.MAX_AUTH_ATTEMPTS);
        this.masterPasswordPreference = findPreference(SettingsPreferenceKey.MASTER_PASSWORD);
        this.deleteAllAccountsPreference = findPreference(SettingsPreferenceKey.DELETE_ALL_ACCOUNTS);
        this.enableAutomationPreference = findPreference(SettingsPreferenceKey.EN_AUTOMATION);
        this.autoDisconnectPreference = findPreference(SettingsPreferenceKey.AUTO_DISCONNECT);
        this.enableNotificationPreference = findPreference(SettingsPreferenceKey.EN_NOTIF);
        this.notifBackupPreference = findPreference(SettingsPreferenceKey.NOTIF_BACKUP);

        this.settingsViewModel.getSettingsUiState().observe(this, (SettingsUiState uiState) -> {
            if (uiState.isNotificationActive()) {
                LocalDate today = LocalDate.now();
                LocalDate nextBackup = this.settingsViewModel.nextBackupDate();
                if (nextBackup.isEqual(today) || today.isAfter(nextBackup)) {
                    this.showBackupNotification();
                }
            }
            if (uiState.isAutomationActive()) {
                // TODO: 23/04/2026 add automation settings
            }
        });

        this.initActivityResultLaunchers();
        this.initListeners();
    }

    private void initActivityResultLaunchers() {
        this.exportFileLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument(settingsViewModel.SQLITE_MIME_TYPE), new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri o) {
                        //null if the user does not create the document
                        if (o != null) {
                            String toastText = "";
                            Context ctx = requireContext().getApplicationContext();
                            File dbFile = ctx.getDatabasePath(AppDatabase.DB_NAME);
                            ContentResolver resolver = ctx.getContentResolver();
                            //Android 12 does not support SQLite MIME type
                            //the MIME type is recognize as "application/octet-stream"
                            if (settingsViewModel.createBackup(dbFile, resolver, o)) {
                                settingsViewModel.updateBackupDate();
                                toastText = getResources().getString(R.string.msg_backup_export_success);
                            }
                            else {
                                //error create backup
                                toastText = getResources().getString(R.string.msg_backup_export_fail);
                            }
                            Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        this.importFileLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(), new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri o) {
                        //null if the user does not open a document
                        if (o != null) {
                            Context ctx = requireContext().getApplicationContext();
                            File dbFile = ctx.getDatabasePath(AppDatabase.DB_NAME);
                            ContentResolver resolver = ctx.getContentResolver();
                            if (settingsViewModel.importBackup(dbFile, resolver, o)) {
                                Toast.makeText(getContext(), getResources().getString(R.string.msg_backup_import_success), Toast.LENGTH_SHORT).show();

                                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                                builder.setCancelable(false);
                                builder.setTitle(R.string.alert_relaunch_app_title);
                                builder.setMessage(R.string.alert_relaunch_app_msg);
                                builder.setPositiveButton(requireContext().getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        System.exit(0);
                                    }
                                });
                                builder.show();
                            }
                            else {
                                //error import backup
                                Toast.makeText(getContext(), getResources().getString(R.string.msg_backup_import_fail), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        this.requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    this.settingsViewModel.updateNotifPref(isGranted);
                }
        );
    }

    private void initListeners() {
        this.exportPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                exportFileLauncher.launch(settingsViewModel.generateBackupFileName());
                return true;
            }
        });

        this.importPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                importFileLauncher.launch(settingsViewModel.OPEN_DOCUMENT_MIME_TYPE);
                return true;
            }
        });

        this.deleteAllAccountsPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                String val = String.valueOf(newValue);
                //first value should be positive entry!
                CharSequence positiveEntry = deleteAllAccountsPreference.getEntries()[0];
                if (val.equalsIgnoreCase(positiveEntry.toString())) {
                    if(settingsViewModel.deleteAllAccounts()) {
                        String msg = getResources().getString(R.string.msg_all_accounts_deleted);
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        this.masterPasswordPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                String masterPassword = String.valueOf(newValue).trim();
                if(settingsViewModel.defineNewMasterPassword(masterPassword)) {
                    String msg = getResources().getString(R.string.msg_new_master_password_set);
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        this.enableNotificationPreference.setOnPreferenceChangeListener((Preference pref, Object newValue) -> {
            boolean val = (boolean) newValue;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                NotificationManager notifManager = requireContext().getSystemService(NotificationManager.class);
                if(notifManager.areNotificationsEnabled()) {
                    this.settingsViewModel.updateNotifPref(val);
                }
                else {
                    if(ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        this.requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                        return false;
                    }
                }
            }
            else {
                this.settingsViewModel.updateNotifPref(val);
            }
            return true;
        });
    }

    private void showBackupNotification() {
        LocalDate lastBackupDate = this.settingsViewModel.lastBackupDate();
        String chName = this.getString(R.string.ch_name_backup);
        NotificationChannel backupNotifCl = new NotificationChannel("ch_backup", chName, NotificationManager.IMPORTANCE_HIGH);

        String notifText = this.getString(R.string.notif_text_backup) + " " + lastBackupDate;

        Notification backupNotif = new NotificationCompat.Builder(this.requireContext(), "ch_backup")
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle(this.getString(R.string.notif_title_backup))
                .setContentText(notifText)
                .setAutoCancel(true)
                .build();

        NotificationManager notifManager = requireContext().getSystemService(NotificationManager.class);
        notifManager.createNotificationChannel(backupNotifCl);
        notifManager.notify(100, backupNotif);
    }
}