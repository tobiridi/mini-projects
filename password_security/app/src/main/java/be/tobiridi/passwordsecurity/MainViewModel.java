package be.tobiridi.passwordsecurity;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.app.Application;
import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.HashMap;

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

    private final HashMap<Integer, Fragment> _fragments;
    private Fragment currentFragDisplay;
    /**
     * Used to prevent close the activity and switch to user authentication.
     */
    private static boolean closeActivity;

    public MainViewModel(Context context) {
        this._fragments = new HashMap<>();
        this.currentFragDisplay = null;
        MainViewModel.closeActivity = true;
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

    public static boolean isCloseMainActivity() {
        return MainViewModel.closeActivity;
    }

    /**
     * Set the new state to {@code false} to prevent finish the {@link MainActivity} when switch activity
     * <b>(e.g: select file from device)</b>.
     * <br/>
     * Set to {@code true} if the activity can be finished.
     * <br/>
     * Set to {@code false} if the activity can not be finished.
     * @param newValue The new state.
     * @see MainViewModel#isCloseMainActivity()
     */
    public static void setCloseMainActivity(boolean newValue) {
        MainViewModel.closeActivity = newValue;
    }
}
