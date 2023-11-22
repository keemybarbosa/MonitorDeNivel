package com.example.monitordenivel.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Equipamento implements Parcelable {
    private int id;
    private String mac;
    private int volume; //Volume em litros
    private int emptycm;
    private int fullcm;

    private String name; //nome do equipamento

    private int measure; //Medida vinda do equipamento

    public Equipamento(int id, String mac, int volume, int emptycm, int fullcm, String name, int measure){
        this.id = id;
        this.mac = mac;
        this.volume = volume;
        this.emptycm = emptycm;
        this.fullcm = fullcm;
        this.name = name;
        this.measure = measure;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getEmptycm() {
        return emptycm;
    }

    public void setEmptycm(int emptycm) {
        this.emptycm = emptycm;
    }

    public int getFullcm() {
        return fullcm;
    }

    public void setFullcm(int fullcm) {
        this.fullcm = fullcm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMeasure() {
        return measure;
    }

    public void setMeasure(int measure) {
        this.measure = measure;
    }

    public Double getPercentual(){
        int volumecm = this.emptycm - this.measure;
        int full = this.emptycm - this.fullcm;
        double volumePercent = 0;

        if (full != 0) {
            volumePercent = (double)volumecm / (double)full;
        }



        return volumePercent * 100 ;
    }

    public String getPercentualAsString(){
        String sPercent = String.format("%.2f", getPercentual()) + "%";
        return sPercent;
    }

    public String getPercentualInfo(){
        Double dPercentual = getPercentual();

        String retorno = "";


        if (measure == 0) return "-";
        if (dPercentual < 0){
            retorno = "Lmt Exced";
        } else if (dPercentual > 100.0){
            retorno = "Transb.";
        } else {
            retorno = getPercentualAsString();
        }

        return retorno;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Equipamento(Parcel in) {
        // Esse construtor apesar de nao ser requerido pela interface,
        // e necessario por causa do protocolo implicito do Parcelable
        // Lembrar da ordem que foi escrita no writeToParcel!!
        id = in.readInt();
        mac = in.readString();
        volume = in.readInt();
        emptycm = in.readInt();
        fullcm= in.readInt();
        name= in.readString();
        measure= in.readInt();
        // Demais campos
    }
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        //int id, String mac, int volume, int emptycm, int fullcm, String name, int measure
        dest.writeInt(id);
        dest.writeString(mac);
        dest.writeInt(volume);
        dest.writeInt(emptycm);
        dest.writeInt(fullcm);
        dest.writeString(name);
        dest.writeInt(measure);
    }

    // Como parte do contrato implicito,
    // sua classe precisa de um atributo estatico chamado "CREATOR"

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Equipamento createFromParcel(Parcel in) {
            return new Equipamento(in);
        }

        public Equipamento[] newArray(int size) {
            return new Equipamento[size];
        }
    };
}
