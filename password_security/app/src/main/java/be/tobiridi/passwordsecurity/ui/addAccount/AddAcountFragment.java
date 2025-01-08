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
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

import be.tobiridi.passwordsecurity.R;

public class AddAcountFragment extends Fragment {
    private AddAcountViewModel addAcountViewModel;
    private EditText editTextAccountName, editTextAccountEmail, editTextAccountPassword;
    private Button resetBtn, validateBtn;

    public static AddAcountFragment newInstance() {
        return new AddAcountFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_acount, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //get views id
        this.editTextAccountName = view.findViewById(R.id.editText_account_name);
        this.editTextAccountEmail = view.findViewById(R.id.editText_email);
        this.editTextAccountPassword = view.findViewById(R.id.editText_password);
        this.resetBtn = view.findViewById(R.id.btn_reset);
        this.validateBtn = view.findViewById(R.id.btn_validate);

        //set buttons listeners
        this.resetBtn.setOnClickListener(this.resetListener());
        this.validateBtn.setOnClickListener(this.validateListener());

        this.addAcountViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(AddAcountViewModel.initializer)).get(AddAcountViewModel.class);
    }

    private View.OnClickListener resetListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextAccountName.getText().clear();
                editTextAccountEmail.getText().clear();
                editTextAccountPassword.getText().clear();
            }
        };
    }

    private View.OnClickListener validateListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg;

                if (addAcountViewModel.createAccount(editTextAccountName, editTextAccountEmail, editTextAccountPassword)) {
                    resetBtn.callOnClick();
                    msg = v.getResources().getString(R.string.msg_add_account_success);
                }
                else {
                    msg = v.getResources().getString(R.string.msg_add_account_fail);
                }

                //close soft keyboard
                editTextAccountName.onEditorAction(EditorInfo.IME_ACTION_DONE);
                editTextAccountEmail.onEditorAction(EditorInfo.IME_ACTION_DONE);
                editTextAccountPassword.onEditorAction(EditorInfo.IME_ACTION_DONE);

                Snackbar.make(v.getContext(), v, msg, Snackbar.LENGTH_SHORT)
                        .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                        .show();
            }
        };
    }

}