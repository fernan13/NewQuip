package com.example.fernando.proyectodam.gestion;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;
import com.example.fernando.proyectodam.pojo.Nota;

import java.util.Arrays;

import static com.example.fernando.proyectodam.contrato.ContratoBaseDatos.Media.TABLA;


public class GestionNota extends Gestion<Nota> {

    private Context c;

    public GestionNota(Context c) {

        super(c);

        this.c  = c;
    }

    public GestionNota(Context c, boolean write) {

        super(c, write);
    }

    @Override
    public int deleteAll() {

        this.deleteAll(ContratoBaseDatos.Elementos.TABLA);

        return 1;
    }

    public int delete(String condicion, String[] argumentos) {

        return this.delete(ContratoBaseDatos.Elementos.TABLA, condicion, argumentos);
    }

    @Override
    public int delete(Nota objeto) {

        String condicionB       = ContratoBaseDatos.Elementos._ID + " = ?";
        String[] argumentosB    = new String[]{String.valueOf(objeto.getId())};

        //Eliminamos la fila de la tabla elemento
        return this.delete(ContratoBaseDatos.Elementos.TABLA, condicionB, argumentosB);
    }

    public Nota get(long id) {

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

        String[] columns = new String[]{

                ContratoBaseDatos.Elementos.TABLA + "." + ContratoBaseDatos.Elementos._ID,
                ContratoBaseDatos.Elementos.TABLA + "." + ContratoBaseDatos.Elementos.TITULO,
                ContratoBaseDatos.Elementos.TABLA + "." + ContratoBaseDatos.Elementos.CONTENIDO,
                ContratoBaseDatos.Elementos.TABLA + "." + ContratoBaseDatos.Elementos.TIPO,
                ContratoBaseDatos.Elementos.TABLA + "." + ContratoBaseDatos.Elementos.PAPELERA,
                ContratoBaseDatos.Elementos.TABLA + "." + ContratoBaseDatos.Elementos.FECHA_NOT,
                ContratoBaseDatos.Elementos.TABLA + "." + ContratoBaseDatos.Elementos.IDCORREO,
                ContratoBaseDatos.Elementos.TABLA + "." + ContratoBaseDatos.Elementos.IDSERVER,
                ContratoBaseDatos.Elementos.TABLA + "." + ContratoBaseDatos.Elementos.COLOR,
                ContratoBaseDatos.Elementos.TABLA + "." + ContratoBaseDatos.Elementos.ORDEN,
                ContratoBaseDatos.Elementos.TABLA + "." + ContratoBaseDatos.Elementos.LOCALIZACION,
                ContratoBaseDatos.Media.TABLA + "." + ContratoBaseDatos.Media.MIMETYPE,
                ContratoBaseDatos.Media.TABLA + "." + ContratoBaseDatos.Media.BLOB,
                ContratoBaseDatos.Media.TABLA + "." + ContratoBaseDatos.Media.IDPADRE
        };

        String where        = ContratoBaseDatos.Elementos._ID + " = ?";
        String[] parametros = new String[]{ String.valueOf(id) };

        Cursor c = this.getCursor(arbol_elementos, columns, where, parametros, null, null, null);

        if( c != null && c.getCount() > 0 ) {

            c.moveToFirst();
            Nota nota = Nota.getNota(c);
            return nota;
        }

        return null;
    }

