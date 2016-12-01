package com.example.fernando.proyectodam.util.Mapas;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;
import com.example.fernando.proyectodam.gestion.GestionProviderElementos;
import com.example.fernando.proyectodam.pojo.ElementoLista;
import com.example.fernando.proyectodam.util.Ficheros.AsyncResponse;
import com.example.fernando.proyectodam.util.Ficheros.UtilFicheros;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MapService extends Service {

    public MapService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        boolean per1 =  ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        boolean per2 =  ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;


        if ( per1 && per2 ) {

            LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            //Generamos una consulta que obtenga todos los elementos que tengan notificacion

            ContentResolver cr  = getContentResolver();
            Cursor  c           = cr.query(GestionProviderElementos.ELEMENTOS_LOCALIZACION, null, null, null,null);

            if ( c.getCount() == 0 ) {

                this.stopSelf();
                return START_NOT_STICKY;
            }
            else
            {
                while ( c.moveToNext() ) {

                    //Obtenemos la localizacion y generamos la alarma
                    long id         = c.getLong(c.getColumnIndex(ContratoBaseDatos.Elementos._ID));

                    //Json to LtnLng
                    Gson gson       = new GsonBuilder().create();
                    String locJson  = c.getString(c.getColumnIndex(ContratoBaseDatos.Elementos.LOCALIZACION));
                    Type type       = new TypeToken<LatLng>(){}.getType();

                    LatLng pos      = gson.fromJson(locJson, type);

                    //100m de radio para la notificacion
                    float radius = 100f;

                    // Expiracion 1h (60mins * 60secs * 1000milliSecs)
                    long expiration = 3600000;

                    //Broadcast para lanzar la notificacion
                    Intent intent1 = new Intent(this, MapNotification.class);
                    intent1.putExtra("id", id);

                    PendingIntent pendingIntent =   PendingIntent.getBroadcast(this, (int)id, intent1,
                                                    PendingIntent.FLAG_CANCEL_CURRENT);

                    locManager.addProximityAlert(pos.latitude, pos.longitude, radius, expiration, pendingIntent);
                }
            }

        }

        return START_STICKY;
    }

}
