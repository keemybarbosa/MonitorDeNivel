package com.example.monitordenivel;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowInsets;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monitordenivel.databinding.ActivityMainantigaBinding;
import com.example.monitordenivel.models.Equipamento;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import com.google.gson.reflect.TypeToken;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainAntigaActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = false;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler(Looper.myLooper());
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            if (Build.VERSION.SDK_INT >= 30) {
                mContentView.getWindowInsetsController().hide(
                        WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            } else {
                // Note that some of these constants are new as of API 16 (Jelly Bean)
                // and API 19 (KitKat). It is safe to use them, as they are inlined
                // at compile-time and do nothing on earlier devices.
                mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            //hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    /*private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
        }
    };*/
    public void teste(){
        carregarDados(findViewById(R.id.fullscreen_content), findViewById(R.id.lstEquipments));
    }

    public void carregarDados(TextView tv1, ListView lvEquipments){
        tv1.setText("Loading...");
        //AsyncTaskRunner runner = new AsyncTaskRunner("http://ec2-3-22-51-1.us-east-2.compute.amazonaws.com:8080/api/measure/last");
        AsyncTaskRunner runner = new AsyncTaskRunner("http://ec2-3-22-51-1.us-east-2.compute.amazonaws.com:8080/api/equipment");

        String returnJson = "";
        try {
            returnJson = runner.execute().get();

            ArrayList<Equipamento> equipamentos = getEquipamentosFromJson(returnJson);
            ArrayAdapter adapter =  new EquipamentoAdapter(MainAntigaActivity.this, equipamentos);
            lvEquipments.setAdapter(adapter);

            //TODO: codigo para teste
            if (equipamentos.size() == 0) {
                equipamentos = getEquipsForTest();
                adapter = new EquipamentoAdapter(MainAntigaActivity.this, equipamentos);
                lvEquipments.setAdapter(adapter);
            }

        } catch (ExecutionException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        } catch (InterruptedException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
        tv1.setText("");
    }

    private ArrayList<Equipamento> getEquipsForTest() {
        //TODO: METODO PARA TESTE
        ArrayList<Equipamento> listEquips = new ArrayList<Equipamento>();
        listEquips.add(new Equipamento(1,"aa:aa:aa:aa:aa:aa",1000,250,40,"POCO1",214));
        listEquips.add(new Equipamento(2,"bb:aa:aa:aa:aa:aa",1000,180,36,"POCO2",78));
        listEquips.add(new Equipamento(3,"cc:aa:aa:aa:aa:aa",1000,100,48,"POCO3",95));
        listEquips.add(new Equipamento(4,"dd:aa:aa:aa:aa:aa",1000,180,50,"POCO4",114));
        listEquips.add(new Equipamento(5,"ee:aa:aa:aa:aa:aa",1000,70,10,"POCO5",23));

        return listEquips;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainantigaBinding binding = ActivityMainantigaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mVisible = true;
        mControlsView = binding.fullscreenContentControls;
        mContentView = binding.fullscreenContent;


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //toggle();
            }
        });

        TextView tv1 = findViewById(R.id.fullscreen_content);
        ListView lvEquipments = findViewById(R.id.lstEquipments);







        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //binding.dummyButton.setOnTouchListener(mDelayHideTouchListener);
        binding.dummyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carregarDados(tv1, lvEquipments);
;            }
        });

        binding.limparButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //

                //tv1.setText("JSON");
                ArrayAdapter adapter = (ArrayAdapter) lvEquipments.getAdapter();
                if (adapter == null){
                    adapter = new EquipamentoAdapter(MainAntigaActivity.this, new ArrayList<Equipamento>());
                }
                adapter.clear();
            }
        });

        binding.btnmain2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication().getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });





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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
/*
        //Criando um timer para atualizar de tempos em tempos a lista de equipamentos
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    teste();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }},1000, 1000);*/
    }

    /*private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }*/

    /*private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }*/

    /*private void show() {
        // Show the system bar
        if (Build.VERSION.SDK_INT >= 30) {
            mContentView.getWindowInsetsController().show(
                    WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }*/

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }



}