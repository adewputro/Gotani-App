package com.ptkebonagung.gotani.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.annotation.NonNull;

import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.ptkebonagung.gotani.MainActivity;
import com.ptkebonagung.gotani.R;
import com.ptkebonagung.gotani.adapter.AdapterListTebangan;

import com.ptkebonagung.gotani.data.DataTebanganHolder;
import com.ptkebonagung.gotani.fragment.DialogBuktiTimbangFragment;
import com.ptkebonagung.gotani.fragment.SublimePickerFragment;
import com.ptkebonagung.gotani.model.TEBANGAN;
import com.ptkebonagung.gotani.utils.APIService;
import com.ptkebonagung.gotani.utils.AlertDialogHelper;
import com.ptkebonagung.gotani.utils.ApiClient;
import com.ptkebonagung.gotani.utils.ApiClient2;
import com.ptkebonagung.gotani.utils.MyApp;
import com.ptkebonagung.gotani.utils.NetworkUtil;
import com.ptkebonagung.gotani.utils.Preference;
import com.ptkebonagung.gotani.utils.RecyclerItemClickListener;
import com.ptkebonagung.gotani.utils.Tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.security.PublicKey;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.N)
public class TebanganActivity extends AppCompatActivity {
    private View parent_view;
    private View searcResultNotFound;
    private View viewDataNotAvailable;

    private RecyclerView recyclerView;
    private ProgressBar progress_bar;
    private EditText et_search;

    private ImageButton bt_clear;

    private int page_number = 1;
    private APIService apiService;
    private APIService apiService2;
    int pageNow = 2;

    /*List SPTA*/
    ArrayList<TEBANGAN> listDtTEBANGAN=new ArrayList<>();
    ArrayList<TEBANGAN> multiselect_listDtTEBANGAN=new ArrayList<>();
    ArrayList<String> paths = new ArrayList<>();
    ArrayList<String> sms_vals = new ArrayList<>();
    private AdapterListTebangan mAdapter = new AdapterListTebangan();
    /*List SPTA*/

    /*pagination variable*/
    private LinearLayoutManager layoutManager;
    private boolean isLoading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount, previousTotal = 0;
    private int viewThreshold = 2;
    /*pagination variable*/

    private boolean isClick = false;
    private boolean isSearch = false;

    boolean isMultiSelect = false;

    /*cek connection property*/
    private Button btnRetry,flotbtn;
//    Calendar calendar = Calendar.getInstance();
//    Locale id = new Locale("in", "ID");
//    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("Y-M-d",id);
    /*cek connection property*/

    private TextView txtDataAvailable;
    private boolean dataAvailable = true;
    private boolean isCheck_all = false;

    protected MyApp mMyApp;

    private LinearLayout linearLayout;

    ActionMode mActionMode;
    Menu context_menu;
    AlertDialogHelper alertDialogHelper;
    Context context;

    private CardView search_bar;
    private View root_view;
    private AppBarLayout appBarLayout;
    String mDateStart;
    String mDateEnd;
    String Date1;
    String Date2;
    String JumlahDate;
    Unbinder unbinder;

//    public void DatePlus(String[] args) throws ParseException{
//
//
//        return;
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyApp              = (MyApp) this.getApplicationContext();

        boolean isLogin = Preference.getKeyStatusIsLoggedIn(getBaseContext());
        if(!isLogin){

            Intent intent = new Intent(TebanganActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        } else {

            boolean hasConnection = NetworkUtil.getConnectivityStatus(getApplicationContext());
            if (hasConnection == false){

                showNotification();

            } else {

                setContentView(R.layout.activity_tebangan);
                appBarLayout = findViewById(R.id.appbar);
                searcResultNotFound = findViewById(R.id.searchResult);
                parent_view         = findViewById(android.R.id.content);
                progress_bar        = (ProgressBar) findViewById(R.id.progress_bar);
                et_search           = (EditText) findViewById(R.id.et_search);

                bt_clear            = (ImageButton) findViewById(R.id.bt_clear);
                search_bar = findViewById(R.id.search_bar);
                layoutManager       = new LinearLayoutManager(getApplicationContext());
                recyclerView        = findViewById(R.id.recyclerView);
                FloatingActionButton flotbtn = findViewById(R.id.flotbtn);
                //recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                viewDataNotAvailable= findViewById(R.id.dataNotAvailable);
                txtDataAvailable    = (TextView) findViewById(R.id.noDataTxt);
                linearLayout        = findViewById(R.id.tombolShare);
                context=this;


                ButterKnife.bind(this);

                unbinder = ButterKnife.bind(this);
                flotbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openDateRangePicker();
//                        openDateRangePicker();
                    }
                });
                Tools.setSystemBarColor(this, R.color.blue_grey_600);


                loadDataTebangan("", "");
                dateItem();



                searchItem();
                clearTextSearch();

                mAdapter = new AdapterListTebangan(getApplicationContext(), listDtTEBANGAN, multiselect_listDtTEBANGAN, R.layout.item_tebangan);
                recyclerView.setAdapter(mAdapter);
                progress_bar.setVisibility(View.GONE);


                recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (isMultiSelect) {
                            multi_select(position);

                        } else {

                            showDialogBuktiTimbang(listDtTEBANGAN.get(position).jenisTebang, listDtTEBANGAN.get(position).tglMasuk, listDtTEBANGAN.get(position).dateOut,
                                    listDtTEBANGAN.get(position).noSpta, listDtTEBANGAN.get(position).kdAntrian,
                                    listDtTEBANGAN.get(position).noRegister, listDtTEBANGAN.get(position).petani,
                                    listDtTEBANGAN.get(position).weightIn, listDtTEBANGAN.get(position).weightOut,
                                    listDtTEBANGAN.get(position).weightNet, listDtTEBANGAN.get(position).weightKw,
                                    listDtTEBANGAN.get(position).rafaksiKw, listDtTEBANGAN.get(position).mbs,
                                    listDtTEBANGAN.get(position).nopol, listDtTEBANGAN.get(position).varietas, listDtTEBANGAN.get(position).no_meja,
                                    "single_select");
//                            Toast.makeText(getApplicationContext(), "Details Page", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        if (!isMultiSelect) {
                            multiselect_listDtTEBANGAN = new ArrayList<TEBANGAN>();
                            isMultiSelect = true;
                            search_bar.setVisibility(View.INVISIBLE);


                            if (mActionMode == null) {
                                mActionMode = startActionMode(mActionModeCallback);
                            }
                        }

