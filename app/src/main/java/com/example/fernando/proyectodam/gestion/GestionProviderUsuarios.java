package com.example.fernando.proyectodam.gestion;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;

/**
 * Created by Fernando on 06/11/2016.
 */

public class GestionProviderUsuarios extends ContentProvider {

    private ContentResolver resolver;
    private GestionUsuarios gu;

    //Uris necesarias

    public final static String AUTORIDAD            = "com.example.fernando.proyectodam.gestion3";
    public final static Uri URI_BASE                = Uri.parse("content://" + AUTORIDAD);

    public static final Uri URI_USUARIOS            = URI_BASE.buildUpon().appendPath(ContratoBaseDatos.Usuarios.TABLA).build();


    //UriMatcher para comparar las URIS que le llegan
    private static final UriMatcher uriMatcher;

    //CONSTANTES INT PARA CADA URI QUE NOS PUEDE LLEGAR
    private static final int USUARIOS           = 1;
    private static final int USUARIOS_ID        = 2;
    private static final int USUARIOS_CORREO    = 3;

    static {

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(  AUTORIDAD, ContratoBaseDatos.Usuarios.TABLA, USUARIOS);
        uriMatcher.addURI(  AUTORIDAD, ContratoBaseDatos.Usuarios.TABLA + "/#", USUARIOS_ID);
        uriMatcher.addURI(  AUTORIDAD, ContratoBaseDatos.Usuarios.TABLA + "/*", USUARIOS_CORREO);
    }

    @Override
    public boolean onCreate() {

        gu          = new GestionUsuarios(getContext());
        resolver    = getContext().getContentResolver();

        return true;
    }

    @Override
    public String getType(Uri uri) {

        int type = uriMatcher.match(uri);

        switch ( type ) {

            case USUARIOS: {

                return "vnd.android.cursor.dir/vnd.quiip.usuarios";
            }

            case USUARIOS_CORREO: {

                return "vnd.android.cursor.item/vnd.quiip.usuarios";
            }
            default: return null;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        String  tabla       = ContratoBaseDatos.Usuarios.TABLA;
        String[] columns    = ContratoBaseDatos.Usuarios.PROJECTION_ALL;

        switch( uriMatcher.match(uri) ) {

            case USUARIOS_CORREO: {

                selection       = ContratoBaseDatos.Usuarios.CORREO + " = ?";
                selectionArgs   = new String[]{uri.getLastPathSegment()};
                break;
            }

        }

        return gu.getCursor( tabla, columns, selection, selectionArgs, null, null, sortOrder);

    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {

        int type    = uriMatcher.match(uri);

        Uri newUri  = null;

        switch ( type ) {

            case USUARIOS: {

                long id = gu.insert(values);
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

            case USUARIOS: {

                fila = gu.deleteAll();

                break;
            }

            case USUARIOS_CORREO: {

                selection       = ContratoBaseDatos.Usuarios._ID + " = ?";
                selectionArgs   = new String[]{uri.getLastPathSegment()};

                fila            = gu.delete(ContratoBaseDatos.Usuarios.TABLA, selection, selectionArgs);
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

            case USUARIOS_CORREO: {

                fila = gu.update(ContratoBaseDatos.Borrados.TABLA, values, selection, selectionArgs);
            }

        }


        resolver.notifyChange( uri, null);

        return fila;
    }

}
