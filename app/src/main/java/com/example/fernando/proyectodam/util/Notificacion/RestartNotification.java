package com.example.fernando.proyectodam.util.Notificacion;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;

import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;
import com.example.fernando.proyectodam.gestion.GestionProviderElementos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Fernando on 22/10/2016.
 */

public class RestartNotification extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            SimpleDateFormat sdf    = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

            Calendar calendar       = Calendar.getInstance();
            Cursor cursor           = context.getContentResolver().query(GestionProviderElementos.ELEMENTOS_NOTIFICACION,
                    null,null,null,null);

            Toast.makeText(context, cursor.getCount(), Toast.LENGTH_LONG).show();

            while ( cursor.moveToNext() ) {

                try
                {

                    String dateStr = cursor.getString(cursor.getColumnIndex(ContratoBaseDatos.Elementos.FECHA_NOT));

                    int alarmId = (int)cursor.getLong(cursor.getColumnIndex(ContratoBaseDatos.Elementos._ID));
                    calendar.setTime(sdf.parse(dateStr));

                    Intent intent1 = new Intent( context, MyNotification.class);
                    intent.putExtra("alarmId", alarmId);

                    PendingIntent pendingIntent  = PendingIntent.getBroadcast( context, alarmId, intent1,
                            PendingIntent.FLAG_CANCEL_CURRENT);

                    AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 , pendingIntent);


                }
                catch( ParseException pe ){}
            }

        }


    }


}
