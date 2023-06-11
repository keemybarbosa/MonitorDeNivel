package com.example.monitordenivel;

public class Equipamento {
    private int id;
    private String mac;

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
}
