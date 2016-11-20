package com.example.fernando.proyectodam.vistas.listas;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;

import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;
import com.example.fernando.proyectodam.contrato.ContratoLista;
import com.example.fernando.proyectodam.gestion.GestionLocationObject;
import com.example.fernando.proyectodam.gestion.GestionProviderElementos;
import com.example.fernando.proyectodam.pojo.Lista;
import com.example.fernando.proyectodam.pojo.LocationObject;
import com.example.fernando.proyectodam.util.Ficheros.UtilFicheros;
import com.example.fernando.proyectodam.util.Notificacion.UtilNotification;
import com.example.fernando.proyectodam.util.Web.UtilWeb;

public class ModeloLista implements ContratoLista.InterfaceModelo {

    private Context c;
    private ContentResolver cr;
    private GestionLocationObject glo;

    public ModeloLista(Context c) {

        this.c  = c;
        cr      = c.getContentResolver();
        glo     = new GestionLocationObject(c);

    }

    @Override
    public Lista getLista(long id) {

        Cursor cursor = cr.query(ContentUris.withAppendedId(
                GestionProviderElementos.URI_CONTENIDO_ELEMENTOS, id),
                null, null, null,null);

        return Lista.getLista(cursor);
    }

    @Override
    public long saveLista(Lista lista) {

        long r;

        if( lista.getId()==0 ) {

            r = this.insertLista(lista);

        } else {

            r = this.updateLista(lista);
        }

        //Comprobamos que tenga informacion
        if ( r != 0 ) UtilWeb.subirInformacion(c,  UtilWeb.URL_UPDATE_ANDROID, lista);

        if ( !lista.getFecha_not().isEmpty() ) UtilNotification.setNotificationAlarm(c, lista);

        return r;
    }

    @Override
    public void saveLocation(LocationObject location) {

        glo.insertLocation(location);
    }

    @Override
    public void eliminarNotificacionLista(Lista lista) {

        lista.setFecha_not("");

        //No actualizamos el contenido en la BD porque se actualizaria al salir de la activity
        UtilNotification.deleteNotificationAlarm(c, lista);
    }

    private long deleteLista(Lista lista) {

        //return gl.delete(lista);
        String where        =   ContratoBaseDatos.Elementos.TABLA + "." +
                                ContratoBaseDatos.Elementos._ID + " = ?";
        String[] args       = new String[]{lista.getId() + ""};

        long filas          = cr.delete(ContentUris.withAppendedId(GestionProviderElementos.ELEMENTOS_ESPECIFICO, 1),
                                        where, args);

        UtilWeb.subirInformacion(c,  UtilWeb.URL_UPDATE_ANDROID, lista.getId());

        return filas;
    }

    private long insertLista(Lista lista) {

        if( lista.getTitulo().trim().compareTo("")==0 && !lista.haveData() ) {
            return 0;
        }

        //Asignamos el correo
        String idcorreo   = UtilFicheros.getValueFromSharedPreferences( c, "usuario", "id_email");
        lista.setCorreo(Integer.parseInt(idcorreo));

        String id = cr.insert( ContentUris.withAppendedId(GestionProviderElementos.ELEMENTOS_ESPECIFICO, 1),
                                lista.getContentValues()).getLastPathSegment();

        return Long.parseLong(id);
    }

    private long updateLista(Lista lista) {

        if( lista.getTitulo().trim().compareTo("")== 0 && !lista.haveData() ) {

            this.deleteLista(lista);
            return 0;

        }

        lista.setActualizar(0);

        return cr.update( ContentUris.withAppendedId(GestionProviderElementos.ELEMENTOS_ESPECIFICO, 1),
                          lista.getContentValues(true), null, null);
    }
}