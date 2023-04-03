package com.ptkebonagung.gotani.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataEmplasemenHolder {

    @SerializedName("data")
    @Expose
    private List<DataEmplasemen> data = null;
    @SerializedName("jumlah_halaman")
    @Expose
    private Integer jumlahHalaman;

    public List<DataEmplasemen> getData() {
        return data;
    }

    public void setData(List<DataEmplasemen> data) {
        this.data = data;
    }

    public Integer getJumlahHalaman() {
        return jumlahHalaman;
    }

    public void setJumlahHalaman(Integer jumlahHalaman) {
        this.jumlahHalaman = jumlahHalaman;
    }

}
