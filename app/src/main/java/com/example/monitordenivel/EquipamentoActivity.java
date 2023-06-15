package com.example.monitordenivel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.monitordenivel.databinding.ActivityEquipamentoBinding;
import com.example.monitordenivel.databinding.ActivityMainBinding;
import com.example.monitordenivel.models.Equipamento;

public class EquipamentoActivity extends AppCompatActivity {

    private Equipamento equipamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ActivityEquipamentoBinding binding = ActivityEquipamentoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        equipamento = new Equipamento(1,"aa:aa:aa:aa:aa:aa",10000,100,30,"RES01", 50);

        binding.tvEqpId.setText("Id: " + equipamento.getId());
        binding.tvEqpName.setText("" + equipamento.getName());
        binding.tvEqpMeasure.setText("Measure: " + equipamento.getMeasure());
        binding.tvEqpMac.setText("Mac: " + equipamento.getMac());
        binding.tvEqpVolume.setText("Volume: " + equipamento.getVolume() + "L");
        binding.tvEqpEmpty.setText("Distância Vazio: " + equipamento.getEmptycm() + "cm");
        binding.tvEqpFull.setText("Distância Cheio: " + equipamento.getFullcm() + "cm");

    }
}