    @Override
    public Cursor getCursor(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return this.getCursor(ContratoBaseDatos.Elementos.TABLA, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    @Override
    public long insert(ContentValues objeto) {

        //Id asignado al elemento insertado en la tabla Elementos

        ContentValues nota = new ContentValues();

        nota.put(ContratoBaseDatos.Elementos.TITULO, objeto.getAsString(ContratoBaseDatos.Elementos.TITULO));
        nota.put(ContratoBaseDatos.Elementos.CONTENIDO, objeto.getAsString(ContratoBaseDatos.Elementos.CONTENIDO));
        nota.put(ContratoBaseDatos.Elementos.TIPO, objeto.getAsInteger(ContratoBaseDatos.Elementos.TIPO));
        nota.put(ContratoBaseDatos.Elementos.PAPELERA, objeto.getAsInteger(ContratoBaseDatos.Elementos.PAPELERA));
        nota.put(ContratoBaseDatos.Elementos.FECHA_NOT, objeto.getAsString(ContratoBaseDatos.Elementos.FECHA_NOT));
        nota.put(ContratoBaseDatos.Elementos.ACTUALIZAR, objeto.getAsInteger(ContratoBaseDatos.Elementos.ACTUALIZAR));
        nota.put(ContratoBaseDatos.Elementos.IDCORREO, objeto.getAsInteger(ContratoBaseDatos.Elementos.IDCORREO));
        nota.put(ContratoBaseDatos.Elementos.IDSERVER, objeto.getAsInteger(ContratoBaseDatos.Elementos.IDSERVER));
        nota.put(ContratoBaseDatos.Elementos.COLOR, objeto.getAsInteger(ContratoBaseDatos.Elementos.COLOR));
        nota.put(ContratoBaseDatos.Elementos.ORDEN, objeto.getAsInteger(ContratoBaseDatos.Elementos.ORDEN));
        nota.put(ContratoBaseDatos.Elementos.LOCALIZACION, objeto.getAsString(ContratoBaseDatos.Elementos.LOCALIZACION));

        long idP    = this.insert(ContratoBaseDatos.Elementos.TABLA, nota);

        ContentValues media = new ContentValues();

        media.put( ContratoBaseDatos.Media.MIMETYPE, "image");
        media.put( ContratoBaseDatos.Media.BLOB, objeto.getAsByteArray(ContratoBaseDatos.Media.BLOB));
        media.put( ContratoBaseDatos.Media.IDPADRE, idP);

        this.insert(ContratoBaseDatos.Media.TABLA, media);

        return idP;
    }

    @Override
    public long insert(Nota objeto) {

        return this.insert(objeto.getContentValues());
    }

    @Override
    public int update(ContentValues valores, String condicion, String[] argumentos){

        return this.update(ContratoBaseDatos.Elementos.TABLA, valores, condicion, argumentos);
    }

    @Override
    public int update(Nota objeto) {

        return this.update(objeto.getContentValues(true));
    }

    //Metodo utilizado para la actualizacion a traves del provider
    public int update( ContentValues valores ) {

        long idp                = valores.getAsLong(ContratoBaseDatos.Elementos._ID);
        String condicionA       = ContratoBaseDatos.Media.IDPADRE + " = ?";
        String[] argumentosA    = new String[]{String.valueOf(idp)};

        ContentValues media     = new ContentValues();
        media.put(ContratoBaseDatos.Media.MIMETYPE, "image");
        media.put(ContratoBaseDatos.Media.BLOB, valores.getAsByteArray(ContratoBaseDatos.Media.BLOB));
        media.put(ContratoBaseDatos.Media.IDPADRE, idp);

        this.update(ContratoBaseDatos.Media.TABLA, media, condicionA, argumentosA);

        //Elementos
        String condicionB       = ContratoBaseDatos.Elementos._ID + " = ?";
        String[] argumentosB    = new String[]{String.valueOf(idp)};

        ContentValues nota      = new ContentValues();

        nota.put(ContratoBaseDatos.Elementos.TITULO, valores.getAsString(ContratoBaseDatos.Elementos.TITULO));
        nota.put(ContratoBaseDatos.Elementos.CONTENIDO, valores.getAsString(ContratoBaseDatos.Elementos.CONTENIDO));
        nota.put(ContratoBaseDatos.Elementos.TIPO, valores.getAsInteger(ContratoBaseDatos.Elementos.TIPO));
        nota.put(ContratoBaseDatos.Elementos.PAPELERA, valores.getAsInteger(ContratoBaseDatos.Elementos.PAPELERA));
        nota.put(ContratoBaseDatos.Elementos.FECHA_NOT, valores.getAsString(ContratoBaseDatos.Elementos.FECHA_NOT));
        nota.put(ContratoBaseDatos.Elementos.ACTUALIZAR, valores.getAsInteger(ContratoBaseDatos.Elementos.ACTUALIZAR));
        nota.put(ContratoBaseDatos.Elementos.IDCORREO, valores.getAsInteger(ContratoBaseDatos.Elementos.IDCORREO));
        nota.put(ContratoBaseDatos.Elementos.IDSERVER, valores.getAsInteger(ContratoBaseDatos.Elementos.IDSERVER));
        nota.put(ContratoBaseDatos.Elementos.COLOR, valores.getAsInteger(ContratoBaseDatos.Elementos.COLOR));
        nota.put(ContratoBaseDatos.Elementos.ORDEN, valores.getAsInteger(ContratoBaseDatos.Elementos.ORDEN));
        nota.put(ContratoBaseDatos.Elementos.LOCALIZACION, valores.getAsString(ContratoBaseDatos.Elementos.LOCALIZACION));

        return this.update(ContratoBaseDatos.Elementos.TABLA, nota, condicionB, argumentosB );

    }


}