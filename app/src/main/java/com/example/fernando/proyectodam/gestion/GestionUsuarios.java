package com.example.fernando.proyectodam.gestion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;

/**
 * Created by Fernando on 06/11/2016.
 */

public class GestionUsuarios extends Gestion {

    private Context c;

    public GestionUsuarios(Context c) {

        super(c);

        this.c  = c;
    }

    public GestionUsuarios(Context c, boolean write) {

        super(c, write);
    }
    @Override
    public long insert(Object objeto) {
        return 0;
    }

    @Override
    public long insert(ContentValues objeto) {
        return this.insert(ContratoBaseDatos.Usuarios.TABLA, objeto);
    }

    @Override
    public int deleteAll() {
        return this.deleteAll(ContratoBaseDatos.Usuarios.TABLA);
    }

    @Override
    public int delete(Object objeto) {
        return 0;
    }

    @Override
    public int update(Object objeto) {
        return 0;
    }

    @Override
    public int update(ContentValues valores, String condicion, String[] argumentos) {
        return 0;
    }

    @Override
    public Cursor getCursor(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return getCursor(columns, selection,selectionArgs, groupBy, having, orderBy);
    }
}
