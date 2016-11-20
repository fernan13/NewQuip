package com.example.fernando.proyectodam.util.Web;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;
import com.example.fernando.proyectodam.gestion.GestionProviderElementos;
import com.example.fernando.proyectodam.gestion.GestionProviderNetworkUpdate;
import com.example.fernando.proyectodam.pojo.Lista;
import com.example.fernando.proyectodam.pojo.Nota;

import java.util.ArrayList;

/**
 * Created by Fernando on 04/11/2016.
 */

public class BroadcastNetworkUpdate extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if ( UtilWeb.getNetworkStatus(context) ){

            context.startService( new Intent(context, MyIntentWebService.class));

        }
    }
}
