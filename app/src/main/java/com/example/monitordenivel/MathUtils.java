package com.example.monitordenivel;

import java.util.Random;

public  class MathUtils {

    static int numeroAleatorio(int min, int max){

        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
