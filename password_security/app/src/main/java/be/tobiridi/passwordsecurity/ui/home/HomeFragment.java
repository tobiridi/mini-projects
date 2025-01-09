package be.tobiridi.passwordsecurity.ui.home;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;

import java.util.List;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.component.HomeAdapter;
import be.tobiridi.passwordsecurity.data.Account;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private ImageButton filterButton;
    private Observer<List<Account>> obSourceAccounts, obMutableAccounts;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.homeViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(HomeViewModel.initializer)).get(HomeViewModel.class);

        //get views id
        this.filterButton = view.findViewById(R.id.filterBtn);
        this.recyclerView = view.findViewById(R.id.recyclerView);
        this.searchView = view.findViewById(R.id.searchView);

        //set RecyclerView
        this.recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        this.recyclerView.setAdapter(new HomeAdapter(List.of()));

        //init the observers
        this.initObservers();
        this.homeViewModel.getSourceAccounts().observe(this.getViewLifecycleOwner(), this.obSourceAccounts);
        this.homeViewModel.getMutableAccounts().observe(this.getViewLifecycleOwner(), this.obMutableAccounts);

        //init the listeners
        this.initListeners();
    }

    @Override
    public void onStart() {
        super.onStart();
        //reset search input when return to this fragment after navigated to another fragment/activity
        searchView.setQuery(null, false);
    }

    private void initObservers() {
        this.obSourceAccounts = new Observer<List<Account>>() {
            @Override
            public void onChanged(List<Account> accounts) {
                homeViewModel.updateMutableAccounts(accounts);
            }
        };

        this.obMutableAccounts = new Observer<List<Account>>() {
            @Override
            public void onChanged(List<Account> accounts) {
                HomeAdapter adapter = (HomeAdapter) recyclerView.getAdapter();
                if (adapter != null) {
                    adapter.replaceAccounts(accounts);
                }
            }
        };
    }

    private void initListeners() {
        this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                homeViewModel.searchFilter(newText);
                return false;
            }
        });

        this.filterButton.setOnClickListener(view -> {
            //TODO: not implemented
        });
    }

}