package com.example.fernando.proyectodam.vistas.main;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;
import com.example.fernando.proyectodam.contrato.ContratoMain;
import com.example.fernando.proyectodam.gestion.GestionProviderElementos;
import com.example.fernando.proyectodam.gestion.GestionProviderNetworkUpdate;
import com.example.fernando.proyectodam.pojo.Lista;
import com.example.fernando.proyectodam.pojo.Nota;
import com.example.fernando.proyectodam.util.Notificacion.UtilNotification;
import com.example.fernando.proyectodam.util.Web.UtilWeb;

import java.util.ArrayList;
import java.util.Arrays;

public class ModeloQuip implements ContratoMain.InterfaceModelo {

    private Context c;
    private ContentResolver cr;
    private Cursor cursor;

    public ModeloQuip(Context c) {

        this.cursor     = null;
        this.c          = c;
        this.cr         = c.getContentResolver();
    }

    @Override
    public long deleteNota(Nota n) {

        String where    = ContratoBaseDatos.Elementos._ID + " = ?";
        String[] args   = new String[]{n.getId()+""};
        return cr.delete( ContentUris.withAppendedId(GestionProviderElementos.ELEMENTOS_ESPECIFICO, 0), where, args);
    }

    @Override
    public long papeleraNota(Nota n) {

        UtilNotification.deleteNotificationAlarm( c , n );

        //Modificamos la nota para que vaya a la papelera
        n.setPapelera(1);
        n.setFecha_not("");

        ContentValues cv = n.getContentValues(true);

        long filas          =  cr.update( ContentUris.withAppendedId(GestionProviderElementos.ELEMENTOS_ESPECIFICO, 0),
                                          cv, null, null );
        String[] values = new String[]{ "papelera", String.valueOf(n.getIdserver())};
        UtilWeb.subirInformacion(c ,  UtilWeb.URL_UPDATE_ANDROID, values);

        return filas;
    }

    @Override
    public long deleteNota(int position) {

        cursor.moveToPosition(position);
        Nota n = Nota.getNota(cursor);

        return this.deleteNota(n);

    }

    @Override
    public Nota getNota(int position) {

        cursor.moveToPosition(position);
        Nota n = Nota.getNota(cursor);

        return n;
    }


    //Eliminar
    @Override
    public long deleteLista(int position) {

        Lista lista = this.getLista(position);
        return this.deleteLista(lista);
    }


    @Override
    public long papeleraLista( Lista l ) {

        UtilNotification.deleteNotificationAlarm( c , l );

        //Modificamos la nota para que vaya a la papelera
        l.setPapelera(1);
        l.setFecha_not("");

        ContentValues cv = l.getContentValues(true);

        long filas      = cr.update( ContentUris.withAppendedId(GestionProviderElementos.ELEMENTOS_ESPECIFICO, 1),
                                     cv, null, null );

        String[] values = new String[]{ "papelera", String.valueOf(l.getIdserver())};
        UtilWeb.subirInformacion(c ,  UtilWeb.URL_UPDATE_ANDROID, values);

        return filas;
    }

    @Override
    public long deleteLista(Lista l) {

        /*return gl.delete(l);*/
        String where    = ContratoBaseDatos.Elementos._ID + " = ?";
        String[] args   = new String[]{l.getId()+""};

        return cr.delete( ContentUris.withAppendedId(GestionProviderElementos.ELEMENTOS_ESPECIFICO, 0), where, args);
    }

    @Override
    public Lista getLista(int position) {

        cursor.moveToPosition(position);
        Lista lista = Lista.getLista(cursor);

        return lista;
    }

    //Papelera
    @Override
    public long recuperarNota(Nota n) {

        //Modificamos la nota para que vaya a la papelera
        n.setPapelera(0);

        ContentValues cv = n.getContentValues(true);

        long filas          = cr.update( ContentUris.withAppendedId(GestionProviderElementos.ELEMENTOS_ESPECIFICO, 0),
                                         cv, null, null );

        String[] values = new String[]{ "recuperar", String.valueOf(n.getIdserver())};
        UtilWeb.subirInformacion(c ,  UtilWeb.URL_UPDATE_ANDROID, values);

        return filas;

    }

    @Override
    public long recuperarNota(int position) {

        Nota n = this.getNota(position);
        return this.recuperarNota(n);

    }

    @Override
    public long recuperarLista(Lista l) {

        //Modificamos la nota para que vaya a la papelera
        l.setPapelera(0);

        ContentValues cv = l.getContentValues(true);

        long filas      = cr.update( ContentUris.withAppendedId(GestionProviderElementos.ELEMENTOS_ESPECIFICO, 1),
                                     cv, null, null );

        String[] values = new String[]{ "recuperar", String.valueOf(l.getIdserver())};
        UtilWeb.subirInformacion(c ,  UtilWeb.URL_UPDATE_ANDROID, values);

        return filas;
    }

    @Override
    public long recuperarLista(int position) {

        Lista l = this.getLista(position);
        return this.recuperarLista(l);
    }

    @Override
    public void vaciarPapelera() {

        //Si no tenemos conexion a internet tenemos que buscar los elementos que tenemos que borrar
        if ( !UtilWeb.getNetworkStatus(c) ) {

            Cursor cursor = cr.query(GestionProviderElementos.ELEMENTOS_PAPELERA,
                                     null, null, null, null);

            ContentValues cv = new ContentValues();

            while( cursor.moveToNext() ) {

                //Insertamos los elementos en la otra tabla
                int tipo = cursor.getInt(cursor.getColumnIndex(ContratoBaseDatos.Elementos.TIPO));

                long id = tipo == 0 ? Nota.getNota(cursor).getId() : Lista.getLista(cursor).getId();
                cv.put(ContratoBaseDatos.Borrados.IDELEMENTO, id);
                cr.insert(GestionProviderNetworkUpdate.URI_BORRADOS, cv);
            }
        }

        //Despues de comprobar eliminamos la informacion
        String where    = ContratoBaseDatos.Elementos.PAPELERA + " = ?";
        String[] args   = new String[]{"1"};

        //Eliminamos los elementos
        cr.delete( ContentUris.withAppendedId(GestionProviderElementos.ELEMENTOS_ESPECIFICO, 0),
                    where, args);

        String[] values = new String[]{ "vaciar"};
        UtilWeb.subirInformacion(c , UtilWeb.URL_UPDATE_ANDROID, values);



    }

    @Override
    public void setCursor(Cursor c) {

        if ( this.cursor != null ) this.cursor.close();
        this.cursor = c;
    }

}