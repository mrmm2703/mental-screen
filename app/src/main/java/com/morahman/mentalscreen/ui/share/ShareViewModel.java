package com.morahman.mentalscreen.ui.share;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ShareViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ShareViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("lmao u aint supposed to be here hehe. gimme a sec to program this lol.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}