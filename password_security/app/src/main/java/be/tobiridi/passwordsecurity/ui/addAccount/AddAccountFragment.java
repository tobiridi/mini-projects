package be.tobiridi.passwordsecurity.ui.addAccount;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.component.TextWatcherResetError;

public class AddAccountFragment extends Fragment {
    private AddAccountViewModel addAccountViewModel;
    private TextInputLayout accountNameInputLayout, accountEmailInputLayout, accountPasswordInputLayout;
    private Button resetBtn, validateBtn;

    public static AddAccountFragment newInstance() {
        return new AddAccountFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //get views id
        this.accountNameInputLayout = view.findViewById(R.id.inputLayout_accountName);
        this.accountEmailInputLayout = view.findViewById(R.id.inputLayout_accountEmail);
        this.accountPasswordInputLayout = view.findViewById(R.id.inputLayout_accountPassword);
        this.resetBtn = view.findViewById(R.id.btn_reset);
        this.validateBtn = view.findViewById(R.id.btn_validate);

        this.addAccountViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(AddAccountViewModel.initializer)).get(AddAccountViewModel.class);
        this.initListeners();
    }

    private void initListeners() {
        this.resetBtn.setOnClickListener(this.resetListener());
        this.validateBtn.setOnClickListener(this.validateListener());
        this.accountNameInputLayout.getEditText().addTextChangedListener(new TextWatcherResetError(this.accountNameInputLayout));
        this.accountEmailInputLayout.getEditText().addTextChangedListener(new TextWatcherResetError(this.accountEmailInputLayout));
        this.accountPasswordInputLayout.getEditText().addTextChangedListener(new TextWatcherResetError(this.accountPasswordInputLayout));
    }

    private View.OnClickListener resetListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountNameInputLayout.setError(null);
                accountNameInputLayout.getEditText().getText().clear();
                
                accountEmailInputLayout.setError(null);
                accountEmailInputLayout.getEditText().getText().clear();

                accountPasswordInputLayout.setError(null);
                accountPasswordInputLayout.getEditText().getText().clear();
            }
        };
    }

    private View.OnClickListener validateListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = "";

                if (addAccountViewModel.createAccount(accountNameInputLayout, accountEmailInputLayout, accountPasswordInputLayout)) {
                    resetBtn.callOnClick();
                    msg = v.getResources().getString(R.string.msg_add_account_success);
                }
                else {
                    msg = v.getResources().getString(R.string.msg_add_account_fail);
                }

                //close soft keyboard
                accountNameInputLayout.getEditText().onEditorAction(EditorInfo.IME_ACTION_DONE);
                accountEmailInputLayout.getEditText().onEditorAction(EditorInfo.IME_ACTION_DONE);
                accountPasswordInputLayout.getEditText().onEditorAction(EditorInfo.IME_ACTION_DONE);

                Snackbar.make(v.getContext(), v, msg, Snackbar.LENGTH_SHORT)
                        .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                        .setAnchorView(R.id.bottomNavigationView)
                        .show();
            }
        };
    }

}