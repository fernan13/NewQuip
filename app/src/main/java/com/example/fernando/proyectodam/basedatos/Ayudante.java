package com.example.fernando.proyectodam.basedatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;

public class Ayudante extends SQLiteOpenHelper {

    //sqlite
    //tipos de datos https://www.sqlite.org/datatype3.html
    //fechas https://www.sqlite.org/lang_datefunc.html
    //trigger https://www.sqlite.org/lang_createtrigger.html

    private static final int VERSION = 1;

    public Ayudante(Context context) {

        super(context, ContratoBaseDatos.BASEDATOS, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sqlA;
        sqlA="create table if not exists " + ContratoBaseDatos.Elementos.TABLA +
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
                ContratoBaseDatos.Elementos.COLOR + " integer " +
                ")";

        String sqlB;
        sqlB="create table if not exists " + ContratoBaseDatos.Media.TABLA +
                " (" +
                ContratoBaseDatos.Media._ID + " integer primary key autoincrement , " +
                ContratoBaseDatos.Media.MIMETYPE + " text, " +
                ContratoBaseDatos.Media.BLOB + " blob, " +
                ContratoBaseDatos.Media.IDPADRE + " integer" +
                ")";

        //Trigger utilizado para la eliminacion de los archivos multimeda asociados a las notas
        String sqlC;
        sqlC=   "CREATE TRIGGER borradoMedia AFTER DELETE ON " + ContratoBaseDatos.Elementos.TABLA +
                " FOR EACH ROW BEGIN DELETE FROM " + ContratoBaseDatos.Media.TABLA + " WHERE " +
                ContratoBaseDatos.Media.TABLA + "." + ContratoBaseDatos.Media.IDPADRE + " = OLD." +
                ContratoBaseDatos.Elementos._ID + "; END";

        //Tabla utilizada para almacenar los elementos vaciados de la papelera si no hay conexion
        String sqlD;
        sqlD=   "create table if not exists " + ContratoBaseDatos.Borrados.TABLA +
                " (" +
                ContratoBaseDatos.Borrados._ID + " integer primary key autoincrement, " +
                ContratoBaseDatos.Borrados.IDELEMENTO + " integer"+
                ")";

        String sqlE;
        sqlE=   "create table if not exists " + ContratoBaseDatos.Usuarios.TABLA +
                " (" +
                ContratoBaseDatos.Usuarios._ID + " integer primary key autoincrement, " +
                ContratoBaseDatos.Usuarios.CORREO + " text " +
                ")";

        db.execSQL(sqlA);
        db.execSQL(sqlB);
        db.execSQL(sqlC);
        db.execSQL(sqlD);
        db.execSQL(sqlE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sqlE = "drop table if exists " + ContratoBaseDatos.Elementos.TABLA;
        String sqlM = "drop table if exists " + ContratoBaseDatos.Media.TABLA;
        String sqlB = "drop table if exists " + ContratoBaseDatos.Borrados.TABLA;

        db.execSQL(sqlE);
        db.execSQL(sqlM);
        db.execSQL(sqlB);
    }
}