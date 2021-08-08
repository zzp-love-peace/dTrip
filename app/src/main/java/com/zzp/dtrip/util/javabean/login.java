package com.zzp.dtrip.util.javabean;

public class login {

    /**
     * code
     */

    private int code;
    /**
     * isError
     */

    private boolean isError;
    /**
     * errorMessage
     */
    private String errorMessage;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean getIsError() {
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
