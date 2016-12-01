package com.example.fernando.proyectodam.gestion;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;

/**
 * Created by Fernando on 04/11/2016.
 */

public class GestionProviderNetworkUpdate extends ContentProvider {

    private ContentResolver resolver;
    private GestionBorrados gb;

    //Uris necesarias

    public final static String AUTORIDAD            = "com.example.fernando.proyectodam.gestion2";
    public final static Uri URI_BASE                = Uri.parse("content://" + AUTORIDAD);

    public static final Uri URI_BORRADOS            = URI_BASE.buildUpon().appendPath(ContratoBaseDatos.Borrados.TABLA).build();


    //UriMatcher para comparar las URIS que le llegan
    private static final UriMatcher uriMatcher;

    //CONSTANTES INT PARA CADA URI QUE NOS PUEDE LLEGAR
    private static final int BORRADOS       = 1;
    private static final int BORRADOS_ID    = 2;
    static {

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(  AUTORIDAD, ContratoBaseDatos.Borrados.TABLA, BORRADOS);
        uriMatcher.addURI(  AUTORIDAD, ContratoBaseDatos.Borrados.TABLA + "/#", BORRADOS);
    }

    @Override
    public boolean onCreate() {

        gb          = new GestionBorrados(getContext());
        resolver    = getContext().getContentResolver();

        return true;
    }

    @Override
    public String getType(Uri uri) {

        int type = uriMatcher.match(uri);

        switch ( type ) {

            case BORRADOS: {

                return "vnd.android.cursor.dir/vnd.quiip.borrados";
            }

            case BORRADOS_ID: {

                return "vnd.android.cursor.item/vnd.quiip.borrados";
            }
            default: return null;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        String  tabla       = ContratoBaseDatos.Borrados.TABLA;
        String[] columns    = ContratoBaseDatos.Borrados.PROJECTION_ALL;

        switch( uriMatcher.match(uri) ) {

            case BORRADOS: {

                selection       = null;
                selectionArgs   = null;

                break;
            }


        }

        return gb.getCursor( tabla, columns, selection, selectionArgs, null, null, sortOrder);

    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {

        int type    = uriMatcher.match(uri);

        Uri newUri  = null;

        switch ( type ) {

            case BORRADOS: {

                long id = gb.insert(values);
                newUri  = ContentUris.withAppendedId( uri, id);

                break;
            }


        }

        resolver.notifyChange( newUri, null);

        return newUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int type    = uriMatcher.match(uri);

        int fila    = 0;

        switch ( type ) {

            case BORRADOS: {

                fila = gb.deleteAll();

                break;
            }

        }


        resolver.notifyChange( uri, null);

        return fila;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int type    = uriMatcher.match(uri);
        int fila    = 0;

        switch ( type ) {

            case BORRADOS: {

                fila = gb.update(ContratoBaseDatos.Borrados.TABLA, values, selection, selectionArgs);
            }

        }


        resolver.notifyChange( uri, null);

        return fila;
    }

}
