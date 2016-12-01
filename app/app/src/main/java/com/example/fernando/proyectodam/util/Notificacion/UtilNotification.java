package com.example.fernando.proyectodam.util.Notificacion;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.example.fernando.proyectodam.pojo.Lista;
import com.example.fernando.proyectodam.pojo.Nota;
import com.example.fernando.proyectodam.util.Notificacion.MyNotification;
import com.example.fernando.proyectodam.util.Notificacion.RestartNotification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Fernando on 22/10/2016.
 */

public class UtilNotification {

    public static void setNotificationAlarm(Context context, Object o )
    {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        Calendar calendar    = Calendar.getInstance();

        //Informacion necesaria
        String fecha_not;
        int alarmId = 0;

        try
        {
            if ( o != null ) {

                if ( o instanceof Nota) {

                    alarmId     = (int)((Nota)o).getId();
                    fecha_not   = ((Nota)o).getFecha_not();
                }
                else
                {
                    alarmId     = (int)((Lista)o).getId();
                    fecha_not = ((Lista)o).getFecha_not();
                }

                if ( !fecha_not.isEmpty() ) {

                    calendar.setTime(sdf.parse(fecha_not));

                    //Broadcast para lanzar la notificacion
                    Intent intent = new Intent( context , MyNotification.class);
                    intent.putExtra("alarmId", alarmId);

                    PendingIntent pendingIntent  =  PendingIntent.getBroadcast( context, alarmId, intent,
                            PendingIntent.FLAG_CANCEL_CURRENT);

                    AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 , pendingIntent);

                    //Refistramos el broadcastreceiver que se encarga de comprobar el boot del dispositivo

                    ComponentName receiver = new ComponentName(context, RestartNotification.class);
                    PackageManager pm = context.getPackageManager();

                    pm.setComponentEnabledSetting(receiver,
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP);

                }

            }

        }
        catch( ParseException pe ){}



    }

    public static void deleteNotificationAlarm( Context c, Object o ) {

        int alarmId;

        if ( o != null ) {

            if ( o instanceof Nota ) {

                alarmId = (int)((Nota)o).getId();
            }
            else
            {
                alarmId = (int)((Lista)o).getId();
            }

            PendingIntent alarmIntent = PendingIntent.getBroadcast(c, alarmId,
                    new Intent(c, MyNotification.class),
                    PendingIntent.FLAG_CANCEL_CURRENT);

            alarmIntent.cancel();

        }

    }

}
