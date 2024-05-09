package com.example.monitordenivel.dao.impl;

import com.example.monitordenivel.dao.ConnectionFactory;
import com.example.monitordenivel.dao.EquipamentoDao;
import com.example.monitordenivel.models.Equipamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EquipamentoDaoImpl implements EquipamentoDao {
    private Connection connection;

    public EquipamentoDaoImpl() throws SQLException, ClassNotFoundException {
        connection = ConnectionFactory.getConnection();
    }

    @Override
    public Equipamento consultarPorMac(String mac) {
        Equipamento equipamento = null;
        String query = "SELECT * FROM equipamento WHERE mac = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, mac);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                equipamento = new Equipamento(
                        resultSet.getInt("id"),
                        resultSet.getString("mac"),
                        resultSet.getInt("volume"),
                        resultSet.getInt("emptycm"),
                        resultSet.getInt("fullcm"),
                        resultSet.getString("name"),
                        resultSet.getInt("measure")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return equipamento;
    }
}
