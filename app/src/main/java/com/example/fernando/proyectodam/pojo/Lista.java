package com.example.fernando.proyectodam.pojo;

import android.content.ContentValues;
import android.database.Cursor;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.fernando.proyectodam.BR;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Fernando on 07/10/2016.
 */

public class Lista extends BaseObservable implements Parcelable {

    private long id;
    private String titulo;
    private ArrayList<ElementoLista> items;
    private int papelera,tipo, actualizar, correo, idserver, color, orden;
    private String fecha_not;
    private String map_not;
    private String place;

    //Elementos utilizados para convertir y extraer arraylist a string
    private static final Gson GSON = new GsonBuilder().create();
    private static final Type TYPE = new TypeToken<ArrayList<ElementoLista>>(){}.getType();

    public Lista() {

        this( 0, "", new ArrayList<ElementoLista>(), 0, 1, 0, 0, 0, Color.parseColor("#FFFFFF"), 0, "", "");
    }

    public Lista ( long id, String titulo, ArrayList<ElementoLista> items, int papelera, int tipo,
                   int actualizar, int correo, int idserver, int color, int orden, String fecha_not,
                   String map_not ) {

        this.id         = id;
        this.titulo     = titulo;
        this.items      = items;
        this.papelera   = papelera;
        this.tipo       = tipo;
        this.actualizar = actualizar;
        this.correo     = correo;
        this.idserver   = idserver;
        this.color      = color;
        this.orden      = orden;
        this.fecha_not  = fecha_not;
        this.map_not    = map_not;
    }

    public Lista ( long id, String titulo, String json, int papelera, int tipo , int actualizar,
                   int correo, int idserver, int color, int orden, String fecha_not, String map_not ) {

        this.id         = id;
        this.titulo     = titulo;
        this.items      = GSON.fromJson( json, TYPE );
        this.papelera   = papelera;
        this.tipo       = tipo;
        this.actualizar = actualizar;
        this.correo     = correo;
        this.idserver   = idserver;
        this.color      = color;
        this.orden      = orden;
        this.fecha_not  = fecha_not;
        this.map_not    = map_not;
    }


    //Setter & Getter

    protected Lista(Parcel in) {

        id          = in.readLong();
        titulo      = in.readString();
        items       = in.createTypedArrayList(ElementoLista.CREATOR);
        papelera    = in.readInt();
        tipo        = in.readInt();
        actualizar  = in.readInt();
        correo      = in.readInt();
        idserver    = in.readInt();
        color       = in.readInt();
        orden       = in.readInt();
        fecha_not   = in.readString();
        map_not     = in.readString();
    }

    public static final Creator<Lista> CREATOR = new Creator<Lista>() {
        @Override
        public Lista createFromParcel(Parcel in) {
            return new Lista(in);
        }

        @Override
        public Lista[] newArray(int size) {
            return new Lista[size];
        }
    };

    @Bindable
    public long getId() {
        return id;
    }

