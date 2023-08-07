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

        // Botão de retorno para tela principal
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


        int idEquipamento = Integer.parseInt(getIntent().getStringExtra("idEquipamento"));
        String mac = getIntent().getStringExtra("mac");

        //TODO: Buscar no banco de dados as informações do equipamento selecionado
        equipamento = new Equipamento(idEquipamento,mac,9999,999,99,"RES99", 0);

        updateEquipmentInfo(false);


        //Runnable responsável por recuperar informações de medidas do dispositivo
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

    /**Método que atualiza os dados na tela de equipamentos, estes dados devem estar
     * previamente inseridos na variável da classe chamada equipamento.
     * @author Keemy Barbosa
     * @param updatePercentual bool - Inidica se é necessário atualizar o percentual.
     * @return null - Sem retorno
     */
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


    /**Método que executa requisição REST no intuituo de obter o dado de medida de um equipamento.
     * @author Keemy Barbosa
     * @return null - Sem retorno
     */
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

    }

}