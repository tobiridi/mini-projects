package be.tobiridi.passwordsecurity.ui.home;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;

import be.tobiridi.passwordsecurity.R;

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
        this.homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel

        //get views id
        this.filterButton = view.findViewById(R.id.filterBtn);
        this.recyclerView = view.findViewById(R.id.recyclerView);
        this.searchView = view.findViewById(R.id.searchView);

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