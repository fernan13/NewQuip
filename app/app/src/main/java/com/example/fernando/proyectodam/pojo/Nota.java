package com.example.fernando.proyectodam.pojo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fernando.proyectodam.BR;
import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;
import com.example.fernando.proyectodam.util.Ficheros.UtilFicheros;
import com.example.fernando.proyectodam.util.Ficheros.UtilImage;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class Nota extends BaseObservable implements Parcelable {

    private long id;
    private String titulo, nota;
    private Bitmap imagen;
    private int papelera,tipo, actualizar, correo, idserver, color, orden;
    private String fecha_not;
    private String map_not;
    private String place;

    public Nota() {

        this(0, "", "", null, 0, 0, 0, 0, 0, Color.parseColor("#FFFFFF"), 0, "", "");
    }

    public Nota(long id, String titulo, String nota, Bitmap imagen, int papelera, int tipo, int actualizar,
                int correo, int idserver, int color, int orden, String fecha_not, String map_not ) {

        this.id         = id;
        this.titulo     = titulo;
        this.nota       = nota;
        this.imagen     = imagen;
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


    protected Nota(Parcel in) {

        id          = in.readLong();
        titulo      = in.readString();
        nota        = in.readString();
        imagen      = in.readParcelable(Bitmap.class.getClassLoader());
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

    public static final Creator<Nota> CREATOR = new Creator<Nota>() {
        @Override
        public Nota createFromParcel(Parcel in) {
            return new Nota(in);
        }

        @Override
        public Nota[] newArray(int size) {
            return new Nota[size];
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

    public void setId(String id) {

        try
        {
            this.id = Long.parseLong(id);

        } catch(NumberFormatException e){

            this.id = 0;
        }

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
    public int getPapelera(){

        return this.papelera;
    }

    public void setPapelera( int papelera ) {

        this.papelera = papelera;
        notifyPropertyChanged(BR.papelera);
    }

    @Bindable
    public String getNota() {

        return nota;
    }

    public void setNota(String nota) {

        this.nota = nota;
        notifyPropertyChanged(BR.nota);

    }

    @Bindable
    public int getActualizar() {

        return actualizar;
    }

    public void setActualizar(int actualizar) {

        this.actualizar = actualizar;
        notifyPropertyChanged(BR.actualizar);

    }

    public void setImagen( Bitmap imagen ) {

        this.imagen = imagen;
        notifyPropertyChanged(BR.imagen);
    }

    //Se llama de forma interna por el binding
    @BindingAdapter({"android:src"})
    public static void loadImage(ImageView view, Bitmap image) {

        view.setImageBitmap(image);
    }

    @Bindable
    public int getTipo(){

        return this.tipo;
    }

    public void setTipo( int tipo ) {

        this.tipo = tipo;
        notifyPropertyChanged(BR.tipo);
    }

    @Bindable
    public int getCorreo(){

        return this.correo;
    }

    public void setCorreo( int correo ) {

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
    public int getColor(){

        return this.color;
    }

    public void setColor( int color ) {

        this.color = color;
        notifyPropertyChanged(BR.color);
    }

    public void setOrden( int orden ) {

        this.orden = orden;
        notifyPropertyChanged(BR.orden);
    }

    @Bindable
    public int getOrden(){

        return this.orden;
    }

    @Bindable
    public Bitmap getImagen() {

        return this.imagen;
    }

    @Bindable
    public String getFecha_not(){

        return this.fecha_not;
    }

    public void setFecha_not( String fecha_not ) {

        this.fecha_not = fecha_not;
        notifyPropertyChanged(BR.fecha_not);
    }

    @Bindable
    public String getMap_not(){

        return this.map_not;
    }

    public void setPlace( String place ) {

        this.place = place;
        notifyPropertyChanged(BR.place);
    }

    @Bindable
    public String getPlace(){

        return this.place;
    }

    public void setMap_not( String map_not) {

        this.map_not = map_not;
        notifyPropertyChanged(BR.map_not);
    }

    public ContentValues getContentValues(){

        return this.getContentValues(false);
    }

    public ContentValues getContentValues(boolean withId){

        ContentValues valores = new ContentValues();

        if(withId){

            valores.put(ContratoBaseDatos.Elementos._ID, this.getId());
        }

        //Convertimos el bitmap en array de bytes
        byte[] imagenBytes = null;

        if ( this.getImagen() != null ) {

            //Redimensionamos la imagen para almacenarla en la BBDD

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            //Bitmap bmp = UtilImage.getImageResize(this.getImagen(), 150, 150);

            this.getImagen().compress(Bitmap.CompressFormat.PNG, 0, bytes);
            imagenBytes = bytes.toByteArray();
        }

        valores.put(ContratoBaseDatos.Elementos.TITULO, this.getTitulo());
        valores.put(ContratoBaseDatos.Elementos.CONTENIDO, this.getNota());
        valores.put(ContratoBaseDatos.Elementos.TIPO, this.getTipo());
        valores.put(ContratoBaseDatos.Media.BLOB, imagenBytes);
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

    public static Nota getNota(Cursor c){

        Nota objeto = new Nota();

        objeto.setId(c.getLong(c.getColumnIndex(ContratoBaseDatos.Elementos._ID)));
        objeto.setTitulo(c.getString(c.getColumnIndex(ContratoBaseDatos.Elementos.TITULO)));
        objeto.setNota(c.getString(c.getColumnIndex(ContratoBaseDatos.Elementos.CONTENIDO)));

        //Imagen
        byte[] imagenBytes = c.getBlob(c.getColumnIndex(ContratoBaseDatos.Media.BLOB));

        if ( imagenBytes == null ) {

            objeto.setImagen(null);
        }
        else
        {
            /*
            *   No redimensionamos la imagen porque la almacenamos pequeña
            * */
            objeto.setImagen(UtilImage.getBitmapFromByteArray(imagenBytes));
        }
        objeto.setPapelera(c.getInt(c.getColumnIndex(ContratoBaseDatos.Elementos.PAPELERA)));
        objeto.setTipo(c.getInt(c.getColumnIndex(ContratoBaseDatos.Elementos.TIPO)));
        objeto.setFecha_not(c.getString(c.getColumnIndex(ContratoBaseDatos.Elementos.FECHA_NOT)));
        objeto.setActualizar(0);
        objeto.setCorreo(c.getInt(c.getColumnIndex(ContratoBaseDatos.Elementos.IDCORREO)));
        objeto.setIdserver(c.getInt(c.getColumnIndex(ContratoBaseDatos.Elementos.IDSERVER)));
        objeto.setColor(c.getInt(c.getColumnIndex(ContratoBaseDatos.Elementos.COLOR)));
        objeto.setOrden(c.getInt(c.getColumnIndex(ContratoBaseDatos.Elementos.ORDEN)));
        objeto.setMap_not(c.getString(c.getColumnIndex(ContratoBaseDatos.Elementos.LOCALIZACION)));

        return objeto;
    }

    public static Nota getNota(JSONObject object ){

        Nota nota = new Nota();

        try
        {
            nota.setTitulo(object.getString(ContratoBaseDatos.Elementos.TITULO));
            nota.setNota(object.getString(ContratoBaseDatos.Elementos.CONTENIDO));

            Bitmap bmp = UtilImage.getBitmapFromString(object.getString(ContratoBaseDatos.Media.BLOB));

            if ( bmp != null ) {

                nota.setImagen(UtilImage.getImageResize(bmp, 250, 250 ));
            }
            else
            {
                nota.setImagen(bmp);
            }

            nota.setPapelera(object.getInt(ContratoBaseDatos.Elementos.PAPELERA));
            nota.setTipo(object.getInt(ContratoBaseDatos.Elementos.TIPO));
            nota.setFecha_not(object.getString(ContratoBaseDatos.Elementos.FECHA_NOT));
            nota.setActualizar(1);
            nota.setIdserver(object.getInt(ContratoBaseDatos.Elementos._ID));

            int color = object.getInt(ContratoBaseDatos.Elementos.COLOR);

            if ( color == 0 ) color = Color.parseColor("#FFFFFF");

            nota.setColor(color);
            nota.setOrden(object.getInt(ContratoBaseDatos.Elementos.ORDEN));
            nota.setMap_not(object.getString(ContratoBaseDatos.Elementos.LOCALIZACION));

        }
        catch( JSONException e ){}

        return nota;
    }

    @Override
    public String toString() {
        return "Nota{" +
                "actualizar=" + actualizar +
                ", id=" + id +
                ", titulo='" + titulo + '\'' +
                ", nota='" + nota + '\'' +
                ", imagen=" + imagen +
                ", papelera=" + papelera +
                ", tipo=" + tipo +
                ", correo=" + correo +
                ", idserver=" + idserver +
                ", fecha_not='" + fecha_not + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(id);
        dest.writeString(titulo);
        dest.writeString(nota);
        dest.writeParcelable(imagen, flags);
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