package com.zzp.dtrip.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DataMessage implements Serializable {

    @SerializedName("errorCode")
    private int errorCode;
    @SerializedName("isError")
    private Boolean isError;
    @SerializedName("list")
    private List<DataDTO> list;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public Boolean getError() {
        return isError;
    }

    public void setError(Boolean error) {
        isError = error;
    }

    public List<DataDTO> getList() {
        return list;
    }

    public void setList(List<DataDTO> list) {
        this.list = list;
    }

    public static class DataDTO {
        @SerializedName("time")
        private String time;
        @SerializedName("mileage")
        private int mileage;
        @SerializedName("type")
        private String type;
        @SerializedName("Id")
        private int Id;
        @SerializedName("DataId")
        private int DataId;
        @SerializedName("username")
        private String username;
        @SerializedName("location")
        private String location;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getMileage() {
            return mileage;
        }

        public void setMileage(int mileage) {
            this.mileage = mileage;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getId() {
            return Id;
        }

        public void setId(int id) {
            Id = id;
        }

        public int getDataId() {
            return DataId;
        }

        public void setDataId(int dataId) {
            DataId = dataId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }
}
