package com.example.monitordenivel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AssyncTaskRunnerB {
    private static final int CONNECTION_TIMEOUT = 30000; // 10 segundos
    private static final int READ_TIMEOUT = 30000; // 10 segundos

    public static void executeAsyncTask(String url, AsyncTaskCallback callback) {
        new Thread(() -> {
            try {
                URL requestUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setReadTimeout(READ_TIMEOUT);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    callback.onTaskCompleted(response.toString());
                } else {
                    callback.onTaskFailed(new Exception("Erro na solicitação: " + responseCode));
                }
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                callback.onTaskFailed(e);
            }
        }).start();
    }
}
