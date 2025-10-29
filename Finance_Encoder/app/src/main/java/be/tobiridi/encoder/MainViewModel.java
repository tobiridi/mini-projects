package be.tobiridi.encoder;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import be.tobiridi.encoder.saving.JSONFileStorage;

public class MainViewModel extends ViewModel {
    private final ExecutorService _backgroundTask;
    private JSONFileStorage jsonFile;

    public MainViewModel() {
        this._backgroundTask = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Runnable r = () -> this.jsonFile.saveToFile();
        this._backgroundTask.submit(r);
        this._backgroundTask.close();
    }

    public boolean loadJsonFile(Context ctx) {
        // load only one time
        if (this.jsonFile == null) {
            Callable<Boolean> callable = () -> {
                try {
                    this.jsonFile = JSONFileStorage.getInstance(ctx);
                    return this.jsonFile != null;

                } catch (IOException | JSONException e) {
                    throw new RuntimeException(e);
                }
            };

            try {
                return this._backgroundTask.submit(callable).get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return this.jsonFile != null;
    }

    public String[] getCategories() {
        return this.jsonFile.getCategories();
    }

    public String[] getPaymentMethods() {
        return this.jsonFile.getPaymentMethods();
    }
}
