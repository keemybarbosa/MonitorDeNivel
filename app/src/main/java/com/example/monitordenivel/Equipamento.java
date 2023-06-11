package com.example.monitordenivel;

public class Equipamento {
    private int id;
    private String mac;
    private int volume;
    private int emptycm;
    private int fullcm;

    public Equipamento(int id, String mac){
        this.id = id;
        this.mac = mac;
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
}
