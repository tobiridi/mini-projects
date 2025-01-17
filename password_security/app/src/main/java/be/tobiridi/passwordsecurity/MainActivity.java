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

import java.util.HashMap;

import be.tobiridi.passwordsecurity.ui.addAccount.AddAcountFragment;
import be.tobiridi.passwordsecurity.ui.home.HomeFragment;
import be.tobiridi.passwordsecurity.ui.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {
    private MainViewModel mainViewModel;
    private FragmentContainerView fragmentContainer;
    private BottomNavigationView bottomNavigation;
    private final HashMap<Integer, Fragment> _fragments = new HashMap<>();
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

        //set fragments for navigation bar
        this._fragments.put(R.id.nav_home, HomeFragment.newInstance());
        this._fragments.put(R.id.nav_add, AddAcountFragment.newInstance());
        this._fragments.put(R.id.nav_settings, SettingsFragment.newInstance());

        this.initListener();
    }

    private void updateFragment(Fragment fragment) {
        this._manager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .setReorderingAllowed(true)
                .commit();
    }

    private void initListener() {
        this.bottomNavigation.setOnItemSelectedListener(item -> {
            this.updateFragment(this._fragments.get(item.getItemId()));
            return true;
        });

    }
}