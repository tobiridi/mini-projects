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

        //get views id
        this.filterButton = view.findViewById(R.id.filterBtn);
        this.recyclerView = view.findViewById(R.id.recyclerView);
        this.searchView = view.findViewById(R.id.searchView);

        //set RecyclerView
        this.recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        this.recyclerView.setAdapter(new HomeAdapter(List.of()));
        this.homeViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(HomeViewModel.initializer)).get(HomeViewModel.class);

        this.homeViewModel.getAccountsLiveData().observe(this.getViewLifecycleOwner(), new Observer<List<Account>>() {
            @Override
            public void onChanged(List<Account> accounts) {
                RecyclerView.Adapter adapter = recyclerView.getAdapter();
                if (adapter instanceof HomeAdapter) {
                    ((HomeAdapter) adapter).replaceCurrentList(accounts);
                }
            }
        });

        this.initListener();
    }

    private void initListener() {
        this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
                //TODO: not implemented
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
                //TODO: not implemented
            }
        });

        this.filterButton.setOnClickListener(view -> {
            //TODO: not implemented
        });
    }

}