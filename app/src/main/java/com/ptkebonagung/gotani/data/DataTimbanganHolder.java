package com.ptkebonagung.gotani.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataTimbanganHolder {

    @SerializedName("data")
    @Expose
    private List<DataTimbangan> data = null;
    @SerializedName("jumlah_halaman")
    @Expose
    private Integer jumlahHalaman;

    public List<DataTimbangan> getData() {
        return data;
    }

    public void setData(List<DataTimbangan> data) {
        this.data = data;
    }

    public Integer getJumlahHalaman() {
        return jumlahHalaman;
    }

    public void setJumlahHalaman(Integer jumlahHalaman) {
        this.jumlahHalaman = jumlahHalaman;
    }

}
