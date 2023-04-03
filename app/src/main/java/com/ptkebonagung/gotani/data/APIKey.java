package com.ptkebonagung.gotani.data;

import com.google.gson.annotations.SerializedName;

public class APIKey {
    @SerializedName("email")
    String email;

    @SerializedName("token")
    String token;

    @SerializedName("key")
    String key;

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setToken(String token){
        this.token = token;
    }

    public String getToken(){
        return token;
    }


    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
