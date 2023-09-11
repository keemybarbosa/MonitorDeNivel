package com.example.monitordenivel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monitordenivel.databinding.ActivityMainBinding;
import com.example.monitordenivel.models.Equipamento;
import com.example.monitordenivel.utils.MathUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    ArrayList<Equipamento> equipamentos = null;
    ArrayList<TextView> tvName = new ArrayList<TextView>();
    ArrayList<TextView> tvPercentual = new ArrayList<TextView>();

    ArrayList<View> viewEquipamento = new ArrayList<View>();


    private Handler handler;
    //Variável será utilizada para um evento de atualização de equipamentos que estão na tela
    private Runnable runEquipments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tvName.add(findViewById(R.id.tvName1));
        tvName.add(findViewById(R.id.tvName2));
        tvName.add(findViewById(R.id.tvName3));
        tvName.add(findViewById(R.id.tvName4));
        tvName.add(findViewById(R.id.tvName5));
        tvPercentual.add(findViewById(R.id.tvPercentual1));
        tvPercentual.add(findViewById(R.id.tvPercentual2));
        tvPercentual.add(findViewById(R.id.tvPercentual3));
        tvPercentual.add(findViewById(R.id.tvPercentual4));
        tvPercentual.add(findViewById(R.id.tvPercentual5));


        binding.btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //carregarDados();
            }
        });

        binding.vwEquip1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carregarEquipamento(0);
            }
        });
        binding.vwEquip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carregarEquipamento(1);
            }
        });
        binding.vwEquip3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carregarEquipamento(2);
            }
        });
        binding.vwEquip4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carregarEquipamento(3);
            }
        });
        binding.vwEquip5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carregarEquipamento(4);
            }
        });


        handler = new Handler();
        //Configuração de um Runnable que verifica a leitura dos dispositivos
        // que estão na tela principal
        runEquipments = new Runnable() {
            @Override
            public void run() {
                carregarDados();
                int i = 0;
                for (Equipamento eq : equipamentos) {

                    eq.setMeasure(new Random().nextInt(6000));

                    atualizarEquipamentoTela(i);
                    i++;
                }

                //Reexecuta após 1.5 segundos
                handler.postDelayed(this,5000);
            }
        };
        //Na primeira vez será executado após 5 segundos
        handler.postDelayed(runEquipments,5000);


    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        carregarDados();
    }

    private void carregarEquipamento(int i) {

        if ((equipamentos == null) || (i >= equipamentos.size())) return;

        Intent intent = new Intent(getApplicationContext(), EquipamentoActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("idEquipamento",equipamentos.get(i).getId() +"");
        intent.putExtra("mac", equipamentos.get(i).getMac() + "");
        intent.putExtra("volume", equipamentos.get(i).getVolume() + "");
        intent.putExtra("emptycm", equipamentos.get(i).getEmptycm() + "");
        intent.putExtra("fullcm", equipamentos.get(i).getFullcm() + "");

        startActivity(intent);

        //overridePendingTransition (0, 0); //Prevent Black Screen
        finish();
    }

    public void carregarDados(){
        //AsyncTaskRunner runner = new AsyncTaskRunner("http://ec2-3-22-51-1.us-east-2.compute.amazonaws.com:8080/api/measure/last");
        AsyncTaskRunner runner = new AsyncTaskRunner("http://ec2-3-22-51-1.us-east-2.compute.amazonaws.com:8080/api/equipment", new AsyncTaskCallback() {
            @Override
            public void onTaskCompleted(String result) {
                equipamentos = getEquipamentosFromJson(result);
                //TODO: codigo para teste
                if (equipamentos.size() == 0) {
                    equipamentos = getEquipsForTest();
                }
/*
                int i = 0;
                for (Equipamento eq : equipamentos) {
                    //TODO:trocar pelo name do equipamento quando disponível
                    atualizarEquipamentoTela(i);
                    i++;
                }*/
            }

            @Override
            public void onTaskFailed(Exception e) {

            }
        });

        runner.execute();

    }

    public void atualizarEquipamentoTela(int i){
        tvName.get(i).setText(equipamentos.get(i).getMac());
        tvPercentual.get(i).setText(equipamentos.get(i).getPercentualInfo());
    }

    private ArrayList<Equipamento> getEquipamentosFromJson(String returnJson) {
        ArrayList<Equipamento> listaEquipamentos = new ArrayList<Equipamento>();

        Gson gson = new Gson();

        try {
            Type equipamentoListType = new TypeToken<List<Equipamento>>() {
            }.getType();
            listaEquipamentos = gson.fromJson(returnJson, equipamentoListType);
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show();
        }


        return listaEquipamentos;

    }

    private ArrayList<Equipamento> getEquipsForTest() {
        //TODO: METODO PARA TESTE
        ArrayList<Equipamento> listEquips = new ArrayList<Equipamento>();

        listEquips.add(new Equipamento(1,"aa:aa:aa:aa:aa:aa",1000,250,40,"RES95", MathUtils.numeroAleatorio(40,250)));
        listEquips.add(new Equipamento(2,"bb:aa:aa:aa:aa:aa",1000,180,36,"RES96",MathUtils.numeroAleatorio(36,180)));
        listEquips.add(new Equipamento(3,"cc:aa:aa:aa:aa:aa",1000,100,48,"RES97",MathUtils.numeroAleatorio(48,100)));
        listEquips.add(new Equipamento(4,"dd:aa:aa:aa:aa:aa",1000,180,50,"RES98",MathUtils.numeroAleatorio(50,180)));
        listEquips.add(new Equipamento(5,"ee:aa:aa:aa:aa:aa",1000,70,10,"RES99",MathUtils.numeroAleatorio(10,70)));

        return listEquips;

    }
}