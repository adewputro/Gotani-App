package com.ptkebonagung.gotani.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataRegister {

    @SerializedName("no_register")
    @Expose
    private String noRegister;

    @SerializedName("petani")
    @Expose
    private String petani;

    @SerializedName("lokasi")
    @Expose
    private String lokasi;

    @SerializedName("is_spt")
    @Expose
    private String jenis;

    public String getNoRegister() {
        return noRegister;
    }

    public String getPetani(){
        return petani;
    }

    public String getLokasi(){
        return lokasi;
    }

    public String getJenis(){
        return jenis;
    }

    public void setNoRegister(String noRegister) {
        this.noRegister = noRegister;
    }

}
