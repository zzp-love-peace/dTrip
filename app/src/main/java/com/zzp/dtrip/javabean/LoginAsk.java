package com.zzp.dtrip.javabean;

import com.google.gson.annotations.SerializedName;

public class LoginAsk {

    /**
     * username
     */
    @SerializedName("username")
    private String account;
    /**
     * password
     */
    @SerializedName("password")
    private String password;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
