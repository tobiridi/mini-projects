package be.tobiridi.passwordsecurity.ui.settings;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.ui.home.HomeFragment;

public class SettingsFragment extends PreferenceFragmentCompat {
    private SettingsViewModel settingsViewModel;

    public static SettingsFragment newInstance() { return new SettingsFragment(); }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        this.initListeners();

        //TODO : links
        // https://developer.android.com/topic/libraries/architecture/datastore?hl=fr
        // https://developer.android.com/reference/android/preference/Preference
        // https://developer.android.com/develop/ui/views/components/settings?hl=fr#java
        // https://developer.android.com/develop/ui/views/components/settings/components-and-attributes?hl=fr
        // https://source.android.com/docs/core/settings/settings-guidelines?hl=fr
    }

    private void initListeners() {

    }
}