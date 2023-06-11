package com.example.monitordenivel.http;

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.Exception

public class HttpHelper {

    fun get(url: String) : String? {

        try {


            // FONTE: https://www.youtube.com/watch?v=px5xDQSHxmQ

            // URL do servidor
            val URL = url; //"http://ec2-3-22-51-1.us-east-2.compute.amazonaws.com:8080/api/measure/last"

            // Criar um cliente que vai disparar a requisição
            val client = OkHttpClient()

            // Criar uma requisição GET
            val request = Request.Builder().url(URL).get().build()

            // Enviar a requição para o servidor
            val response = client.newCall(request).execute()

            //Extrair o body da resposta
            val responseBody = response.body?.string();

            //Exibir o body da requisição
            if (responseBody != null){
                val json = responseBody.toString()
                return "====> " + json
            }

        } catch (e: Exception ){
            return e.message.toString();
        }

        return null


    }

}
