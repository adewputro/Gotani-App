package com.ptkebonagung.gotani.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.annotation.NonNull;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ptkebonagung.gotani.MainActivity;
import com.ptkebonagung.gotani.R;
import com.ptkebonagung.gotani.adapter.AdapterListTimbangan;
import com.ptkebonagung.gotani.data.DataTimbanganHolder;
import com.ptkebonagung.gotani.model.SPTA;
import com.ptkebonagung.gotani.model.TIMBANGAN;
import com.ptkebonagung.gotani.utils.APIService;
import com.ptkebonagung.gotani.utils.ApiClient;
import com.ptkebonagung.gotani.utils.ApiClient2;
import com.ptkebonagung.gotani.utils.MyApp;
import com.ptkebonagung.gotani.utils.NetworkUtil;
import com.ptkebonagung.gotani.utils.Preference;
import com.ptkebonagung.gotani.utils.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimbanganActivity extends AppCompatActivity {

    private View parent_view;

    private RecyclerView recyclerView;

    private AdapterListTimbangan mAdapter = new AdapterListTimbangan();
    private ProgressBar progress_bar;
    private EditText et_search;
    private ImageButton bt_clear;

    //Pagination variabel dan API Service Class
    private int page_number = 1;
    private APIService apiService;
    private APIService apiService2;

    private int page_now    = 2;
    //Pagination variabel dan API Service Class

    /*List Timbangan*/
    private List<TIMBANGAN> listDataTimbangan = new ArrayList<>();

    /*List Timbangan*/

    /*pagination variable*/
    private LinearLayoutManager layoutManager;
    private boolean isLoading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount, previousTotal = 0;
    private int viewThreshold = 2;
    /*pagination variable*/

    private boolean isClick = false;
    private boolean isSearch = false;

    private Button btnRetry;
    private View searchNotFound;
    private View noDataAvailable;
    private boolean dataAvailable = true;
    private TextView txtDataNoAvailable;

    protected MyApp mMyApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyApp  = (MyApp) this.getApplicationContext();

        boolean isLogin = Preference.getKeyStatusIsLoggedIn(getBaseContext());

        if (isLogin == false){

            Intent intent = new Intent(TimbanganActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        } else {

            boolean hasConnection = checkConnection();
            if (hasConnection == false){

                showNotification();

            } else {

                setContentView(R.layout.activity_timbangan);
                parent_view         = findViewById(android.R.id.content);

                progress_bar        = (ProgressBar) findViewById(R.id.progress_bar);
                et_search           = (EditText) findViewById(R.id.et_search);
                bt_clear            = (ImageButton) findViewById(R.id.bt_clear);

                searchNotFound      = findViewById(R.id.searchResult);
                noDataAvailable     = findViewById(R.id.dataNotAvailable);
                txtDataNoAvailable  = findViewById(R.id.noDataTxt);

                layoutManager       = new LinearLayoutManager(getApplicationContext());
                recyclerView        = findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                Tools.setSystemBarColor(this, R.color.blue_grey_600);

                loadDataTimbangan("");
                searchDataTimbangan();
                clearTextSearchDataTimbangan();
                //initComponent();
            }
        }
    }

    private void showNotification(){
        setContentView(R.layout.activity_no_item_internet_image);
        btnRetry    = (Button) findViewById(R.id.bt_retry);
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

    private boolean checkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    private void loadDataTimbangan(final String keyWord){
        String key_email                        = Preference.getKeyEmailRegistered(getBaseContext());
        String key_api                          = Preference.getKeyApi(getBaseContext());

        HashMap<String, String> param           = new HashMap<>();
        apiService                              = ApiClient.getRetrofitInstance().create(APIService.class);
        apiService2                             = ApiClient2.getRetrofitInstance().create(APIService.class);

        Call<DataTimbanganHolder> dataTimbangan = null;

        if (keyWord.length() > 0){

            param.clear();
            listDataTimbangan.clear();

            param.put("register", keyWord);
            param.put("email", key_email);
            param.put("key", key_api);

            dataTimbangan       = apiService.getDataTimbangan(page_number, param);
            Log.d("API Service 1","Load Timbangan via Service 1");

            if (dataTimbangan.request().url().query() == null){
                dataTimbangan   = apiService2.getDataTimbangan(page_number, param);
                Log.d("API Service 2","Load Timbangan via Service 2");
            }

            progress_bar.setVisibility(View.VISIBLE);
            showDataTimbanganFiltered(dataTimbangan, keyWord);

        } else {

            param.clear();
            listDataTimbangan.clear();

            param.put("email", key_email);
            param.put("key", key_api);

            dataTimbangan   = apiService.getDataTimbangan(page_number, param);
            Log.d("API Service 1","Load Timbangan via Service 1");

            if (dataTimbangan.request().url().query() == null){
                dataTimbangan   = apiService2.getDataTimbangan(page_number, param);
                Log.d("API Service 2","Load Timbangan via Service 2");
            }

            progress_bar.setVisibility(View.VISIBLE);
            showDataTimbangan(dataTimbangan);
        }

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    String keyword      = keyWord;
                    visibleItemCount    = layoutManager.getChildCount();
                    totalItemCount      = layoutManager.getItemCount();
                    pastVisibleItems    = layoutManager.findFirstVisibleItemPosition();

                    if(dy > 0){

                        if(isLoading){
                            if(totalItemCount > previousTotal){
                                isLoading       = false;
                                previousTotal   = totalItemCount;
                            }
                        }

                        if(!isLoading && ((totalItemCount - visibleItemCount) <= (pastVisibleItems + 0))){

                            page_number++;
                            performPagination(keyWord);
                            isLoading = true;

                        }
                    }
                }
            });
    }

    private void showDataTimbanganFiltered(Call<DataTimbanganHolder> dataTimbangan, final String keyWord){
        dataTimbangan.enqueue(new Callback<DataTimbanganHolder>() {
            @Override
            public void onResponse(Call<DataTimbanganHolder> call, Response<DataTimbanganHolder> response) {
                if (response.isSuccessful()){
                    int dataSize    = response.body().getData().size();

                    if (dataSize > 0){
                        searchNotFound.setVisibility(View.GONE);

                        for (int i=0; i < dataSize; i++){
                            TIMBANGAN timbanganList     = new TIMBANGAN();
                            timbanganList.kode_antrian  = response.body().getData().get(i).getKdAntrian();
                            timbanganList.noSPTA        = response.body().getData().get(i).getNoSpta();
                            timbanganList.nopol         = response.body().getData().get(i).getNopol();
                            timbanganList.weightIn      = response.body().getData().get(i).getWeightIn();
                            timbanganList.dateIn        = response.body().getData().get(i).getDateIn();
                            listDataTimbangan.add(timbanganList);
                        }

                        mAdapter    = new AdapterListTimbangan(getApplicationContext(), listDataTimbangan, R.layout.item_timbangan);
                        recyclerView.setAdapter(mAdapter);
                        progress_bar.setVisibility(View.GONE);

                    } else {

                        progress_bar.setVisibility(View.GONE);
                        searchNotFound.setVisibility(View.VISIBLE);

                    }

                } else {

                    progress_bar.setVisibility(View.GONE);
                    Snackbar.make(parent_view,"Data gagal disinkronkan", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DataTimbanganHolder> call, Throwable t) {
                progress_bar.setVisibility(View.GONE);
                Snackbar.make(parent_view,"Gagal untuk mengambil data", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void showDataTimbangan(Call<DataTimbanganHolder> dataTimbangan){
        dataTimbangan.enqueue(new Callback<DataTimbanganHolder>() {
            @Override
            public void onResponse(Call<DataTimbanganHolder> call, Response<DataTimbanganHolder> response) {
                if (response.isSuccessful()){
                    int sizeData = response.body().getData().size();

                    if (sizeData > 0){

                        for (int i=0; i < sizeData; i++){
                            TIMBANGAN timbanganList     = new TIMBANGAN();
                            timbanganList.kode_antrian  = response.body().getData().get(i).getKdAntrian();
                            timbanganList.noSPTA        = response.body().getData().get(i).getNoSpta();
                            timbanganList.nopol         = response.body().getData().get(i).getNopol();
                            timbanganList.weightIn      = response.body().getData().get(i).getWeightIn();
                            timbanganList.dateIn        = response.body().getData().get(i).getDateIn();
                            listDataTimbangan.add(timbanganList);
                        }

                        mAdapter    = new AdapterListTimbangan(getApplicationContext(), listDataTimbangan, R.layout.item_timbangan);
                        recyclerView.setAdapter(mAdapter);
                        progress_bar.setVisibility(View.GONE);

                    } else {
                        progress_bar.setVisibility(View.GONE);
                        //Snackbar.make(parent_view, "Data yang dicari tidak ada ..", Snackbar.LENGTH_SHORT).show();
                        dataAvailable   = false;
                        noDataAvailable.setVisibility(View.VISIBLE);
                        txtDataNoAvailable.setText("Mohon maaf saat ini data TIMBANGAN \n untuk anda belum tersedia");
                    }

                } else {
                    progress_bar.setVisibility(View.GONE);
                    Snackbar.make(parent_view,"Data gagal disinkronkan", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DataTimbanganHolder> call, Throwable t) {
                progress_bar.setVisibility(View.GONE);
                Snackbar.make(parent_view,"Gagal untuk mengambil data", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void performPagination(final String keyWord){
        progress_bar.setVisibility(View.VISIBLE);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String key_email                                = Preference.getKeyEmailRegistered(getBaseContext());
                String key_api                                  = Preference.getKeyApi(getBaseContext());

                HashMap<String, String> param                   = new HashMap<>();
                Call<DataTimbanganHolder> otherDataTimbangan    = null;

                if (keyWord.length() > 0){

                    param.clear();
                    param.put("register", keyWord);
                    param.put("email", key_email);
                    param.put("key", key_api);

                    otherDataTimbangan = apiService.getDataTimbangan(page_number, param);
                    Log.d("API Service 1","Load Timbangan via Service 1");

                    if (otherDataTimbangan.request().url().query() == null){
                        otherDataTimbangan   = apiService2.getDataTimbangan(page_number, param);
                        Log.d("API Service 2","Load Timbangan via Service 2");
                    }

                    showOtherDataTimbangan(otherDataTimbangan);

                } else {

                    param.clear();
                    param.put("email", key_email);
                    param.put("key", key_api);

                    otherDataTimbangan = apiService.getDataTimbangan(page_number, param);
                    Log.d("API Service 1","Load Timbangan via Service 1");

                    if (otherDataTimbangan.request().url().query() == null){
                        otherDataTimbangan   = apiService2.getDataTimbangan(page_number, param);
                        Log.d("API Service 2","Load Timbangan via Service 2");
                    }

                    showOtherDataTimbangan(otherDataTimbangan);

                }
            }
        }, 1000);
    }

    private void showOtherDataTimbangan(Call<DataTimbanganHolder> otherDataTimbangan) {
        otherDataTimbangan.enqueue(new Callback<DataTimbanganHolder>() {
            @Override
            public void onResponse(Call<DataTimbanganHolder> call, Response<DataTimbanganHolder> response) {
                if (response.body().getData().size() > 0){
                    List<TIMBANGAN> otherListTimbangan  = new ArrayList<>();
                    int sizeData                        = response.body().getData().size();

                    for (int i=0; i < sizeData; i++){
                        TIMBANGAN otherTimbangan    = new TIMBANGAN();
                        otherTimbangan.kode_antrian = response.body().getData().get(i).getKdAntrian();
                        otherTimbangan.noSPTA       = response.body().getData().get(i).getNoSpta();
                        otherTimbangan.nopol        = response.body().getData().get(i).getNopol();
                        otherTimbangan.weightIn     = response.body().getData().get(i).getWeightIn();
                        otherTimbangan.dateIn       = response.body().getData().get(i).getDateIn();
                        otherListTimbangan.add(otherTimbangan);
                    }

                    mAdapter.addMoreDataTimbangan(otherListTimbangan);
                    page_now++;
                    progress_bar.setVisibility(View.GONE);

                    if (isClick || isSearch){
                        otherListTimbangan.clear();
                    }
                } else {

                    progress_bar.setVisibility(View.GONE);
                    Snackbar.make(parent_view,"No more data available ..", Snackbar.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<DataTimbanganHolder> call, Throwable t) {
                progress_bar.setVisibility(View.GONE);
                Snackbar.make(parent_view,"Gagal untuk mengambil data", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void searchDataTimbangan(){
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                if (actionID == EditorInfo.IME_ACTION_SEARCH){
                    searchItemAction(et_search.getText().toString().toUpperCase());
                }
                return false;
            }
        });
    }

    private void searchItemAction(String keyword) {

        boolean isConnection = NetworkUtil.getConnectivityStatus(getApplicationContext());

        if(isConnection == true){

            if(dataAvailable == true){

                if (keyword.equals("")){

                    View view = this.getCurrentFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    Snackbar.make(parent_view, "Mohon isi register yang ingin dicari", Snackbar.LENGTH_LONG).show();

                } else {

                    mAdapter.clearDataTimbangan();

                    page_number = 1;
                    page_now    = 2;

                    /*pagination variable*/
                    isLoading           = true;
                    pastVisibleItems    = visibleItemCount = totalItemCount = previousTotal = 0;
                    viewThreshold       = 2;
                    /*pagination variable*/

                    isClick = true;
                    isSearch = true;

                    loadDataTimbangan(keyword);

                }

            } else {

                et_search.setText("");

            }

        } else {

            showNotification();

        }
    }

    private void clearTextSearchDataTimbangan(){
        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshData();
            }
        });
    }

    private void refreshData(){

        boolean isConnection = NetworkUtil.getConnectivityStatus(getApplicationContext());

        if (isConnection == true){

            if(dataAvailable == true){

                searchNotFound.setVisibility(View.GONE);
                et_search.setText("");
                mAdapter.clearDataTimbangan();

                page_number = 1;
                page_now    = 2;

                /*pagination variable*/
                isLoading = true;
                pastVisibleItems = visibleItemCount = totalItemCount = previousTotal = 0;
                viewThreshold = 2;
                /*pagination variable*/

                isClick = true;
                listDataTimbangan.clear();
                loadDataTimbangan("");

            } else {

                et_search.setText("");
//                View view = this.getCurrentFocus();
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }

        } else {

            showNotification();

        }
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

    /*private void initComponent() {
        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search.setText("");
            }
        });
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard();
                    searchAction();
                    return true;
                }
                return false;
            }
        });

        List<News> items = DataGenerator.getNewsData(this, 10);

        //set data and list adapter
        mAdapter = new AdapterListNews(this, items, R.layout.item_timbangan);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterListNews.OnItemClickListener() {
            @Override
            public void onItemClick(View view, News obj, int position) {
                Snackbar.make(parent_view, "Item " + obj.title + " clicked", Snackbar.LENGTH_SHORT).show();
            }
        });

    }*/

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void searchAction() {
        progress_bar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        final String query = et_search.getText().toString().trim();
        if (!query.equals("")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progress_bar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }, 2000);
        } else {
            Toast.makeText(this, "Please fill search input", Toast.LENGTH_SHORT).show();
        }
    }
}
