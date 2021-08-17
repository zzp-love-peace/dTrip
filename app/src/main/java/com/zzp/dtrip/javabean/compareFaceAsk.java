package com.zzp.dtrip.javabean;

import com.google.gson.annotations.SerializedName;

public class compareFaceAsk {

    /**
     * bytes
     */
    @SerializedName("bytes")
    private String bytes;
    /**
     * id
     */
    @SerializedName("id")
    private int id;

    public String getBytes() {
        return bytes;
    }

    public void setBytes(String bytes) {
        this.bytes = bytes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
