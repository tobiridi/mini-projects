package be.tobiridi.passwordsecurity;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import be.tobiridi.passwordsecurity.data.AccountDataSource;
import be.tobiridi.passwordsecurity.data.UserPreferencesDataSource;

public class MainViewModel extends ViewModel {
    /*********************/
    /* ViewModel Factory */
    /*********************/
    public static final ViewModelInitializer<MainViewModel> initializer = new ViewModelInitializer<>(
            MainViewModel.class,
            creationExtras -> {
                Application app = creationExtras.get(APPLICATION_KEY);
                assert app != null;

                return new MainViewModel(app.getApplicationContext());
            }
    );

    private final UserPreferencesDataSource _userPreferencesDataSource;
    private final AccountDataSource _accountDataSource;

    public MainViewModel(Context context) {
        this._userPreferencesDataSource = UserPreferencesDataSource.getInstance(context);
        this._accountDataSource = AccountDataSource.getInstance(context);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        this._userPreferencesDataSource.closeExecutorService();
        this._accountDataSource.closeExecutorService();
    }

}
