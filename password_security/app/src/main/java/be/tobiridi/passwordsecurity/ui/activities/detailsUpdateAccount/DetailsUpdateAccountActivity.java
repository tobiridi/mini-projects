package be.tobiridi.passwordsecurity.ui.activities.detailsUpdateAccount;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.data.entities.Account;
import be.tobiridi.passwordsecurity.ui.fragments.addAccount.AddAccountFragment;
import be.tobiridi.passwordsecurity.ui.fragments.detailsAccount.DetailsAccountFragment;
import be.tobiridi.passwordsecurity.ui.fragments.home.HomeFragment;
import be.tobiridi.passwordsecurity.ui.fragments.settings.SettingsFragment;

public class DetailsUpdateAccountActivity extends AppCompatActivity {
    /**
     * Name of the intent parameter to send an {@link Account} to this activity.
     */
    public static final String INTENT_ACCOUNT_DETAILS = "account";
    private MaterialButton updateBtn;
    private MaterialToolbar toolbar;
    private FragmentContainerView fragmentContainer;
    private final FragmentManager _fragmentManager = this.getSupportFragmentManager();
    private DetailsUpdateAccountViewModel detailsUpdateViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details_update_account);

        //get views id
        this.fragmentContainer = this.findViewById(R.id.fragmentContainerView);
        this.toolbar = this.findViewById(R.id.toolbar);
        this.updateBtn = this.findViewById(R.id.btn_update_account);
        this.toolbar.setTitle(R.string.toolbar_title_account_details);
        this.setSupportActionBar(toolbar);

        this.detailsUpdateViewModel = new ViewModelProvider(this).get(DetailsUpdateAccountViewModel.class);

        this.initListeners();
        this.initFragManager();

        this.detailsUpdateViewModel.getDetailsUpdateUiState().observe(this, (DetailsUpdateAccountUiState uiState) -> {
            if (uiState.isInEditMode()) {
                this.toolbar.setTitle(R.string.toolbar_title_account_update);
                this.updateBtn.setText(R.string.done);
                this.updateBtn.setIcon(AppCompatResources.getDrawable(this, R.drawable.round_check_24));
                this.updateBtn.setContentDescription(this.getString(R.string.btn_desc_save));
            }
            else {
                this.toolbar.setTitle(R.string.toolbar_title_account_details);
                this.updateBtn.setText(R.string.modify);
                this.updateBtn.setIcon(AppCompatResources.getDrawable(this, R.drawable.round_edit_24));
                this.updateBtn.setContentDescription(this.getString(R.string.btn_desc_modify));
            }
        });
    }

    private void initListeners() {
        this.toolbar.setNavigationOnClickListener((View navBtn) -> {
            this.getOnBackPressedDispatcher().onBackPressed();
        });

        this.updateBtn.setOnClickListener((View btn) -> {
            detailsUpdateViewModel.switchEditMode();
            this.switchFragmentDisplay();
        });
    }

    private void initFragManager() {
        //get account data
        Account displayAccount = (Account) this.getIntent().getSerializableExtra(INTENT_ACCOUNT_DETAILS);

        //set the fragments to fragment manager
        int fragContainerId = this.fragmentContainer.getId();
        // TODO: 09/06/2026 add fragments instance
        Fragment detailsFrag = DetailsAccountFragment.newInstance(displayAccount);
        Fragment modifyFrag = null;

        this._fragmentManager.beginTransaction()
                .add(fragContainerId, detailsFrag, "FRAGMENT_ACCOUNT_DETAILS")
//                .add(fragContainerId, modifyFrag, "FRAGMENT_ACCOUNT_MODIFY")
//                .hide(modifyFrag)
                .commit();
    }

    private void switchFragmentDisplay() {
        // TODO: 17/06/2026 switch to update or details account fragment
        int fragContainerId = this.fragmentContainer.getId();
    }
}