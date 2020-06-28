package com.android.passwordmanager;

import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    public int showTime;
    public int pwdSize;
    public int WNC;

    public MainViewModel(int showTimeReserved, int pwdSizeReserved, int WNCReserved) {
        showTime = showTimeReserved;
        pwdSize = pwdSizeReserved;
        WNC = WNCReserved;
    }
}
