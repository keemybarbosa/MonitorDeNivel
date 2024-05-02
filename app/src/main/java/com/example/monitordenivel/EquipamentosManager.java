package com.example.monitordenivel;

import android.app.Application;
import android.app.NotificationChannel;
import android.widget.Toast;

import com.example.monitordenivel.models.Equipamento;
import com.example.monitordenivel.utils.WebServiceConstants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
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



    }

}
