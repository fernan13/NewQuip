package com.example.fernando.proyectodam.basedatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;

public class Ayudante extends SQLiteOpenHelper {

    //sqlite
    //tipos de datos https://www.sqlite.org/datatype3.html
    //fechas https://www.sqlite.org/lang_datefunc.html
    //trigger https://www.sqlite.org/lang_createtrigger.html

    private static final int VERSION = 5;
    private static final String ele =  "create table if not exists " + ContratoBaseDatos.Elementos.TABLA +
                                        " (" +
                                            ContratoBaseDatos.Elementos._ID + " integer primary key autoincrement , " +
                                            ContratoBaseDatos.Elementos.TITULO + " text, " +
                                            ContratoBaseDatos.Elementos.CONTENIDO + " text, " +
                                            ContratoBaseDatos.Elementos.TIPO + " integer, " +
                                            ContratoBaseDatos.Elementos.PAPELERA + " integer, " +
                                            ContratoBaseDatos.Elementos.FECHA_NOT + " text," +
                                            ContratoBaseDatos.Elementos.ACTUALIZAR + " integer, " +
                                            ContratoBaseDatos.Elementos.IDCORREO + " integer, " +
                                            ContratoBaseDatos.Elementos.IDSERVER + " integer, " +
                                            ContratoBaseDatos.Elementos.COLOR + " integer, " +
                                            ContratoBaseDatos.Elementos.ORDEN + " integer, " +
                                            ContratoBaseDatos.Elementos.LOCALIZACION + " text " +
                                        ")";


    private static final String eleT =   "create table if not exists temp_" + ContratoBaseDatos.Elementos.TABLA +
                                        " (" +
                                            ContratoBaseDatos.Elementos._ID + " integer primary key autoincrement , " +
                                            ContratoBaseDatos.Elementos.TITULO + " text, " +
                                            ContratoBaseDatos.Elementos.CONTENIDO + " text, " +
                                            ContratoBaseDatos.Elementos.TIPO + " integer, " +
                                            ContratoBaseDatos.Elementos.PAPELERA + " integer, " +
                                            ContratoBaseDatos.Elementos.FECHA_NOT + " text," +
                                            ContratoBaseDatos.Elementos.ACTUALIZAR + " integer, " +
                                            ContratoBaseDatos.Elementos.IDCORREO + " integer, " +
                                            ContratoBaseDatos.Elementos.IDSERVER + " integer, " +
                                            ContratoBaseDatos.Elementos.COLOR + " integer, " +
                                            ContratoBaseDatos.Elementos.ORDEN + " integer, " +
                                            ContratoBaseDatos.Elementos.LOCALIZACION + " text " +
                                        ")";

    private static final String media = "create table if not exists " + ContratoBaseDatos.Media.TABLA +
                                        " (" +
                                            ContratoBaseDatos.Media._ID + " integer primary key autoincrement , " +
                                            ContratoBaseDatos.Media.MIMETYPE + " text, " +
                                            ContratoBaseDatos.Media.BLOB + " blob, " +
                                            ContratoBaseDatos.Media.IDPADRE + " integer" +
                                        ")";


    private static final String mediaT = "create table if not exists temp_" + ContratoBaseDatos.Media.TABLA +
                                        " (" +
                                        ContratoBaseDatos.Media._ID + " integer primary key autoincrement , " +
                                        ContratoBaseDatos.Media.MIMETYPE + " text, " +
                                        ContratoBaseDatos.Media.BLOB + " blob, " +
                                        ContratoBaseDatos.Media.IDPADRE + " integer" +
                                        ")";

    private static final String trig =  "CREATE TRIGGER borradoMedia AFTER DELETE ON " +
                                        ContratoBaseDatos.Elementos.TABLA + " FOR EACH ROW BEGIN DELETE FROM " +
                                        ContratoBaseDatos.Media.TABLA + " WHERE " + ContratoBaseDatos.Media.TABLA +
                                        "." + ContratoBaseDatos.Media.IDPADRE + " = OLD." +
                                        ContratoBaseDatos.Elementos._ID + "; END";

    private static final String borr =  "create table if not exists " + ContratoBaseDatos.Borrados.TABLA +
                                        " (" +
                                            ContratoBaseDatos.Borrados._ID + " integer primary key autoincrement, " +
                                            ContratoBaseDatos.Borrados.IDELEMENTO + " integer"+
                                        ")";

    private static final String borrT =  "create table if not exists temp_" + ContratoBaseDatos.Borrados.TABLA +
                                        " (" +
                                        ContratoBaseDatos.Borrados._ID + " integer primary key autoincrement, " +
                                        ContratoBaseDatos.Borrados.IDELEMENTO + " integer"+
                                        ")";

    private static final String usu =  "create table if not exists " + ContratoBaseDatos.Usuarios.TABLA +
                                        " (" +
                                            ContratoBaseDatos.Usuarios._ID + " integer primary key autoincrement, " +
                                            ContratoBaseDatos.Usuarios.CORREO + " text" +
                                        ")";

    private static final String usuT =  "create table if not exists temp_" + ContratoBaseDatos.Usuarios.TABLA +
                                        " (" +
                                        ContratoBaseDatos.Usuarios._ID + " integer primary key autoincrement, " +
                                        ContratoBaseDatos.Usuarios.CORREO + " text" +
                                        ")";

    public Ayudante(Context context) {

        super(context, ContratoBaseDatos.BASEDATOS, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(ele);
        db.execSQL(media);
        db.execSQL(trig);
        db.execSQL(borr);
        db.execSQL(usu);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        //En primer lugar debemos de crear las tablas temporales para volcar la informacion
        for ( String table : ContratoBaseDatos.TABLES ) {

            Cursor c            = db.rawQuery("select * from " + table, null );
            //String[] columns    = null;
            String sql          = null;
            String sqlT         = null;

            switch (table ) {

                case ContratoBaseDatos.Elementos.TABLA : {

                    sql     = ele;
                    sqlT    = eleT;

                    break;
                }

                case ContratoBaseDatos.Media.TABLA : {

                    sql     = media;
                    sqlT    = mediaT;

                    break;
                }

                case ContratoBaseDatos.Borrados.TABLA : {

                    sql     = borr;
                    sqlT    = borrT;

                    break;
                }

                case ContratoBaseDatos.Usuarios.TABLA : {

                    sql     = usu;
                    sqlT    = usuT;

                    break;
                }
            }

            //Crear tabla temporal y almacenar la informacion
            db.execSQL(sqlT);

            //Volcamos la informacion en la tabla temporal
            while ( c.moveToNext() ) {

                ContentValues cv = new ContentValues();

                //Metodo utilizado para convertir volcar una fila en un contentvalues
                DatabaseUtils.cursorRowToContentValues(c, cv);
                db.insert( "temp_" + table, null, cv);
            }

            //Eliminamos la taba
            c.close();
            db.execSQL("drop table if exists " + table );

            //Cargamos la informacion almacenada
            Cursor cT = db.rawQuery("select * from temp_" + table, null);

            //Creamos la tabla
            db.execSQL(sql);

            //Volcamos la informacion
            while ( cT.moveToNext() ) {

                ContentValues cv = new ContentValues();

                //Metodo utilizado para convertir volcar una fila en un contentvalues
                DatabaseUtils.cursorRowToContentValues(cT, cv);

                db.insert( table, null, cv);
            }

            //Eliminamos la tabla temporal
            cT.close();
            db.execSQL("drop table if exists temp_" + table );
        }
    }

}