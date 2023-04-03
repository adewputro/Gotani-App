package com.ptkebonagung.gotani;

import android.util.Log;

import com.ptkebonagung.gotani.data.APIKey;
import com.ptkebonagung.gotani.utils.APIService;
import com.ptkebonagung.gotani.utils.ApiClient;
import com.ptkebonagung.gotani.utils.Preference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirebaseInstanceID extends FirebaseInstanceIdService {
    private APIService apiService   = ApiClient.getRetrofitInstance().create(APIService.class);

    @Override
    public void onTokenRefresh() {
        String email    = Preference.getKeyEmailRegistered(getBaseContext());
        String token    = FirebaseInstanceId.getInstance().getToken();
        String key      = Preference.getKeyApi(getBaseContext());
        sendToServer(email, token, key);
    }

    private void sendToServer(String email, final String token, String key_api) {
        Call<APIKey> updateToken = apiService.updateToken(email, token, key_api);

        updateToken.enqueue(new Callback<APIKey>() {
            @Override
            public void onResponse(Call<APIKey> call, Response<APIKey> response) {
                Preference.setFcmToken(getBaseContext(), token);
                Log.w("FCM TOKEN ", "Update Token Success, Token : "+token);
            }

            @Override
            public void onFailure(Call<APIKey> call, Throwable t) {
                Log.w("FCM TOKEN ", "Update Token Unsuccess");
            }
        });
    }
}
