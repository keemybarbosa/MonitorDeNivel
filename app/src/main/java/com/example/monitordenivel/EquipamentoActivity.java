package com.example.monitordenivel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ClipDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.monitordenivel.databinding.ActivityEquipamentoBinding;
import com.example.monitordenivel.models.Equipamento;
import com.example.monitordenivel.utils.WebServiceConstants;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class EquipamentoActivity extends AppCompatActivity {

    private Equipamento equipamento;

    //Variavel usada para salvar os equipamentos que vem da outra tela
    public ArrayList<Equipamento> equipamentos = null;

    ActivityEquipamentoBinding binding;

    private Handler handler;
    private Runnable runnable;

    /* BEGIN - Up Animation */
    private Handler mUpHandler = new Handler();

    private int carregandoCount = 0;


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
                intent.putExtra("fromEquipamentos",true);
                intent.putParcelableArrayListExtra("listaEquipamentos", equipamentos);
                EquipamentosManager.clearCandleGraph();
                startActivity(intent);
                finish();
            }
        });


        equipamentos = new ArrayList<Equipamento>();
        equipamentos = getIntent().getParcelableArrayListExtra("equipamentos");

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


        //setGraphEquipment();
        EquipamentosManager.clearCandleGraph();
        setCandleGraphEquipment(pMac, true);


        //Runnable responsável por recuperar informações de medidas do dispositivo
        runnable = new Runnable(){
            @Override
            public void run() {
                buscarMedida(equipamento.getMac());

                updateEquipmentInfo(true);

                //Só atualiza a cada 20 passagens na atualização para não sobrecarregar
                if (carregandoCount%10 == 0) {
                    setCandleGraphEquipment(equipamento.getMac(), true);
                } else {
                    setCandleGraphEquipment(equipamento.getMac(), false);
                }
                carregandoCount = carregandoCount % 10;
                carregandoCount++;

                if(EquipamentosManager.bCarregando){
                    binding.lblStatusGraph.setText("Carregando...");
                } else {
                    binding.lblStatusGraph.setText("Ok");
                }

                handler.postDelayed(this,5000);
            }
        };

        handler.postDelayed(runnable,100);


    }
    private void setCandleGraphEquipment(String mac, boolean atualizar){
        binding.candleStickGraph.getDescription().setText("Volume acumulado");

        // Creating a list to store CandleEntry objects
        List<CandleEntry> entries = new ArrayList<>();
        entries.add(new CandleEntry(0, 80f, 90f, 70f, 85f)); // Up (green)

        //Só busca os dados em banco quando o parâmetro atualizar for verdadeiro
        if (atualizar) {
            EquipamentosManager.bCarregando = true;
            EquipamentosManager.atualizarCandleGraphByMac(mac);
        }

        entries = EquipamentosManager.getCandleGraphEntries();

        // Created a CandleDataSet from the entries
        CandleDataSet dataSet = new CandleDataSet(entries, "Nível");

        dataSet.setDrawIcons(false);
        dataSet.setIncreasingColor(Color.GREEN); // Color for up (green) candlesticks
        dataSet.setIncreasingPaintStyle(Paint.Style.FILL); // Set the paint style to Fill for green candlesticks
        dataSet.setDecreasingColor(Color.RED); // Color for down (red) candlesticks
        dataSet.setShadowColorSameAsCandle(true); // Using the same color for shadows as the candlesticks
        dataSet.setDrawValues(false);             // Hiding the values on the chart if not needed

        // Created a CandleData object from the CandleDataSet
        CandleData data = new CandleData(dataSet);

        // Seinft the CandleData to the CandleStickChart
        binding.candleStickGraph.setData(data);
        binding.candleStickGraph.invalidate();
    }

    private List<CandleEntry> getCandleEquipmentEntries(String mac) {
        List<CandleEntry> entries = new ArrayList<>();
/*
        String sql = "WITH last_30_days AS ( " +
                "  SELECT " +
                "    date_trunc('day', date_time) AS date, " +
                "    mac, " +
                "    MIN(measure) AS low, " +
                "    MAX(measure) AS high " +
                "  FROM " +
                "    measure " +
                "  WHERE " +
                "    date_time >= current_date - interval '29 days' " +
                "    AND mac = 'D4:D4:DA:E4:BE:64' " +
                "  GROUP BY " +
                "    date_trunc('day', date_time), " +
                "    mac " +
                "), " +
                "open_values AS ( " +
                "  SELECT DISTINCT ON (date_trunc('day', date_time), mac) " +
                "    date_trunc('day', date_time) AS date, " +
                "    mac, " +
                "    measure AS open " +
                "  FROM " +
                "    measure " +
                "  WHERE " +
                "    date_time >= current_date - interval '29 days' " +
                "    AND mac = 'D4:D4:DA:E4:BE:64' " +
                "  ORDER BY " +
                "    date_trunc('day', date_time), " +
                "    mac, " +
                "    date_time " +
                "), " +
                "close_values AS ( " +
                "  SELECT DISTINCT ON (date_trunc('day', date_time), mac) " +
                "    date_trunc('day', date_time) AS date, " +
                "    mac, " +
                "    measure AS close " +
                "  FROM " +
                "    measure " +
                "  WHERE " +
                "    date_time >= current_date - interval '29 days' " +
                "    AND mac = 'D4:D4:DA:E4:BE:64' " +
                "  ORDER BY " +
                "    date_trunc('day', date_time) DESC, " +
                "    mac, " +
                "    date_time DESC " +
                ") " +
                "SELECT " +
                "  ld.date, " +
                "  ov.open, " +
                "  ld.low, " +
                "  ld.high, " +
                "  cv.close, " +
                "  ld.mac " +
                "FROM " +
                "  last_30_days ld " +
                "JOIN " +
                "  open_values ov ON ld.date = ov.date AND ld.mac = ov.mac " +
                "JOIN " +
                "  close_values cv ON ld.date = cv.date AND ld.mac = cv.mac " +
                "ORDER BY " +
                "  ld.mac, " +
                "  ld.date; ";

                int indice = 0;
                try (Connection connection = ConnectionFactory.getConnection()) {
                    String query = sql;
                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        //statement.setString(1, mac);
                        try (ResultSet resultSet = statement.executeQuery()) {
                            if (resultSet.next()) {

                                entries.add(
                                        new CandleEntry(indice,
                                        resultSet.getFloat("high"),
                                        resultSet.getFloat("low"),
                                        resultSet.getFloat("open"),
                                        resultSet.getFloat("close")
                                        )
                                );
                                indice++;

                            }
                        }
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }*/
                return entries;

    }

    private void setGraphEquipment() {
        // GRÁFICO
        // on below line we are adding data to our graph view.
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                // on below line we are adding
                // each point on our x and y axis.
                new DataPoint(0, 18),
                new DataPoint(1, 25),
                new DataPoint(2, 4),
                new DataPoint(3, 100),
                new DataPoint(4, 15),
                new DataPoint(5, 13),
                new DataPoint(6, 90),
                new DataPoint(7, 12),
                new DataPoint(8, 56),
                new DataPoint(10, 56),
                new DataPoint(20, 56),
                new DataPoint(29, 30),
                new DataPoint(30, 100)
        });

        binding.graphEquipment.getViewport().setYAxisBoundsManual(true);
        binding.graphEquipment.getViewport().setMinY(0);
        binding.graphEquipment.getViewport().setMaxY(100);
        binding.graphEquipment.getViewport().setMaxX(30);
        //binding.graphEquipment.getViewport().setScalable(true);
        //binding.graphEquipment.getViewport().setScalableY(true);

        // after adding data to our line graph series.
        // on below line we are setting
        // title for our graph view.
        binding.graphEquipment.setTitle("My Graph View");

        // on below line we are setting
        // text color to our graph view.
        binding.graphEquipment.setTitleColor(R.color.purple_200);

        // on below line we are setting
        // our title text size.
        binding.graphEquipment.setTitleTextSize(18);

        // on below line we are adding
        // data series to our graph view.
        binding.graphEquipment.addSeries(series);
    }

    /**Método que atualiza os dados na tela de equipamentos, estes dados devem estar
     * previamente inseridos na variável da classe chamada equipamento.
     * @author Keemy Barbosa
     * @param updatePercentual bool - Inidica se é necessário atualizar o percentual.
     * @return null - Sem retorno
     */
    private void updateEquipmentInfo(boolean updatePercentual) {

        DecimalFormat formatter = new DecimalFormat("#0.0");
        DecimalFormat formatter00 = new DecimalFormat("#0.00");

        //if (equipamento.getMeasure() == 0) return;
        String sVolLitters = "";

        if (updatePercentual) {
            Double dPercentual = equipamento.getPercentual();

            if (dPercentual < 0){
                binding.tvEqpPercentual.setText("Lmt Exced");
                binding.tvEqpMessage.setText("Medida capturada excede fundo reservatório!");
                sVolLitters = "0";
            } else if (dPercentual > 100.0){
                binding.tvEqpPercentual.setText("Transb.");
                binding.tvEqpMessage.setText("Transbordo!");

                mImageDrawable.setLevel((int) (MAX_LEVEL));

                sVolLitters = "" + equipamento.getVolume();
            } else {
                binding.tvEqpPercentual.setText(equipamento.getPercentualAsString());
                binding.tvEqpMessage.setText("");

                mImageDrawable.setLevel((int) (dPercentual/100 * MAX_LEVEL));
                sVolLitters = "" + formatter.format(dPercentual/100 * equipamento.getVolume());
            }


        }

        double dMCA = (equipamento.getEmptycm()/10.0f) -
                      (equipamento.getMeasure()/10.0f);

        binding.tvEqpMCA.setText(formatter00.format(dMCA/100.0f)  + " m.c.a. (" + sVolLitters + "L)");

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

        //AsyncTaskRunner runner = new AsyncTaskRunner("http://vps52736.publiccloud.com.br:8080/api/measure/last/" + mac, new AsyncTaskCallback() {

        String taskURL = WebServiceConstants.MEASURE_ENDPOINT + "last/";
        AsyncTaskRunner runner = new AsyncTaskRunner(taskURL + mac, new AsyncTaskCallback() {
            @Override
            public void onTaskCompleted(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int measure = jsonObject.getInt("measure");
                    equipamento.setMeasure(measure);
                } catch (JSONException e) {
                    e.printStackTrace();

                    //TODO: LINHA COMENTADA EM 01/05/2024 - TESTAR PARA VER IMPLICAÇÕES
                    //throw new RuntimeException(e);
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