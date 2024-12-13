package be.tobiridi.passwordsecurity.ui.addAccount;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.tobiridi.passwordsecurity.R;

public class AddAcountFragment extends Fragment {

    private AddAcountViewModel mViewModel;

    public static AddAcountFragment newInstance() {
        return new AddAcountFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_acount, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AddAcountViewModel.class);
        // TODO: Use the ViewModel
    }

}