package be.tobiridi.passwordsecurity.ui.home;

import androidx.lifecycle.ViewModel;

import java.util.List;

import be.tobiridi.passwordsecurity.data.FakeData;

public class HomeViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    /**
     * TODO: test temp
     * @return
     */
    public List<FakeData> getFakeData() {
        return FakeData.getFakeData();
    }
}