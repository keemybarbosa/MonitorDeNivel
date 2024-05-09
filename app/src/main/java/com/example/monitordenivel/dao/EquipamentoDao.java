package com.example.monitordenivel.dao;

import com.example.monitordenivel.models.Equipamento;

public interface EquipamentoDao {
    public Equipamento consultarPorMac(String Mac);
}
