package be.tobiridi.passwordsecurity.component;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.data.Account;
import be.tobiridi.passwordsecurity.ui.home.HomeViewModel;

/**
 * Link the {@link HomeViewHolder} to the RecyclerView.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeViewHolder> {
    private List<Account> accounts;
    private int lastSize;
    private final HomeViewModel _homeViewModel;

    public HomeAdapter(@NonNull List<Account> accounts, HomeViewModel homeViewModel) {
        this.accounts = accounts;
        this.lastSize = this.accounts.size();
        this._homeViewModel = homeViewModel;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item_home, parent, false);

        return new HomeViewHolder(view, this);
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
            this.notifyItemInserted(currentSize - 1);
            this.lastSize = currentSize;
        }
        else if (this.lastSize > currentSize) {
            //item already deleted
            this.lastSize = currentSize;
        }
    }

    public void deleteAccount(Account account, int position) {
        if (this._homeViewModel.deleteAccount(account)) {
            this.notifyItemRemoved(position);
        }
    }
}
