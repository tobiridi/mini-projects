package be.tobiridi.passwordsecurity.ui.settings;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.database.AppDatabase;

public class SettingsFragment extends PreferenceFragmentCompat {
    private SettingsViewModel settingsViewModel;
    private Preference importPreference;
    private Preference exportPreference;
    private SeekBarPreference attemptsPreference;
    private ListPreference deleteAllAccountsPreference;
    private ActivityResultLauncher<String> exportFileLauncher;
    //private ActivityResultLauncher<String[]> importFileLauncher;

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

        this.exportFileLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument(settingsViewModel.SQLITE_MIME_TYPE), new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri o) {
                        //null if the user does not create the document
                        if (o != null) {
                            // get database file
                            Context appContext = requireContext().getApplicationContext();
                            File dbFile = appContext.getDatabasePath(AppDatabase.DB_NAME);

                            if (dbFile.canRead()) {
                                ContentResolver resolver = appContext.getContentResolver();

                                try (FileInputStream fins = new FileInputStream(dbFile);
                                     OutputStream fos = resolver.openOutputStream(o)) {
                                    long bytesCopied = FileUtils.copy(fins, fos);
                                    if (bytesCopied > 0) {
                                        Toast.makeText(getContext(), getResources().getString(R.string.msg_backup_created), Toast.LENGTH_SHORT)
                                                .show();
                                    }

                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                });
//
//        this.importFileLauncher = registerForActivityResult(
//                new ActivityResultContracts.OpenDocument(), new ActivityResultCallback<Uri>() {
//                    @Override
//                    public void onActivityResult(Uri o) {
//                        //null if the user does not open a document
//                        if (o != null) {
//                            System.out.println(o);
//
//                            Context appContext = requireContext().getApplicationContext();
//                            //close database
//                            AppDatabase database = AppDatabase.getInstance(appContext);
//                            if (database.isOpen()) {
//                                database.close();
//                            }
//
//                            File dbFile = appContext.getDatabasePath(AppDatabase.DB_NAME);
//
//                            if (dbFile.canRead()) {
//                                ContentResolver resolver = appContext.getContentResolver();
//
//                                try (FileInputStream fins = resolver.openInputStream(o);) {
//                                    long bytesCopied = FileUtils.copy(fins, resolver.openOutputStream(o));
//                                    if (bytesCopied > 0) {
//                                        Toast.makeText(getContext(), getResources().getString(R.string.msg_backup_created), Toast.LENGTH_SHORT)
//                                                .show();
//                                    }
//
//                                } catch (IOException e) {
//                                    throw new RuntimeException(e);
//                                }
//                            }
//                        }
//                    }
//                });

        this.initListeners();
    }

    private void initListeners() {
        this.exportPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                AppDatabase.getInstance(getContext()).MakeWalCheckpoint();
                exportFileLauncher.launch(settingsViewModel.BACKUP_FILE_NAME);
                return true;
            }
        });

//        this.importPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(@NonNull Preference preference) {
//                String[] input = {settingsViewModel.SQLITE_MIME_TYPE};
//                importFileLauncher.launch(input);
//                return true;
//            }
//        });
    }
}