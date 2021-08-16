package com.zzp.dtrip.javabean;

import com.google.gson.annotations.SerializedName;

public class RegisterAsk {

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
    /**
     * sex
     */
    @SerializedName("sex")
    private String sex;

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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
