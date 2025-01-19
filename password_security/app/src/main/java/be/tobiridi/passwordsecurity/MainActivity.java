package be.tobiridi.passwordsecurity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import be.tobiridi.passwordsecurity.ui.addAccount.AddAcountFragment;
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.mainViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(MainViewModel.initializer)).get(MainViewModel.class);

        //get views id
        this.fragmentContainer = findViewById(R.id.fragmentContainerView);
        this.bottomNavigation = findViewById(R.id.bottomNavigationView);

        this.initFragmentManager();
        this.initListeners();
    }

    private void initFragmentManager() {
        HomeFragment homeFrag = HomeFragment.newInstance();
        AddAcountFragment addAccountFrag = AddAcountFragment.newInstance();
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
                    .addToBackStack(null)
                    .setReorderingAllowed(true)
                    .commit();

            mainViewModel.setCurrentFragDisplay(selectedFrag);
            return true;
        });
    }
}