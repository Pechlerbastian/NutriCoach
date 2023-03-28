package de.uniaugsburg.app.ui.add;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    public AddViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("dummy");
    }

    public LiveData<String> getText() {
        return mText;
    }
}