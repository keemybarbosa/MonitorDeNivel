package com.example.monitordenivel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.monitordenivel.databinding.ActivityEquipamentoBinding;
import com.example.monitordenivel.databinding.ActivityMainBinding;
import com.example.monitordenivel.models.Equipamento;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.concurrent.ExecutionException;

public class EquipamentoActivity extends AppCompatActivity {

    private Equipamento equipamento;

    ActivityEquipamentoBinding binding;

    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler();


        binding = ActivityEquipamentoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Remove o runnable para voltar de tela
                handler.removeCallbacks(runnable);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //TODO: Carregar equipamento vindo da tela anterior

        int idEquipamento = Integer.parseInt(getIntent().getStringExtra("idEquipamento"));
        String mac = getIntent().getStringExtra("mac");

        equipamento = new Equipamento(idEquipamento,mac,9999,999,99,"RES99", 0);

        updateEquipmentInfo(false);


        runnable = new Runnable(){
            @Override
            public void run() {
                buscarMedida();

                updateEquipmentInfo(true);

                handler.postDelayed(this,1500);
            }
        };

        handler.postDelayed(runnable,2000);


    }

    private void updateEquipmentInfo(boolean updatePercentual) {

        if (updatePercentual)
            binding.tvEqpPercentual.setText(equipamento.getPercentual());


        //TODO: exibindo o mac no lugar do nome por enquanto
        binding.tvEqpName.setText("" + equipamento.getMac());


        binding.tvEqpId.setText("Id: " + equipamento.getId());

        binding.tvEqpMeasure.setText("Measure: " + equipamento.getMeasure());
        binding.tvEqpMac.setText("Mac: " + equipamento.getMac());
        binding.tvEqpVolume.setText("Volume: " + equipamento.getVolume() + "L");
        binding.tvEqpEmpty.setText("Distância Vazio: " + equipamento.getEmptycm() + "cm");
        binding.tvEqpFull.setText("Distância Cheio: " + equipamento.getFullcm() + "cm");
    }

    private int getRandomNumber(int min, int max) {
        // Gerar um número randômico entre min e max (inclusive)
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    public void buscarMedida(){
        AsyncTaskRunner runner = new AsyncTaskRunner("http://ec2-3-22-51-1.us-east-2.compute.amazonaws.com:8080/api/measure/last", new AsyncTaskCallback() {
            @Override
            public void onTaskCompleted(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int measure = jsonObject.getInt("measure");
                    equipamento.setMeasure(measure);
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onTaskFailed(Exception e) {

            }
        });

        runner.execute();

        /*String returnJson = "";
        try {
            returnJson = runner.execute().get();

            System.out.println(returnJson);

            if (returnJson.trim() == ""){
                return;
            }
            //Json Parsing
            try{
                JSONObject jsonObject = new JSONObject(returnJson);
                int measure = jsonObject.getInt("measure");
                equipamento.setMeasure(measure);



            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }


        } catch (ExecutionException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        } catch (InterruptedException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }*/
    }

}