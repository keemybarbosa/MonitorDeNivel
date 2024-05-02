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
import com.example.monitordenivel.utils.WebServiceConstants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

   //public ArrayList<Equipamento> equipamentos = null;
    ArrayList<TextView> tvName = new ArrayList<TextView>();
    ArrayList<TextView> tvPercentual = new ArrayList<TextView>();

    ArrayList<TextView> tvUpdate = new ArrayList<TextView>();

    ArrayList<View> viewEquipamento = new ArrayList<View>();

    TextView tvMessages;


    private Handler handler;
    //Variável será utilizada para um evento de atualização de equipamentos que estão na tela
    private Runnable runEquipments;

    public boolean stopLoad = false;

    public int measurePending = 0;


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
        tvUpdate.add(findViewById(R.id.tvUpdate1));
        tvUpdate.add(findViewById(R.id.tvUpdate2));
        tvUpdate.add(findViewById(R.id.tvUpdate3));
        tvUpdate.add(findViewById(R.id.tvUpdate4));
        tvUpdate.add(findViewById(R.id.tvUpdate5));

        tvMessages = findViewById(R.id.tvMessages);

        //Verifica se está voltando da tela de equipamentos

        boolean bComeFromEquipment = getIntent().getBooleanExtra("fromEquipamentos", false);
        if (bComeFromEquipment) {
            int i = 0;
            for (Equipamento eq : EquipamentosManager.equipamentos) {
                EquipamentosManager.AtualizarEquipamentoPorMac(eq.getMac());
                atualizarEquipamentoTela(i);
                i++;
            }
        }
        /*
            Toast.makeText(this, "Voltando de Equipamentos", Toast.LENGTH_LONG).show();
            equipamentos = new ArrayList<Equipamento>();
            equipamentos = getIntent().getParcelableArrayListExtra("listaEquipamentos");

            //Obtem Lista de equipamentos que foram enviados por parametro

            equipamentos.add(new Equipamento(1,"abcde",1000,1000,1000,"NOME",1000));

            for (int i = 0; i < equipamentos.size(); i++) {
                atualizarEquipamentoTela(i);
            }

        }
        */



        binding.btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //carregarDados();
            }
        });

        binding.vwEquip1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEquipamento(0);
            }
        });
        binding.vwEquip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEquipamento(1);
            }
        });
        binding.vwEquip3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEquipamento(2);
            }
        });
        binding.vwEquip4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEquipamento(3);
            }
        });
        binding.vwEquip5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEquipamento(4);
            }
        });


        handler = new Handler();
        //Configuração de um Runnable que verifica a leitura dos dispositivos
        // que estão na tela principal
        runEquipments = new Runnable() {


            @Override
            public void run() {

                int INTERVAL = 10000;

                EquipamentosManager.CarregarEquipamentos();
                int i = 0;
                for (Equipamento eq : EquipamentosManager.equipamentos) {
                    EquipamentosManager.AtualizarEquipamentoPorMac(eq.getMac());
                    atualizarEquipamentoTela(i);
                    i++;
                }

                //Reexecuta após 1.5 segundos
                handler.postDelayed(this,INTERVAL);
            }
        };
        //Na primeira vez será executado após 5 segundos
        handler.postDelayed(runEquipments,5000);

        //System.out.println(Utils.obterDensidadeTela(this));
        binding.tvDPI.setText("DPI: " + Utils.obterDensidadeTela(this));

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //carregarArrayEquipamentos();
        tvMessages.setText("Carregar PostCreate\n" + tvMessages.getText());
    }

    private void clickEquipamento(int i) {

        if ((EquipamentosManager.equipamentos == null) || (i >= EquipamentosManager.equipamentos.size())) return;
        if (EquipamentosManager.equipamentos.get(i).getMac().equals("")) return;

        Intent intent = new Intent(getApplicationContext(), EquipamentoActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("idEquipamento",EquipamentosManager.equipamentos.get(i).getId() +"");
        intent.putExtra("mac", EquipamentosManager.equipamentos.get(i).getMac() + "");
        intent.putExtra("volume", EquipamentosManager.equipamentos.get(i).getVolume() + "");
        intent.putExtra("emptycm", EquipamentosManager.equipamentos.get(i).getEmptycm() + "");
        intent.putExtra("fullcm", EquipamentosManager.equipamentos.get(i).getFullcm() + "");

        intent.putExtra("measure", EquipamentosManager.equipamentos.get(i).getMeasure() + "");

        intent.putParcelableArrayListExtra("equipamentos", EquipamentosManager.equipamentos);

        startActivity(intent);

        //overridePendingTransition (0, 0); //Prevent Black Screen
        finish();
    }

    public void atualizarEquipamentoTela(int i){
        Date currentDate = Calendar.getInstance().getTime();

        tvName.get(i).setText(EquipamentosManager.equipamentos.get(i).getMac());
        tvPercentual.get(i).setText(EquipamentosManager.equipamentos.get(i).getPercentualInfo());

        //DAta de atualização
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm:ss");
        String formattedDate = dateFormat.format(currentDate);
        tvUpdate.get(i).setText("last update: " + formattedDate);

        tvMessages.setText("Atualizando " + i + (Utils.random(1000)) + "\n" + tvMessages.getText());





    }

}