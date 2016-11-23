package com.example.fernando.proyectodam.vistas.notas;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;

import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;
import com.example.fernando.proyectodam.contrato.ContratoNota;
import com.example.fernando.proyectodam.gestion.GestionLocationObject;
import com.example.fernando.proyectodam.gestion.GestionProviderElementos;
import com.example.fernando.proyectodam.pojo.LocationObject;
import com.example.fernando.proyectodam.pojo.Nota;
import com.example.fernando.proyectodam.util.Ficheros.UtilFicheros;
import com.example.fernando.proyectodam.util.Notificacion.UtilNotification;
import com.example.fernando.proyectodam.util.Web.UtilWeb;

public class ModeloNota implements ContratoNota.InterfaceModelo {

    private Context c;
    private ContentResolver cr;
    private GestionLocationObject gsLoc;

    public ModeloNota(Context c) {

        this.c  = c;
        cr      = c.getContentResolver();
        gsLoc   = new GestionLocationObject(c);
    }

    @Override
    public Nota getNota(long id) {

        Cursor cursor = cr.query(ContentUris.withAppendedId(
                                                     GestionProviderElementos.URI_CONTENIDO_ELEMENTOS, id),
                                                     null, null, null,null);

        return Nota.getNota(cursor);
    }

    @Override
    public long saveNota(Nota n) {

        long r;

        if( n.getId()==0 ) {

            r = this.insertNota(n);

        } else {

            r = this.updateNota(n);
        }

        //Comprobamos que tenga informacion
        if ( r != 0 ) UtilWeb.subirInformacion( c,  UtilWeb.URL_UPDATE_ANDROID, n );

        if ( !n.getFecha_not().isEmpty() ) UtilNotification.setNotificationAlarm(c, n);

        return r;
    }

    @Override
    public void eliminarNotificacionNota(Nota nota) {

        nota.setFecha_not("");

        UtilNotification.deleteNotificationAlarm( c, nota );
    }

    @Override
    public void saveLocation(LocationObject location) {

        gsLoc.insertLocation(location);
    }

    private long deleteNota(Nota n) {

        String where    =   ContratoBaseDatos.Elementos.TABLA + "." +
                            ContratoBaseDatos.Elementos._ID + " = ?";
        String[] args       = new String[]{n.getId() + ""};

        long filas = cr.delete(ContentUris.withAppendedId(GestionProviderElementos.ELEMENTOS_ESPECIFICO, 0),
                         where, args);

        UtilWeb.subirInformacion( c,  UtilWeb.URL_UPDATE_ANDROID, n.getIdserver() );

        return filas;
    }

    private long insertNota(Nota n) {

        if(n.getNota().trim().compareTo("")==0 && n.getTitulo().trim().compareTo("")==0) {

            return 0;
        }

        //Asignamos el correo
        String idcorreo   = UtilFicheros.getValueFromSharedPreferences( c, "usuario", "id_email");
        n.setCorreo(Integer.parseInt(idcorreo));
        String id = cr.insert( ContentUris.withAppendedId(GestionProviderElementos.ELEMENTOS_ESPECIFICO,0),
                                n.getContentValues()).getLastPathSegment();

        return Long.parseLong(id);
    }

    private long updateNota(Nota n) {

        if( n.getNota().trim().compareTo("")==0 && n.getTitulo().trim().compareTo("")==0 ) {

            this.deleteNota(n);
            return 0;

        }

        n.setActualizar(0);
        return cr.update( ContentUris.withAppendedId(GestionProviderElementos.ELEMENTOS_ESPECIFICO,0),
                          n.getContentValues(true), null, null);
    }

}