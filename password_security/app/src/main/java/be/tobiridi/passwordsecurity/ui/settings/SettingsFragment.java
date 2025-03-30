package be.tobiridi.passwordsecurity.ui.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import be.tobiridi.passwordsecurity.MainActivity;
import be.tobiridi.passwordsecurity.MainViewModel;
import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.ui.Authentication.AuthenticationActivity;

public class SettingsFragment extends PreferenceFragmentCompat {
    private SettingsViewModel settingsViewModel;
    private Preference importPreference;
    private Preference exportPreference;
    private SeekBarPreference attemptsPreference;
    private ListPreference deleteAllAccountsPreference;
    private ActivityResultLauncher<String> exportFileLauncher;
    private ActivityResultLauncher<String[]> importFileLauncher;

    public static SettingsFragment newInstance() { return new SettingsFragment(); }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        this.settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        //get preferences
        this.importPreference = findPreference("import_database");
        this.exportPreference = findPreference("export_database");
        this.attemptsPreference = findPreference("attempts");
        this.deleteAllAccountsPreference = findPreference("delete_all_accounts");

        //configure ActivityResultLauncher
        this.exportFileLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument(settingsViewModel.SQLITE_MIME_TYPE), new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri o) {
                        //null if the user does not create the document
                        if (o != null) {
                            String toastText = "";
                            Context ctx = requireContext().getApplicationContext();
                            //Android 12 does not support SQLite MIME type
                            //the MIME type is recognize as "application/octet-stream"
                            if (settingsViewModel.createBackup(ctx, o)) {
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
                            if (settingsViewModel.importBackup(ctx, o)) {
                                //TODO: make a better implementation,
                                // reload the app completely OR stay in the app and update database data to UI
                                // OR close all activity and relaunch the app to authenticate
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Reload the app");
                                builder.setPositiveButton(requireContext().getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //close the app, after rewrite de database file
                                        //SettingsFragment.this.requireActivity().finishAffinity();
                                        Intent intent = new Intent(SettingsFragment.this.requireActivity(), AuthenticationActivity.class);
                                        startActivity(intent);
                                        SettingsFragment.this.requireActivity().finish();
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

        this.initListeners();
    }

    private void initListeners() {
        this.exportPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                if (isActivityAssociate()) {
                    //prevent to close the activity
                    MainViewModel.setCloseMainActivity(false);
                }
                exportFileLauncher.launch(settingsViewModel.BACKUP_FILE_NAME);
                return true;
            }
        });

        this.importPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                if (isActivityAssociate()) {
                    //prevent to close the activity
                    MainViewModel.setCloseMainActivity(false);
                }
                importFileLauncher.launch(settingsViewModel.OPEN_DOCUMENT_MIME_TYPE);
                return true;
            }
        });
    }

    private boolean isActivityAssociate() {
        FragmentActivity activity = this.requireActivity();
        return (activity instanceof MainActivity);
    }
}