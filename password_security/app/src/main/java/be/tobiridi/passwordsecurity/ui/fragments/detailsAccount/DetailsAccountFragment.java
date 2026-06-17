package be.tobiridi.passwordsecurity.ui.fragments.detailsAccount;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.data.entities.Account;
import be.tobiridi.passwordsecurity.ui.components.accountField.AccountField;
import be.tobiridi.passwordsecurity.ui.components.accountField.AccountFieldInputLayout;

public class DetailsAccountFragment extends Fragment {
    private DetailsAccountViewModel detailsAccountViewModel;
    private final Account detailsAccount;
    private LinearLayout layout;
    private TextInputLayout accountNameInputLayout, accountPasswordInputLayout;

    private DetailsAccountFragment(Account account) {
        this.detailsAccount = account;
    }

    public static DetailsAccountFragment newInstance(Account account) {
        return new DetailsAccountFragment(account);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //get views id
        this.layout = view.findViewById(R.id.layout_detailsAccount);
        this.accountNameInputLayout = view.findViewById(R.id.accountField_name);
        this.accountPasswordInputLayout = view.findViewById(R.id.accountField_password);

        //disable modification, accept copy/past
        this.accountNameInputLayout.getEditText().setKeyListener(null);
        this.accountNameInputLayout.getEditText().setTextIsSelectable(true);
        this.accountNameInputLayout.getEditText().setShowSoftInputOnFocus(false);

        this.accountPasswordInputLayout.getEditText().setKeyListener(null);
        this.accountPasswordInputLayout.getEditText().setTextIsSelectable(true);
        this.accountPasswordInputLayout.getEditText().setShowSoftInputOnFocus(false);

        this.detailsAccountViewModel = new ViewModelProvider(this).get(DetailsAccountViewModel.class);
        this.detailsAccountViewModel.setAccount(this.detailsAccount);
        this.detailsAccountViewModel.getDetailsAccountUiState().observe(this.getViewLifecycleOwner(), (DetailsAccountUiState uiState) -> {
            Account acc = uiState.getAccount();
            this.accountNameInputLayout.getEditText().setText(acc.getName());
            this.accountPasswordInputLayout.getEditText().setText(acc.getPassword());
            this.displayAccountExtraFields(acc);
        });
    }

    private void displayAccountExtraFields(Account displayAccount) {
        if (displayAccount.getEmail() != null)
            this.addAccountView(displayAccount.getEmail(), AccountField.EMAIL);
        if (displayAccount.getUsername() != null)
            this.addAccountView(displayAccount.getUsername(), AccountField.USERNAME);
        if (displayAccount.getNote() != null)
            this.addAccountView(displayAccount.getNote(), AccountField.NOTE);
    }

    private void addAccountView(String text, AccountField accField) {
        AccountFieldInputLayout accountField = new AccountFieldInputLayout(this.requireContext(), accField);
        EditText editText = accountField.getEditText();

        editText.setText(text);
        accountField.getDeleteButton().setVisibility(View.GONE);

        //disable modification, accept copy/past
        editText.setKeyListener(null);
        editText.setTextIsSelectable(true);
        editText.setShowSoftInputOnFocus(false);

        this.layout.addView(accountField.getParentLayout());
    }
}