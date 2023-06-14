package com.example.monitordenivel;

import android.os.AsyncTask;

import com.example.monitordenivel.http.HttpHelper;

public class AsyncTaskRunner extends AsyncTask<String, String, String> {

    private String url = "";

    private AsyncTaskRunner() {

    }
    public AsyncTaskRunner(String url) {
        this.url = url;
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpHelper helper = new HttpHelper();
        String result = helper.get(url);
        System.out.println(result);
        return result ;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
