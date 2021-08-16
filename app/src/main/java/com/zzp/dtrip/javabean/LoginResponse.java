package com.zzp.dtrip.javabean;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    /**
     * code
     */
    @SerializedName("code")
    private int code;
    /**
     * isError
     */
    @SerializedName("isError")
    private boolean isError;
    /**
     * errorMessage
     */
    @SerializedName("errorMessage")
    private String errorMessage;
    /**
     * data
     */
    @SerializedName("data")
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isIsError() {
        return isError;
    }

    public void setIsError(boolean isError) {
        this.isError = isError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
    }
}
