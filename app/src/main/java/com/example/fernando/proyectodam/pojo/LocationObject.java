package com.example.fernando.proyectodam.pojo;

import com.example.fernando.proyectodam.basedatos.AyudanteOrm;
import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Fernando on 15/11/2016.
 */
//Anotacion utilizada para especificar el nombre la tabla a crear
@DatabaseTable(tableName = ContratoBaseDatos.LocationObject.TABLE_NAME)
public class LocationObject {

    @DatabaseField(columnName =  ContratoBaseDatos.LocationObject.FIELD_NAME_ID, generatedId = true)
    private int mId;

    @DatabaseField(columnName =  ContratoBaseDatos.LocationObject.FIELD_NAME_LATITUD)
    private double latitud;

    @DatabaseField(columnName =  ContratoBaseDatos.LocationObject.FIELD_NAME_LONGITUD)
    private double longitud;

    @DatabaseField(columnName =  ContratoBaseDatos.LocationObject.FIELD_NAME_FECHA)
    private String fecha;


    @DatabaseField(columnName =  ContratoBaseDatos.LocationObject.FIELD_NAME_EMAIL)
    private int email;

    @DatabaseField(columnName =  ContratoBaseDatos.LocationObject.FIELD_NAME_TITULO)
    private String titulo;

    public LocationObject(){}


    public LocationObject ( double latitud, double longitud, String fecha, int email, String titulo ) {

        this.latitud    = latitud;
        this.longitud   = longitud;
        this.fecha      = fecha;
        this.email      = email;
        this.titulo     = titulo;
    }

    public int getmId(){

        return mId;
    }

    public void setmId(int mId){

        this.mId = mId;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getEmail(){

        return mId;
    }

    public void setEmail(int email){

        this.email = email;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
