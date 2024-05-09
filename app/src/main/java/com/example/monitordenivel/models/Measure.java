package com.example.monitordenivel.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.sql.Timestamp;

public class Measure implements Parcelable {
    private int id;
    private int measure;
    private Timestamp dateTime;
    private String mac;

    public Measure(int id, int measure, Timestamp dateTime, String mac) {
        this.id = id;
        this.measure = measure;
        this.dateTime = dateTime;
        this.mac = mac;
    }

    public Measure() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMeasure() {
        return measure;
    }

    public void setMeasure(int measure) {
        this.measure = measure;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

    }
}