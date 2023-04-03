package com.ptkebonagung.gotani;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.ptkebonagung.gotani.activity.BeritaActivity;
import com.ptkebonagung.gotani.activity.EmplasemenActivity;
import com.ptkebonagung.gotani.activity.GilinganActivity;
import com.ptkebonagung.gotani.activity.LoginActivity;
import com.ptkebonagung.gotani.activity.RegisterActivity;
import com.ptkebonagung.gotani.activity.SptaActivity;
import com.ptkebonagung.gotani.activity.TebanganActivity;
import com.ptkebonagung.gotani.activity.TimbanganActivity;
import com.ptkebonagung.gotani.data.DataRegisterHolder;
import com.ptkebonagung.gotani.utils.APIService;
import com.ptkebonagung.gotani.utils.ApiClient;
import com.ptkebonagung.gotani.utils.ApiClient2;
import com.ptkebonagung.gotani.utils.ForceUpdateChecker;
import com.ptkebonagung.gotani.utils.MyApp;
import com.ptkebonagung.gotani.utils.NetworkUtil;
import com.ptkebonagung.gotani.utils.Tools;
import com.ptkebonagung.gotani.utils.Preference;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ForceUpdateChecker.OnUpdateNeededListener {
    //SharedPreferences pref;
    private View parent_view;
    private Button btnRetry;
    private TextView txtRegister;
    private APIService apiService;
    private static final String TAG = MainActivity.class.getSimpleName();


    protected MyApp mMyApp;

    AsyncTask<?,?,?> asyncTask;
    private boolean isActiveUrlPgKebonAgung = false;


    private static final int STORAGE_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyApp      = (MyApp) this.getApplicationContext();
        parent_view = findViewById(android.R.id.content);
        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // set in-app defaults
        Map<String, Object> remoteConfigDefaults = new HashMap();
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_REQUIRED, false);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_CURRENT_VERSION, "1.0");
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_URL,
                "https://play.google.com/store/apps/details?id=com.ptkebonagung.gotani");

        firebaseRemoteConfig.setDefaultsAsync(remoteConfigDefaults);
        firebaseRemoteConfig.fetch(60) // fetch every minutes
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "remote config is fetched.");
                            firebaseRemoteConfig.activate();
                        }
                    }
                });

        boolean is_login = Preference.getKeyStatusIsLoggedIn(getBaseContext());
        if (!is_login){

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        } else {

            boolean hasConnection = NetworkUtil.getConnectivityStatus(getApplicationContext());
            if (hasConnection == false){

                showNotification();

            } else {

                setContentView(R.layout.activity_main);
                initToolbar();

                FloatingActionButton spta = findViewById(R.id.float_spta);
                spta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, SptaActivity.class);
                        startActivity(intent);
                    }
                });

                FloatingActionButton empl = findViewById(R.id.float_emplasemen);
                empl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, EmplasemenActivity.class);
                        startActivity(intent);
                    }
                });

                FloatingActionButton timb = findViewById(R.id.float_timbangan);
                timb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, TimbanganActivity.class);
                        startActivity(intent);
                    }
                });

                FloatingActionButton gil = findViewById(R.id.float_gilingan);
                gil.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, GilinganActivity.class);
                        startActivity(intent);
                    }
                });

                FloatingActionButton tebang = findViewById(R.id.float_tebangan);
                tebang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, TebanganActivity.class);
                        startActivity(intent);
                    }
                });

                FloatingActionButton register = findViewById(R.id.float_register);
                register.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent   = new Intent(MainActivity.this, RegisterActivity.class);
                        startActivity(intent);
                    }
                });

                FloatingActionButton berita = findViewById(R.id.float_berita);
                berita.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent   = new Intent(MainActivity.this, BeritaActivity.class);
                        startActivity(intent);
                    }
                });

                tokenLog();

                ForceUpdateChecker.with(this).onUpdateNeeded(this).check();
                try {
                    URL url = new URL("http://gotani.pgtrangkil.com:1111");

                    System.out.println(url);
                    checkURL checkURL = new checkURL(url){
                        @Override
                        protected void onPostExecute(Boolean aBoolean) {
                            super.onPostExecute(aBoolean);
                            System.out.println("API Status is Active ? : "+aBoolean);
                            setDataRegister(aBoolean);
                        }
                    };
                    checkURL.execute();
                    System.out.println("TASK EXECUTE");
                } catch (MalformedURLException e) {
                    Log.wtf("LOG","Not Reachable");
                }
            }
        }

        CheckPermission();
    }


    private void CheckPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        }
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("PERMISSION","Permission Granted Successfull");
            } else {
                Log.d("PERMISSION","Permission Granted Unsuccessfull");
            }
        }
    }*/

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

    private void setDataRegister(Boolean aBoolean) {
        txtRegister                     = findViewById(R.id.daftarRegister);
        String key_email                = Preference.getKeyEmailRegistered(getBaseContext());
        String key_api                  = Preference.getKeyApi(getBaseContext());
        HashMap<String, String> param   = new HashMap<>();

        param.put("email", key_email);
        param.put("key", key_api);

        if (aBoolean){
            apiService                                  = ApiClient.getRetrofitInstance().create(APIService.class);
        } else {
            apiService                                  = ApiClient2.getRetrofitInstance().create(APIService.class);
        }

        System.out.println(aBoolean);
        Call<DataRegisterHolder> dataRegisterUser   = apiService.getDataRegister(param);

        /*Log.d("API Service 1","Load Register via Service 1");

        if (dataRegisterUser.request().url().query() == null){
            dataRegisterUser   = apiService2.getDataRegister(param);
            Log.d("API Service 2","Load Register via Service 2");
        }*/

        dataRegisterUser.enqueue(new Callback<DataRegisterHolder>() {
            @Override
            public void onResponse(Call<DataRegisterHolder> call, Response<DataRegisterHolder> response) {
                if (response.isSuccessful()){
                    if (response.body().getData().size() > 0){

                    /*String register = "";
                    if (dataSize == 1){

                        register = response.body().getData().get(0).getNoRegister();

                    } else {

                        for (int i=0; i < dataSize; i++){
                            if(i < dataSize-1){

                                register += response.body().getData().get(i).getNoRegister() + " , ";

                            } else {

                                register += response.body().getData().get(i).getNoRegister();

                            }
                        }
                    }

                    txtRegister.setText(register);
                    txtRegister.setMovementMethod(new ScrollingMovementMethod());*/
                        int dataSize    = response.body().getData().size();

                        String status = "Anda Memiliki <b><font color=red>"+dataSize+"</font></b> Register." +
                                "Untuk Melihat Daftar Register Anda," +
                                "Silahkan Klik Menu Daftar Register ";
                        txtRegister.setText(Html.fromHtml(status));

                    } else {

                        txtRegister.setText("Anda Belum Memiliki Register");

                    }
                } else {
                    Snackbar.make(parent_view, "Gagal mengambil data register anda", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DataRegisterHolder> call, Throwable t) {
                Snackbar.make(parent_view, "Gagal mengambil data register anda", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    /* diganti ke class Network Util
    private boolean checkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }
    */

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Tools.setSystemBarColor(this, R.color.colorPrimary);
        //String username = pref.getString("username","tamu");
        String username = getUsername(Preference.getKeyEmailRegistered(getBaseContext()));
        toolbar.setTitle("Hai, "+username);
    }

    private String getUsername(String email){
        //username diambil dari karakter 1 sampai sebelum karakter @
        String[] simpleUname = email.split("@");
        return simpleUname[0];
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_logout) {
            /*SharedPreferences.Editor edit = this.pref.edit();
            edit.clear();
            edit.commit();*/

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Konfirmasi Logout")
                    .setMessage("Apakah anda yakin ingin logout?")
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Preference.clearLoggedInDataUser(getBaseContext());
                            Intent intent        = new Intent(MainActivity.this, LoginActivity.class);
                            String logout_status = "logout_success";
                            intent.putExtra("logoutStatus", logout_status);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();

        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void tokenLog(){
        String token = Preference.getFcmToken(getBaseContext());
        System.out.println("Token FCM : "+token);
    }

    @Override
    public void onBackPressed() {
        finish();
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

    private class checkURL extends AsyncTask<Void, Void, Boolean> {
        private URL mURL;

        public checkURL(URL url){
            mURL = url;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) mURL.openConnection();
                urlConnection.setConnectTimeout(10 * 10000);
                urlConnection.connect();

                if (urlConnection.getResponseCode() == 200) {
                    Log.wtf("Connection", "Success !");
                    return true;
                } else {
                    return false;
                }

            } catch (MalformedURLException e1){
                return false;
            } catch (IOException e) {
                return false;
            }

        }
    }

    @Override
    public void onUpdateNeeded(final String updateUrl) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Gotani versi baru tersedia")
                .setMessage("Mohon segera update aplikasi melalui Play Store.")
                .setPositiveButton("Update",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                redirectStore(updateUrl);
                            }
                        }).setNegativeButton("No, thanks",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).create();
        dialog.show();
    }

    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
