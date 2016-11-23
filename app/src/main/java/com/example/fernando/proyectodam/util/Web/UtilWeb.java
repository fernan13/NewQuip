package com.example.fernando.proyectodam.util.Web;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;

import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;
import com.example.fernando.proyectodam.gestion.GestionProviderElementos;
import com.example.fernando.proyectodam.gestion.GestionProviderNetworkUpdate;
import com.example.fernando.proyectodam.pojo.Lista;
import com.example.fernando.proyectodam.pojo.Nota;
import com.example.fernando.proyectodam.util.Ficheros.UtilFicheros;
import com.example.fernando.proyectodam.util.Ficheros.UtilImage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Fernando on 02/11/2016.
 */

public class UtilWeb {

    public static final String SERVER                   = "https://quiip-fernan13.c9users.io/android/";
    public static final String URL_UPDATE_ANDROID       = SERVER + "updatefromandroid.php";
    public static final String URL_TRASH                = SERVER + "emptytrash.php";
    public static final String URL_EMAIl                = SERVER + "registerandroiduser.php";
    public static final String URL_UPDATE_SERVER        = SERVER + "updatefromserver.php";

    private static String getJsonFromObject( Context c, Object o ) {

        Gson gson = new GsonBuilder().create();
        HashMap<String, Object> items = new HashMap();

        if ( o != null ) {

            if ( o instanceof Nota ) {

                //Este paso es realizado para poder convertir la imagen a string
                Nota nota = (Nota) o;
                items.put("id", nota.getIdserver());
                items.put("titulo", nota.getTitulo());
                items.put("nota", nota.getNota());
                items.put("imagen", UtilImage.getStringFromBitmap(nota.getImagen()));
                items.put("papelera", nota.getPapelera());
                items.put("tipo", nota.getTipo());
                items.put("fecha_not", nota.getFecha_not());
                items.put("color", nota.getColor());
                items.put("orden", nota.getOrden());
            }
            else
            {
                //Nos llega una lista
                if ( o instanceof Lista ) {

                    Lista lista = (Lista) o;
                    items.put("id", lista.getIdserver());
                    items.put("titulo", lista.getTitulo());
                    items.put("items", lista.getItems());
                    items.put("papelera", lista.getPapelera());
                    items.put("tipo", lista.getTipo());
                    items.put("fecha_not", lista.getFecha_not());
                    items.put("color", lista.getColor());
                    items.put("orden", lista.getOrden());
                }
                else
                {
                    //Nos llega un array donde se encuentra la informacion
                    items.put("accion", o);
                }
            }

        }

        SharedPreferences sp = c.getSharedPreferences("usuario", Context.MODE_PRIVATE);
        items.put("email", sp.getString( "email", ""));

        TelephonyManager tm = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        String tmDevice, tmSerial, androidId;
        tmDevice    = "" + tm.getDeviceId();
        tmSerial    = "" + tm.getSimSerialNumber();
        androidId   = "" + android.provider.Settings.Secure.getString( c.getContentResolver(),
                           android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();

        items.put("idDispositivo", deviceId );

        return gson.toJson(items);
    }


    public static String getStringToInputStream (InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    public static boolean getNetworkStatus( Context c ){

        ConnectivityManager connMgr = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }


    public static void subirInformacion ( Context c , String url, Object o ) {

        new AsyncTask<Object, Void, Void>(){

            @Override
            protected Void doInBackground(Object... params) {

                Context c       = (Context)params[0];
                String url_web  = (String)params[1];
                Object o        = params[2];

                // Operaciones http

                OutputStream os = null;
                InputStream is = null;
                HttpURLConnection conn = null;

                try {

                    //constants
                    URL url = new URL(url_web);
                    String message = getJsonFromObject(c, o);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout( 10000 /*milliseconds*/ );
                    conn.setConnectTimeout( 15000 /* milliseconds */ );
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setFixedLengthStreamingMode(message.getBytes().length);

                    //make some HTTP header nicety
                    conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                    conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                    //open
                    conn.connect();

                    //setup send
                    os = new BufferedOutputStream(conn.getOutputStream());
                    os.write(message.getBytes());
                    //clean up
                    os.flush();

                    //Obtenemos la respuesta del servidor
                    is = conn.getInputStream();

                    String response         = getStringToInputStream(is);

                    if ( !response.isEmpty() ) {

                        JSONObject jsonobject   = new JSONObject(response);
                        Object object_response  = jsonobject.get("r");

                        ContentResolver cr  = c.getContentResolver();
                        String value        = object_response.toString();

                        if ( !value.isEmpty() )
                        {
                            switch ( Integer.parseInt(value) ) {

                                case 1: {

                                    //Se ha realizado la actualizacion en la BBDD
                                    long id = 0;

                                    ContentValues cv;
                                    Uri uri;

                                    if ( o instanceof String[] ) {

                                        //Aqui obtendriamos el id del elemento recuperado o borrado
                                        id = Long.parseLong(((String[])o)[1]);

                                        Cursor cursor = cr.query(ContentUris.withAppendedId(
                                        GestionProviderElementos.ELEMENTOS_ID_SERVER, id),
                                        null, null, null, null);

                                        //Movemos el cursor para obtener la informacion
                                        cursor.moveToFirst();
                                        int tipo    = cursor.getInt(cursor.getColumnIndex(ContratoBaseDatos.Elementos.TIPO));

                                        if ( tipo == 0 ) {

                                            Nota n = Nota.getNota(cursor);

                                            n.setActualizar(1);
                                            cv  = n.getContentValues(true);
                                            uri = ContentUris.withAppendedId(
                                                  GestionProviderElementos.ELEMENTOS_ESPECIFICO, 0);
                                        }
                                        else
                                        {
                                            Lista l = Lista.getLista(cursor);

                                            l.setActualizar(1);
                                            cv  = l.getContentValues(true);
                                            uri = ContentUris.withAppendedId(
                                                    GestionProviderElementos.ELEMENTOS_ESPECIFICO, 1);
                                        }
                                    }
                                    else
                                    {
                                        //Aqui debemos de asignar el id del servidor
                                        int idserver = jsonobject.getInt("id");

                                        if ( o instanceof Nota ) {

                                            ((Nota)o).setActualizar(1);
                                            ((Nota)o).setIdserver(idserver);
                                            ((Nota)o).setOrden(idserver);
                                            cv = ((Nota)o).getContentValues(true);
                                            uri = ContentUris.withAppendedId(
                                                    GestionProviderElementos.ELEMENTOS_ESPECIFICO, 0);

                                        }
                                        else
                                        {
                                            ((Lista)o).setActualizar(1);
                                            ((Lista)o).setIdserver(idserver);
                                            ((Lista)o).setOrden(idserver);
                                            cv  = ((Lista)o).getContentValues(true);
                                            uri = ContentUris.withAppendedId(
                                                  GestionProviderElementos.ELEMENTOS_ESPECIFICO, 1);
                                        }

                                    }

                                    //Actualizamos la informacion
                                    cr.update( uri, cv, null, null);

                                    break;
                                }

                                case 2 : {

                                    //Si nos llega un 2 es que se ha vaciado la papelera
                                    cr.delete(GestionProviderNetworkUpdate.URI_BORRADOS, null, null);
                                    break;
                                }
                            }

                        }

                    }


                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    //clean up
                    try {
                        if (os != null) os.close();
                        if (is != null) is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (conn != null) conn.disconnect();
                }

                return null;

            }

        }.execute( c, url, o );
    }

    public static void bajarInformacion (Context c, final AsyncWebResponse response, String url, Object o ) {

        new AsyncTask<Object, Void, Void>() {

            @Override
            protected void onPreExecute() {

                if ( response != null ) response.sendRequest();
            }

            @Override
            protected Void doInBackground(Object... params) {

                Context c = (Context) params[0];
                String url_web = (String) params[1];
                Object o = params[2];

                if ( getNetworkStatus(c) ) {

                    // Operaciones http

                    OutputStream os = null;
                    InputStream is = null;
                    HttpURLConnection conn = null;

                    try {

                        //constants
                        URL url = new URL(url_web);
                        String message = getJsonFromObject(c, o);
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setReadTimeout(10000 /*milliseconds*/);
                        conn.setConnectTimeout(15000 /* milliseconds */);
                        conn.setRequestMethod("POST");
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setFixedLengthStreamingMode(message.getBytes().length);

                        //make some HTTP header nicety
                        conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                        conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                        //open
                        conn.connect();

                        //setup send
                        os = new BufferedOutputStream(conn.getOutputStream());
                        os.write(message.getBytes());
                        //clean up
                        os.flush();

                        //Obtenemos la respuesta del servidor
                        is = conn.getInputStream();
                        String response = getStringToInputStream(is);

                        ContentResolver cr      = c.getContentResolver();
                        JSONObject jsonobject   = new JSONObject(response);
                        ArrayList<Integer>  ids = new ArrayList();

                        if ( response.contains("actualizar") ) {

                            JSONArray up            = jsonobject.getJSONArray("actualizar");

                            for (int i = 0; i < up.length(); i++) {

                                JSONObject el = up.getJSONObject(i);
                                int type = el.getInt(ContratoBaseDatos.Elementos.TIPO);
                                String idcorreo   = UtilFicheros.getValueFromSharedPreferences(c, "usuario", "id_email");

                                if ( type == 0 ) {

                                    Nota nota = Nota.getNota(el);
                                    nota.setCorreo(Integer.parseInt(idcorreo));
                                    Uri uri = ContentUris.withAppendedId(GestionProviderElementos.ELEMENTOS_ESPECIFICO,0);

                                    //Debemos de saber si esta insertado o no
                                    Cursor cursor = cr.query( ContentUris.withAppendedId(
                                                    GestionProviderElementos.ELEMENTOS_ID_SERVER,
                                                    nota.getIdserver()), null, null, null, null);

                                    if ( !cursor.moveToFirst() ) {

                                        //Insertamos la nota
                                        cr.insert( uri, nota.getContentValues());
                                    }
                                    else
                                    {
                                        //Le damos su id a la nota
                                        long id = cursor.getLong(cursor.getColumnIndex(ContratoBaseDatos.Elementos._ID));
                                        nota.setId(id);

                                        cr.update( uri, nota.getContentValues(true), null, null);

                                    }
                                }
                                else
                                {
                                    Lista lista = Lista.getLista(el);
                                    lista.setCorreo(Integer.parseInt(idcorreo));
                                    Uri uri = ContentUris.withAppendedId(GestionProviderElementos.ELEMENTOS_ESPECIFICO,1);

                                    //Debemos de saber si esta insertado o no
                                    Cursor cursor = cr.query( ContentUris.withAppendedId(
                                                    GestionProviderElementos.ELEMENTOS_ID_SERVER,
                                                    lista.getIdserver()), null, null, null, null);

                                    if ( !cursor.moveToFirst() ) {

                                        //Insertamos la nota
                                        cr.insert( uri, lista.getContentValues());
                                    }
                                    else
                                    {
                                        //Le damos su id a la nota
                                        long id = cursor.getLong(cursor.getColumnIndex(ContratoBaseDatos.Elementos._ID));
                                        lista.setId(id);

                                        cr.update( uri, lista.getContentValues(true), null, null);

                                    }
                                }

                                //Añadimos los ids para subirlos al servidor y eliminar sus registros de la tabla actualizar
                                ids.add(el.getInt(ContratoBaseDatos.Elementos._ID));
                            }
                        }

                        if ( response.contains("eliminar") ) {

                            JSONArray del   = jsonobject.getJSONArray("eliminar");

                            //Debemos de eliminar los elementos si existen
                            for ( int i = 0; i < del.length(); i++ ) {

                                long id = Long.parseLong(del.getString(i));
                                Uri uri = ContentUris.withAppendedId(GestionProviderElementos.ELEMENTOS_ID_SERVER, id);

                                ids.add((int)id);
                                cr.delete( uri, null, null);
                            }

                        }

                        if ( !ids.isEmpty() ) UtilWeb.subirInformacion(c, url_web, ids);

                    }
                    catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    finally
                    {
                        //clean up
                        try
                        {
                            if (os != null) os.close();
                            if (is != null) is.close();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (conn != null) conn.disconnect();
                    }

                }

                return null;

            }

            @Override
            protected void onPostExecute(Void aVoid) {

                if ( response != null ) response.getRequest();
            }

        }.execute(c, url, o);
    }

}
