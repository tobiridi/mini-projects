package be.tobiridi.passwordsecurity;

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
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import be.tobiridi.passwordsecurity.ui.addAccount.AddAccountFragment;
import be.tobiridi.passwordsecurity.ui.home.HomeFragment;
import be.tobiridi.passwordsecurity.ui.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {
    private MainViewModel mainViewModel;
    private FragmentContainerView fragmentContainer;
    private BottomNavigationView bottomNavigation;
    private final FragmentManager _manager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        //set status bar padding on layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
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

        this.initFragmentManager();
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

    private void initFragmentManager() {
        HomeFragment homeFrag = HomeFragment.newInstance();
        AddAccountFragment addAccountFrag = AddAccountFragment.newInstance();
        SettingsFragment settingsFrag = SettingsFragment.newInstance();

        this._manager.beginTransaction()
                .add(this.fragmentContainer.getId(), homeFrag, "HOME")
                .add(this.fragmentContainer.getId(), addAccountFrag, "ADD_ACCOUNT")
                .add(this.fragmentContainer.getId(), settingsFrag, "SETTINGS")
                .hide(addAccountFrag)
                .hide(settingsFrag)
                .commit();

        this.mainViewModel.putFragment(R.id.nav_home, homeFrag);
        this.mainViewModel.putFragment(R.id.nav_add, addAccountFrag);
        this.mainViewModel.putFragment(R.id.nav_settings, settingsFrag);

        this.mainViewModel.setCurrentFragDisplay(homeFrag);
    }

    private void initListeners() {
        this.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFrag = mainViewModel.getFragment(item.getItemId());

            this._manager.beginTransaction()
                    .hide(mainViewModel.getCurrentFragDisplay())
                    .show(selectedFrag)
                    .commit();

            mainViewModel.setCurrentFragDisplay(selectedFrag);
            return true;
        });
    }
}