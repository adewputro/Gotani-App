package com.ptkebonagung.gotani.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataTebangan {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("jenis_tebang")
    @Expose
    private String jenisTebang;

    @SerializedName("tgl_masuk")
    @Expose
    private String tglMasuk;

    @SerializedName("date_out")
    @Expose
    private String dateOut;

    @SerializedName("no_spta")
    @Expose
    private String noSpta;

    @SerializedName("kd_antrian")
    @Expose
    private String kdAntrian;

    @SerializedName("no_register")
    @Expose
    private String noRegister;

    @SerializedName("petani")
    @Expose
    private String petani;

    @SerializedName("weight_in")
    @Expose
    private String weightIn;

    @SerializedName("weight_out")
    @Expose
    private String weightOut;

    @SerializedName("weight_net")
    @Expose
    private String weightNet;

    @SerializedName("weight_kw")
    @Expose
    private String weightKw;

    @SerializedName("rafaksi_kw")
    @Expose
    private String rafaksiKw;

    @SerializedName("mbs")
    @Expose
    private String mbs;

    @SerializedName("nopol")
    @Expose
    private String nopol;

    @SerializedName("varietas")
    @Expose
    private String varietas;

    @SerializedName("no_meja")
    @Expose
    private String no_meja;

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

    public String getJenisTebang() {
        return jenisTebang;
    }

    public void setJenisTebang(String jenisTebang) {
        this.jenisTebang = jenisTebang;
    }

    public String getTglMasuk() {
        return tglMasuk;
    }

    public void setTglMasuk(String tglMasuk) {
        this.tglMasuk = tglMasuk;
    }

    public String getDateOut() {
        return dateOut;
    }

    public void setDateOut(String dateOut) {
        this.dateOut = dateOut;
    }

    public String getNoSpta() {
        return noSpta;
    }

    public void setNoSpta(String noSpta) {
        this.noSpta = noSpta;
    }

    public String getKdAntrian() {
        return kdAntrian;
    }

    public void setKdAntrian(String kdAntrian) {
        this.kdAntrian = kdAntrian;
    }

    public String getNoRegister() {
        return noRegister;
    }

    public void setNoRegister(String noRegister) {
        this.noRegister = noRegister;
    }

    public String getPetani() {
        return petani;
    }

    public void setPetani(String petani) {
        this.petani = petani;
    }

    public String getWeightIn() {
        return weightIn;
    }

    public void setWeightIn(String weightIn) {
        this.weightIn = weightIn;
    }

    public String getWeightOut() {
        return weightOut;
    }

    public void setWeightOut(String weightOut) {
        this.weightOut = weightOut;
    }

    public String getWeightNet() {
        return weightNet;
    }

    public void setWeightNet(String weightNet) {
        this.weightNet = weightNet;
    }

    public String getWeightKw() {
        return weightKw;
    }

    public void setWeightKw(String weightKw) {
        this.weightKw = weightKw;
    }

    public String getRafaksiKw() {
        return rafaksiKw;
    }

    public void setRafaksiKw(String rafaksiKw) {
        this.rafaksiKw = rafaksiKw;
    }

    public String getMbs() {
        return mbs;
    }

    public void setMbs(String mbs) {
        this.mbs = mbs;
    }

    public String getNopol() {
        return nopol;
    }

    public void setNopol(String nopol) {
        this.nopol = nopol;
    }

    public String getVarietas() {
        return varietas;
    }

    public void setVarietas(String varietas) {
        this.varietas = varietas;
    }

    public String getNo_meja() {
        return this.no_meja;
    }

    public void setNo_meja(String no_meja) {
        this.no_meja = no_meja;
    }
}
