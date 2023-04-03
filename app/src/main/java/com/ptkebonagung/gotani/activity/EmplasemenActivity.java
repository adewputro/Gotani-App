package com.ptkebonagung.gotani.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
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
import com.ptkebonagung.gotani.adapter.AdapterListEmplasemen;
import com.ptkebonagung.gotani.data.DataEmplasemenHolder;
import com.ptkebonagung.gotani.model.EMPLASEMEN;
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

public class EmplasemenActivity extends AppCompatActivity {
    private View parent_view;

    private RecyclerView recyclerView;
    //private AdapterListNews mAdapter;
    private AdapterListEmplasemen mAdapter = new AdapterListEmplasemen();
    private ProgressBar progress_bar;
    private EditText et_search;
    private ImageButton bt_clear;

    private int page_number = 1;
    private APIService apiService;
    private APIService apiService2;

    private int page_now    = 2;

    /*List Emplasement*/
    private List<EMPLASEMEN> listDataEmplasemen = new ArrayList<>();
    /*List Emplasement*/

    /*pagination variable*/
    private LinearLayoutManager layoutManager;
    private boolean isLoading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount, previousTotal = 0;
    private int viewThreshold = 2;
    /*pagination variable*/

    private boolean isClick = false;
    private boolean isSearch = false;

    /*cek connection property*/
    private Button btnRetry;
    /*cek connection property*/

    private View searchNotFound;
    private View noDataAvailable;
    private boolean dataIsAvailable = true;

    private TextView txtItemNotAvailable;

    protected MyApp mMyApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyApp  = (MyApp) this.getApplicationContext();

