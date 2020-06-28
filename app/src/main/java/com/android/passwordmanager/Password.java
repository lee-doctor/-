package com.android.passwordmanager;

public class Password {
    private String account_name;
    private String account;
    private String password;
    private String describe;
    private String time;

    public Password(String account_name, String account, String password, String describe, String time) {
        this.account_name = account_name;
        this.account = account;
        this.password = password;
        this.describe = describe;
        this.time = time;
    }

    public String getAccount_name() {
        return account_name;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public String getDescribe() {
        return describe;
    }

    public String getTime() {
        return time;
    }
}
