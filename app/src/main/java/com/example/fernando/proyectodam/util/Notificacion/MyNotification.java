package com.example.fernando.proyectodam.util.Notificacion;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import com.example.fernando.proyectodam.R;
import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;
import com.example.fernando.proyectodam.gestion.GestionProviderElementos;
import com.example.fernando.proyectodam.pojo.Lista;
import com.example.fernando.proyectodam.pojo.Nota;
import com.example.fernando.proyectodam.util.Ficheros.UtilImage;
import com.example.fernando.proyectodam.vistas.main.VistaQuip;

import java.util.Calendar;

/**
 * Created by Fernando on 21/10/2016.
 */


public class MyNotification extends BroadcastReceiver
{
    private ContentResolver cr;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context);
        int alarmId = intent.getExtras().getInt("alarmId");

        //Debemos de obtener la informacion de la nota o lista
        Uri uri     = ContentUris.withAppendedId(GestionProviderElementos.URI_CONTENIDO_ELEMENTOS, alarmId);
        Cursor c    = context.getContentResolver().query( uri, null, null, null, null);

        if ( c.moveToFirst() ) {

            int tipo    = c.getInt(c.getColumnIndex(ContratoBaseDatos.Elementos.TIPO));
            cr          = context.getContentResolver();

            if ( tipo == 0 ) {

                Nota n = Nota.getNota(c);
                nBuilder.setContentTitle(n.getTitulo());
                nBuilder.setContentText(n.getNota());

                //Agregamos la imagen de la nota si la tiene
                if ( n.getImagen() != null ) {

                    nBuilder.setStyle( new NotificationCompat.BigPictureStyle().bigPicture(n.getImagen()));
                }

                //Actualizamos la informacion de la nota
                n.setFecha_not("");
                cr.update( ContentUris.withAppendedId(GestionProviderElementos.ELEMENTOS_ESPECIFICO,0),
                            n.getContentValues(true), null, null);

                //Agregamos el intent para entrar a nuestra aplicacion desde la notificacion
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

                PendingIntent contentIntent = PendingIntent.getActivity(context, alarmId,
                                              targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                nBuilder.setContentIntent(contentIntent);

            }
            else
            {
                Lista l = Lista.getLista(c);
                nBuilder.setContentTitle(l.getTitulo());

                l.setFecha_not("");
                String where       = ContratoBaseDatos.Elementos._ID + " = ?";
                String[] args      = new String[]{String.valueOf(l.getId())};

                cr.update( ContentUris.withAppendedId(GestionProviderElementos.ELEMENTOS_ESPECIFICO, 1),
                           l.getContentValues(true), where, args);

                Intent targetIntent = new Intent(context, VistaQuip.class);
                Bundle b = new Bundle();
                b.putParcelable("elemento", l);
                targetIntent.putExtras(b);

                PendingIntent contentIntent = PendingIntent.getActivity(context, alarmId,
                        targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                nBuilder.setContentIntent(contentIntent);
            }

            nBuilder.setWhen(Calendar.getInstance().getTimeInMillis());
            nBuilder.setSmallIcon(R.drawable.ic_alarm_on_black_24px);

            // Patrón de vibración: 2 segundos vibra
            long[] pattern = new long[]{2000,500,2000};
            nBuilder.setVibrate(pattern);

            //Cambiar el color de la notificacion
            nBuilder.setLights(Color.MAGENTA, 1, 0);

            // Sonido por defecto de notificaciones
            Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            nBuilder.setSound(defaultSound);

            //La notificacion se elimina cuando pulsamos sobre ella
            nBuilder.setAutoCancel(true);

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(
                    Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, nBuilder.build());

            //Terminamos la ejecucion del broadcast

            /*
            *   FLAG_CANCEL_CURRENT nos permite indicar que si se registra un pendingitem con
            *   el mismo id existiendo uno el anterior se cancela y elimina
            * */

            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, alarmId,
                    new Intent(context, MyNotification.class),
                    PendingIntent.FLAG_CANCEL_CURRENT);
            alarmIntent.cancel();


            //Quitamos el broadcast receiver de reinicio
            ComponentName receiver = new ComponentName(context, RestartNotification.class);
            PackageManager pm = context.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);


        }

    }


}
