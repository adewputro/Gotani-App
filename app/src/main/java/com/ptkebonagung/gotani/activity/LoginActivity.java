package com.ptkebonagung.gotani.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ptkebonagung.gotani.MainActivity;
import com.ptkebonagung.gotani.R;
import com.ptkebonagung.gotani.data.APIKey;
import com.ptkebonagung.gotani.utils.APIService;
import com.ptkebonagung.gotani.utils.ApiClient;
import com.ptkebonagung.gotani.utils.NetworkUtil;
import com.ptkebonagung.gotani.utils.Tools;
import com.ptkebonagung.gotani.utils.Preference;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private View parent_view;
    private Context context;

    final private String url = "http://192.168.30.5:8069",
                          db = "kebonagung";

    private APIService apiService;

    /* property untuk Login */
    private Button btnLogin;
    private TextView email, password;
    /* property untuk Login */

    /*property untuk firebase auth*/
    private FirebaseAuth mAuth;
    private View layoutPB;
    private Button btnRetry;
    /*property untuk firebase auth*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //layoutNoConnection  = findViewById(R.id.noConnection);
        boolean hasConnetion = NetworkUtil.getConnectivityStatus(getApplicationContext());

        if (hasConnetion == false){
            showNotification();
        } else {
            setContentView(R.layout.activity_login);
            //layoutNoConnection.setVisibility(View.GONE);
            boolean isLogon = Preference.getKeyStatusIsLoggedIn(getBaseContext());

            if (isLogon == true){
                updateUI();
            } else {
                Tools.setSystemBarColor(this, android.R.color.white);
                Tools.setSystemBarLight(this);

                parent_view = findViewById(android.R.id.content);
                context     = this;
                mAuth       = FirebaseAuth.getInstance();
                apiService  = ApiClient.getRetrofitInstance().create(APIService.class);

                btnLoginOnClick();
                logoutMessage();
            }
        }

        /*findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView username = findViewById(R.id.email);
                TextView password = findViewById(R.id.password);
                odooLogin login = new odooLogin(context, username.getText().toString(), password.getText().toString());
                login.execute();
            }
        });*/
    }

    private void showNotification(){
        setContentView(R.layout.activity_no_item_internet_image);
        btnRetry            = findViewById(R.id.bt_retry);
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

    private void updateUI(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void btnLoginOnClick(){
        btnLogin    = findViewById(R.id.login);
        email       = findViewById(R.id.email);
        password    = findViewById(R.id.password);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(NetworkUtil.getConnectivityStatus(getApplicationContext()) == true){

                    String formStatus = checkForm(email.getText().toString(), password.getText().toString());

                    if(formStatus.equalsIgnoreCase("form_is_complete")){

                        boolean isEmail = checkIsEmail(email.getText().toString());

                        if(isEmail == true){
                            //Fungsi login dengan firebase auth dan sharedpreference untuk session
                            showProgress();
                            mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){

                                                String KEY_API  = "0fd2ebbbe0fe2a978ca126e12314c910";
                                                String TOKEN    = FirebaseInstanceId.getInstance().getToken();

                                                if(TOKEN == null){
                                                    TOKEN = "";
                                                }

                                                updateUserToken(email.getText().toString() , TOKEN, KEY_API);

                                            } else {
                                                hideProgress();
                                                showMessage();
                                            }
                                        }
                                    });

                        } else {

                            Snackbar.make(parent_view,"Periksa kembali email anda!!",Snackbar.LENGTH_LONG).show();

                        }

                    } else {

                        Snackbar.make(parent_view,"Email atau password ada yang kosong!!",Snackbar.LENGTH_LONG).show();

                    }

                } else {

                    showNotification();

                }
            }
        });
    }

    private void updateUserToken(final String email, final String TOKEN, final String KEY_API){

        Call<APIKey> putKeyAPIUser = apiService.putKeyAPIUser(
                email,
                TOKEN,
                KEY_API);

        putKeyAPIUser.enqueue(new Callback<APIKey>() {
            @Override
            public void onResponse(Call<APIKey> call, Response<APIKey> response) {
                hideProgress();
                System.out.println("FCM Token : "+TOKEN);
                goToDashboard(mAuth, KEY_API);
            }

            @Override
            public void onFailure(Call<APIKey> call, Throwable t) {
                System.out.println("Update token unsuccessfull");
            }
        });
    }

    private void goToDashboard(FirebaseAuth mAuth, String KEY_API){
        Preference.setKeyEmailRegistered(getBaseContext(),mAuth.getCurrentUser().getEmail());
        Preference.setKeyUserId(getBaseContext(), mAuth.getCurrentUser().getUid());
        Preference.setKeyStatusIsLoggedIn(getBaseContext(), true);
        Preference.setKeyAPI(getBaseContext(), KEY_API);

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void logoutMessage(){

        String logout_status = getIntent().getStringExtra("logoutStatus");

        if(logout_status != null){
            Snackbar.make(parent_view,"Terimakasih sudah menggunakan aplikasi GoTani",Snackbar.LENGTH_LONG).show();
            logout_status = null;
        }

    }

    private void showProgress(){
        layoutPB = findViewById(R.id.logonPB);
        layoutPB.setVisibility(View.VISIBLE);
    }

    private void hideProgress(){
        layoutPB = findViewById(R.id.logonPB);
        layoutPB.setVisibility(View.GONE);
    }

    private void showMessage(){
        Snackbar.make(parent_view,"Email atau password salah, ulangi kembali!!", Snackbar.LENGTH_LONG).show();
    }

    private boolean checkIsEmail(String email){
        boolean isEmail = false;

        if(email.contains("@")){
            isEmail = true;
        }

        return isEmail;
    }

    private String checkForm(String email, String password){
        String formStatus = "not_complete";

        if(!email.isEmpty() && !password.isEmpty()){
            formStatus = "form_is_complete";
        }

        return formStatus;
    }

    /*private class odooLogin extends AsyncTask<Void, Void, Void> {
        String username, password;
        Context context;
        SharedPreferences pref;

        public odooLogin(final Context context, String username, String password){
            this.username = username;
            this.password = password;
            this.context = context;
            this.pref = getSharedPreferences("users",Context.MODE_PRIVATE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            XmlRpcClientConfigImpl common_config = new XmlRpcClientConfigImpl();
            try {
                common_config.setServerURL(
                        new URL(String.format("%s/xmlrpc/2/common", url)));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            XmlRpcClient client = new XmlRpcClient();
            try {
                Object uid = client.execute(common_config, "authenticate", asList(
                        db, username, password, emptyMap()));
                if (uid instanceof Boolean){
                    Snackbar.make(parent_view, "Username atau password anda salah!", Snackbar.LENGTH_SHORT).show();
                } else {
                    SharedPreferences.Editor edit = this.pref.edit();
                    edit.putBoolean("is_login", true);
                    edit.putInt("user_id", (int)uid);
                    edit.putString("username", username);
                    edit.putString("password", password);
                    edit.commit();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    ((Activity)context).finish();
                }
            } catch (XmlRpcException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            System.out.println("Finished");
            super.onPostExecute(aVoid);
        }
    }*/

}
