package com.ptkebonagung.gotani.utils;

import com.ptkebonagung.gotani.data.APIKey;
import com.ptkebonagung.gotani.data.DataEmplasemenHolder;
import com.ptkebonagung.gotani.data.DataGilinganHolder;
import com.ptkebonagung.gotani.data.DataRegisterHolder;
import com.ptkebonagung.gotani.data.DataSPTAHolder;
import com.ptkebonagung.gotani.data.DataTebanganHolder;
import com.ptkebonagung.gotani.data.DataTimbanganHolder;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface APIService {

    //API Service Data Emplasemen
    @FormUrlEncoded
    @POST("Spta/{halaman}")
    Call<DataSPTAHolder> getDataSPTA(@Path("halaman") int page,
                                     @FieldMap HashMap<String, String> param);
    //Call<ResponseBody> getDataSPTA(@Path("halaman") int page, @FieldMap HashMap<String, String> param);
    //API Service Data Emplasemen

    //API Service Authentication User
    @FormUrlEncoded
    @PUT("Auth")
    Call<APIKey> putKeyAPIUser(@Field("email") String email,
                               @Field("token") String token,
                               @Field("key") String key);

    @FormUrlEncoded
    @PUT("Auth")
    Call<APIKey> updateToken(@Field("email") String email,
                             @Field("token") String token,
                             @Field("key") String key);
    //API Service Authentication User

    //API Service Data Emplasemen
    @FormUrlEncoded
    @POST("Emplasemen/{halaman}")
    Call<DataEmplasemenHolder> getDataEmplasemen(@Path("halaman") int page,
                                                 @FieldMap HashMap<String, String> param);
    //API Service Data Emplasemen

    //API Service Data Timbangan
    @FormUrlEncoded
    @POST("Timbangan/{halaman}")
    Call<DataTimbanganHolder> getDataTimbangan(@Path("halaman") int page,
                                               @FieldMap HashMap<String, String> param);
    //API Service Data Timbangan

    //API Service Data Gilingan
    @FormUrlEncoded
    @POST("Mejatebu/{halaman}")
    Call<DataGilinganHolder> getDataGilingan(@Path("halaman") int page,
                                             @FieldMap HashMap<String, String> param);
    //API Service Data Gilingan

    //API Service Data Tebangan
    @FormUrlEncoded
    @POST("Tebangan/{halaman}")
    Call<DataTebanganHolder> getDataTebangan(@Path("halaman") int page,
                                             @FieldMap HashMap<String, String> param);
    //API Service Data Tebangan
    @FormUrlEncoded
    @POST("TebanganFilter/{halaman}")
    Call<DataTebanganHolder> getDataFilterTebangan(@Path("halaman") int page,
                                             @FieldMap HashMap<String, String> param);
    //API Service Data Register
    @FormUrlEncoded
    @POST("Register")
    Call<DataRegisterHolder> getDataRegister(@FieldMap HashMap<String, String> param);
    //API Service Data Register
}
