package com.ptkebonagung.gotani.data;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;

public class DataSPTA {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("no_spta")
    @Expose
    private String noSpta;
    @SerializedName("no_register")
    @Expose
    private String noRegister;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("tgl_berlaku_awal")
    @Expose
    private String tglBerlakuAwal;
    @SerializedName("tgl_berlaku_akhir")
    @Expose
    private String tglBerlakuAkhir;
    @SerializedName("jenis_tebang")
    @Expose
    private String jenisTebang;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getNoSpta() {
        return noSpta;
    }

    public void setNoSpta(String noSpta) {
        this.noSpta = noSpta;
    }

    public String getNoRegister() {
        return noRegister;
    }

    public void setNoRegister(String noRegister) {
        this.noRegister = noRegister;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTglBerlakuAwal() {
        return tglBerlakuAwal;
    }

    public void setTglBerlakuAwal(String tglBerlakuAwal) {
        this.tglBerlakuAwal = tglBerlakuAwal;
    }

    public String getTglBerlakuAkhir() {
        return tglBerlakuAkhir;
    }

    public void setTglBerlakuAkhir(String tglBerlakuAkhir) {
        this.tglBerlakuAkhir = tglBerlakuAkhir;
    }

    public String getJenisTebang() {
        return jenisTebang;
    }

    public void setJenisTebang(String jenisTebang) {
        this.jenisTebang = jenisTebang;
    }


}
