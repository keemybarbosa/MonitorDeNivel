package com.example.monitordenivel.utils;

public class WebServiceConstants {

    public static final String BASE_DOMAIN = "vps52736.publiccloud.com.br";
    //public static final String BASE_DOMAIN = "localhost"; //Não funciona a parte de api quando usa-se localhost
    public static final String BASE_URL = "http://" + BASE_DOMAIN + ":8080/";


    public static final String API_ENDPOINT = "api/";
    public static final String MEASURE_ENDPOINT = BASE_URL + API_ENDPOINT + "measure/";
    public static final String EQUIPMENT_ENDPOINT = BASE_URL + API_ENDPOINT +"equipment";

    // Outras constantes aqui...
}
