package com.zzp.dtrip.javabean;

import com.google.gson.annotations.SerializedName;

public class compareFaceResponse {

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
    private Object data;

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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
