package be.tobiridi.passwordsecurity.component;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.data.Account;
import be.tobiridi.passwordsecurity.ui.home.HomeViewModel;

/**
 * Link the {@link HomeViewHolder} to the RecyclerView.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeViewHolder> implements Filterable {
    private List<Account> sourceAccounts;
    private List<Account> filteredAccounts;
    private final HomeViewModel _homeViewModel;
    private Filter filter;

    public HomeAdapter(@NonNull List<Account> sourceAccounts, HomeViewModel homeViewModel) {
        this.sourceAccounts = sourceAccounts;
        //use a different reference than source account, used to apply a filter
        this.filteredAccounts = new ArrayList<>(sourceAccounts);
        this._homeViewModel = homeViewModel;
        this.filter = this.getFilter();
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
        Account acc = this.filteredAccounts.get(position);
        holder.setAccount(acc);
    }

    @Override
    public int getItemCount() {
        return this.filteredAccounts.size();
    }

    @Override
    public Filter getFilter() {
        //reuse the same filter
        if (this.filter == null) {
            this.filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    List<Account> filtered;

                    if (constraint != null) {
                        filtered = sourceAccounts.stream()
                                .filter(a -> a.getName().toLowerCase().contains(constraint))
                                .collect(Collectors.toList());
                    }
                    else {
                        filtered = filteredAccounts;
                    }

                    results.values = filtered;
                    results.count = filtered.size();
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    //TODO: implement DiffUtil for better performance, if the list is big
                    // https://developer.android.com/reference/androidx/recyclerview/widget/DiffUtil

                    int count = getItemCount();
                    if (results.count == count) {
                        //do not recreate views, filter retrieves the same list
                        return;
                    }

                    //recreate all views
                    HomeAdapter.this.notifyItemRangeRemoved(0, count);
                    HomeAdapter.this.filteredAccounts = (List<Account>) results.values;
                    HomeAdapter.this.notifyItemRangeInserted(0, filteredAccounts.size());
                }
            };
        }
        return this.filter;
    }

    /**
     * Should be trigger when the source data changed.
     */
    public void sourceAccountsChanged(List<Account> accounts) {
        //DELETE : occurred when the user interact with a viewHolder
        //UPDATE : ???
        this.sourceAccounts = accounts;
        int sourceSize = this.sourceAccounts.size();
        int currentSize = this.getItemCount();
        int diffSize = sourceSize - currentSize;

        //DELETE ALL
        if (sourceSize == 0) {
            this.filteredAccounts.clear();
            this.notifyItemRangeRemoved(0, currentSize);
        }
        //ADD, ADD ALL
        else if (diffSize >= 1) {
            this.addAccounts();
        }
    }

    /**
     * Add the news {@link Account} to the adapter list items.
     * <br/>
     * The added items are the difference between the source and the current items list.
     */
    public void addAccounts() {
        int sourceSize = this.sourceAccounts.size();
        int currentSize = this.getItemCount();

        List<Account> newAccounts;
        if (currentSize == 0)
            newAccounts = this.sourceAccounts;
        else
            newAccounts = this.sourceAccounts.subList(currentSize, sourceSize);

        this.filteredAccounts.addAll(newAccounts);
        this.notifyItemRangeInserted(currentSize, this.getItemCount());
    }

    public void deleteAccount(Account account, int position) {
        if (this._homeViewModel.deleteAccount(account)) {
            this.filteredAccounts.remove(position);
            this.notifyItemRemoved(position);
        }
    }

//    public void updateAccount(Account account, int position) {
//        if (this._homeViewModel.updateAccount(account)) {
//            this.filteredAccounts.set(position, account);
//            this.notifyItemChanged(position);
//        }
//    }
}
