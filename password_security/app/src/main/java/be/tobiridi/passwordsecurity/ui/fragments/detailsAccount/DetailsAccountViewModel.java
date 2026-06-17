package be.tobiridi.passwordsecurity.ui.fragments.detailsAccount;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import be.tobiridi.passwordsecurity.data.entities.Account;

public class DetailsAccountViewModel extends ViewModel {
    private final MutableLiveData<DetailsAccountUiState> mutableDetailsAccountUiState;

    public DetailsAccountViewModel() {
        this.mutableDetailsAccountUiState = new MutableLiveData<>();
    }

    public void setAccount(Account detailsAccount) {
        var uiState = new DetailsAccountUiState(detailsAccount);
        this.mutableDetailsAccountUiState.setValue(uiState);
    }

    public LiveData<DetailsAccountUiState> getDetailsAccountUiState() {
        return this.mutableDetailsAccountUiState;
    }
}