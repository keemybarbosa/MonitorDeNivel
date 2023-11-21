package com.example.monitordenivel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ClipDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.monitordenivel.databinding.ActivityEquipamentoBinding;
import com.example.monitordenivel.databinding.ActivityMainBinding;
import com.example.monitordenivel.models.Equipamento;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class EquipamentoActivity extends AppCompatActivity {

    private Equipamento equipamento;

    ActivityEquipamentoBinding binding;

    private Handler handler;
    private Runnable runnable;

    /* BEGIN - Up Animation */
    private Handler mUpHandler = new Handler();


    /* ****************************/
    /* ****************************/
    /* ****************************/
    /* ANIMAÇÃO DA BARRA DE NÍVEL */
    /* ****************************/
    /* ****************************/
    /* ****************************/

    private ClipDrawable mImageDrawable;

    // a field in your class
    private int mLevel = 0;
    private int fromLevel = 0;
    private int toLevel = 0;

    public static final int MAX_LEVEL = 10000;
    public static final int LEVEL_DIFF = 100;
    public static final int DELAY = 30;
    private Runnable animateUpImage = new Runnable() {

        @Override
        public void run() {
            doTheUpAnimation(fromLevel, toLevel);
        }
    };
    /* END - Up Animation */

    /* BEGIN - Down Animation */
    private Handler mDownHandler = new Handler();
    private Runnable animateDownImage = new Runnable() {

        @Override
        public void run() {
            doTheDownAnimation(fromLevel, toLevel);
        }
    };
    /* END - Down Animation */



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


        //Obtendo variáveis passadas pro parâmetro
        int pVolume = Integer.parseInt(getIntent().getStringExtra("volume"));
        int pEmptycm = Integer.parseInt(getIntent().getStringExtra("emptycm"));
        int pFullcm = Integer.parseInt(getIntent().getStringExtra("fullcm"));
        String pMac = getIntent().getStringExtra("mac");
        int pMeasure = Integer.parseInt(getIntent().getStringExtra("measure"));

        //TODO: Buscar no banco de dados as informações do equipamento selecionado
        equipamento = new Equipamento(idEquipamento,pMac,pVolume,pEmptycm,pFullcm,"RES99", pMeasure);

        updateEquipmentInfo(false);

        mImageDrawable = (ClipDrawable) binding.imgNivel.getDrawable();
        mImageDrawable.setLevel(5000);


        //Runnable responsável por recuperar informações de medidas do dispositivo
        runnable = new Runnable(){
            @Override
            public void run() {
                buscarMedida(equipamento.getMac());

                updateEquipmentInfo(true);

                handler.postDelayed(this,1500);
            }
        };

        handler.postDelayed(runnable,100);


    }

    /**Método que atualiza os dados na tela de equipamentos, estes dados devem estar
     * previamente inseridos na variável da classe chamada equipamento.
     * @author Keemy Barbosa
     * @param updatePercentual bool - Inidica se é necessário atualizar o percentual.
     * @return null - Sem retorno
     */
    private void updateEquipmentInfo(boolean updatePercentual) {

        DecimalFormat formatter = new DecimalFormat("#0.0");

        //if (equipamento.getMeasure() == 0) return;

        if (updatePercentual) {
            Double dPercentual = equipamento.getPercentual();

            if (dPercentual < 0){
                binding.tvEqpPercentual.setText("Lmt Exced");
                binding.tvEqpMessage.setText("Medida capturada excede fundo reservatório!");
            } else if (dPercentual > 100.0){
                binding.tvEqpPercentual.setText("Transb.");
                binding.tvEqpMessage.setText("Transbordo!");
            } else {
                binding.tvEqpPercentual.setText(equipamento.getPercentualAsString());
                binding.tvEqpMessage.setText("");

                mImageDrawable.setLevel((int) (dPercentual/100 * MAX_LEVEL));

            }


        }


        //TODO: exibindo o mac no lugar do nome por enquanto
        binding.tvEqpName.setText("" + equipamento.getMac());

        binding.tvEqpId.setText("Id: " + equipamento.getId());

        binding.tvEqpMeasure.setText("Leitura: " + formatter.format(equipamento.getMeasure()/10.0f) + "cm");
        binding.tvEqpMac.setText("Mac: " + equipamento.getMac());
        binding.tvEqpVolume.setText("Volume: " + equipamento.getVolume() + "L");
        binding.tvEqpEmpty.setText("Distância Vazio: " + formatter.format(equipamento.getEmptycm()/10.0f) + "cm");
        binding.tvEqpFull.setText("Distância Cheio: " + formatter.format(equipamento.getFullcm()/10.0f) + "cm");
    }


    /**Método que executa requisição REST no intuituo de obter o dado de medida de um equipamento.
     * @author Keemy Barbosa
     * @param mac String - mac do equipamento a ser localizado
     * @return null - Sem retorno
     */
    public void buscarMedida(String mac){

        AsyncTaskRunner runner = new AsyncTaskRunner("http://ec2-3-22-51-1.us-east-2.compute.amazonaws.com:8080/api/measure/last/" + mac, new AsyncTaskCallback() {
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
                System.out.println("teste");
            }
        });

        runner.execute();

    }

    @Override
    public void onBackPressed() {
        //
        binding.btnVoltar.performClick();

    }

    private void doTheUpAnimation(int fromLevel, int toLevel) {
        mLevel += LEVEL_DIFF;
        mImageDrawable.setLevel(mLevel);
        if (mLevel <= toLevel) {
            mUpHandler.postDelayed(animateUpImage, DELAY);
        } else {
            mUpHandler.removeCallbacks(animateUpImage);
            EquipamentoActivity.this.fromLevel = toLevel;
        }
    }

    private void doTheDownAnimation(int fromLevel, int toLevel) {
        mLevel -= LEVEL_DIFF;
        mImageDrawable.setLevel(mLevel);
        if (mLevel >= toLevel) {
            mDownHandler.postDelayed(animateDownImage, DELAY);
        } else {
            mDownHandler.removeCallbacks(animateDownImage);
            EquipamentoActivity.this.fromLevel = toLevel;
        }
    }

}