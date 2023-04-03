package com.ptkebonagung.gotani.activity;

        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.net.Uri;
        import android.os.Build;
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
        import com.ptkebonagung.gotani.adapter.AdapterListSPTA;
        import com.ptkebonagung.gotani.data.DataSPTAHolder;
        import com.ptkebonagung.gotani.fragment.DialogSPTAFragment;
        import com.ptkebonagung.gotani.model.SPTA;
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
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;

        import retrofit2.Call;
        import retrofit2.Callback;
        import retrofit2.Response;

public class SptaActivity extends AppCompatActivity implements AlertDialogHelper.AlertDialogListener{
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
    ArrayList<SPTA> listDtSPTA=new ArrayList<>();
    ArrayList<SPTA> multiselect_listDtSPTA=new ArrayList<>();
    ArrayList<String> paths = new ArrayList<>();
    ArrayList<String> sms_vals = new ArrayList<>();
    private AdapterListSPTA mAdapter = new AdapterListSPTA();
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
    private Button btnRetry;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyApp              = (MyApp) this.getApplicationContext();

        boolean isLogin = Preference.getKeyStatusIsLoggedIn(getBaseContext());
        if(!isLogin){

            Intent intent = new Intent(SptaActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        } else {

            boolean hasConnection = NetworkUtil.getConnectivityStatus(getApplicationContext());
            if (hasConnection == false){

                showNotification();

            } else {

                setContentView(R.layout.activity_spta);
                appBarLayout = findViewById(R.id.appbar);
                searcResultNotFound = findViewById(R.id.searchResult);
                parent_view         = findViewById(android.R.id.content);
                progress_bar        = (ProgressBar) findViewById(R.id.progress_bar);
                et_search           = (EditText) findViewById(R.id.et_search);
                bt_clear            = (ImageButton) findViewById(R.id.bt_clear);
                search_bar = findViewById(R.id.search_bar);
                layoutManager       = new LinearLayoutManager(getApplicationContext());
                recyclerView        = findViewById(R.id.recyclerView);
                //recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                viewDataNotAvailable= findViewById(R.id.dataIsAvailable);
                txtDataAvailable    = (TextView) findViewById(R.id.noDataTxt);
                linearLayout        = findViewById(R.id.tombolShare);
                context=this;
                Tools.setSystemBarColor(this, R.color.blue_grey_600);

//                appBarLayout.setBackgroundColor(getResources().getColor(R.color.blue_grey_600));

                loadSPTA("");
                searchItem();
                clearTextSearch();

                mAdapter = new AdapterListSPTA(getApplicationContext(), listDtSPTA, multiselect_listDtSPTA, R.layout.item_spta);
                recyclerView.setAdapter(mAdapter);
                progress_bar.setVisibility(View.GONE);


                recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (isMultiSelect) {
                            multi_select(position);

                        } else {

                            showDialogSPTA(listDtSPTA.get(position).no_spta, listDtSPTA.get(position).jenis_tebang, listDtSPTA.get(position).tanggal_berlaku_awal, listDtSPTA.get(position).tanggal_berlaku_akhir,
                                    listDtSPTA.get(position).register_id, listDtSPTA.get(position).name, "single_select");
//                            Toast.makeText(getApplicationContext(), "Details Page", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        if (!isMultiSelect) {
                            multiselect_listDtSPTA = new ArrayList<SPTA>();
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
        //initComponent();
        //clearTextSearch();
        //searchAction();
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

    private void loadSPTA(final String keyWord){
        String email_key_api            = Preference.getKeyEmailRegistered(getBaseContext());
        String key_api                  = Preference.getKeyApi(getBaseContext());

        HashMap<String, String> param   = new HashMap<>();

        apiService                      = ApiClient.getRetrofitInstance().create(APIService.class);
        apiService2                     = ApiClient2.getRetrofitInstance().create(APIService.class);

        Call<DataSPTAHolder> dataSPTA   = null;

        if (keyWord.length() > 0){

            param.clear();
            listDtSPTA.clear();

            param.put("email", email_key_api);
            param.put("key", key_api);
            param.put("register", keyWord);

            dataSPTA        = apiService.getDataSPTA(page_number, param);

            System.out.println(dataSPTA.request().url().query());

            progress_bar.setVisibility(View.VISIBLE);

            dataSPTA.enqueue(new Callback<DataSPTAHolder>() {
                @Override
                public void onResponse(Call<DataSPTAHolder> call, Response<DataSPTAHolder> response) {
                    if (response.isSuccessful()){
                        //Log.w("Data : ", new Gson().toJson(response.body().getData().size()));
                        int sizeData = response.body().getData().size();

                        if (sizeData > 0){
                            searcResultNotFound.setVisibility(View.GONE);

                            for (int i=0; i < sizeData; i++){
                                SPTA spta = new SPTA();
                                spta.no_spta                = response.body().getData().get(i).getNoSpta();
                                spta.register_id            = response.body().getData().get(i).getNoRegister();
                                spta.tanggal_berlaku_awal   = response.body().getData().get(i).getTglBerlakuAwal();
                                spta.tanggal_berlaku_akhir  = response.body().getData().get(i).getTglBerlakuAkhir();
                                spta.name                   = response.body().getData().get(i).getName();
                                spta.jenis_tebang           = response.body().getData().get(i).getJenisTebang();
                                listDtSPTA.add(spta);
                            }

                            mAdapter = new AdapterListSPTA(getApplicationContext(), listDtSPTA, multiselect_listDtSPTA, R.layout.item_spta);
                            recyclerView.setAdapter(mAdapter);
                            progress_bar.setVisibility(View.GONE);

                        } else {

                            progress_bar.setVisibility(View.GONE);
                            //Snackbar.make(parent_view,"Data yang dicari tidak ada ..", Snackbar.LENGTH_SHORT).show();
                            searcResultNotFound.setVisibility(View.VISIBLE);
                        }

                    } else {

                        progress_bar.setVisibility(View.GONE);
                        Snackbar.make(parent_view,"Data gagal disinkronkan", Snackbar.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onFailure(Call<DataSPTAHolder> call, Throwable t) {
                    progress_bar.setVisibility(View.GONE);
                    Snackbar.make(parent_view,"Gagal untuk mengambil data", Snackbar.LENGTH_LONG).show();
                }
            });


        } else {

            param.clear();
            listDtSPTA.clear();

            param.put("email", email_key_api);
            param.put("key", key_api);

            dataSPTA = apiService.getDataSPTA(page_number, param);

            progress_bar.setVisibility(View.VISIBLE);

            dataSPTA.enqueue(new Callback<DataSPTAHolder>() {
                @Override
                public void onResponse(Call<DataSPTAHolder> call, Response<DataSPTAHolder> response) {
                    if (response.isSuccessful()){
                        //Log.w("Data : ", new Gson().toJson(response.body().getData().size()));
                        int sizeData = response.body().getData().size();

                        if (sizeData > 0){

                            for (int i=0; i < sizeData; i++){
                                SPTA spta = new SPTA();
                                spta.no_spta                = response.body().getData().get(i).getNoSpta();
                                spta.register_id            = response.body().getData().get(i).getNoRegister();
                                spta.tanggal_berlaku_awal   = response.body().getData().get(i).getTglBerlakuAwal();
                                spta.tanggal_berlaku_akhir  = response.body().getData().get(i).getTglBerlakuAkhir();
                                spta.name                   = response.body().getData().get(i).getName();
                                spta.jenis_tebang           = response.body().getData().get(i).getJenisTebang();
                                listDtSPTA.add(spta);
                            }

                            mAdapter = new AdapterListSPTA(getApplicationContext(), listDtSPTA, multiselect_listDtSPTA, R.layout.item_spta );
                            recyclerView.setAdapter(mAdapter);
                            progress_bar.setVisibility(View.GONE);

                        } else {
                            progress_bar.setVisibility(View.GONE);
                            //Snackbar.make(parent_view,"Data yang dicari tidak ada ..", Snackbar.LENGTH_SHORT).show();
                            txtDataAvailable.setText("Mohon maaf saat ini data SPTA \n untuk anda belum tersedia");
                            viewDataNotAvailable.setVisibility(View.VISIBLE);
                            dataAvailable   = false;
                        }

                    } else {

                        progress_bar.setVisibility(View.GONE);
                        Snackbar.make(parent_view,"Data gagal disinkronkan", Snackbar.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onFailure(Call<DataSPTAHolder> call, Throwable t) {
                    progress_bar.setVisibility(View.GONE);
                    Snackbar.make(parent_view,"Gagal untuk mengambil data", Snackbar.LENGTH_LONG).show();
                }
            });
        }



        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                String key_word = keyWord;
                visibleItemCount = layoutManager.getChildCount();
                totalItemCount   = layoutManager.getItemCount();
                pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                if(dy > 0){

                    if(isLoading){
                        if(totalItemCount > previousTotal){
                            isLoading       = false;
                            previousTotal   = totalItemCount;
                        }
                    }

                    if(!isLoading && ((totalItemCount - visibleItemCount) <= (pastVisibleItems + 0))){

                        page_number++;
                        performPagination(key_word);
                        isLoading = true;

                    }
                }
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
                Call<DataSPTAHolder> dataSPTA = null;

                if (KeyWord.length() > 0){

                    param.clear();
                    param.put("email", email_key_api);
                    param.put("key", key_api);
                    param.put("register", KeyWord);

                    dataSPTA = apiService.getDataSPTA(page_number, param);
                    Log.d("API Service 1","Load SPTA via Service 1");

                    if (dataSPTA.request().url().query() == null){
                        dataSPTA = apiService2.getDataSPTA(page_number, param);
                        Log.d("API Service 2","Load SPT via Service 2");
                    }

                } else {

                    param.clear();
                    param.put("email", email_key_api);
                    param.put("key", key_api);

                    dataSPTA = apiService.getDataSPTA(page_number, param);
                    Log.d("API Service 1","Load SPTA via Service 1");

                    if (dataSPTA.request().url().query() == null){
                        dataSPTA = apiService2.getDataSPTA(page_number, param);
                        Log.d("API Service 2","Load SPTA via Service 2");
                    }

                }

                dataSPTA.enqueue(new Callback<DataSPTAHolder>() {
                    @Override
                    public void onResponse(Call<DataSPTAHolder> call, Response<DataSPTAHolder> response) {

                        if (response.body().getData().size() != 0){

                            List<SPTA> sptaMore = new ArrayList<>();
                            int sizeData = response.body().getData().size();

                            for (int i=0; i < sizeData; i++){
                                SPTA spta = new SPTA();
                                spta.no_spta                = response.body().getData().get(i).getNoSpta();
                                spta.register_id            = response.body().getData().get(i).getNoRegister();
                                spta.tanggal_berlaku_awal   = response.body().getData().get(i).getTglBerlakuAwal();
                                spta.tanggal_berlaku_akhir  = response.body().getData().get(i).getTglBerlakuAkhir();
                                spta.name                   = response.body().getData().get(i).getName();
                                spta.jenis_tebang           = response.body().getData().get(i).getJenisTebang();
                                sptaMore.add(spta);
                            }

                            mAdapter.addMoreSPTA(sptaMore);
                            pageNow++;
                            progress_bar.setVisibility(View.GONE);


                            if (isClick || isSearch){
                                sptaMore.clear();
                            }

                        } else {

                            progress_bar.setVisibility(View.GONE);
                            Snackbar.make(parent_view,"No more data available ..", Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DataSPTAHolder> call, Throwable t) {
                        Snackbar.make(parent_view,"Gagal untuk mengambil data", Snackbar.LENGTH_LONG).show();
                    }
                });


            }
        }, 1000);

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

        boolean isHasConnection = NetworkUtil.getConnectivityStatus(getApplicationContext());

        if (isHasConnection == true){

            if (dataAvailable == true){

                searcResultNotFound.setVisibility(View.GONE);

                et_search.setText("");
                mAdapter.clearSPTA();

                page_number = 1;
                pageNow = 2;

                /*pagination variable*/
                isLoading = true;
                pastVisibleItems = visibleItemCount = totalItemCount = previousTotal = 0;
                viewThreshold = 2;
                /*pagination variable*/

                isClick = true;
                listDtSPTA.clear();

                loadSPTA("");

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

    private void searchItemAction(String keyword) {

        boolean isHasConnection = NetworkUtil.getConnectivityStatus(getApplicationContext());

        if (isHasConnection == true){

            if (dataAvailable == true){

                if (keyword.equals("")){
                    View view = this.getCurrentFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    Snackbar.make(parent_view, "Mohon isi register yang ingin dicari", Snackbar.LENGTH_LONG).show();

                } else {

                    mAdapter.clearSPTA();
                    page_number = 1;
                    pageNow = 2;

                    /*pagination variable*/
                    isLoading = true;
                    pastVisibleItems = visibleItemCount = totalItemCount = previousTotal = 0;
                    viewThreshold = 2;
                    /*pagination variable*/

                    isClick = true;
                    isSearch = true;

                    loadSPTA(keyword);
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

    private void showDialogSPTA(String no_spta, String jenis_tebang, String tanggal_berlaku_awal, String tanggal_berlaku_akhir,
                                String register_id, String name, String activity_select) {

        boolean isHasConnection = NetworkUtil.getConnectivityStatus(getApplicationContext());

        if (isHasConnection == true){

            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();
            }

            if (activity_select.equalsIgnoreCase("single_select")){
                DialogSPTAFragment newFragment = new DialogSPTAFragment();
                Bundle bundle = new Bundle();
                bundle.putString("no_spta", no_spta);
                bundle.putString("jenis_tebang", jenis_tebang);
                bundle.putString("tanggal_berlaku_awal", tanggal_berlaku_awal);
                bundle.putString("tanggal_berlaku_akhir", tanggal_berlaku_akhir);
                bundle.putString("register", register_id);
                bundle.putString("pemilik", name);

                newFragment.setArguments(bundle);

                FragmentTransaction transaction = fragmentManager.beginTransaction();

                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
            } else if (activity_select.equalsIgnoreCase("multi_select")) {

                create_bitmap(no_spta, jenis_tebang, tanggal_berlaku_awal, tanggal_berlaku_akhir,
                        register_id, name);

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
            if (multiselect_listDtSPTA.contains(listDtSPTA.get(position))) {
                multiselect_listDtSPTA.remove(listDtSPTA.get(position));
            } else {
                multiselect_listDtSPTA.add(listDtSPTA.get(position));
            }

            if (multiselect_listDtSPTA.size() > 0)
                mActionMode.setTitle("" + multiselect_listDtSPTA.size() + " Selected");
            else
                mActionMode.setTitle("0 Selected");

            refreshAdapter();
        }
    }

    public void load_select(int position, String perintah) {
        if (mActionMode != null) {
            String path = "SPTA"+multiselect_listDtSPTA.get(position).no_spta.toUpperCase();

            String spta = multiselect_listDtSPTA.get(position).no_spta;
            String cuttingType = getCuttingType(multiselect_listDtSPTA.get(position).jenis_tebang);
            String startDate   = getStartDate(multiselect_listDtSPTA.get(position).tanggal_berlaku_awal);
            String startTime   = getStartTime(multiselect_listDtSPTA.get(position).tanggal_berlaku_awal);
            String endDate     = getEndDate(multiselect_listDtSPTA.get(position).tanggal_berlaku_akhir);
            String endTime     = getEndTime(multiselect_listDtSPTA.get(position).tanggal_berlaku_akhir);
            String register    = multiselect_listDtSPTA.get(position).register_id;
            String owner       = multiselect_listDtSPTA.get(position).name;

            final String dari = startDate + " " + startTime;
            final String batas = endDate + " " + endTime;
            final String reg = register;
            final String pet = owner;
            final String jt = cuttingType;

            String sms_val = "SPTA: "+spta+"\nMasa berlaku\n  " +dari+ "\n  "+batas + "\nRegister: "+reg + "\nPemilik: "+pet+"\nJenis: "+jt+"\n\n";
            if (perintah.equalsIgnoreCase("create_image")) {
                showDialogSPTA(multiselect_listDtSPTA.get(position).no_spta, multiselect_listDtSPTA.get(position).jenis_tebang, multiselect_listDtSPTA.get(position).tanggal_berlaku_awal, multiselect_listDtSPTA.get(position).tanggal_berlaku_akhir,
                        multiselect_listDtSPTA.get(position).register_id, multiselect_listDtSPTA.get(position).name, "multi_select");
                paths.add(path);
            } else if (perintah.equalsIgnoreCase("send_sms")){
                sms_vals.add(sms_val);
            }

        }
    }

    public void refreshAdapter()
    {
        mAdapter.selected_items=multiselect_listDtSPTA;
        mAdapter.items=listDtSPTA;
        mAdapter.notifyDataSetChanged();
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items

            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_multi_select, menu);
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
                case R.id.action_share:
                    if (multiselect_listDtSPTA.size()>0) {
                        progress_bar.setVisibility(View.VISIBLE);
                        for (int i=0; i < paths.size()  ; i++){
                            deleteImage(paths.get(i) + ".jpg");
                        }
                        paths.clear();
                        sms_vals.clear();
                        for (int i = 0; i < multiselect_listDtSPTA.size(); i++) {
                            load_select(i, "create_image");
                            if (i==multiselect_listDtSPTA.size()-1){
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
                                uri = Uri.parse(picDir + "/" + paths.get(i) + ".jpg");
                                uris.add(uri);
                            }
                            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                            startActivity(Intent.createChooser(intent, "Share via"));
                        }
                    }
                    return true;
                case R.id.action_chat:
                    if (multiselect_listDtSPTA.size()>0) {
                        for (int i=0; i < paths.size()  ; i++){
                            deleteImage(paths.get(i) + ".jpg");
                        }
                        paths.clear();
                        sms_vals.clear();
                        for (int i = 0; i < multiselect_listDtSPTA.size(); i++) {
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
                    return  true;
                case R.id.action_select_all:
                    multiselect_listDtSPTA.clear();
                    refreshAdapter();
                    for (int i=0; i < paths.size()  ; i++){
                        deleteImage(paths.get(i) + ".jpg");
                    }
                    paths.clear();
                    sms_vals.clear();
                    if (isCheck_all){
                        item.setIcon(R.drawable.ic_select_none);

                        if (multiselect_listDtSPTA.size() > 0)
                            mActionMode.setTitle("" + multiselect_listDtSPTA.size() +" Selected");
                        else
                            mActionMode.setTitle("0 Selected");
                        isCheck_all=false;
                    } else {
                        item.setIcon(R.drawable.ic_check_all);

                        for (int i=0; i < listDtSPTA.size()  ; i++){
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
            multiselect_listDtSPTA = new ArrayList<SPTA>();
            refreshAdapter();
        }
    };
    @Override
    public void onPositiveClick(int from) {

    }

    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }

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

    private void create_bitmap(String no_spta, String jenis_tebang, String tanggal_berlaku_awal, String tanggal_berlaku_akhir,
                               String register_id, String name) {
        root_view = LayoutInflater.from(context).inflate(R.layout.dialog_spta, null);
        LinearLayout im = root_view.findViewById(R.id.tombolShare);
        im.setVisibility(View.INVISIBLE);
        ImageView barcode = root_view.findViewById(R.id.barcode);
        TextView t_barcode = root_view.findViewById(R.id.t_barcode);
        TextView txtCuttingType = root_view.findViewById(R.id.txtViewFragCuttingType);
        TextView txtStartDate = root_view.findViewById(R.id.txtViewFragStartDate);
        TextView txtStartTime = root_view.findViewById(R.id.txtViewFragStartTime);
        TextView txtEndDate = root_view.findViewById(R.id.txtViewFragEndDate);
        TextView txtEndTime = root_view.findViewById(R.id.txtViewFragEndTime);
        TextView txtRegister = root_view.findViewById(R.id.txtViewFragRegister);
        TextView txtOwner = root_view.findViewById(R.id.txtViewFragOwner);

        String cuttingType = getCuttingType(jenis_tebang);
        String startDate = getStartDate(tanggal_berlaku_awal);
        String startTime = getStartTime(tanggal_berlaku_awal);
        String endDate = getEndDate(tanggal_berlaku_akhir);
        String endTime = getEndTime(tanggal_berlaku_akhir);
        String sregister = register_id;
        String sowner = name;
        /*String varietas    = "-";
        String fieldArea   = "-";
        String policeNumb  = "-";*/

        /*show argument from bundle to fragment detail SPTA property*/
        txtCuttingType.setText(cuttingType);
        txtStartDate.setText("dari " + startDate.toUpperCase());
        txtStartTime.setText(startTime.toUpperCase());
        txtEndDate.setText("s.d " + endDate.toUpperCase());
        txtEndTime.setText(endTime.toUpperCase());
        txtRegister.setText(sregister.toUpperCase());
        txtOwner.setText(sowner.toUpperCase());
        t_barcode.setText(no_spta);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(no_spta, BarcodeFormat.CODE_128, 700, 120);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            barcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        root_view.layout(0, 0, width, height);
        sharePict(root_view, no_spta);
    }

    private void sharePict(View view, String spta){
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

            String fileName = "SPTA"+spta.toUpperCase()+".jpg";
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
                    Log.e("-->", "Image " + spta.toUpperCase() + " saved");
                } else {
//                    Toast.makeText(view.getContext(), "Image " + spta.toUpperCase() + " not saved to your device Pictures " + "directory!", Toast.LENGTH_SHORT).show();
                    Log.e("-->", "Image " + spta.toUpperCase() + " not saved");
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
}
