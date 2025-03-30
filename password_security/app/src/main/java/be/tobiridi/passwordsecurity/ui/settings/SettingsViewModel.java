package be.tobiridi.passwordsecurity.ui.settings;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.FileUtils;

import androidx.lifecycle.ViewModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import be.tobiridi.passwordsecurity.database.AppDatabase;

public class SettingsViewModel extends ViewModel {
    public final String BACKUP_FILE_NAME = "backup.sqlite";
    /** 2018 - new SQLite MIME type */
    public final String SQLITE_MIME_TYPE = "application/vnd.sqlite3";
    public final String[] OPEN_DOCUMENT_MIME_TYPE = {SQLITE_MIME_TYPE, "application/octet-stream"};
    private final ExecutorService _backgroundTask;

    public SettingsViewModel() {
        this._backgroundTask = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        this._backgroundTask.shutdown();
    }

    public boolean createBackup(Context context, Uri fileCreated) {
        Callable<Boolean> exportTask = (() -> {
            AppDatabase.getInstance(context).MakeWalCheckpoint();
            File dbFile = context.getDatabasePath(AppDatabase.DB_NAME);
            ContentResolver resolver = context.getContentResolver();

            try (FileInputStream fins = new FileInputStream(dbFile);
                 FileOutputStream fos = (FileOutputStream) resolver.openOutputStream(fileCreated)) {

                long bytesCopied = FileUtils.copy(fins, fos);
                fos.flush();
                return bytesCopied > 0;

            } catch (IOException e) {
                return false;
            }
        });

        try {
            return this._backgroundTask.submit(exportTask).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean importBackup(Context context, Uri fileSelected) {
        Callable<Boolean> importTask = (() -> {
            AppDatabase.closeDatabase();
            File dbFile = context.getDatabasePath(AppDatabase.DB_NAME);
            ContentResolver resolver = context.getContentResolver();

            try (FileInputStream fins = (FileInputStream) resolver.openInputStream(fileSelected);
                 FileOutputStream fos = new FileOutputStream(dbFile)) {

                long bytesCopied = FileUtils.copy(fins, fos);
                fos.flush();
                return bytesCopied > 0;

            } catch (IOException e) {
                return false;
            }
        });

        try {
            return this._backgroundTask.submit(importTask).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}