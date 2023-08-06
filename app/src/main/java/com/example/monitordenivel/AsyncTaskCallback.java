package com.example.monitordenivel;

public interface AsyncTaskCallback {
    void onTaskCompleted(String result);

    void onTaskFailed(Exception e);
}
