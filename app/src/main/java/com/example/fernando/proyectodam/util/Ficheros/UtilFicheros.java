package com.example.fernando.proyectodam.util.Ficheros;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Fernando on 03/11/2016.
 */

public class UtilFicheros {

    public static void saveSharedPreferences( final Context c, String nombre, String atributo, String valor ) {

        new AsyncTask< String, Void, Void >() {

            @Override
            protected Void doInBackground(String... params) {

                String nombre   = params[0];
                String atributo = params[1];
                String valor    = params[2];

                SharedPreferences sp = c.getSharedPreferences( nombre, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                editor.putString(atributo, valor);
                editor.commit();

                return null;
            }

        }.execute(nombre, atributo, valor);
    }


    public static void getValueFromSharedPreferences( AsyncResponse ar, Context c, String nombre, String atributo ) {

        final AsyncResponse response = ar;

        new AsyncTask<Object, Void, String>(){

            @Override
            protected String doInBackground(Object... params) {

                Context context     = (Context)params[0];
                String nombre       = (String)params[1];
                String atributo     = (String)params[2];

                SharedPreferences sp = context.getSharedPreferences(nombre, Context.MODE_PRIVATE);

                return sp.getString(atributo, "");
            }

            @Override
            protected void onPostExecute(String o) {

                response.enviarInformacion(o);
            }

        }.execute(c,nombre, atributo);
    }


    public static String getValueFromSharedPreferences( Context c, String nombre, String atributo ) {

        SharedPreferences sp = c.getSharedPreferences(nombre, Context.MODE_PRIVATE);
        return sp.getString(atributo, "");

    }
}
