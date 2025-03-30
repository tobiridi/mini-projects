package be.tobiridi.passwordsecurity;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.app.Application;
import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.HashMap;

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
    private final HashMap<Integer, Fragment> _fragments;
    private Fragment currentFragDisplay;

    public MainViewModel(Context context) {
        this._userPreferencesDataSource = UserPreferencesDataSource.getInstance(context);
        this._accountDataSource = AccountDataSource.getInstance(context);
        this._fragments = new HashMap<>();
        this.currentFragDisplay = null;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        this._userPreferencesDataSource.closeExecutorService();
        this._accountDataSource.closeExecutorService();
    }

    public Fragment getCurrentFragDisplay() {
        return this.currentFragDisplay;
    }

    public void setCurrentFragDisplay(Fragment currentFragDisplay) {
        this.currentFragDisplay = currentFragDisplay;
    }

    public void putFragment(int resourceId, Fragment fragment) {
        this._fragments.put(resourceId, fragment);
    }

    public Fragment getFragment(int resourceId) {
        return this._fragments.get(resourceId);
    }
}
