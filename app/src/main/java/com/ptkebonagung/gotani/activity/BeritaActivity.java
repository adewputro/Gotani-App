package com.ptkebonagung.gotani.activity;

import android.app.Activity;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ptkebonagung.gotani.MainActivity;
import com.ptkebonagung.gotani.R;
import com.ptkebonagung.gotani.utils.MyApp;
import com.ptkebonagung.gotani.utils.NetworkUtil;
import com.ptkebonagung.gotani.utils.Preference;

public class BeritaActivity extends AppCompatActivity {

    private View view_cek_connection;
    private Button btnRetry;

    protected MyApp mMyApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyApp  = (MyApp) this.getApplicationContext();

        boolean isLogin = Preference.getKeyStatusIsLoggedIn(getBaseContext());

        if (isLogin == false){

            Intent intent = new Intent(BeritaActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        } else {

            if(NetworkUtil.getConnectivityStatus(getApplicationContext()) == false){
                showNotification();
            } else {
                setContentView(R.layout.activity_no_item_bg_cactus);
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
