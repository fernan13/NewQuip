package com.example.fernando.proyectodam.gestion;

import android.content.AsyncQueryHandler;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;
import com.example.fernando.proyectodam.util.Ficheros.UtilFicheros;

import java.util.Arrays;


/**
 * Created by Fernando on 20/10/2016.
 */

public class GestionProviderElementos extends ContentProvider {

    private ContentResolver resolver;

    private GestionNota gn;
    private GestionLista gl;

    //Uris necesarias

    public final static String AUTORIDAD            = "com.example.fernando.proyectodam.gestion";
    public final static Uri URI_BASE                = Uri.parse("content://" + AUTORIDAD);

    public static final Uri URI_CONTENIDO_ELEMENTOS = Uri.parse("content://" + AUTORIDAD + "/" + ContratoBaseDatos.Elementos.TABLA);
    public static final Uri ELEMENTOS_ESPECIFICO    = Uri.parse("content://" + AUTORIDAD + "/" + ContratoBaseDatos.Elementos.TABLA +
                                                      "/" + ContratoBaseDatos.Elementos.TIPO );

    public static final Uri ELEMENTOS_PAPELERA      = Uri.parse("content://" + AUTORIDAD + "/" + ContratoBaseDatos.Elementos.TABLA +
                                                      "/"  + ContratoBaseDatos.Elementos.TIPO + "/" + ContratoBaseDatos.Elementos.PAPELERA  );

    public static final Uri ELEMENTOS_NOTIFICACION  = Uri.parse("content://" + AUTORIDAD + "/" + ContratoBaseDatos.Elementos.TABLA +
                                                                "/" + ContratoBaseDatos.Elementos.FECHA_NOT);

    public static final Uri ELEMENTOS_ACTUALIZAR    = Uri.parse("content://" + AUTORIDAD + "/" + ContratoBaseDatos.Elementos.TABLA +
                                                                "/" + ContratoBaseDatos.Elementos.ACTUALIZAR);

    public static final Uri ELEMENTOS_ID_SERVER     = Uri.parse("content://" + AUTORIDAD + "/" + ContratoBaseDatos.Elementos.TABLA +
                                                                "/" + ContratoBaseDatos.Elementos.IDSERVER);

    //UriMatcher para comparar las URIS que le llegan
    private static final UriMatcher uriMatcher;

    //CONSTANTES INT PARA CADA URI QUE NOS PUEDE LLEGAR
    private static final int ELEMENTOS_MATCHER              = 1;
    private static final int ELEMENTOS_ESPECIFICO_MATCHER   = 2;
    private static final int ELEMENTOS_PAPELERA_MATCHER     = 3;
    private static final int ELEMENTOS_FECHA_NOT            = 4;
    private static final int ELEMENTOS_IDSERVER             = 5;

    private static final int ELEMENTOS_ID_MATCHER           = 6;
    private static final int ELEMENTOS_EL_MATCHER           = 7;
    private static final int MEDIA_MATCHER                  = 8;
    private static final int MEDIA_ID_MATCHER               = 9;
    private static final int ELEMENTOS_MEDIA_MATCHER        = 10;
    private static final int ELEMENTOS_MEDIA_ID_MATCHER     = 11;

    private static final int ELEMENTOS_UPDATE_SERVER        = 12;

    static {

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(  AUTORIDAD, ContratoBaseDatos.Elementos.TABLA, ELEMENTOS_MATCHER);
        uriMatcher.addURI(  AUTORIDAD, ContratoBaseDatos.Elementos.TABLA + "/" +
                            ContratoBaseDatos.Elementos.TIPO + "/#", ELEMENTOS_ESPECIFICO_MATCHER);
        uriMatcher.addURI(  AUTORIDAD, ContratoBaseDatos.Elementos.TABLA + "/" +
                            ContratoBaseDatos.Elementos.TIPO + "/" + ContratoBaseDatos.Elementos.PAPELERA,
                            ELEMENTOS_PAPELERA_MATCHER);
        uriMatcher.addURI(  AUTORIDAD, ContratoBaseDatos.Elementos.TABLA + "/" + ContratoBaseDatos.Elementos.FECHA_NOT,
                            ELEMENTOS_FECHA_NOT);

        uriMatcher.addURI( AUTORIDAD, ContratoBaseDatos.Elementos.TABLA + "/" + ContratoBaseDatos.Elementos.IDSERVER
                + "/#", ELEMENTOS_IDSERVER);

        uriMatcher.addURI(  AUTORIDAD, ContratoBaseDatos.Elementos.TABLA + "/#", ELEMENTOS_ID_MATCHER);
        uriMatcher.addURI(  AUTORIDAD, ContratoBaseDatos.Elementos.TABLA + "/*", ELEMENTOS_EL_MATCHER);
        uriMatcher.addURI(  AUTORIDAD, ContratoBaseDatos.Elementos.TABLA + "/*/" + ContratoBaseDatos.Media.TABLA,
                            ELEMENTOS_MEDIA_MATCHER);
        uriMatcher.addURI(  AUTORIDAD, ContratoBaseDatos.Elementos.TABLA + "/*/" + ContratoBaseDatos.Media.TABLA + "/#",
                            ELEMENTOS_MEDIA_ID_MATCHER);

        uriMatcher.addURI(  AUTORIDAD, ContratoBaseDatos.Media.TABLA, MEDIA_MATCHER);
        uriMatcher.addURI(  AUTORIDAD, ContratoBaseDatos.Media.TABLA + "/#", MEDIA_ID_MATCHER);

        uriMatcher.addURI( AUTORIDAD, ContratoBaseDatos.Elementos.TABLA +
                                      "/" + ContratoBaseDatos.Elementos.ACTUALIZAR, ELEMENTOS_UPDATE_SERVER);
    }

