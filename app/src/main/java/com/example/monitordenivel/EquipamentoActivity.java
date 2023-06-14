package com.example.monitordenivel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.monitordenivel.databinding.ActivityEquipamentoBinding;
import com.example.monitordenivel.databinding.ActivityMainBinding;

public class EquipamentoActivity extends AppCompatActivity {

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

    }
}