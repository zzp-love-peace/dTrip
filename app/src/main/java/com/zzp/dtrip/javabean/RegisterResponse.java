package com.zzp.dtrip.javabean;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {

    /**
     * code
     */
    @SerializedName("code")
    private long code;
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

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
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
}
