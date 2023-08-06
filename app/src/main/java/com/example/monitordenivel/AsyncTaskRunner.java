package com.example.monitordenivel;

import android.os.AsyncTask;

import com.example.monitordenivel.http.HttpHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class AsyncTaskRunner extends AsyncTask<String, String, String> {

    private String url = "";
    private AsyncTaskCallback callback;

    private AsyncTaskRunner() {

    }
    public AsyncTaskRunner(String url, AsyncTaskCallback callback) {
        this.url = url;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... strings) {
        try{
            HttpHelper helper = new HttpHelper();

            String result = helper.get(url);
            System.out.println(result);
            return result;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            // Verifique o código de status da resposta antes de fazer o parse do JSON
            int statusCode = extractStatusCode(result);
            if (statusCode >= 200 && statusCode < 300) {
                // O código de status está na faixa de sucesso (2xx)
                // Faça o parse do JSON
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    // Agora você pode usar o objeto jsonObject
                    // ...
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Trate o erro de parse JSON, se necessário
                }
            } else {
                // O código de status indica um erro (4xx ou 5xx)
                // Trate o erro, se necessário
                callback.onTaskFailed(new Exception("Erro na solicitação: " + statusCode));
            }
            callback.onTaskCompleted(result);
        } else {
            // A tarefa falhou, trate a exceção aqui
            callback.onTaskFailed(new Exception("A tarefa falhou."));
        }
    }

    private int extractStatusCode(String response) {
        try {
            // Analisar a resposta como um objeto JSON para obter o código de status
            JSONObject jsonObject = new JSONObject(response);
            return jsonObject.optInt("status_code", -1);
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

