package com.ptkebonagung.gotani.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataRegisterHolder {

    @SerializedName("data")
    @Expose
    private List<DataRegister> data = null;

    public List<DataRegister> getData() {
        return data;
    }

    public void setData(List<DataRegister> data) {
        this.data = data;
    }

}