                        multi_select(position);

                    }
                }));

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
    private void openDateRangePicker(){
        SublimePickerFragment pickerFrag = new SublimePickerFragment();
        pickerFrag.setCallback(new SublimePickerFragment.Callback() {
            @Override
            public void onCancelled() {
                Toast.makeText(TebanganActivity.this, "User cancel",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDateTimeRecurrenceSet(final SelectedDate selectedDate, int hourOfDay, int minute,
                                                SublimeRecurrencePicker.RecurrenceOption recurrenceOption,
                                                String recurrenceRule) {

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
                mDateStart = formatDate.format(selectedDate.getStartDate().getTime());
                mDateEnd = formatDate.format(selectedDate.getEndDate().getTime());

//                int sl = Integer.parseInt(mDateStart);
//                Date ls = Date.valueOf(mDateStart);

//                    Date date = df.parse("2011-01-01");



//                    Date ew = new Date();
//                String ls = Date.from(mDateEnd)
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//                Calendar c = Calendar.getInstance();
//                c.setTime(etMill);
//                c.add(Calendar.DATE, 1);
//                Log.d("SS", etMill)





                if (mDateEnd.equals(mDateStart)){
                    Date1 = mDateStart+" "+"06:00:00";
                    Date2 = mDateEnd+" "+"23:00:00";
//                    try {
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                        Calendar c = Calendar.getInstance();
//                        c.setTime(sdf.parse(mDateEnd));
//                        c.add(Calendar.DATE, 1);
//                        SimpleDateFormat dateName = new SimpleDateFormat("yyyy-MM-dd");
//                        mDateEnd = dateName.format(c.getTime());
//                        String keyword = "";
//                        String dt ="dt";
//                        dateItemAction(keyword, dt);
////                        Snackbar.make(parent_view,Jumlah, Snackbar.LENGTH_LONG).show();
//                    }catch (Exception e){
//
//                    }


                }else{
                    Date1 = mDateStart+" "+"06:00:00";
                    Date2 = mDateEnd+" "+"23:59:00";
//                    String keyword = "";
//                    String dt ="dt";
//                    dateItemAction(keyword, dt);
                }

                String keyword = "";
                String dt ="dt";
                dateItemAction(keyword, dt);
            }
        });


        // ini configurasi agar library menggunakan method Date Range Picker
        SublimeOptions options = new SublimeOptions();
        options.setCanPickDateRange(true);
        options.setPickerToShow(SublimeOptions.Picker.DATE_PICKER);

        Bundle bundle = new Bundle();
        bundle.putParcelable("SUBLIME_OPTIONS", options);
        pickerFrag.setArguments(bundle);

        pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        pickerFrag.show(getSupportFragmentManager(), "SUBLIME_PICKER");
    }
    private void loadDataFilterTebangan(final String startDate, final String endDate){
//            txtStart.setText(startDate);
//
    }

    private void loadDataTebangan(final String keyWord, final String dt){

        String key_email                        = Preference.getKeyEmailRegistered(getBaseContext());
        String key_api                          = Preference.getKeyApi(getBaseContext());

        HashMap<String, String> param           = new HashMap<>();
        apiService                              = ApiClient.getRetrofitInstance().create(APIService.class);
        apiService2                             = ApiClient2.getRetrofitInstance().create(APIService.class);

        Call<DataTebanganHolder> dataTebangan   = null;

        if (keyWord.length() > 0) {

            param.clear();
            listDtTEBANGAN.clear();

            param.put("register", keyWord);
            param.put("email", key_email);
            param.put("key", key_api);

            dataTebangan = apiService.getDataTebangan(page_number, param);
            Log.d("API Service 1", "Load Tebangan via Service 1");

            if (dataTebangan.request().url().query() == null) {
                dataTebangan = apiService2.getDataTebangan(page_number, param);
                Log.d("API Service 2", "Load Tebangan via Service 2");
            }
            //                            loadDataTebangan("");
            progress_bar.setVisibility(View.VISIBLE);
            showFilteredDataTebangan(dataTebangan);

            //                        }

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
        }else if(dt.length() > 0){
            param.clear();
//            listDtTEBANGAN.clear();
            String dt1 = Date1;
            String dt2 = Date2;


            param.put("date_start", dt1);
            param.put("date_end", dt2);
            param.put("email", key_email);
            param.put("key", key_api);

            dataTebangan = apiService.getDataFilterTebangan(page_number, param);
            Log.d("API Service 1", "Load Tebangan via Service 1");

            if (dataTebangan.request().url().query() == null) {
                dataTebangan = apiService2.getDataFilterTebangan(page_number, param);
                Log.d("API Service 2", "Load Tebangan via Service 2");
            }
            //                            loadDataTebangan("");
            progress_bar.setVisibility(View.VISIBLE);
            showFilteredDateDataTebangan(dataTebangan);


            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    String strDate = dt1;
                    String edDate = dt2;
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (dy > 0) {

                        if (isLoading) {
                            if (totalItemCount > previousTotal) {
                                isLoading = false;
                                previousTotal = totalItemCount;
                            }
                        }

                        if (!isLoading && ((totalItemCount - visibleItemCount) <= (pastVisibleItems + 0))) {
                            page_number++;
                            filterPerformPagination(dt1, dt2);
                            isLoading = true;
                        }
                    }
                }
            });
        }else {

            param.clear();
            listDtTEBANGAN.clear();

            param.put("email", key_email);
            param.put("key", key_api);

            dataTebangan    = apiService.getDataTebangan(page_number, param);
            Log.d("API Service 1","Load Tebangan via Service 1");

            if (dataTebangan.request().url().query() == null){
                dataTebangan   = apiService2.getDataTebangan(page_number, param);
                Log.d("API Service 2","Load Tebangan via Service 2");
            }


            progress_bar.setVisibility(View.VISIBLE);
            showDataTebangan(dataTebangan);
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


    }
    private void showDataTebangan(Call<DataTebanganHolder> dataTebangan) {
        dataTebangan.enqueue(new Callback<DataTebanganHolder>() {
            @Override
            public void onResponse(Call<DataTebanganHolder> call, Response<DataTebanganHolder> response) {
                if (response.isSuccessful()){
                    int dataSize = response.body().getData().size();

                    if (dataSize > 0){

                        for (int i=0; i < dataSize; i++){
                            TEBANGAN tebangan = new TEBANGAN();

                            tebangan.jenisTebang= response.body().getData().get(i).getJenisTebang();

                            tebangan.tglMasuk   = response.body().getData().get(i).getTglMasuk();
                            tebangan.dateOut    = response.body().getData().get(i).getDateOut();

                            tebangan.noSpta     = response.body().getData().get(i).getNoSpta();
                            tebangan.kdAntrian  = response.body().getData().get(i).getKdAntrian();

                            tebangan.noRegister = response.body().getData().get(i).getNoRegister();
                            tebangan.petani     = response.body().getData().get(i).getPetani();

                            tebangan.weightIn   = response.body().getData().get(i).getWeightIn();
                            tebangan.weightOut  = response.body().getData().get(i).getWeightOut();

                            tebangan.weightNet  = response.body().getData().get(i).getWeightNet();
                            tebangan.weightKw   = response.body().getData().get(i).getWeightKw();

                            tebangan.rafaksiKw  = response.body().getData().get(i).getRafaksiKw();
                            tebangan.mbs        = response.body().getData().get(i).getMbs();

                            tebangan.nopol      = response.body().getData().get(i).getNopol();
                            tebangan.varietas   = response.body().getData().get(i).getVarietas();
                            tebangan.no_meja   = response.body().getData().get(i).getNo_meja();

                            listDtTEBANGAN.add(tebangan);
                        }

                        mAdapter    = new AdapterListTebangan(getApplicationContext(), listDtTEBANGAN,multiselect_listDtTEBANGAN, R.layout.item_tebangan);
                        recyclerView.setAdapter(mAdapter);
                        progress_bar.setVisibility(View.GONE);



                    } else {

                        progress_bar.setVisibility(View.GONE);
                        //Snackbar.make(parent_view,"Data yang dicari tidak ada ..", Snackbar.LENGTH_SHORT).show();
                        viewDataNotAvailable.setVisibility(View.VISIBLE);
                        dataAvailable   = false;
                        txtDataAvailable.setText("Mohon maaf saat ini data TEBANGAN \n untuk anda belum tersedia");

                    }

                } else {

                    progress_bar.setVisibility(View.GONE);
                    Snackbar.make(parent_view,"Data gagal disinkronkan", Snackbar.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<DataTebanganHolder> call, Throwable t) {
                progress_bar.setVisibility(View.GONE);
                Snackbar.make(parent_view,"Gagal untuk mengambil data", Snackbar.LENGTH_LONG).show();
            }
        });
    }
    private void showFilteredDataTebangan(Call<DataTebanganHolder> dataTebangan){
        dataTebangan.enqueue(new Callback<DataTebanganHolder>() {
            @Override
            public void onResponse(Call<DataTebanganHolder> call, Response<DataTebanganHolder> response) {
                if (response.isSuccessful()){
                    int sizeData    = response.body().getData().size();

                    if(sizeData > 0){
                        searcResultNotFound.setVisibility(View.GONE);

                        for (int i=0; i < sizeData; i++){
                            TEBANGAN tebangan   = new TEBANGAN();

                            tebangan.jenisTebang= response.body().getData().get(i).getJenisTebang();

                            tebangan.tglMasuk   = response.body().getData().get(i).getTglMasuk();
                            tebangan.dateOut    = response.body().getData().get(i).getDateOut();

                            tebangan.noSpta     = response.body().getData().get(i).getNoSpta();
                            tebangan.kdAntrian  = response.body().getData().get(i).getKdAntrian();

                            tebangan.noRegister = response.body().getData().get(i).getNoRegister();
                            tebangan.petani     = response.body().getData().get(i).getPetani();

                            tebangan.weightIn   = response.body().getData().get(i).getWeightIn();
                            tebangan.weightOut  = response.body().getData().get(i).getWeightOut();

                            tebangan.weightNet  = response.body().getData().get(i).getWeightNet();
                            tebangan.weightKw   = response.body().getData().get(i).getWeightKw();

                            tebangan.rafaksiKw  = response.body().getData().get(i).getRafaksiKw();
                            tebangan.mbs        = response.body().getData().get(i).getMbs();

                            tebangan.nopol      = response.body().getData().get(i).getNopol();
                            tebangan.varietas   = response.body().getData().get(i).getVarietas();
                            tebangan.no_meja   = response.body().getData().get(i).getNo_meja();

                            listDtTEBANGAN.add(tebangan);
                        }

                        mAdapter    = new AdapterListTebangan(getApplicationContext(), listDtTEBANGAN,multiselect_listDtTEBANGAN, R.layout.item_tebangan);
                        recyclerView.setAdapter(mAdapter);
                        progress_bar.setVisibility(View.GONE);

                    } else {
                        progress_bar.setVisibility(View.GONE);
                        searcResultNotFound.setVisibility(View.VISIBLE);


                    }

                } else {
                    progress_bar.setVisibility(View.GONE);
                    Snackbar.make(parent_view,"Data gagal diambil", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DataTebanganHolder> call, Throwable t) {
                progress_bar.setVisibility(View.GONE);
                Snackbar.make(parent_view,"Gagal untuk mengambil data", Snackbar.LENGTH_LONG).show();
            }
        });
    }
    private void showFilteredDateDataTebangan(Call<DataTebanganHolder> dataTebangan){
        dataTebangan.enqueue(new Callback<DataTebanganHolder>() {
            @Override
            public void onResponse(Call<DataTebanganHolder> call, Response<DataTebanganHolder> response) {
                if (response.isSuccessful()){
                    int sizeData    = response.body().getData().size();

                    if(sizeData > 0){
                        searcResultNotFound.setVisibility(View.GONE);

                        for (int i=0; i < sizeData; i++){
                            TEBANGAN tebangan   = new TEBANGAN();

                            tebangan.jenisTebang= response.body().getData().get(i).getJenisTebang();

                            tebangan.tglMasuk   = response.body().getData().get(i).getTglMasuk();
                            tebangan.dateOut    = response.body().getData().get(i).getDateOut();

                            tebangan.noSpta     = response.body().getData().get(i).getNoSpta();
                            tebangan.kdAntrian  = response.body().getData().get(i).getKdAntrian();

                            tebangan.noRegister = response.body().getData().get(i).getNoRegister();
                            tebangan.petani     = response.body().getData().get(i).getPetani();

                            tebangan.weightIn   = response.body().getData().get(i).getWeightIn();
                            tebangan.weightOut  = response.body().getData().get(i).getWeightOut();

                            tebangan.weightNet  = response.body().getData().get(i).getWeightNet();
                            tebangan.weightKw   = response.body().getData().get(i).getWeightKw();

                            tebangan.rafaksiKw  = response.body().getData().get(i).getRafaksiKw();
                            tebangan.mbs        = response.body().getData().get(i).getMbs();

                            tebangan.nopol      = response.body().getData().get(i).getNopol();
                            tebangan.varietas   = response.body().getData().get(i).getVarietas();
                            tebangan.no_meja   = response.body().getData().get(i).getNo_meja();

                            listDtTEBANGAN.add(tebangan);
                        }

                        mAdapter    = new AdapterListTebangan(getApplicationContext(), listDtTEBANGAN,multiselect_listDtTEBANGAN, R.layout.item_tebangan);
                        recyclerView.setAdapter(mAdapter);
                        progress_bar.setVisibility(View.GONE);

                    } else {
                        progress_bar.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        progress_bar.setVisibility(View.GONE);
                        searcResultNotFound.setVisibility(View.VISIBLE);

//                        refreshData();
                    }

                } else {
                    progress_bar.setVisibility(View.GONE);
                    Snackbar.make(parent_view,"Data gagal diambil", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DataTebanganHolder> call, Throwable t) {
                progress_bar.setVisibility(View.GONE);
                Snackbar.make(parent_view,"Gagal untuk mengambil data", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void performPagination(final String KeyWord){

        progress_bar.setVisibility(View.VISIBLE);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String email_key_api = Preference.getKeyEmailRegistered(getBaseContext());
                String key_api       = Preference.getKeyApi(getBaseContext());

                HashMap<String, String> param = new HashMap<>();
                Call<DataTebanganHolder> otherDataTebangan      = null;

                if (KeyWord.length() > 0){

                    param.clear();
                    param.put("email", email_key_api);
                    param.put("key", key_api);
                    param.put("register", KeyWord);

                    otherDataTebangan = apiService.getDataTebangan(page_number, param);
                    Log.d("API Service 1","Load SPTA via Service 1");

                    if (otherDataTebangan.request().url().query() == null){
                        otherDataTebangan = apiService2.getDataTebangan(page_number, param);
                        Log.d("API Service 2","Load SPT via Service 2");
                    }

                } else {

                    param.clear();
                    param.put("email", email_key_api);
                    param.put("key", key_api);

                    otherDataTebangan = apiService.getDataTebangan(page_number, param);
                    Log.d("API Service 1","Load SPTA via Service 1");

                    if (otherDataTebangan.request().url().query() == null){
                        otherDataTebangan = apiService2.getDataTebangan(page_number, param);
                        Log.d("API Service 2","Load SPTA via Service 2");
                    }

                }

                showOtherDataTebangan(otherDataTebangan);


            }
        }, 1000);

    }
    private void filterPerformPagination(final String startDate,final String endDate){

        progress_bar.setVisibility(View.VISIBLE);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String email_key_api = Preference.getKeyEmailRegistered(getBaseContext());
                String key_api       = Preference.getKeyApi(getBaseContext());

                HashMap<String, String> param = new HashMap<>();
                Call<DataTebanganHolder> otherDataTebangan      = null;

                if (startDate.length() > 0){

                    param.clear();
                    param.put("email", email_key_api);
                    param.put("key", key_api);
                    param.put("date_start", startDate);
                    param.put("date_end", endDate);

                    otherDataTebangan = apiService.getDataFilterTebangan(page_number, param);
                    Log.d("API Service 1","Load SPTA via Service 1");

                    if (otherDataTebangan.request().url().query() == null){
                        otherDataTebangan = apiService2.getDataFilterTebangan(page_number, param);
                        Log.d("API Service 2","Load SPT via Service 2");
                    }

                } else {

                    param.clear();
                    param.put("email", email_key_api);
                    param.put("key", key_api);

                    otherDataTebangan = apiService.getDataTebangan(page_number, param);
                    Log.d("API Service 1","Load SPTA via Service 1");

                    if (otherDataTebangan.request().url().query() == null){
                        otherDataTebangan = apiService2.getDataTebangan(page_number, param);
                        Log.d("API Service 2","Load SPTA via Service 2");
                    }

                }

                showOtherDataTebangan(otherDataTebangan);


            }
        }, 1000);

    }
    private void showOtherDataTebangan(final Call<DataTebanganHolder> otherDataTebangan){
        otherDataTebangan.enqueue(new Callback<DataTebanganHolder>() {
            @Override
            public void onResponse(Call<DataTebanganHolder> call, Response<DataTebanganHolder> response) {
                if (response.body().getData().size() != 0){

                    int sizeData = response.body().getData().size();
                    final List<TEBANGAN> otherTebangan = new ArrayList<>();

                    for (int i=0; i < sizeData; i++){

                        TEBANGAN tebangan   = new TEBANGAN();

                        tebangan.jenisTebang= response.body().getData().get(i).getJenisTebang();

                        tebangan.tglMasuk   = response.body().getData().get(i).getTglMasuk();
                        tebangan.dateOut    = response.body().getData().get(i).getDateOut();

                        tebangan.noSpta     = response.body().getData().get(i).getNoSpta();
                        tebangan.kdAntrian  = response.body().getData().get(i).getKdAntrian();

                        tebangan.noRegister = response.body().getData().get(i).getNoRegister();
                        tebangan.petani     = response.body().getData().get(i).getPetani();

                        tebangan.weightIn   = response.body().getData().get(i).getWeightIn();
                        tebangan.weightOut  = response.body().getData().get(i).getWeightOut();

                        tebangan.weightNet  = response.body().getData().get(i).getWeightNet();
                        tebangan.weightKw   = response.body().getData().get(i).getWeightKw();

                        tebangan.rafaksiKw  = response.body().getData().get(i).getRafaksiKw();
                        tebangan.mbs        = response.body().getData().get(i).getMbs();

                        tebangan.nopol      = response.body().getData().get(i).getNopol();
                        tebangan.varietas   = response.body().getData().get(i).getVarietas();
                        tebangan.no_meja   = response.body().getData().get(i).getNo_meja();

                        otherTebangan.add(tebangan);
                    }

                    mAdapter.addMoreTebangan(otherTebangan);
                    //page_number++;
                    progress_bar.setVisibility(View.GONE);



                    if (isClick || isSearch){
                        otherTebangan.clear();
                    }

                } else {
                    progress_bar.setVisibility(View.GONE);
                    Snackbar.make(parent_view,"No more data available ..", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataTebanganHolder> call, Throwable t) {
                progress_bar.setVisibility(View.GONE);
                Snackbar.make(parent_view,"Gagal untuk mengambil data", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void clearTextSearch(){
        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshData();
            }
        });
    }

    private void refreshData(){
        recyclerView.setVisibility(View.VISIBLE);
        boolean isHasConnection = NetworkUtil.getConnectivityStatus(getApplicationContext());

        if (isHasConnection == true){

            if (dataAvailable == true){

                searcResultNotFound.setVisibility(View.GONE);

                et_search.setText("");

                mAdapter.clearData();

                page_number = 1;
                pageNow = 2;

                /*pagination variable*/
                isLoading = true;
                pastVisibleItems = visibleItemCount = totalItemCount = previousTotal = 0;
                viewThreshold = 2;
                /*pagination variable*/

                isClick = true;
                listDtTEBANGAN.clear();

                loadDataTebangan("", "");

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

    private void dateItem(){

    }
    private void searchItem(){
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    /*hideKeyboard();
                    searchAction();
                    return true;*/
                    searchItemAction(et_search.getText().toString().toUpperCase());
                }

                return false;
            }
        });
    }

    private void dateItemAction(String keyword, String dt) {

        boolean isHasConnection = NetworkUtil.getConnectivityStatus(getApplicationContext());

        if (isHasConnection == true){

            if (dataAvailable == true){

                if (dt.equals("")){
                    View view = this.getCurrentFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    Snackbar.make(parent_view, "Mohon isi register yang ingin dicari", Snackbar.LENGTH_LONG).show();

                } else {

                    mAdapter.clearData();
                    page_number = 1;
                    pageNow = 2;

                    /*pagination variable*/
                    isLoading = true;
                    pastVisibleItems = visibleItemCount = totalItemCount = previousTotal = 0;
                    viewThreshold = 2;
                    /*pagination variable*/

                    isClick = true;
                    isSearch = true;

                    loadDataTebangan(keyword, dt);
                }

            } else {

//                et_search.setText("");

            }

        } else {

            showNotification();

        }

    }
    private void searchItemAction(String keyword) {
        String dt = "";
        boolean isHasConnection = NetworkUtil.getConnectivityStatus(getApplicationContext());

        if (isHasConnection == true){

            if (dataAvailable == true){

                if (keyword.equals("")){
                    View view = this.getCurrentFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    Snackbar.make(parent_view, "Mohon isi register yang ingin dicari", Snackbar.LENGTH_LONG).show();

                } else {

                    mAdapter.clearData();
                    page_number = 1;
                    pageNow = 2;

                    /*pagination variable*/
                    isLoading = true;
                    pastVisibleItems = visibleItemCount = totalItemCount = previousTotal = 0;
                    viewThreshold = 2;
                    /*pagination variable*/

                    isClick = true;
                    isSearch = true;

                    loadDataTebangan(keyword, dt);
                }

            } else {

                et_search.setText("");

            }

        } else {

            showNotification();

        }

    }

    /*private void initComponent() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        List<News> items = DataGenerator.getNewsData(this, 10);

        //set data and list adapter
        mAdapter = new AdapterListNews(this, items, R.layout.item_spta);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterListNews.OnItemClickListener() {
            @Override
            public void onItemClick(View view, News obj, int position) {
                showDialogSPTA();
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

    private void showDialogBuktiTimbang(String jenisTebang, String tglMasuk, String dateOut,
                                        String noSpta, String kdAntrian,
                                        String noRegister, String petani,
                                        String weightIn, String weightOut,
                                        String weightNet, String weightKw,
                                        String rafaksiKw, String mbs,
                                        String nopol, String varietas, String no_meja, String activity_select) {

        boolean hasConnection = NetworkUtil.getConnectivityStatus(getApplicationContext());

        if (hasConnection == true){

            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();
            }
            if (activity_select.equalsIgnoreCase("single_select")) {
                DialogBuktiTimbangFragment newFragment = new DialogBuktiTimbangFragment();

                Bundle bundle = new Bundle();
                bundle.putString("jenis_tebang", jenisTebang);
                bundle.putString("waktu_masuk", tglMasuk);
                bundle.putString("waktu_keluar", dateOut);
                bundle.putString("noSPTA", noSpta);
                bundle.putString("noAntrian", kdAntrian);
                bundle.putString("register", noRegister);
                bundle.putString("pemilik", petani);
                bundle.putString("berat_bruto", weightIn);
                bundle.putString("berat_tara", weightOut);
                bundle.putString("berat_netto", weightNet);
                bundle.putString("berat_netto_kw", weightKw);
                bundle.putString("rafaksi", rafaksiKw);
                bundle.putString("mutu", mbs);
                bundle.putString("nomor_polisi", nopol);
                bundle.putString("varietas", varietas);
                bundle.putString("no_meja", no_meja);

                newFragment.setArguments(bundle);
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
            } else if (activity_select.equalsIgnoreCase("multi_select")){
                create_bitmap( jenisTebang,  tglMasuk,  dateOut,
                         noSpta,  kdAntrian,
                         noRegister,  petani,
                         weightIn,  weightOut,
                         weightNet,  weightKw,
                         rafaksiKw,  mbs,
                         nopol,  varietas,  no_meja);
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
        Activity currActivity = mMyApp .getCurrentActivity() ;
        if ( this .equals(currActivity))
            mMyApp .setCurrentActivity( null ) ;
    }

    public void multi_select(int position) {
        if (mActionMode != null) {
            appBarLayout.setBackgroundColor(getResources().getColor(R.color.blue_grey_600));
            if (multiselect_listDtTEBANGAN.contains(listDtTEBANGAN.get(position))) {
                multiselect_listDtTEBANGAN.remove(listDtTEBANGAN.get(position));
            } else {
                multiselect_listDtTEBANGAN.add(listDtTEBANGAN.get(position));
            }

            if (multiselect_listDtTEBANGAN.size() > 0)
                mActionMode.setTitle("" + multiselect_listDtTEBANGAN.size() + " Selected");
            else
                mActionMode.setTitle("0 Selected");

            refreshAdapter();
        }
    }

    public void load_select(int position, String perintah) {
        if (mActionMode != null) {
            String path = "TEBANGAN"+multiselect_listDtTEBANGAN.get(position).kdAntrian.toUpperCase();

            int bobot               = Integer.parseInt(multiselect_listDtTEBANGAN.get(position).weightKw) - Integer.parseInt(multiselect_listDtTEBANGAN.get(position).rafaksiKw);
            String jenis_tebang     = setJenisTebang(multiselect_listDtTEBANGAN.get(position).jenisTebang);
            String waktu_masuk      = setTimeStamp(multiselect_listDtTEBANGAN.get(position).tglMasuk);
            String waktu_keluar     = setTimeStamp(multiselect_listDtTEBANGAN.get(position).dateOut);
            String nomor_SPTA       = multiselect_listDtTEBANGAN.get(position).noSpta.toUpperCase();
            String nomor_antrian    = multiselect_listDtTEBANGAN.get(position).no_meja.toUpperCase();
            String register_        = multiselect_listDtTEBANGAN.get(position).noRegister.toUpperCase();
            String pemilik_         = multiselect_listDtTEBANGAN.get(position).petani.toUpperCase();
            String bruto_           = multiselect_listDtTEBANGAN.get(position).weightIn.toUpperCase();
            String tara_            = multiselect_listDtTEBANGAN.get(position).weightOut.toUpperCase();
            String nettoKG_         = multiselect_listDtTEBANGAN.get(position).weightNet.toUpperCase();
            String nettoKW_         = multiselect_listDtTEBANGAN.get(position).weightOut.toUpperCase()+" => "+bobot;
            String rafaksiKW_       = multiselect_listDtTEBANGAN.get(position).rafaksiKw;
            String mutu_            = "MBS "+multiselect_listDtTEBANGAN.get(position).mbs.toUpperCase() + " (MEJA "+multiselect_listDtTEBANGAN.get(position).no_meja+")";
            String nomor_polisi     = multiselect_listDtTEBANGAN.get(position).nopol.toUpperCase();
            String varietas_        = multiselect_listDtTEBANGAN.get(position).varietas.toUpperCase();

            String sms_val = "SPTA: "+nomor_SPTA+"\nMasa berlaku\n  " +waktu_masuk+ "\n  "+waktu_keluar + "\nRegister: "+register_ + "\nPemilik: "+pemilik_+"\nJenis: "+jenis_tebang+"\n\n";
            if (perintah.equalsIgnoreCase("create_image")) {
                showDialogBuktiTimbang(listDtTEBANGAN.get(position).jenisTebang, listDtTEBANGAN.get(position).tglMasuk, listDtTEBANGAN.get(position).dateOut,
                        listDtTEBANGAN.get(position).noSpta, listDtTEBANGAN.get(position).kdAntrian,
                        listDtTEBANGAN.get(position).noRegister, listDtTEBANGAN.get(position).petani,
                        listDtTEBANGAN.get(position).weightIn, listDtTEBANGAN.get(position).weightOut,
                        listDtTEBANGAN.get(position).weightNet, listDtTEBANGAN.get(position).weightKw,
                        listDtTEBANGAN.get(position).rafaksiKw, listDtTEBANGAN.get(position).mbs,
                        listDtTEBANGAN.get(position).nopol, listDtTEBANGAN.get(position).varietas, listDtTEBANGAN.get(position).no_meja, "multi_select");
                paths.add(path);
            } else if (perintah.equalsIgnoreCase("send_sms")){
                sms_vals.add(sms_val);
            }

        }
    }

    public void refreshAdapter()
    {
        mAdapter.selected_items=multiselect_listDtTEBANGAN;
        mAdapter.itemTebangan=listDtTEBANGAN;
        mAdapter.notifyDataSetChanged();
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items

            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_multi_select_nochat, menu);
            context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_share_nochat:
                    if (multiselect_listDtTEBANGAN.size()>0) {
                        progress_bar.setVisibility(View.VISIBLE);
                        for (int i=0; i < paths.size()  ; i++){
                            deleteImage(paths.get(i) + ".jpg");
                        }
                        paths.clear();
                        sms_vals.clear();
                        for (int i = 0; i < multiselect_listDtTEBANGAN.size(); i++) {
                            load_select(i, "create_image");
                            if (i==multiselect_listDtTEBANGAN.size()-1){
                                progress_bar.setVisibility(View.GONE);
                            }
                        }

                        if (paths.size() != 0) {
                            Log.e("-->", "Paths " + paths.size());
                            Uri uri = null;
                            ArrayList<Uri> uris = new ArrayList<>();
                            Intent intent = new Intent();
                            intent.setAction(android.content.Intent.ACTION_SEND_MULTIPLE);
                            intent.setType("image/jpeg");
                            File picDir = null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                                picDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Gotani");
                            } else {
                                picDir = new File(Environment.getExternalStorageDirectory() + "/Gotani");
                            }
                            for (int i = 0; i < paths.size(); i++) {
                                uri = Uri.parse(picDir +"/" + paths.get(i) + ".jpg");
                                uris.add(uri);
                            }
                            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                            startActivity(Intent.createChooser(intent, "Share via"));
                        }
                    }
                    return true;
                /*case R.id.action_chat:
                    if (multiselect_listDtTEBANGAN.size()>0) {
                        for (int i=0; i < paths.size()  ; i++){
                            deleteImage(paths.get(i) + ".jpg");
                        }
                        paths.clear();
                        sms_vals.clear();
                        for (int i = 0; i < multiselect_listDtTEBANGAN.size(); i++) {
                            load_select(i, "send_sms");
                        }
                        if (sms_vals.size() != 0) {
                            StringBuilder sms_send;
                            sms_send = new StringBuilder();
                            for (int i = 0; i < sms_vals.size(); i++) {
                                sms_send.append(sms_vals.get(i));
                            }
                            Intent message = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + ""));
                            message.putExtra("sms_body", (Serializable) sms_send);
                            startActivity(message);
                        }
                    }
                    return  true;*/
                case R.id.action_select_all_nochat:
                    multiselect_listDtTEBANGAN.clear();
                    refreshAdapter();
                    for (int i=0; i < paths.size()  ; i++){
                        deleteImage(paths.get(i) + ".jpg");
                    }
                    paths.clear();
                    sms_vals.clear();
                    if (isCheck_all){
                        item.setIcon(R.drawable.ic_select_none);

                        if (multiselect_listDtTEBANGAN.size() > 0)
                            mActionMode.setTitle("" + multiselect_listDtTEBANGAN.size() +" Selected");
                        else
                            mActionMode.setTitle("0 Selected");
                        isCheck_all=false;
                    } else {
                        item.setIcon(R.drawable.ic_check_all);

                        for (int i=0; i < listDtTEBANGAN.size()  ; i++){
                            multi_select(i);
                        }
                        isCheck_all=true;
                    }

                    return  true;
                default:
                    return false;
            }
        }


        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            appBarLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            search_bar.setVisibility(View.VISIBLE);
            multiselect_listDtTEBANGAN = new ArrayList<TEBANGAN>();
            refreshAdapter();
        }
    };


    private String getCuttingType(String cuttingType){
        String cutting_type = "";

        if (cuttingType.equalsIgnoreCase("PG")){
            cutting_type = "TEBANG PG";
        } else {
            cutting_type = "TEBANG SENDIRI";
        }

        return cutting_type;
    }

    private String getStartDate(String startDate){
        String start_date[] = startDate.split(" ");
        String make_date[]  = start_date[0].split("-");
        String MakeDate     = make_date[2].concat("-").concat(make_date[1]).concat("-").concat(make_date[0]);
        return MakeDate;
    }


    private String getStartTime(String startDate){
        String start_date[] = startDate.split(" ");
        return start_date[1];
    }

    private String getEndDate(String endDate){
        String end_date[] = endDate.split(" ");
        String make_date[]= end_date[0].split("-");
        String MakeDate   = make_date[2].concat("-").concat(make_date[1]).concat("-").concat(make_date[0]);
        return MakeDate;
    }

    private String getEndTime(String endTime){
        String end_time[] = endTime.split(" ");
        return end_time[1];
    }

    private void create_bitmap(String JenisTebang, String tglMasuk, String dateOut,
                               String noSpta, String kdAntrian,
                               String noRegister, String petani,
                               String weightIn, String weightOut,
                               String weightNet, String weightKw,
                               String rafaksiKw, String mbs,
                               String nopol, String varietas, String no_meja) {
        root_view = LayoutInflater.from(context).inflate(R.layout.dialog_bukti_timbang, null);
        LinearLayout im = root_view.findViewById(R.id.tombolShare);
        im.setVisibility(View.INVISIBLE);
        TextView jenisTebang    = root_view.findViewById(R.id.jenisTebang);
        TextView waktuMasuk     = root_view.findViewById(R.id.waktuMasuk);
        TextView waktuKeluar    = root_view.findViewById(R.id.waktuKeluar);
        TextView nomorSPTA      = root_view.findViewById(R.id.noSPTA);
        TextView nomorAntrian   = root_view.findViewById(R.id.noAntrian);
        TextView register       = root_view.findViewById(R.id.register);
        TextView pemilik        = root_view.findViewById(R.id.pemilik);
        TextView bruto          = root_view.findViewById(R.id.beratBruto);
        TextView tara           = root_view.findViewById(R.id.beratTara);
        TextView nettoKG        = root_view.findViewById(R.id.beratNettoKg);
        TextView nettoKW        = root_view.findViewById(R.id.beratNettoKw);
        TextView rafaksiKW      = root_view.findViewById(R.id.rafaksiKW);
        TextView mutu           = root_view.findViewById(R.id.mutu);
        TextView nomorPolisi    = root_view.findViewById(R.id.nopol);
        TextView Vr       = root_view.findViewById(R.id.varietas);

        int bobot               = Integer.parseInt(weightKw) - Integer.parseInt(rafaksiKw);
        String jenis_tebang     = setJenisTebang(JenisTebang);
        String waktu_masuk      = setTimeStamp(tglMasuk);
        String waktu_keluar     = setTimeStamp(dateOut);
        String nomor_SPTA       = noSpta.toUpperCase();
        String nomor_antrian    = kdAntrian.toUpperCase();
        String register_        = noRegister.toUpperCase();
        String pemilik_         = petani.toUpperCase();
        String bruto_           = weightIn.toUpperCase();
        String tara_            = weightOut.toUpperCase();
        String nettoKG_         = weightNet.toUpperCase();
        String nettoKW_         = weightKw+" => "+bobot;
        String rafaksiKW_       = setRafaksi(rafaksiKw);
        String mutu_            = "MBS "+mbs.toUpperCase() + " (MEJA "+no_meja+")";
        String nomor_polisi     = nopol.toUpperCase();
        String varietas_        = varietas.toUpperCase();

        jenisTebang.setText(jenis_tebang);
        waktuMasuk.setText(waktu_masuk);
        waktuKeluar.setText(waktu_keluar);
        nomorSPTA.setText(nomor_SPTA);
        nomorAntrian.setText(nomor_antrian);
        register.setText(register_);
        pemilik.setText(pemilik_);
        bruto.setText(bruto_);
        tara.setText(tara_);
        nettoKG.setText(nettoKG_);
        nettoKW.setText(nettoKW_);
        rafaksiKW.setText(rafaksiKW_);
        mutu.setText(mutu_);
        nomorPolisi.setText(nomor_polisi);
        Vr.setText(varietas_);


        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        root_view.layout(0, 0, width, height);
        sharePict(root_view, kdAntrian);
    }

    private void sharePict(View view, String tebangan){
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File picDir = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                picDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Gotani");
            } else {
                picDir = new File(Environment.getExternalStorageDirectory() + "/Gotani");
            }
            if (!picDir.exists()) {
                picDir.mkdirs();
            }

            int width = view.getWidth();
            int height = view.getHeight();

            int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

            String fileName = "TEBANGAN"+tebangan.toUpperCase()+".jpg";
            File picFile = new File(picDir + "/" + fileName);

            try {
                picFile.createNewFile();
                FileOutputStream picOut = new FileOutputStream(picFile);
                view.setDrawingCacheEnabled(true);

                view.measure(measuredWidth, measuredHeight);
                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());


                view.buildDrawingCache(true);
                Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
                boolean saved = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, picOut);
                if (saved) {
//                    Toast.makeText(view.getContext(), "Image " + spta.toUpperCase() + " saved to your device Pictures " + "directory!", Toast.LENGTH_SHORT).show();
                    Log.e("-->", "Image " + tebangan.toUpperCase() + " saved");
                } else {
//                    Toast.makeText(view.getContext(), "Image " + spta.toUpperCase() + " not saved to your device Pictures " + "directory!", Toast.LENGTH_SHORT).show();
                    Log.e("-->", "Image " + tebangan.toUpperCase() + " not saved");
                }
                picOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            view.destroyDrawingCache();


        } else {
            //Error
            Toast.makeText(view.getContext(), "External Storage Unmounted", Toast.LENGTH_SHORT).show();
        }

    }

    public void deleteImage(String path) {
        File picDir = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            picDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Gotani");
        } else {
            picDir = new File(Environment.getExternalStorageDirectory() + "/Gotani");
        }
        String file_path = picDir + "/" + path;
        File fdelete = new File(file_path);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Log.e("-->", "file Deleted :" + file_path);
            } else {
                Log.e("-->", "file not Deleted :" + file_path);
            }
        }
    }
    private String setTimeStamp(String waktu) {
        String result;

        if (waktu == null || waktu == ""){
            result = "-";
        } else {
            String tempRes[]    = waktu.split(" ");
            String time         = tempRes[1];
            String tmpDate[]    = tempRes[0].split("-");

            String tmpTime[]= time.split("\\.");
            time            = tmpTime[0];

            String dateStamp    = tmpDate[2].concat("-").concat(tmpDate[1]).concat("-").concat(tmpDate[0]);
            result              = time.concat(" ").concat(dateStamp);
        }

        return result;
    }

    private String setRafaksi(String rafaksi) {
        String result;

        if (rafaksi == null || rafaksi == " "){
            result = "0";
        } else {
            result = rafaksi;
        }

        return result;
    }

    private String setJenisTebang(String jenis_tebang) {
        String result;

        if (jenis_tebang == null || jenis_tebang == ""){
            result = "-";
        } else {
            if (jenis_tebang.contains("PG")){
                result = "TEBANG PG";
            } else {
                result = "TEBANG SENDIRI";
            }
        }

        return result;
    }
}
