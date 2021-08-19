package com.zzp.dtrip.util;

import com.google.gson.annotations.SerializedName;

import java.util.List;

//这个类是用于根据id查找数据的返回值
public class TotalMileage {

    @SerializedName("errorCode")
    private Integer errorCode;
    @SerializedName("isError")
    private Boolean isError;
    @SerializedName("set")
    private List<String> set;

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public Boolean getIsError() {
        return isError;
    }

    public void setIsError(Boolean isError) {
        this.isError = isError;
    }

    public List<String> getSet() {
        return set;
    }

    public void setSet(List<String> set) {
        this.set = set;
    }
}
