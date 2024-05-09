package com.example.monitordenivel.data;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseHelper {

    // Método para realizar uma operação de consulta ao banco de dados PostgreSQL
    public void realizarConsulta() {
        // Inicie uma AsyncTask para realizar a operação de rede em uma thread separada
        new ConsultaAsyncTask().execute();
    }

    // AsyncTask para realizar a operação de rede em uma thread separada
    private static class ConsultaAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            // Código para a operação de rede (consulta ao banco de dados PostgreSQL)
            Connection connection = null;
            Statement statement = null;
            ResultSet resultSet = null;

            try {
                // Estabeleça a conexão com o banco de dados
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection("jdbc:postgresql://seu_host:seu_porta/seu_banco", "seu_usuario", "sua_senha");

                // Crie uma declaração para a consulta
                statement = connection.createStatement();
                resultSet = statement.executeQuery("SELECT * FROM sua_tabela");

                // Processar o resultado da consulta (aqui você pode fazer o que precisar com os dados)
                while (resultSet.next()) {
                    // Exemplo de leitura de dados do ResultSet
                    String nome = resultSet.getString("nome");
                    int idade = resultSet.getInt("idade");

                    // Faça o que precisar com os dados obtidos
                    Log.d("Consulta", "Nome: " + nome + ", Idade: " + idade);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // Feche os recursos
                try {
                    if (resultSet != null) resultSet.close();
                    if (statement != null) statement.close();
                    if (connection != null) connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Este método é chamado na thread principal após a conclusão da AsyncTask,
            // aqui você pode realizar qualquer atualização da IU se necessário.
        }
    }
}
