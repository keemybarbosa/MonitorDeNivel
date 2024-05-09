package com.example.monitordenivel;

import android.app.Application;
import android.app.NotificationChannel;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.monitordenivel.dao.ConnectionFactory;
import com.example.monitordenivel.dao.MeasureDao;
import com.example.monitordenivel.dao.impl.MeasureDaoImpl;
import com.example.monitordenivel.models.Equipamento;
import com.example.monitordenivel.models.Measure;
import com.example.monitordenivel.utils.WebServiceConstants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class EquipamentosManager {
    public static ArrayList<Equipamento> equipamentos = new ArrayList<>();

    public void EquipamentoManager() {
        //equipamentos = new ArrayList<>();
    }

    public static void CarregarEquipamentos(){
        //Método que chama de forma assíncrona uma requisição que atualiza uma lista de equipamentos

        String taskURL = ""; //WebServiceConstants.EQUIPMENT_ENDPOINT + "/";
        taskURL = "http://vps52736.publiccloud.com.br:8080/api/equipment";

        try {
            AssyncTaskRunnerB.executeAsyncTask(taskURL, new AsyncTaskCallback() {

                @Override
                public void onTaskCompleted(String result) {
                    ArrayList<Equipamento> listEquipamentos = null;

                    if (!result.startsWith("failed") && !result.startsWith("timeout")) {
                        listEquipamentos = getEquipamentosFromJson(result);
                        for (Equipamento eq: listEquipamentos) {
                            UpdateOrInsertEquipment(eq);
                        }
                    }
                }

                @Override
                public void onTaskFailed(Exception e) {
                    int a = 0;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static void UpdateOrInsertEquipment(Equipamento eq) {
        boolean bExiste = false;

        //Procura o equipamento na lista
        for (Equipamento equipamento : equipamentos) {
            if (equipamento.getMac().equals(eq.getMac())) {
                bExiste = true;
                break;
            }
        }

        if (!bExiste) {
            equipamentos.add(eq);
        }
    }

    private static ArrayList<Equipamento> getEquipamentosFromJson(String returnJson) {
        ArrayList<Equipamento> listaEquipamentos = new ArrayList<Equipamento>();

        Gson gson = new Gson();

        try {
            Type equipamentoListType = new TypeToken<List<Equipamento>>() {
            }.getType();
            listaEquipamentos = gson.fromJson(returnJson, equipamentoListType);
        } catch (Exception e){
            e.printStackTrace();
        }


        return listaEquipamentos;

    }

    public static void AtualizarEquipamentoPorMac(String mac) {
        new AsyncTask<String, Void, Measure>() {
            @Override
            protected Measure doInBackground(String... params) {
                String mac = params[0];
                Measure lastMeasure = null;
                try (Connection connection = ConnectionFactory.getConnection()) {
                    String query = "SELECT * FROM measure WHERE mac = ? ORDER BY date_time DESC LIMIT 1";
                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        statement.setString(1, mac);
                        try (ResultSet resultSet = statement.executeQuery()) {
                            if (resultSet.next()) {
                                lastMeasure = new Measure();
                                lastMeasure.setId(resultSet.getInt("id"));
                                lastMeasure.setMeasure(resultSet.getInt("measure"));
                                lastMeasure.setDateTime(resultSet.getTimestamp("date_time"));
                                lastMeasure.setMac(resultSet.getString("mac"));

                                for(Equipamento eq : EquipamentosManager.equipamentos){
                                    if(eq.getMac().equals(mac)){
                                        eq.setMeasure(lastMeasure.getMeasure());
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm:ss");
                                        String formattedDate = dateFormat.format(lastMeasure.getDateTime());

                                        eq.setMessage("atualização: " + formattedDate);
                                    }
                                }


                            }
                        }
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return lastMeasure;
            }

            @Override
            protected void onPostExecute(Measure lastMeasure) {
                super.onPostExecute(lastMeasure);
                if (lastMeasure != null) {
                    System.out.println("Última medida registrada:");
                    System.out.println("ID: " + lastMeasure.getId());
                    System.out.println("Medida: " + lastMeasure.getMeasure());
                    System.out.println("Data e Hora: " + lastMeasure.getDateTime());
                    System.out.println("MAC do Equipamento: " + lastMeasure.getMac());
                } else {
                    System.out.println("Nenhuma medida encontrada para o equipamento.");
                }
            }
        }.execute(mac);
    }


    public static void AtualizarEquipamentoPorMac2(String mac) {

        //TODO: INICIO - Localizar equipamentos por mac, o códifo abaixo foi comentado para refatoração

        try (
                Connection connection = ConnectionFactory.getConnection()
        ) {
            // Criar uma instância de Equipamento (substitua os valores pelos adequados)
            Equipamento equipamento = new Equipamento(1, mac, 100, 50, 90, "Equipamento 1", 75);

            // Criar uma instância de MeasureDao
            MeasureDao measureDao = new MeasureDaoImpl(connection);

            // Obter a última medida registrada para o equipamento específico
            Measure lastMeasure = measureDao.getLastMeasureByEquipment(equipamento);

            // Verificar se a medida foi encontrada e exibir as informações
            if (lastMeasure != null) {
                System.out.println("Última medida registrada:");
                System.out.println("ID: " + lastMeasure.getId());
                System.out.println("Medida: " + lastMeasure.getMeasure());
                System.out.println("Data e Hora: " + lastMeasure.getDateTime());
                System.out.println("MAC do Equipamento: " + lastMeasure.getMac());
            } else {
                System.out.println("Nenhuma medida encontrada para o equipamento.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


        //TODO: FIM - Localizar equipamentos por mac, o códifo abaixo foi comentado para refatoração

        /*

        String taskUrl = WebServiceConstants.MEASURE_ENDPOINT + "last/";
        AssyncTaskRunnerB.executeAsyncTask(taskUrl + mac, new AsyncTaskCallback() {
            @Override
            public void onTaskCompleted(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int measure = jsonObject.getInt("measure");
                    for(Equipamento eq : EquipamentosManager.equipamentos){
                        if(eq.getMac().equals(mac)){
                            eq.setMeasure(measure);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //throw new RuntimeException(e);
                }

            }

            @Override
            public void onTaskFailed(Exception e) {
                e.printStackTrace();
                //System.out.println("A solicitação falhou: " + e.getMessage());
            }
        });

        */

    }

}
