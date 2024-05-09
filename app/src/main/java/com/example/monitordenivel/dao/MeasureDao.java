package com.example.monitordenivel.dao;

import com.example.monitordenivel.models.Equipamento;
import com.example.monitordenivel.models.Measure;

public interface MeasureDao {
    public Measure getLastMeasureByEquipment(Equipamento equipamento);
}
