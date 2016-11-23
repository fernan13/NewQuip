package com.example.fernando.proyectodam.contrato;

import android.content.ContentResolver;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

public class ContratoBaseDatos {

    public final static String BASEDATOS    = "quiip.sqlite";
    public final static String[] TABLES     = {
                                                    Elementos.TABLA,
                                                    Media.TABLA,
                                                    Borrados.TABLA,
                                                    Usuarios.TABLA
                                                };
    private ContratoBaseDatos(){
    }


    public static abstract class Elementos implements BaseColumns {

        public static final String TABLA                = "Elementos";
        public static final String TITULO               = "Titulo";
        public static final String CONTENIDO            = "Contenido";
        public static final String TIPO                 = "Tipo";
        public static final String PAPELERA             = "Papelera";
        public static final String FECHA_NOT            = "Notificacion";
        public static final String ACTUALIZAR           = "Actualizar";
        public static final String IDCORREO             = "Correo";
        public static final String IDSERVER             = "Servidor";
        public static final String COLOR                = "Color";
        public static final String ORDEN                = "Orden";

        public static final String[] PROJECTION_ALL     = {
                                                                _ID,
                                                                TITULO,
                                                                CONTENIDO,
                                                                TIPO,
                                                                PAPELERA,
                                                                FECHA_NOT,
                                                                ACTUALIZAR,
                                                                IDCORREO,
                                                                IDSERVER,
                                                                COLOR,
                                                                ORDEN
                                                            };


        public static final String SORT_ORDER_DEFAULT   = ORDEN + "," + Elementos.TABLA + "." + _ID + " asc";

    }

    public static abstract class Media implements BaseColumns {

        public static final String TABLA                = "Media";
        public static final String MIMETYPE             = "MimeType";
        public static final String BLOB                 = "BlobType";
        public static final String IDPADRE              = "ParentId";

        public static final String[] PROJECTION_ALL     = { _ID, MIMETYPE, BLOB, IDPADRE};
        public static final String SORT_ORDER_DEFAULT   = IDPADRE + " desc";
    }

    public static abstract class Borrados implements BaseColumns {

        public static final String TABLA                = "Borrados";
        public static final String IDELEMENTO           = "IdElemento";

        public static final String[] PROJECTION_ALL     = { _ID, IDELEMENTO };
        public static final String SORT_ORDER_DEFAULT   = _ID + " desc";
    }

    public static abstract class Usuarios implements BaseColumns {

        public static final String TABLA                = "Usuarios";
        public static final String CORREO               = "Correo";

        public static final String[] PROJECTION_ALL     = {_ID, CORREO };
        public static final String SORT_ORDER_DEFAULT   = _ID + " desc";
    }

    public static abstract class LocationObject {

        public static final String TABLE_NAME           = "location";
        public static final String FIELD_NAME_ID        = "id";
        public static final String FIELD_NAME_LATITUD   = "latitud";
        public static final String FIELD_NAME_LONGITUD  = "longitud";
        public static final String FIELD_NAME_FECHA     = "fecha";
        public static final String FIELD_NAME_EMAIL     = "email";
        public static final String FIELD_NAME_TITULO    = "titulo";

    }
}