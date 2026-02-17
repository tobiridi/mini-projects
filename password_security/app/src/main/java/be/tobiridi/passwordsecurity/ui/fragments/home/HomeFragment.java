package be.tobiridi.passwordsecurity.ui.fragments.home;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.data.entities.Account;
import be.tobiridi.passwordsecurity.ui.components.HomeAdapter;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

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
        this.recyclerView = view.findViewById(R.id.recyclerView);
        this.searchView = view.findViewById(R.id.searchView);
        this.progressBar = view.findViewById(R.id.progressBar);

        //set RecyclerView
        this.recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        this.homeViewModel.getHomeUiState().observe(this.getViewLifecycleOwner(), (HomeUiState uiState) -> {
            int progBarVisibility = uiState.isLoading() ? VISIBLE : GONE;
            this.progressBar.setVisibility(progBarVisibility);

            // TODO: 16/02/2026 check if integration of ui state works properly
            List<Account> displayedAccounts = uiState.getDecryptedAccounts();
            if (!displayedAccounts.isEmpty()) {
                HomeAdapter adapter = (HomeAdapter) this.recyclerView.getAdapter();
                if (adapter == null) {
                    //init adapter when activity creation
                    adapter = new HomeAdapter(displayedAccounts, this.homeViewModel);
                    this.recyclerView.setAdapter(adapter);
                }
                else {
                    //don't update the data source of adapter if just filtering data, "optimisation"
                    if (!uiState.isFiltering()) {
                        adapter.sourceAccountsChanged(displayedAccounts);
                    }
                    String searchText = uiState.getSearchText();
                    adapter.getFilter().filter(searchText.toLowerCase());
                }
            }
        });

        this.initListeners();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.homeViewModel.getHomeUiState().removeObservers(this);
    }

    private void initListeners() {
        this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                homeViewModel.updateSearchText(newText);
                return true;
            }
        });
    }
}