package com.android.passwordmanager.login;

import org.litepal.annotation.Encrypt;
import org.litepal.crud.LitePalSupport;

public class User extends LitePalSupport {

    private int id;

    private String user;

    //@Encrypt(algorithm = AES)
    private String password;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
