package be.tobiridi.passwordsecurity.ui.addAccount;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;

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
    private TextInputLayout accountNameInputLayout, accountPasswordInputLayout;
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
        //if TextInputLayout fields are present in the layout, add them to init the EnumSet
        this.addedFields = EnumSet.of(AccountField.NAME, AccountField.PASSWORD);

        //get views id
        this.accountNameInputLayout = view.findViewById(R.id.accountField_name);
        this.accountPasswordInputLayout = view.findViewById(R.id.accountField_password);
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
    }

    private void resetForm() {
        this.addedFields.forEach(f -> {
            TextInputLayout input = this.addAccountLayout.findViewById(f.getId());
            input.setError(null);
            input.getEditText().getText().clear();
            //close soft keyboard
            input.getEditText().onEditorAction(EditorInfo.IME_ACTION_DONE);
        });
    }

    private void validateForm() {
        String msg = "";

        // TextInputLayout used to create the account
        List<TextInputLayout> inputFields = this.addedFields.stream()
                .map(f -> (TextInputLayout) this.addAccountLayout.findViewById(f.getId()))
                .collect(Collectors.toList());

        if (addAccountViewModel.createAccount(inputFields, this.addedFields)) {
            this.resetBtn.callOnClick();
            msg = this.getResources().getString(R.string.msg_add_account_success);
        }
        else {
            msg = this.getResources().getString(R.string.msg_add_account_fail);
        }

        Snackbar.make(this.requireContext(), this.requireView(), msg, Snackbar.LENGTH_SHORT)
                .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                .setAnchorView(R.id.bottomNavigationView)
                .show();
    }

    private void showFieldSelectionDialog() {
        List<AccountField> remainingFields = Arrays.stream(AccountField.values())
                .filter(f -> !this.addedFields.contains(f))
                .collect(Collectors.toList());

        String[] items = remainingFields.stream()
                .map(field -> getResources().getString(field.getLabel()))
                .toArray(String[]::new);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.add_field)
                .setItems(items, (dialog, which) -> {
                    AccountField selected = remainingFields.get(which);

                    if (remainingFields.size() == 1) {
                        this.addFieldFloatBtn.setEnabled(false);
                    }
                    this.addedFields.add(selected);
                    this.addDynamicField(selected);
                })
                .show();
    }

    private void addDynamicField(AccountField field) {
        // update the index if the layout associates to this fragment changed
        // get the index of the last input field
        int lastInputChildIndex = this.addAccountLayout.getChildCount() - 3;
        View lastChildInput = this.addAccountLayout.getChildAt(lastInputChildIndex);
        AccountFieldInputLayout customInputLayout = new AccountFieldInputLayout(this.requireContext(), field);

        // adapt the layout params for the custom TextInputLayout, move it below to the previous input field
        LinearLayout parentLayout = customInputLayout.getParentLayout();
        ViewGroup.LayoutParams parentLayoutParams = parentLayout.getLayoutParams();
        if (parentLayoutParams instanceof ConstraintLayout.LayoutParams) {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) parentLayoutParams;
            params.topToBottom = lastChildInput.getId();
            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        }

        customInputLayout.getDeleteButton().setOnClickListener(v -> {
            int removeIndex = this.addAccountLayout.indexOfChild(customInputLayout.getParentLayout());
            // update the next custom view position
            // change the condition if the layout associates to this fragment changed
            // only move if the next child is a custom TextInputLayout
            if (!(removeIndex + 1 >= this.addAccountLayout.getChildCount() - 2)) {
                View previousChild = this.addAccountLayout.getChildAt(removeIndex - 1);
                View nextChild = this.addAccountLayout.getChildAt(removeIndex + 1);
                ViewGroup.LayoutParams nextChildLayoutParams = nextChild.getLayoutParams();
                if (nextChildLayoutParams instanceof ConstraintLayout.LayoutParams) {
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) nextChildLayoutParams;
                    params.topToBottom = previousChild.getId();
                    nextChild.setLayoutParams(params);
                }
            }
            // don't move the next view because you delete the last custom view
            // the previous view is a XML hardcoded TextInputLayout
            this.addAccountLayout.removeViewAt(removeIndex);
            this.addedFields.remove(field);
            this.addFieldFloatBtn.setEnabled(true);
        });

        //position the custom TextInputLayout below the last TextInputLayout
        this.addAccountLayout.addView(customInputLayout.getParentLayout(), lastInputChildIndex + 1);
    }
}