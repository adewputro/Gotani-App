package com.ptkebonagung.gotani.data;

import java.util.List;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;

public class DataSPTAHolder {

    @SerializedName("data")
    @Expose
    private List<DataSPTA> data = null;
    @SerializedName("jumlah_halaman")
    @Expose
    private Integer jumlahHalaman;

    public DataSPTAHolder(List<DataSPTA> data, Integer jumlahHalaman){
        this.data = data;
        this.jumlahHalaman = jumlahHalaman;
    }

    public List<DataSPTA> getData() {
        return data;
    }

    public void setData(List<DataSPTA> data) {
        this.data = data;
    }

    public Integer getJumlahHalaman() {
        return jumlahHalaman;
    }

    public void setJumlahHalaman(Integer jumlahHalaman) {
        this.jumlahHalaman = jumlahHalaman;
    }
}