        //Check user login atau tidak, jika tidak login, dikembalikan ke halaman login
        boolean isLogin = Preference.getKeyStatusIsLoggedIn(getBaseContext());
        if (isLogin == false){

            Intent intent = new Intent(EmplasemenActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        } else {

            boolean hasConnection = checkConnection();
            if (hasConnection == false){

                showNotification();

            } else {

                setContentView(R.layout.activity_emplasemen);
                parent_view     = findViewById(android.R.id.content);

                searchNotFound  = findViewById(R.id.searchResult);
                progress_bar    = (ProgressBar) findViewById(R.id.progress_bar);
                et_search       = (EditText) findViewById(R.id.et_search);
                bt_clear        = (ImageButton) findViewById(R.id.bt_clear);

                noDataAvailable     = findViewById(R.id.dataNotAvailable);
                txtItemNotAvailable = findViewById(R.id.noDataTxt);

                layoutManager   = new LinearLayoutManager(getApplicationContext());
                recyclerView    = findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);


                Tools.setSystemBarColor(this, R.color.blue_grey_600);

                loadDataEmplasment("");
                searchDataEmplasemen();
                clearTextSearchData();
                //initComponent();

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

    private boolean checkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    private void loadDataEmplasment(final String keyword){
        String email_key_api                        = Preference.getKeyEmailRegistered(getBaseContext());
        String key_api                              = Preference.getKeyApi(getBaseContext());

        HashMap<String, String> param               = new HashMap<>();
        apiService                                  = ApiClient.getRetrofitInstance().create(APIService.class);
        apiService2                                 = ApiClient2.getRetrofitInstance().create(APIService.class);

        Call<DataEmplasemenHolder> dataEmplasemen   = null;

        if (keyword.length() > 0){

            param.clear();
            listDataEmplasemen.clear();

            param.put("email", email_key_api);
            param.put("key", key_api);
            param.put("register", keyword);

            dataEmplasemen  = apiService.getDataEmplasemen(page_number, param);
            Log.d("API Service 1","Load Emplasment via Service 1");

            if (dataEmplasemen.request().url().query() == null){
                dataEmplasemen = apiService2.getDataEmplasemen(page_number, param);
                Log.d("API Service 2","Load Emplasment via Service 2");
            }

            progress_bar.setVisibility(View.VISIBLE);

            dataEmplasemen.enqueue(new Callback<DataEmplasemenHolder>() {
                @Override
                public void onResponse(Call<DataEmplasemenHolder> call, Response<DataEmplasemenHolder> response) {

                    if(response.isSuccessful()){

                        int sizeData = response.body().getData().size();

                        if(sizeData > 0){
                            searchNotFound.setVisibility(View.GONE);

                            for (int i=0; i < sizeData; i++){
                                EMPLASEMEN emplasemenList   = new EMPLASEMEN();
                                emplasemenList.kd_antrian   = response.body().getData().get(i).getKdAntrian();
                                emplasemenList.nospta       = response.body().getData().get(i).getNoSpta();
                                emplasemenList.no_register  = response.body().getData().get(i).getNoRegister();
                                emplasemenList.no_pol       = response.body().getData().get(i).getNopol();
                                emplasemenList.tgl_antrian  = response.body().getData().get(i).getTglAntrian();
                                emplasemenList.tgl_estimasi = response.body().getData().get(i).getTglEstimasi();
                                listDataEmplasemen.add(emplasemenList);
                            }

                            mAdapter    = new AdapterListEmplasemen(getApplicationContext(), listDataEmplasemen, R.layout.item_emplasemen);
                            recyclerView.setAdapter(mAdapter);
                            progress_bar.setVisibility(View.GONE);

                        } else {

                            progress_bar.setVisibility(View.GONE);
                            //Snackbar.make(parent_view, "Data yang dicari tidak ada ..", Snackbar.LENGTH_SHORT).show();
                            searchNotFound.setVisibility(View.VISIBLE);

                        }

                    } else {

                        Snackbar.make(parent_view,"Data gagal disinkronkan", Snackbar.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onFailure(Call<DataEmplasemenHolder> call, Throwable t) {
                    Snackbar.make(parent_view,"Gagal untuk mengambil data", Snackbar.LENGTH_LONG).show();
                }
            });

        } else {

            param.clear();
            listDataEmplasemen.clear();

            param.put("email", email_key_api);
            param.put("key", key_api);

            dataEmplasemen      = apiService.getDataEmplasemen(page_number, param);
            Log.d("API Service 1","Load Emplasment via Service 1");

            if (dataEmplasemen.request().url().query() == null){
                dataEmplasemen  = apiService2.getDataEmplasemen(page_number, param);
                Log.d("API Service 2","Load Emplasment via Service 2");
            }

            progress_bar.setVisibility(View.VISIBLE);

            dataEmplasemen.enqueue(new Callback<DataEmplasemenHolder>() {
                @Override
                public void onResponse(Call<DataEmplasemenHolder> call, Response<DataEmplasemenHolder> response) {

                    if(response.isSuccessful()){

                        int sizeData = response.body().getData().size();

                        if(sizeData > 0){

                            for (int i=0; i < sizeData; i++){
                                EMPLASEMEN emplasemenList   = new EMPLASEMEN();
                                emplasemenList.kd_antrian   = response.body().getData().get(i).getKdAntrian();
                                emplasemenList.nospta       = response.body().getData().get(i).getNoSpta();
                                emplasemenList.no_register  = response.body().getData().get(i).getNoRegister();
                                emplasemenList.no_pol       = response.body().getData().get(i).getNopol();
                                emplasemenList.tgl_antrian  = response.body().getData().get(i).getTglAntrian();
                                emplasemenList.tgl_estimasi = response.body().getData().get(i).getTglEstimasi();
                                listDataEmplasemen.add(emplasemenList);
                            }

                            mAdapter    = new AdapterListEmplasemen(getApplicationContext(), listDataEmplasemen, R.layout.item_emplasemen);
                            recyclerView.setAdapter(mAdapter);
                            progress_bar.setVisibility(View.GONE);

                        } else {

                            progress_bar.setVisibility(View.GONE);
                            txtItemNotAvailable.setText("Mohon maaf saat ini data EMPLASEMEN \n untuk anda belum tersedia");
                            //Snackbar.make(parent_view, "Data yang dicari tidak ada ..", Snackbar.LENGTH_SHORT).show();
                            noDataAvailable.setVisibility(View.VISIBLE);
                            dataIsAvailable = false;

                        }

                    } else {

                        Snackbar.make(parent_view,"Data gagal disinkronkan", Snackbar.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onFailure(Call<DataEmplasemenHolder> call, Throwable t) {
                    Snackbar.make(parent_view,"Gagal untuk mengambil data", Snackbar.LENGTH_LONG).show();
                }
            });

        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                String keyWord      = keyword;
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

    private void performPagination(final String keyWord){
        progress_bar.setVisibility(View.VISIBLE);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String key_email    = Preference.getKeyEmailRegistered(getBaseContext());
                String key_api      = Preference.getKeyApi(getBaseContext());

                HashMap<String, String> param                   = new HashMap<>();
                Call<DataEmplasemenHolder> otherDataEmplesemen  = null;

                if (keyWord.length() > 0){

                    param.clear();
                    param.put("email", key_email);
                    param.put("key", key_api);
                    param.put("register", keyWord);

                    otherDataEmplesemen     = apiService.getDataEmplasemen(page_number, param);
                    Log.d("API Service 1","Load Emplasment via Service 1");

                    if (otherDataEmplesemen.request().url().query() == null){
                        otherDataEmplesemen = apiService2.getDataEmplasemen(page_number, param);
                        Log.d("API Service 2","Load Emplasment via Service 2");
                    }

                } else {

                    param.clear();
                    param.put("email", key_email);
                    param.put("key", key_api);

                    otherDataEmplesemen     = apiService.getDataEmplasemen(page_number, param);
                    Log.d("API Service 1","Load Emplasment via Service 1");

                    if (otherDataEmplesemen.request().url().query() == null){
                        otherDataEmplesemen = apiService2.getDataEmplasemen(page_number, param);
                        Log.d("API Service 2","Load Emplasment via Service 2");
                    }


                }

                otherDataEmplesemen.enqueue(new Callback<DataEmplasemenHolder>() {
                    @Override
                    public void onResponse(Call<DataEmplasemenHolder> call, Response<DataEmplasemenHolder> response) {
                        if (response.body().getData().size() != 0){
                            List<EMPLASEMEN> dtEmplasemen = new ArrayList<>();
                            int sizeData = response.body().getData().size();

                            for (int i=0; i < sizeData; i++){
                                EMPLASEMEN emplasemenListOther   = new EMPLASEMEN();
                                emplasemenListOther.kd_antrian   = response.body().getData().get(i).getKdAntrian();
                                emplasemenListOther.nospta       = response.body().getData().get(i).getNoSpta();
                                emplasemenListOther.no_register  = response.body().getData().get(i).getNoRegister();
                                emplasemenListOther.no_pol       = response.body().getData().get(i).getNopol();
                                emplasemenListOther.tgl_antrian  = response.body().getData().get(i).getTglAntrian();
                                emplasemenListOther.tgl_estimasi = response.body().getData().get(i).getTglEstimasi();
                                dtEmplasemen.add(emplasemenListOther);
                            }

                            mAdapter.addMoreEmplasemen(dtEmplasemen);
                            page_now++;
                            progress_bar.setVisibility(View.GONE);

                            if (isClick || isSearch){
                                dtEmplasemen.clear();
                            }

                        } else {

                            progress_bar.setVisibility(View.GONE);
                            Snackbar.make(parent_view,"No more data available ..", Snackbar.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<DataEmplasemenHolder> call, Throwable t) {
                        Snackbar.make(parent_view,"Gagal untuk mengambil data", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        }, 1000);
    }

    private void searchDataEmplasemen() {
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

        boolean hasConnection   = NetworkUtil.getConnectivityStatus(getApplicationContext());

        if (hasConnection == true){

            if (dataIsAvailable == true){

                if (keyword.equals("")){

                    View view = this.getCurrentFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    Snackbar.make(parent_view, "Mohon isi register yang ingin dicari", Snackbar.LENGTH_LONG).show();

                } else {

                    mAdapter.clearEmplasemenData();

                    page_number = 1;
                    page_now    = 2;

                    /*pagination variable*/
                    isLoading           = true;
                    pastVisibleItems    = visibleItemCount = totalItemCount = previousTotal = 0;
                    viewThreshold       = 2;
                    /*pagination variable*/

                    isClick = true;
                    isSearch = true;

                    loadDataEmplasment(keyword);
                }
            } else {

                et_search.setText("");

            }

        } else {

            showNotification();

        }
    }

    private void clearTextSearchData() {
        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshData();
            }
        });
    }

    private void refreshData(){

        boolean hasConnection = NetworkUtil.getConnectivityStatus(getApplicationContext());

        if (hasConnection == true){

            if (dataIsAvailable == true){

                searchNotFound.setVisibility(View.GONE);

                et_search.setText("");
                mAdapter.clearEmplasemenData();

                page_number = 1;
                page_now    = 2;

                /*pagination variable*/
                isLoading = true;
                pastVisibleItems = visibleItemCount = totalItemCount = previousTotal = 0;
                viewThreshold = 2;
                /*pagination variable*/

                isClick = true;
                listDataEmplasemen.clear();
                loadDataEmplasment("");

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

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        List<News> items = DataGenerator.getNewsData(this, 10);

        //set data and list adapter
        mAdapter = new AdapterListNews(this, items, R.layout.item_emplasemen);
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