    public void setId(long id) {

        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public String getTitulo() {

        return titulo;
    }

    public void setTitulo(String titulo) {

        this.titulo = titulo;
        notifyPropertyChanged(BR.titulo);
    }

    @Bindable
    public ArrayList<ElementoLista> getItems() {
        return items;
    }

    @Bindable
    public void setItems(ArrayList<ElementoLista> items) {

        this.items = items;
        notifyPropertyChanged(BR.items);
    }

    @Bindable
    public int getPapelera(){

        return this.papelera;
    }

    public void setPapelera( int papelera ) {

        this.papelera = papelera;
        notifyPropertyChanged(BR.papelera);
    }

    @Bindable
    public int getActualizar() {

        return actualizar;
    }

    public void setActualizar(int actualizar) {

        this.actualizar = actualizar;
        notifyPropertyChanged(BR.actualizar);

    }

    @Bindable
    public long getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {

        this.tipo = tipo;
        notifyPropertyChanged(BR.tipo);
    }

    @Bindable
    public String getFecha_not(){

        return this.fecha_not;
    }

    public void setFecha_not(String fecha_not) {

        this.fecha_not = fecha_not;
        notifyPropertyChanged(BR.fecha_not);
    }

    @Bindable
    public int getCorreo() {
        return correo;
    }

    public void setCorreo(int correo) {

        this.correo = correo;
        notifyPropertyChanged(BR.correo);
    }

    @Bindable
    public int getIdserver(){

        return this.idserver;
    }

    public void setIdserver( int idserver ) {

        this.idserver = idserver;
        notifyPropertyChanged(BR.idserver);
    }

    @Bindable
    public int getColor() {
        return color;
    }

    public void setColor( int color) {

        this.color = color;
        notifyPropertyChanged(BR.color);
    }

    @Bindable
    public int getOrden(){

        return this.orden;
    }

    public void setOrden( int orden ) {

        this.orden = orden;
        notifyPropertyChanged(BR.orden);
    }

    @Bindable
    public String getMap_not(){

        return this.map_not;
    }

    public void setMap_not( String map_not) {

        this.map_not = map_not;
        notifyPropertyChanged(BR.map_not);
    }

    @Bindable
    public String getPlace(){

        return this.place;
    }

    public void setPlace( String place) {

        this.place = place;
        notifyPropertyChanged(BR.place);
    }

    @Override
    public String toString() {
        return "Lista{" +
                "actualizar=" + actualizar +
                ", id=" + id +
                ", titulo='" + titulo + '\'' +
                ", items=" + items +
                ", papelera=" + papelera +
                ", tipo=" + tipo +
                ", correo=" + correo +
                ", idserver=" + idserver +
                ", fecha_not='" + fecha_not + '\'' +
                '}';
    }

    public static Lista getLista(Cursor c ) {

        long id         = c.getLong(c.getColumnIndex(ContratoBaseDatos.Elementos._ID));
        String titulo   = c.getString(c.getColumnIndex(ContratoBaseDatos.Elementos.TITULO));
        String json     = c.getString(c.getColumnIndex(ContratoBaseDatos.Elementos.CONTENIDO));
        int papelera    = c.getInt(c.getColumnIndex(ContratoBaseDatos.Elementos.PAPELERA));
        int tipo        = c.getInt(c.getColumnIndex(ContratoBaseDatos.Elementos.TIPO));
        int actualizar  = c.getInt(c.getColumnIndex(ContratoBaseDatos.Elementos.ACTUALIZAR));
        int correo      = c.getInt(c.getColumnIndex(ContratoBaseDatos.Elementos.IDCORREO));
        int idserver    = c.getInt(c.getColumnIndex(ContratoBaseDatos.Elementos.IDSERVER));
        String fecha    = c.getString((c.getColumnIndex(ContratoBaseDatos.Elementos.FECHA_NOT)));
        int color       = c.getInt(c.getColumnIndex(ContratoBaseDatos.Elementos.COLOR));
        int orden       = c.getInt(c.getColumnIndex(ContratoBaseDatos.Elementos.ORDEN));
        String  map     = c.getString(c.getColumnIndex(ContratoBaseDatos.Elementos.LOCALIZACION));

        return new Lista( id, titulo, json, papelera, tipo, actualizar, correo, idserver, color, orden, fecha, map );
    }

    public static Lista getLista(JSONObject o) {

        try
        {
            long id         = 0;
            String titulo   = o.getString(ContratoBaseDatos.Elementos.TITULO);
            String json     = o.getString(ContratoBaseDatos.Elementos.CONTENIDO);
            int papelera    = o.getInt(ContratoBaseDatos.Elementos.PAPELERA);
            int tipo        = o.getInt(ContratoBaseDatos.Elementos.TIPO);
            String fecha    = o.getString(ContratoBaseDatos.Elementos.FECHA_NOT);
            String map      = o.getString(ContratoBaseDatos.Elementos.LOCALIZACION);
            int correo      = 0;
            int idserver    = o.getInt(ContratoBaseDatos.Elementos._ID);
            int actualizar  = 1;
            int color       = o.getInt(ContratoBaseDatos.Elementos.COLOR);
            int orden       = o.getInt(ContratoBaseDatos.Elementos.ORDEN);

            if ( color == 0 ) color = Color.parseColor("#FFFFFF");
            return new Lista( id, titulo, json, papelera, tipo, actualizar, correo, idserver,
                              color, orden, fecha, map );

        }
        catch( JSONException e ){}

        return new Lista();
    }

    public ContentValues getContentValues() {

        return this.getContentValues(false);
    }

    public ContentValues getContentValues(boolean withId){

        ContentValues valores = new ContentValues();

        if(withId){

            valores.put(ContratoBaseDatos.Elementos._ID, this.getId());
        }

        valores.put(ContratoBaseDatos.Elementos.TITULO, this.getTitulo());
        valores.put(ContratoBaseDatos.Elementos.CONTENIDO, GSON.toJson(items));
        valores.put(ContratoBaseDatos.Elementos.TIPO, this.getTipo());
        valores.put(ContratoBaseDatos.Elementos.PAPELERA, this.getPapelera());
        valores.put(ContratoBaseDatos.Elementos.FECHA_NOT, this.getFecha_not());
        valores.put(ContratoBaseDatos.Elementos.ACTUALIZAR, this.getActualizar());
        valores.put(ContratoBaseDatos.Elementos.IDCORREO, this.getCorreo());
        valores.put(ContratoBaseDatos.Elementos.IDSERVER, this.getIdserver());
        valores.put(ContratoBaseDatos.Elementos.COLOR, this.getColor());
        valores.put(ContratoBaseDatos.Elementos.ORDEN, this.getOrden());
        valores.put(ContratoBaseDatos.Elementos.LOCALIZACION, this.getMap_not());

        return valores;
    }

    //Metodo utilizado para comprobar si la lista esta llena o no
    public boolean haveData(){

        if ( items.isEmpty() ) return false;

        if ( !titulo.isEmpty() ) return true;

        for ( ElementoLista el : items ) {

            if ( el.haveText() ) {

                return true;
            }
        }

        return false;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(titulo);
        dest.writeTypedList(items);
        dest.writeInt(papelera);
        dest.writeInt(tipo);
        dest.writeInt(actualizar);
        dest.writeInt(correo);
        dest.writeInt(idserver);
        dest.writeInt(color);
        dest.writeInt(orden);
        dest.writeString(fecha_not);
        dest.writeString(map_not);
    }
}
