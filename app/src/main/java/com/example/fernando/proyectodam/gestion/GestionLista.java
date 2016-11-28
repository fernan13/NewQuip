package com.example.fernando.proyectodam.gestion;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;
import com.example.fernando.proyectodam.pojo.Lista;

import java.util.Arrays;

import static com.example.fernando.proyectodam.contrato.ContratoBaseDatos.Media.TABLA;

/**
 * Created by Fernando on 08/10/2016.
 */

public class GestionLista extends Gestion<Lista> {

    private Context c;

    public GestionLista(Context c) {

        super(c);

        this.c  = c;
    }

    public GestionLista(Context c, boolean write) {

        super(c, write);
    }

    @Override
    public long insert(Lista objeto) {

        return this.insert(objeto.getContentValues());

    }

    @Override
    public long insert(ContentValues objeto) {


        return this.insert(ContratoBaseDatos.Elementos.TABLA, objeto);
    }

    @Override
    public int deleteAll() {

        this.deleteAll(ContratoBaseDatos.Elementos.TABLA);

        return 1;
    }


    @Override
    public int delete(Lista objeto) {

        //Eliminamos en primer lugar en la tabla arbol y en segundo lugar en la de elementos
        String condicionB       = ContratoBaseDatos.Elementos._ID + " = ?";
        String[] argumentosB    = new String[]{String.valueOf(objeto.getId())};

        return this.delete( ContratoBaseDatos.Elementos.TABLA, condicionB, argumentosB);

    }

    @Override
    public int update(Lista objeto) {

        ContentValues valores = objeto.getContentValues();

        String condicionB       = ContratoBaseDatos.Elementos._ID + " = ?";
        String[] argumentosB    = new String[]{String.valueOf(objeto.getId())};

        return this.update( ContratoBaseDatos.Elementos.TABLA, valores, condicionB, argumentosB );
    }


    @Override
    public int update(ContentValues valores, String condicion, String[] argumentos) {

        return this.update(ContratoBaseDatos.Elementos.TABLA, valores, condicion, argumentos);
    }

    @Override
    public Cursor getCursor(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return null;
    }

    public Lista get(long id) {

        String  arbol_elementos = ContratoBaseDatos.Elementos.TABLA + " inner join " +
                                  ContratoBaseDatos.Usuarios.TABLA + " on " +
                                  ContratoBaseDatos.Elementos.TABLA + "." +
                                  ContratoBaseDatos.Elementos.IDCORREO + " = " +
                                  ContratoBaseDatos.Usuarios.TABLA + "." +
                                  ContratoBaseDatos.Usuarios._ID;

        String[] columns = new String[]{

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
                ContratoBaseDatos.Elementos.TABLA + "." + ContratoBaseDatos.Elementos.LOCALIZACION,

        };

        String where        = ContratoBaseDatos.Elementos._ID + " = ?";
        String[] parametros = new String[]{ String.valueOf(id) };

        Cursor c = this.getCursor( arbol_elementos, columns, where, parametros, null, null, null);

        if( c != null && c.getCount() > 0 ) {

            c.moveToFirst();
            Lista lista = Lista.getLista(c);

            return lista;
        }

        return null;
    }


}
