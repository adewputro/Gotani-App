package com.ptkebonagung.gotani.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.ptkebonagung.gotani.MainActivity;
import com.ptkebonagung.gotani.R;
import com.ptkebonagung.gotani.adapter.AdapterListRegister;
import com.ptkebonagung.gotani.data.DataRegisterHolder;
import com.ptkebonagung.gotani.model.GILINGAN;
import com.ptkebonagung.gotani.model.REGISTER;
import com.ptkebonagung.gotani.utils.APIService;
import com.ptkebonagung.gotani.utils.ApiClient;
import com.ptkebonagung.gotani.utils.ApiClient2;
import com.ptkebonagung.gotani.utils.MyApp;
import com.ptkebonagung.gotani.utils.NetworkUtil;
import com.ptkebonagung.gotani.utils.Preference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.ClipboardManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {


    private ArrayAdapter adapterListRregister;
    private AdapterListRegister adapterRegister;
    private View parent_view;
    private ListView listviewRegister;
    private APIService apiService;
    private APIService apiService2;
    private TextView textNoItemAvailable;
    private View noItemAvailable;
    private Button btnRetry;
    private ProgressBar pbar;

    protected MyApp mMyApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyApp          = (MyApp) this.getApplicationContext();
        parent_view     = findViewById(android.R.id.content);

        boolean isLogin = Preference.getKeyStatusIsLoggedIn(getBaseContext());

        if (isLogin == false){

            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        } else {

            boolean hasConnection = NetworkUtil.getConnectivityStatus(getApplicationContext());

            if (hasConnection == false){

                showNotification();

            } else {

                setContentView(R.layout.activity_register);
                listviewRegister                = findViewById(R.id.listviewregister);
                textNoItemAvailable             = findViewById(R.id.noDataTxt);
                noItemAvailable                 = findViewById(R.id.dataNotAvailable);
                pbar                            = findViewById(R.id.progress_bar);


                setListRegister();

            }
        }
    }

    private void showNotification(){
        setContentView(R.layout.activity_no_item_internet_image);
        btnRetry    = findViewById(R.id.bt_retry);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });
    }

    private void setListRegister(){
        final ClipboardManager copyText = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        String key_email                = Preference.getKeyEmailRegistered(getBaseContext());
        String key_api                  = Preference.getKeyApi(getBaseContext());
        HashMap<String, String> param   = new HashMap<>();

        param.put("email", key_email);
        param.put("key", key_api);

        apiService                                  = ApiClient.getRetrofitInstance().create(APIService.class);
        apiService2                                 = ApiClient2.getRetrofitInstance().create(APIService.class);

        Call<DataRegisterHolder> dataRegisterUser   = apiService.getDataRegister(param);
        Log.d("API Service 1","Load Register via Service 1");

        if (dataRegisterUser.request().url().query() == null){
            dataRegisterUser   = apiService2.getDataRegister(param);
            Log.d("API Service 2","Load Register via Service 2");
        }

        final List<REGISTER> listRegister        = new ArrayList<>();

        dataRegisterUser.enqueue(new Callback<DataRegisterHolder>() {
            @Override
            public void onResponse(Call<DataRegisterHolder> call, Response<DataRegisterHolder> response) {
                if (response.body().getData().size() > 0){

                    int dataSize = response.body().getData().size();

                    for (int i=0; i < dataSize; i++){
                        REGISTER reg = new REGISTER();
                        reg.no_register = response.body().getData().get(i).getNoRegister();
                        reg.petani = response.body().getData().get(i).getPetani();
                        reg.lokasi = response.body().getData().get(i).getLokasi();
                        reg.jenis = response.body().getData().get(i).getNoRegister();
                        if (response.body().getData().get(i).getJenis().equals("t")){
                            reg.jenis = "SPT";
                        } else{
                            reg.jenis ="SBH";
                        }
                        listRegister.add(reg);
                    }

//                    adapterListRregister    = new ArrayAdapter<>(getApplicationContext(), R.layout.listview_register, listRegister);
                    adapterRegister = new AdapterListRegister(getApplicationContext(), listRegister);
                    listviewRegister.setAdapter(adapterRegister);
                    listviewRegister.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            ClipData cliptext   = ClipData.newPlainText("Copied Text", listRegister.get(i).no_register);
                            copyText.setPrimaryClip(cliptext);
                            Snackbar.make(view, "Ready to Paste "+listRegister.get(i).no_register+ " "+listRegister.get(i).jenis, Snackbar.LENGTH_SHORT).show();
                        }
                    });

                    pbar.setVisibility(View.GONE);

                } else {

                    pbar.setVisibility(View.GONE);
                    noItemAvailable.setVisibility(View.VISIBLE);
                    textNoItemAvailable.setText("Mohon maaf saat ini data REGISTER \n untuk anda belum tersedia");

                }
            }

            @Override
            public void onFailure(Call<DataRegisterHolder> call, Throwable t) {
                Snackbar.make(parent_view, "Gagal mengambil data register anda", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed(){
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onResume () {
        super.onResume() ;
        mMyApp.setCurrentActivity(this);
    }

    @Override
    public void onPause(){
        clearReferences();
        super.onPause();
    }

    @Override
    protected void onDestroy () {
        clearReferences() ;
        super.onDestroy() ;
    }

    private void clearReferences() {
        Activity currActivity = mMyApp.getCurrentActivity() ;
        if ( this.equals(currActivity))
            mMyApp.setCurrentActivity( null ) ;
    }
}
