package be.tobiridi.passwordsecurity.component;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import be.tobiridi.passwordsecurity.R;

/**
 * All functionality for the home fragment RecyclerView.
 */
public class HomeViewHolder extends RecyclerView.ViewHolder {
    private final TextView _accountName;
    private final TextView _accountEmail;

    public HomeViewHolder(@NonNull View itemView) {
        super(itemView);
        this._accountName = itemView.findViewById(R.id.accountName);
        this._accountEmail = itemView.findViewById(R.id.accountEmail);
    }

    public void setNewAccount(String name, String email) {
        this._accountName.setText(name);
        this._accountEmail.setText(email);
    }

}
