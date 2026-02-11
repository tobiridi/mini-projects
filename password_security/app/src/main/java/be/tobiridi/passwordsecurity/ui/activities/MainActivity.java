package be.tobiridi.passwordsecurity.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.ui.fragments.addAccount.AddAccountFragment;
import be.tobiridi.passwordsecurity.ui.fragments.home.HomeFragment;
import be.tobiridi.passwordsecurity.ui.fragments.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {
    private MainViewModel mainViewModel;
    private FragmentContainerView fragmentContainer;
    private BottomNavigationView bottomNavigation;
    private final FragmentManager _fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        //set status bar padding on layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layout_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        //set navigation bar padding on bottomNavigationView
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottomNavigationView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, 0);
            return insets;
        });

        this.mainViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(MainViewModel.initializer)).get(MainViewModel.class);

        //get views id
        this.fragmentContainer = findViewById(R.id.fragmentContainerView);
        this.bottomNavigation = findViewById(R.id.bottomNavigationView);

        this.initObservers();
        this.initListeners();
    }

    @Override
    protected void onStop() {
        super.onStop();
        PowerManager manager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        //occurred when lock the device
        if (!manager.isInteractive())
            this.finishAffinity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mainViewModel.getMainUiState().removeObservers(this);
    }

    private void initObservers() {
        this.mainViewModel.getMainUiState().observe(this, (MainUiState uiState) -> {
            if(!uiState.isFragManagerInit()) {
                //set the fragments to fragment manager
                int fragContainerId = this.fragmentContainer.getId();
                HomeFragment homeFrag = HomeFragment.newInstance();
                AddAccountFragment addAccountFrag = AddAccountFragment.newInstance();
                SettingsFragment settingsFrag = SettingsFragment.newInstance();

                this._fragmentManager.beginTransaction()
                        .add(fragContainerId, homeFrag, "FRAGMENT_HOME")
                        .add(fragContainerId, addAccountFrag, "FRAGMENT_ADD_ACCOUNT")
                        .add(fragContainerId, settingsFrag, "FRAGMENT_SETTINGS")
                        .hide(homeFrag)
                        .hide(addAccountFrag)
                        .hide(settingsFrag)
                        .commit();

                //update the UI to display a fragment otherwise everything is hidden
                this.mainViewModel.updateDisplayFragment(homeFrag);
            }
            else {
                FragmentTransaction transaction = this._fragmentManager.beginTransaction();
                Fragment previous = uiState.getPreviousFragDisplay();
                //can be null if the first fragment to display
                if (previous != null) {
                    transaction.hide(previous);
                }
                transaction.show(uiState.getCurrentFragDisplay());
                transaction.commit();
            }
        });
    }

    private void initListeners() {
        this.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            String tag = null;

            if (itemId == R.id.nav_home) {
                tag = "FRAGMENT_HOME";
            }
            else if (itemId == R.id.nav_add) {
                tag = "FRAGMENT_ADD_ACCOUNT";
            }
            else if (itemId == R.id.nav_settings) {
                tag = "FRAGMENT_SETTINGS";
            }

            if (tag != null) {
                Fragment fragment = this._fragmentManager.findFragmentByTag(tag);
                mainViewModel.updateDisplayFragment(fragment);
                return true;
            }
            return false;
        });
    }
}