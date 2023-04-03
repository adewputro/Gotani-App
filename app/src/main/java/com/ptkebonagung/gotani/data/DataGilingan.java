package com.ptkebonagung.gotani.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataGilingan {

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

    @SerializedName("kd_antrian")
    @Expose
    private String kdAntrian;

    @SerializedName("nopol")
    @Expose
    private String nopol;

    @SerializedName("mbs")
    @Expose
    private String mbs;

    @SerializedName("tgl_nilai")
    @Expose
    private String tglNilai;

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

    public String getKdAntrian() {
        return kdAntrian;
    }

    public void setKdAntrian(String kdAntrian) {
        this.kdAntrian = kdAntrian;
    }

    public String getNopol() {
        return nopol;
    }

    public void setNopol(String nopol) {
        this.nopol = nopol;
    }

    public String getMbs() {
        return mbs;
    }

    public void setMbs(String mbs) {
        this.mbs = mbs;
    }

    public String getTglNilai() {
        return tglNilai;
    }

    public void setTglNilai(String tglNilai) {
        this.tglNilai = tglNilai;
    }
}
