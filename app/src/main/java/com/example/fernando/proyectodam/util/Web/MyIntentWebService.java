package com.example.fernando.proyectodam.util.Web;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;
import com.example.fernando.proyectodam.gestion.GestionProviderElementos;
import com.example.fernando.proyectodam.gestion.GestionProviderNetworkUpdate;
import com.example.fernando.proyectodam.pojo.Lista;
import com.example.fernando.proyectodam.pojo.Nota;

import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentWebService extends IntentService {

    public MyIntentWebService() {

        super("MyIntentWebService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            //Actualizamos la informacion
            ContentResolver cr  = getContentResolver();
            Uri uri             = ContentUris.withAppendedId(GestionProviderElementos.ELEMENTOS_ACTUALIZAR, 0);

            Cursor c            = cr.query(uri, null, null, null, null );

            while( c.moveToNext() ) {

                int tipo = c.getInt(c.getColumnIndex(ContratoBaseDatos.Elementos.TIPO));

                Object o = tipo == 0 ? Nota.getNota(c) : Lista.getLista(c);

                UtilWeb.subirInformacion(this, UtilWeb.URL_UPDATE_ANDROID, o);
            }

            //Comprobamos si existen elementos en la tabla borrados
            Cursor c2 = cr.query(GestionProviderNetworkUpdate.URI_BORRADOS, null, null, null, null);

            if ( c2.getCount() > 0 ) {

                ArrayList<Long> ids = new ArrayList();

                while( c2.moveToNext() ) {

                    ids.add(c2.getLong(c2.getColumnIndex(ContratoBaseDatos.Borrados.IDELEMENTO)));
                }

                UtilWeb.subirInformacion(this, UtilWeb.URL_TRASH, ids);
            }
        }
    }

}
