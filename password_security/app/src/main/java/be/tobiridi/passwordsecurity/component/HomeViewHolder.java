package be.tobiridi.passwordsecurity.component;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.data.Account;

/**
 * All functionality for the home fragment RecyclerView.
 */
public class HomeViewHolder extends RecyclerView.ViewHolder {
    private final TextView _accountName;
    private final TextView _accountEmail;
    private Account account;
    private final HomeAdapter _adapter;

    public HomeViewHolder(@NonNull View itemView, HomeAdapter adapter) {
        super(itemView);
        this._adapter = adapter;
        this._accountName = itemView.findViewById(R.id.accountName);
        this._accountEmail = itemView.findViewById(R.id.accountEmail);

        itemView.setOnClickListener(this.layoutClick());
        itemView.setOnLongClickListener(this.layoutLongClick());
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
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                View dialogView = LayoutInflater.from(builder.getContext())
                        .inflate(R.layout.dialog_account_info, null);

                //get views id
                TextView emailTextView = dialogView.findViewById(R.id.tv_email);
                TextView passwordTextView = dialogView.findViewById(R.id.tv_password);

                emailTextView.setText(account.getEmail());
                passwordTextView.setText(account.getPassword());

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

    private View.OnLongClickListener layoutLongClick() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(R.string.alert_account_change);
                builder.setMessage(account.getName() + "\n" + account.getEmail() + "\n" + account.getPassword());
                builder.setPositiveButton(R.string.modify, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO: update the account
                    }
                });
                builder.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int pos = HomeViewHolder.this.getAdapterPosition();
                        HomeViewHolder.this._adapter.deleteAccount(account, pos);
                    }
                });
                builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return true;
            }
        };
    }

}