    @Override
    public boolean onCreate() {

        gn          = new GestionNota(getContext());
        gl          = new GestionLista(getContext());
        resolver    = getContext().getContentResolver();

        return true;
    }

    @Override
    public String getType(Uri uri) {

        int type = uriMatcher.match(uri);

        switch ( type ) {

            case ELEMENTOS_MATCHER: {

                return "vnd.android.cursor.dir/vnd.quiip.elementos";
            }

            case ELEMENTOS_ID_MATCHER: {

                return "vnd.android.cursor.item/vnd.quiip.elementos";
            }
            case MEDIA_MATCHER: {

                return "vnd.android.cursor.dir/vnd.quiip.media";
            }

            case MEDIA_ID_MATCHER: {

                return "vnd.android.cursor.item/vnd.quiip.media";
            }
            default: return null;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        String  arbol_elementos =   ContratoBaseDatos.Elementos.TABLA + " inner join " +
                                    ContratoBaseDatos.Usuarios.TABLA + " on " +
                                    ContratoBaseDatos.Elementos.TABLA + "." +
                                    ContratoBaseDatos.Elementos.IDCORREO + " = " +
                                    ContratoBaseDatos.Usuarios.TABLA + "." +
                                    ContratoBaseDatos.Usuarios._ID + " left join " +
                                    ContratoBaseDatos.Media.TABLA + " on " +
                                    ContratoBaseDatos.Elementos.TABLA + "." +
                                    ContratoBaseDatos.Elementos._ID + " = " +
                                    ContratoBaseDatos.Media.TABLA + "." +
                                    ContratoBaseDatos.Media.IDPADRE;

        String[] columns            = new String[]{

                ContratoBaseDatos.Elementos.TABLA + "." + ContratoBaseDatos.Elementos._ID,
                ContratoBaseDatos.Elementos.TABLA + "." + ContratoBaseDatos.Elementos.TITULO,
                ContratoBaseDatos.Elementos.TABLA + "." + ContratoBaseDatos.Elementos.CONTENIDO,
                ContratoBaseDatos.Elementos.TABLA + "." + ContratoBaseDatos.Elementos.TIPO,
                ContratoBaseDatos.Elementos.TABLA + "." + ContratoBaseDatos.Elementos.PAPELERA,
                ContratoBaseDatos.Elementos.TABLA + "." + ContratoBaseDatos.Elementos.FECHA_NOT,
                ContratoBaseDatos.Elementos.TABLA + "." + ContratoBaseDatos.Elementos.ACTUALIZAR,
                ContratoBaseDatos.Elementos.TABLA + "." + ContratoBaseDatos.Elementos.IDCORREO,
                ContratoBaseDatos.Elementos.TABLA + "." + ContratoBaseDatos.Elementos.IDSERVER,
                ContratoBaseDatos.Elementos.TABLA + "." + ContratoBaseDatos.Elementos.COLOR,
                ContratoBaseDatos.Elementos.TABLA + "." + ContratoBaseDatos.Elementos.ORDEN,
                ContratoBaseDatos.Media.TABLA + "." + ContratoBaseDatos.Media.MIMETYPE,
                ContratoBaseDatos.Media.TABLA + "." + ContratoBaseDatos.Media.BLOB,
                ContratoBaseDatos.Media.TABLA + "." + ContratoBaseDatos.Media.IDPADRE
        };

        String idcorreo     = UtilFicheros.getValueFromSharedPreferences(getContext(), "usuario", "id_email");
        sortOrder           = ContratoBaseDatos.Elementos.SORT_ORDER_DEFAULT;

        switch( uriMatcher.match(uri) ) {

            case ELEMENTOS_MATCHER: {

                if ( selection == null && selectionArgs == null ) {


                    selection       = ContratoBaseDatos.Elementos.PAPELERA + " = ? AND " +
                                      ContratoBaseDatos.Elementos.TABLA + "." +
                                      ContratoBaseDatos.Elementos.IDCORREO + " = ?";
                    selectionArgs   = new String[]{"0", idcorreo};

                }

                break;
            }

            case ELEMENTOS_ESPECIFICO_MATCHER: {

                if ( selection == null && selectionArgs == null ) {


                    selection       =   ContratoBaseDatos.Elementos.TIPO + " = ? AND " +
                                        ContratoBaseDatos.Elementos.PAPELERA + " = ? AND " +
                                        ContratoBaseDatos.Elementos.TABLA + "." +
                                        ContratoBaseDatos.Elementos.IDCORREO + " = ?";

                    selectionArgs   =   new String[]{uri.getLastPathSegment(),"0", idcorreo};

                }


                break;
            }

            case ELEMENTOS_FECHA_NOT: {

                if ( selection == null && selectionArgs == null ) {


                    selection       = ContratoBaseDatos.Elementos.FECHA_NOT + " != ? AND " +
                                      ContratoBaseDatos.Elementos.TABLA + "." +
                                      ContratoBaseDatos.Elementos.IDCORREO + " = ?";

                    selectionArgs   = new String[]{"", idcorreo};

                    sortOrder       = ContratoBaseDatos.Elementos.FECHA_NOT + " asc";

                }


                break;
            }

            case ELEMENTOS_PAPELERA_MATCHER: {


                selection       =   ContratoBaseDatos.Elementos.PAPELERA + " = ? AND " +
                                    ContratoBaseDatos.Elementos.TABLA + "." +
                                    ContratoBaseDatos.Elementos.IDCORREO + " = ?";

                selectionArgs   =   new String[]{"1", idcorreo};



                break;
            }

            case ELEMENTOS_ID_MATCHER : {

                selection       = ContratoBaseDatos.Elementos.TABLA + "." +
                                  ContratoBaseDatos.Elementos._ID + " = ? AND " +
                                  ContratoBaseDatos.Elementos.TABLA + "." +
                                  ContratoBaseDatos.Elementos.IDCORREO + " = ?";

                selectionArgs   = new String[]{uri.getLastPathSegment(), idcorreo};
                break;
            }

            case ELEMENTOS_IDSERVER : {

                selection       = ContratoBaseDatos.Elementos.TABLA + "." +
                                  ContratoBaseDatos.Elementos.IDSERVER + " = ? AND " +
                                  ContratoBaseDatos.Elementos.TABLA + "." +
                                  ContratoBaseDatos.Elementos.IDCORREO + " = ?";

                selectionArgs   = new String[]{uri.getLastPathSegment(), idcorreo};

                break;
            }

            case ELEMENTOS_UPDATE_SERVER : {

                selection       = ContratoBaseDatos.Elementos.TABLA + "." +
                                  ContratoBaseDatos.Elementos.ACTUALIZAR + " = ? AND " +
                                  ContratoBaseDatos.Elementos.TABLA + "." +
                                  ContratoBaseDatos.Elementos.IDCORREO + " = ?";

                selectionArgs   = new String[]{uri.getLastPathSegment(), idcorreo};

                break;
            }
        }

        return gn.getCursor( arbol_elementos, columns, selection, selectionArgs, null, null, sortOrder);

    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {

        int type    = uriMatcher.match(uri);

        Uri newUri  = null;

        switch ( type ) {

            case ELEMENTOS_ESPECIFICO_MATCHER: {

                long id;

                if ( uri.getLastPathSegment().equals("0") ){

                    id = gn.insert(values);
                }
                else
                {
                    id = gl.insert(values);
                }

                newUri  = ContentUris.withAppendedId( URI_CONTENIDO_ELEMENTOS, id);

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

            case ELEMENTOS_ESPECIFICO_MATCHER: {

                if ( uri.getLastPathSegment().equals("0") ){

                    fila = gn.delete(ContratoBaseDatos.Elementos.TABLA, selection, selectionArgs);
                }
                else
                {
                    fila = gl.delete(ContratoBaseDatos.Elementos.TABLA, selection, selectionArgs);
                }
            }

            case ELEMENTOS_ID_MATCHER: {

                selection       = ContratoBaseDatos.Elementos._ID + " = ?";
                selectionArgs   = new String[]{uri.getLastPathSegment()};

                gn.delete(ContratoBaseDatos.Elementos.TABLA, selection, selectionArgs);
            }

            case ELEMENTOS_IDSERVER: {

                selection       = ContratoBaseDatos.Elementos.IDSERVER + " = ?";
                selectionArgs   = new String[]{uri.getLastPathSegment()};

                gn.delete(ContratoBaseDatos.Elementos.TABLA, selection, selectionArgs);
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

            case ELEMENTOS_ESPECIFICO_MATCHER: {

                if ( uri.getLastPathSegment().equals("0") ) {

                    fila = gn.update(values);
                }
                else
                {
                    long id = values.getAsLong(ContratoBaseDatos.Elementos._ID);

                    selection       = ContratoBaseDatos.Elementos._ID + " = ?";
                    selectionArgs   = new String[]{String.valueOf(id)};

                    fila = gl.update(values,selection,selectionArgs);
                }

                break;
            }

        }


        resolver.notifyChange( uri, null);

        return fila;
    }

}
