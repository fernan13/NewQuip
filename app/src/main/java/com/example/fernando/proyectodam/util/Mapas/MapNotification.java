package com.example.fernando.proyectodam.util.Mapas;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import com.example.fernando.proyectodam.R;
import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;
import com.example.fernando.proyectodam.gestion.GestionProviderElementos;
import com.example.fernando.proyectodam.pojo.Lista;
import com.example.fernando.proyectodam.pojo.Nota;
import com.example.fernando.proyectodam.util.Ficheros.UtilFicheros;
import com.example.fernando.proyectodam.util.Ficheros.UtilImage;
import com.example.fernando.proyectodam.util.Web.UtilWeb;
import com.example.fernando.proyectodam.vistas.main.VistaQuip;

/**
 * Created by Fernando on 24/11/2016.
 */

public class MapNotification extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        String entering                     = LocationManager.KEY_PROXIMITY_ENTERING;
        boolean prox                        = intent.getBooleanExtra(entering, false);
        ContentResolver cr                  = context.getContentResolver();
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context);

        //id notificacion
        int id = (int)intent.getLongExtra("id", -1);

        if ( id != -1 ) {

            nBuilder.setContentTitle(UtilFicheros.getValueForSharedPreferences(context, "mapa", "titulo"));
            nBuilder.setWhen(System.currentTimeMillis());
            nBuilder.setAutoCancel(true);
            nBuilder.setSmallIcon(R.drawable.ic_place_black_24px);
            nBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

            //El usuario se acerca
            if ( prox ) {

                nBuilder.setContentText("Ya mismo llegas!");
            }
            else
            {
                //El usuario se aleja
                nBuilder.setContentText("Te has pasado!");
            }

            //Debemos de agregar al usuario el pending item para entrar en la nota

            Uri uri     = ContentUris.withAppendedId(GestionProviderElementos.URI_CONTENIDO_ELEMENTOS, id);
            Cursor c    = context.getContentResolver().query( uri, null, null, null, null);

            if ( c.moveToFirst() ) {

                int tipo    = c.getInt(c.getColumnIndex(ContratoBaseDatos.Elementos.TIPO));

                if ( tipo == 0 ) {

                    Nota n = Nota.getNota(c);

                    n.setMap_not("");
                    cr.update( ContentUris.withAppendedId(GestionProviderElementos.ELEMENTOS_ESPECIFICO, 0),
                                n.getContentValues(true), null, null);

                    UtilWeb.subirInformacion( context,  UtilWeb.URL_UPDATE_ANDROID, n );

                    Intent targetIntent = new Intent(context, VistaQuip.class);
                    Bundle b = new Bundle();

                    //Realizamos esta accion para que la actividad no ternime por OOM
                    Bitmap bmp = n.getImagen();
                    n.setImagen(null);
                    byte[] res = null;

                    if ( bmp != null ) {

                        res = UtilImage.getByteArrayFromBitmap(bmp);
                    }

                    b.putParcelable("elemento", n);
                    b.putByteArray("imagen", res);

                    targetIntent.putExtras(b);

                    PendingIntent contentIntent = PendingIntent.getActivity(context, id,
                            targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    nBuilder.setContentTitle(n.getTitulo());
                    nBuilder.setContentIntent(contentIntent);

                }
                else
                {
                    Lista l = Lista.getLista(c);

                    //Actualizamos la lista
                    l.setMap_not("");
                    cr.update(  ContentUris.withAppendedId(GestionProviderElementos.ELEMENTOS_ESPECIFICO, 1),
                                l.getContentValues(true), null, null);

                    UtilWeb.subirInformacion( context,  UtilWeb.URL_UPDATE_ANDROID, l );

                    Intent targetIntent = new Intent(context, VistaQuip.class);
                    Bundle b = new Bundle();
                    b.putParcelable("elemento", l);
                    targetIntent.putExtras(b);

                    PendingIntent contentIntent =   PendingIntent.getActivity(context, id,
                                                    targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    nBuilder.setContentTitle(l.getTitulo());
                    nBuilder.setContentIntent(contentIntent);

                }
            }

            //Cancelamos la notificacion
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, id,
                                        new Intent(context, MapNotification.class),
                                        PendingIntent.FLAG_CANCEL_CURRENT);
            alarmIntent.cancel();

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(
                                                        Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(1, nBuilder.build());

        }
    }
}
