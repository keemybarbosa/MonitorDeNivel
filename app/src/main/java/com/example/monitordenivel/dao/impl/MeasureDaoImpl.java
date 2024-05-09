package com.example.monitordenivel.dao.impl;

import com.example.monitordenivel.dao.MeasureDao;
import com.example.monitordenivel.models.Equipamento;
import com.example.monitordenivel.models.Measure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class MeasureDaoImpl implements MeasureDao {
    private Connection connection;

    public MeasureDaoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Measure getLastMeasureByEquipment(Equipamento equipamento) {
        Measure lastMeasure = null;

        String query = "SELECT * FROM measure WHERE mac = ? ORDER BY date_time DESC LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, equipamento.getMac());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                int measure = resultSet.getInt("measure");
                Timestamp dateTime = resultSet.getTimestamp("date_time");
                String mac = resultSet.getString("mac");

                lastMeasure = new Measure(id, measure, dateTime, mac);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lastMeasure;
    }
}