package com.ptkebonagung.gotani.utils;

import android.os.AsyncTask;

public class AsyncTaskCheckUrl extends AsyncTask<Void, Void, Boolean> {
    public AsyncTaskResponse response = null;


    @Override
    protected Boolean doInBackground(Void... voids) {
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }
}
