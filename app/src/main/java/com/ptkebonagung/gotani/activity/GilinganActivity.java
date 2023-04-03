package com.ptkebonagung.gotani.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

import com.ptkebonagung.gotani.MainActivity;
import com.ptkebonagung.gotani.R;
import com.ptkebonagung.gotani.adapter.AdapterListGilingan;
import com.ptkebonagung.gotani.data.DataGilinganHolder;
import com.ptkebonagung.gotani.model.GILINGAN;
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

public class GilinganActivity extends AppCompatActivity {
    private View parent_view;

    private RecyclerView recyclerView;
    private AdapterListGilingan mAdapter = new AdapterListGilingan();
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
    private List<GILINGAN> listGilingan = new ArrayList<>();
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

            Intent intent = new Intent(GilinganActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        } else {

            boolean hasConnection = NetworkUtil.getConnectivityStatus(getApplicationContext());
            if (hasConnection == false){

                showNotification();

            } else {

                setContentView(R.layout.activity_gilingan);
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
                loadDataGilingan("");
                searchDataGilingan();
                clearTextSearchDataGilingan();
            }
        }
        //initComponent();
    }

    private void showNotification(){
        setContentView(R.layout.activity_no_item_internet_image);
        btnRetry    = findViewById(R.id.bt_retry);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });
    }

    private void loadDataGilingan(final String keyWord) {
        String key_email    = Preference.getKeyEmailRegistered(getBaseContext());
        String key_api      = Preference.getKeyApi(getBaseContext());

        HashMap<String, String> param           = new HashMap<>();
        apiService                              = ApiClient.getRetrofitInstance().create(APIService.class);
        apiService2                             = ApiClient2.getRetrofitInstance().create(APIService.class);

        Call<DataGilinganHolder> dataGilingan   = null;

        if (keyWord.length() > 0){
            param.clear();
            listGilingan.clear();

            param.put("register", keyWord);
            param.put("email", key_email);
            param.put("key", key_api);

            dataGilingan = apiService.getDataGilingan(page_number, param);
            Log.d("API Service 1","Load Gilingan via Service 1");

            if (dataGilingan.request().url().query() == null){
                dataGilingan   = apiService2.getDataGilingan(page_number, param);
                Log.d("API Service 2","Load Gilingan via Service 2");
            }

            progress_bar.setVisibility(View.VISIBLE);
            showDataGilinganFiltered(dataGilingan, keyWord);

        } else {

            param.clear();
            listGilingan.clear();

            param.put("email", key_email);
            param.put("key", key_api);

            dataGilingan = apiService.getDataGilingan(page_number, param);
            Log.d("API Service 1","Load Gilingan via Service 1");

            if (dataGilingan.request().url().query() == null){
                dataGilingan   = apiService2.getDataGilingan(page_number, param);
                Log.d("API Service 2","Load Gilingan via Service 2");
            }

            progress_bar.setVisibility(View.VISIBLE);
            showDataGilingan(dataGilingan, keyWord);
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

    private void performPagination(final String keyWord) {
        progress_bar.setVisibility(View.VISIBLE);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String key_email                                = Preference.getKeyEmailRegistered(getBaseContext());
                String key_api                                  = Preference.getKeyApi(getBaseContext());

                HashMap<String, String> param                   = new HashMap<>();
                Call<DataGilinganHolder> otherDataGilingan      = null;

                if (keyWord.length() > 0){

                    param.clear();
                    param.put("register", keyWord);
                    param.put("email", key_email);
                    param.put("key", key_api);

                    otherDataGilingan       = apiService.getDataGilingan(page_number, param);
                    Log.d("API Service 1","Load Gilingan via Service 1");

                    if (otherDataGilingan.request().url().query() == null){
                        otherDataGilingan   = apiService2.getDataGilingan(page_number, param);
                        Log.d("API Service 2","Load Gilingan via Service 2");
                    }

                    showOtherDataGilingan(otherDataGilingan);

                } else {

                    param.clear();
                    param.put("email", key_email);
                    param.put("key", key_api);

                    otherDataGilingan       = apiService.getDataGilingan(page_number, param);
                    Log.d("API Service 1","Load Gilingan via Service 1");

                    if (otherDataGilingan.request().url().query() == null){
                        otherDataGilingan   = apiService2.getDataGilingan(page_number, param);
                        Log.d("API Service 2","Load Gilingan via Service 2");
                    }

                    showOtherDataGilingan(otherDataGilingan);

                }
            }
        }, 1000);

    }

    private void showOtherDataGilingan(final Call<DataGilinganHolder> otherDataGilingan) {
        otherDataGilingan.enqueue(new Callback<DataGilinganHolder>() {
            @Override
            public void onResponse(Call<DataGilinganHolder> call, Response<DataGilinganHolder> response) {
                int dataSize = response.body().getData().size();

                if (dataSize > 0){
                    List<GILINGAN> otherListGilingan  = new ArrayList<>();

                    for (int i=0; i < dataSize; i++){
                        GILINGAN gilingan   = new GILINGAN();
                        gilingan.kd_antrian = response.body().getData().get(i).getKdAntrian();
                        gilingan.no_spta    = response.body().getData().get(i).getNoSpta();
                        gilingan.nopol      = response.body().getData().get(i).getNopol();
                        gilingan.mbs        = response.body().getData().get(i).getMbs();
                        gilingan.tgl_nilai  = response.body().getData().get(i).getTglNilai();
                        otherListGilingan.add(gilingan);
                    }

                    mAdapter.addMoreDataGilingan(otherListGilingan);
                    page_now++;

                    progress_bar.setVisibility(View.GONE);

                    if (isClick || isSearch){
                        otherListGilingan.clear();
                    }

                } else {

                    progress_bar.setVisibility(View.GONE);
                    Snackbar.make(parent_view,"No more data available ..", Snackbar.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<DataGilinganHolder> call, Throwable t) {
                progress_bar.setVisibility(View.GONE);
                Snackbar.make(parent_view,"Gagal untuk mengambil data", Snackbar.LENGTH_LONG).show();
            }
        });
    }


    private void showDataGilingan(Call<DataGilinganHolder> dataGilingan, String keyWord) {
        dataGilingan.enqueue(new Callback<DataGilinganHolder>() {
            @Override
            public void onResponse(Call<DataGilinganHolder> call, Response<DataGilinganHolder> response) {
                if (response.isSuccessful()){

                    int sizeData = response.body().getData().size();

                    if (sizeData > 0){

                        for (int i=0; i < sizeData; i++){
                            GILINGAN gilingan   = new GILINGAN();
                            gilingan.kd_antrian = response.body().getData().get(i).getKdAntrian();
                            gilingan.no_spta    = response.body().getData().get(i).getNoSpta();
                            gilingan.nopol      = response.body().getData().get(i).getNopol();
                            gilingan.mbs        = response.body().getData().get(i).getMbs();
                            gilingan.tgl_nilai  = response.body().getData().get(i).getTglNilai();
                            listGilingan.add(gilingan);
                        }

                        mAdapter    = new AdapterListGilingan(getApplicationContext(), listGilingan, R.layout.item_gilingan);
                        recyclerView.setAdapter(mAdapter);
                        progress_bar.setVisibility(View.GONE);

                    } else {

                        progress_bar.setVisibility(View.GONE);
                        //Snackbar.make(parent_view, "Data yang dicari tidak ada ..", Snackbar.LENGTH_SHORT).show();
                        noDataAvailable.setVisibility(View.VISIBLE);
                        dataAvailable = false;
                        txtDataNoAvailable.setText("Mohon maaf saat ini data GILINGAN \n untuk anda belum tersedia");
                    }

                } else {

                    progress_bar.setVisibility(View.GONE);
                    Snackbar.make(parent_view,"Data gagal disinkronkan", Snackbar.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<DataGilinganHolder> call, Throwable t) {

                progress_bar.setVisibility(View.GONE);
                Snackbar.make(parent_view,"Gagal untuk mengambil data", Snackbar.LENGTH_LONG).show();

            }
        });
    }

    private void showDataGilinganFiltered(Call<DataGilinganHolder> dataGilingan, final String keyWord) {
        dataGilingan.enqueue(new Callback<DataGilinganHolder>() {
            @Override
            public void onResponse(Call<DataGilinganHolder> call, Response<DataGilinganHolder> response) {
                if (response.isSuccessful()){
                    int dataSize = response.body().getData().size();

                    if (dataSize > 0) {
                        searchNotFound.setVisibility(View.GONE);

                        for (int i=0; i < dataSize; i++){
                            GILINGAN gilingan   = new GILINGAN();
                            gilingan.kd_antrian = response.body().getData().get(i).getKdAntrian();
                            gilingan.no_spta    = response.body().getData().get(i).getNoSpta();
                            gilingan.nopol      = response.body().getData().get(i).getNopol();
                            gilingan.mbs        = response.body().getData().get(i).getMbs();
                            gilingan.tgl_nilai  = response.body().getData().get(i).getTglNilai();
                            listGilingan.add(gilingan);
                        }

                        mAdapter    = new AdapterListGilingan(getApplicationContext(), listGilingan, R.layout.item_gilingan);
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
            public void onFailure(Call<DataGilinganHolder> call, Throwable t) {
                Snackbar.make(parent_view,"Gagal untuk mengambil data", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void searchDataGilingan(){
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchItemAction(et_search.getText().toString().toUpperCase());
                }
                return false;
            }
        });
    }

    private void searchItemAction(String keyword) {
        boolean isConnection    = NetworkUtil.getConnectivityStatus(getApplicationContext());

        if (isConnection == true){

            if (dataAvailable == true){

                if (keyword.equals("")){

                    View view = this.getCurrentFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    Snackbar.make(parent_view, "Mohon isi register yang ingin dicari", Snackbar.LENGTH_LONG).show();

                } else {

                    mAdapter.clearDataGilingan();

                    page_number = 1;
                    page_now    = 2;

                    /*pagination variable*/
                    isLoading           = true;
                    pastVisibleItems    = visibleItemCount = totalItemCount = previousTotal = 0;
                    viewThreshold       = 2;
                    /*pagination variable*/

                    isClick = true;
                    isSearch = true;

                    loadDataGilingan(keyword);

                }

            } else {

                et_search.setText("");

            }

        } else {

            showNotification();

        }


    }

    private void clearTextSearchDataGilingan(){
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

            if (dataAvailable == true){

                searchNotFound.setVisibility(View.GONE);
                et_search.setText("");
                mAdapter.clearDataGilingan();

                page_number = 1;
                page_now    = 2;

                /*pagination variable*/
                isLoading = true;
                pastVisibleItems = visibleItemCount = totalItemCount = previousTotal = 0;
                viewThreshold = 2;
                /*pagination variable*/

                isClick = true;
                listGilingan.clear();
                loadDataGilingan("");

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
        mMyApp.setCurrentActivity(this) ;

        boolean isConnection = NetworkUtil.getConnectivityStatus(getApplicationContext());
        if (isConnection == false){
            showNotification();
        }

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
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        et_search = (EditText) findViewById(R.id.et_search);
        bt_clear = (ImageButton) findViewById(R.id.bt_clear);

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


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        List<News> items = DataGenerator.getNewsData(this, 10);

        //set data and list adapter
        mAdapter = new AdapterListNews(this, items, R.layout.item_gilingan);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterListNews.OnItemClickListener() {
            @Override
            public void onItemClick(View view, News obj, int position) {
                Snackbar.make(parent_view, "Item " + obj.title + " clicked", Snackbar.LENGTH_SHORT).show();
            }
        });

    }

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
    }*/
}
