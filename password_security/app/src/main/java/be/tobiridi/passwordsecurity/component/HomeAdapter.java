package be.tobiridi.passwordsecurity.component;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.data.Account;

/**
 * Link the {@link HomeViewHolder} to the RecyclerView.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeViewHolder> {
    private List<Account> accounts;
    private int lastSize;

    public HomeAdapter(@NonNull List<Account> accounts) {
        this.accounts = accounts;
        this.lastSize = this.accounts.size();
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item_home, parent, false);

        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        Account acc = this.accounts.get(position);
        holder.setAccount(acc);
    }

    @Override
    public int getItemCount() {
        return this.accounts.size();
    }

    public void updateAccounts() {
        int currentSize = this.getItemCount();

        if (this.lastSize < currentSize) {
            //item added
            this.notifyItemInserted(currentSize);
            this.lastSize = currentSize;
        }
    }
}
