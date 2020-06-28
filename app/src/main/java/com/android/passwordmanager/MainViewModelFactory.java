package com.android.passwordmanager;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MainViewModelFactory implements ViewModelProvider.Factory {

    private int showTimeReserved;
    private int pwdSizeReserved;
    private int WNCReserved;

    public MainViewModelFactory(int showTimeReserved, int pwdSizeReserved, int WNCReserved) {
        this.showTimeReserved = showTimeReserved;
        this.pwdSizeReserved = pwdSizeReserved;
        this.WNCReserved = WNCReserved;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainViewModel(showTimeReserved, pwdSizeReserved, WNCReserved);
    }
}
