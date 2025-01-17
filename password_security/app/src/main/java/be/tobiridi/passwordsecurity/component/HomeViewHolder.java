package be.tobiridi.passwordsecurity.component;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.data.Account;

/**
 * All functionality for the home fragment RecyclerView.
 */
public class HomeViewHolder extends RecyclerView.ViewHolder {
    private final TextView _accountName;
    private final TextView _accountEmail;
    private final ConstraintLayout _layout;
    private Account account;

    public HomeViewHolder(@NonNull View itemView) {
        super(itemView);
        this._accountName = itemView.findViewById(R.id.accountName);
        this._accountEmail = itemView.findViewById(R.id.accountEmail);
        this._layout = itemView.findViewById(R.id.rv_item);

        this._layout.setOnClickListener(this.layoutClick());
    }

    public void setAccount(Account account) {
        this.account = account;

        this._accountName.setText(account.getName());
        this._accountEmail.setText(account.getEmail());
    }

    private View.OnClickListener layoutClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: maybe better option to create custom with different layout
                View dialogView = LayoutInflater.from(v.getContext())
                                .inflate(R.layout.dialog_account_info, null);

                //get views id
                TextView emailTextView = dialogView.findViewById(R.id.tv_email);
                TextView passwordTextView = dialogView.findViewById(R.id.tv_password);

                emailTextView.setText(account.getEmail());
                passwordTextView.setText(account.getPassword());

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(account.getName());
                builder.setView(dialogView);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        };
    }

}
