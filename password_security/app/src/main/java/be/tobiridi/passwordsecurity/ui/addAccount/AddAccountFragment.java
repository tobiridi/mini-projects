package be.tobiridi.passwordsecurity.ui.addAccount;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.component.TextWatcherResetError;
import be.tobiridi.passwordsecurity.component.accountField.AccountField;
import be.tobiridi.passwordsecurity.component.accountField.AccountFieldInputLayout;

public class AddAccountFragment extends Fragment {
    private AddAccountViewModel addAccountViewModel;
    private TextInputLayout accountNameInputLayout, accountPasswordInputLayout, accountConfirmPasswordInputLayout;
    private Button resetBtn, validateBtn;
    private FloatingActionButton addFieldFloatBtn;
    private EnumSet<AccountField> addedFields;
    private ConstraintLayout addAccountLayout;

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
        this.accountNameInputLayout = view.findViewById(R.id.accountField_name);
        this.accountPasswordInputLayout = view.findViewById(R.id.accountField_password);
        this.accountConfirmPasswordInputLayout = view.findViewById(R.id.accountField_confirm_password);
        this.resetBtn = view.findViewById(R.id.btn_reset);
        this.validateBtn = view.findViewById(R.id.btn_validate);
        this.addFieldFloatBtn = view.findViewById(R.id.floatBtn_add_field);
        this.addAccountLayout = view.findViewById(R.id.layout_addAccount);

        this.addAccountViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(AddAccountViewModel.initializer)).get(AddAccountViewModel.class);
        this.initListeners();
    }

    private void initListeners() {
        this.resetBtn.setOnClickListener(v -> resetForm());
        this.validateBtn.setOnClickListener(v -> validateForm());
        this.addFieldFloatBtn.setOnClickListener(v -> showFieldSelectionDialog());
        this.accountNameInputLayout.getEditText().addTextChangedListener(new TextWatcherResetError(this.accountNameInputLayout));
        this.accountPasswordInputLayout.getEditText().addTextChangedListener(new TextWatcherResetError(this.accountPasswordInputLayout));
        this.accountConfirmPasswordInputLayout.getEditText().addTextChangedListener(new TextWatcherResetError(this.accountConfirmPasswordInputLayout));
    }

    private void resetForm() {
        for (int i = 0; i < this.addAccountLayout.getChildCount(); i++) {
            View child = this.addAccountLayout.getChildAt(i);
            if (child instanceof TextInputLayout) {
                TextInputLayout input = (TextInputLayout) child;
                input.setError(null);
                input.getEditText().getText().clear();
                //close soft keyboard
                input.getEditText().onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        }
    }

    private void validateForm() {
        String msg = "";

        //TODO: make verification
//        if (addAccountViewModel.createAccount(accountNameInputLayout, accountEmailInputLayout, accountPasswordInputLayout)) {
//            resetBtn.callOnClick();
//            msg = v.getResources().getString(R.string.msg_add_account_success);
//        }
//        else {
//            msg = v.getResources().getString(R.string.msg_add_account_fail);
//        }

        Snackbar.make(this.requireContext(), this.getView(), msg, Snackbar.LENGTH_SHORT)
                .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                .setAnchorView(R.id.bottomNavigationView)
                .show();
    }

    private void showFieldSelectionDialog() {
        List<AccountField> remainingFields;
        // null when the EnumSet is empty
        if (this.addedFields != null) {
            remainingFields = Arrays.stream(AccountField.values())
                    .filter(f -> !addedFields.contains(f))
                    .collect(Collectors.toList());
        }
        else {
            remainingFields = Arrays.stream(AccountField.values())
                    .collect(Collectors.toList());
        }

        String[] items = remainingFields.stream()
                .map(field -> getResources().getString(field.getLabel()))
                .toArray(String[]::new);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.add_field)
                .setItems(items, (dialog, which) -> {
                    AccountField selected = remainingFields.get(which);
                    if (this.addedFields == null) {
                        this.addedFields = EnumSet.of(selected);
                    }
                    else {
                        this.addedFields.add(selected);
                    }

                    //TODO: always display all fields or add wanted field ????
                    // disable button when no option left ????
                    if (remainingFields.size() == 1) {
                        this.addFieldFloatBtn.setEnabled(false);
                    }
                    this.addDynamicField(selected);
                })
                .show();
    }

    private void addDynamicField(AccountField field) {
        //update the index if the layout associates to this fragment changed
        int lastInputLayoutIndex = this.addAccountLayout.getChildCount() - 3;
        TextInputLayout lastInputLayout = (TextInputLayout) this.addAccountLayout.getChildAt(lastInputLayoutIndex);
        AccountFieldInputLayout customInputLayout = new AccountFieldInputLayout(requireContext(), field);

        //adapt the layout params for the custom TextInputLayout
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.topToBottom = lastInputLayout.getId();
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        //convert 16px to 16dp
        layoutParams.topMargin = Math.round(16 * this.getResources().getDisplayMetrics().density);
        customInputLayout.setLayoutParams(layoutParams);

        //position the custom TextInputLayout below the last TextInputLayout
        this.addAccountLayout.addView(customInputLayout, lastInputLayoutIndex + 1);
    }

}