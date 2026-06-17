package be.tobiridi.passwordsecurity.ui.activities.detailsUpdateAccount;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DetailsUpdateAccountViewModel extends ViewModel {
    private final MutableLiveData<DetailsUpdateAccountUiState> mutableDetailsUpdateUiState;

    public DetailsUpdateAccountViewModel() {
        var uiState = new DetailsUpdateAccountUiState(false);
        this.mutableDetailsUpdateUiState = new MutableLiveData<>(uiState);
    }

    public LiveData<DetailsUpdateAccountUiState> getDetailsUpdateUiState() {
        return this.mutableDetailsUpdateUiState;
    }

    public void switchEditMode() {
        var oldState = this.mutableDetailsUpdateUiState.getValue();
        var uiState = new DetailsUpdateAccountUiState(!oldState.isInEditMode());
        this.mutableDetailsUpdateUiState.setValue(uiState);
    }
}
