package com.example.monitordenivel.utils;

import java.util.Random;

public class MathUtils {

    public static int numeroAleatorio(int min, int max){

        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
