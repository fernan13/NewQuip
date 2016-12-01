package com.example.fernando.proyectodam.gestion;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.location.Location;

import com.example.fernando.proyectodam.basedatos.AyudanteOrm;
import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;
import com.example.fernando.proyectodam.pojo.LocationObject;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Fernando on 15/11/2016.
 */

public class GestionLocationObject {

    private AyudanteOrm helper;
    private Dao<LocationObject, Integer> locationDao;

    public GestionLocationObject( Context c ) {

        helper          = new AyudanteOrm(c);
        helper.getWritableDatabase();
        locationDao     = helper.getLocationDao();
    }


    public void insertLocation ( LocationObject location ) {

        try
        {
            locationDao.create(location);
        }
        catch( SQLException e){

            e.printStackTrace();
        }
    }

    public void updateLocation ( LocationObject location ){

        try
        {
            locationDao.update(location);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteLocation ( LocationObject location ){

        try
        {
            locationDao.delete(location);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<LocationObject> getAllLocation(){

        List<LocationObject> locations = null;

        try
        {
            locations = locationDao.queryForAll();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return locations;
    }

    public List<LocationObject> getAllLocationForEmail( int email ) {

        List<LocationObject> locations = null;

        try
        {
            locations = locationDao.queryBuilder()
                                   .where()
                                   .eq(ContratoBaseDatos.LocationObject.FIELD_NAME_EMAIL, email)
                                   .query();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return locations;
    }

    public List<LocationObject> getHistorial( int email, int id ) {

        List<LocationObject> locations = null;

        try
        {
            locations = locationDao.queryBuilder()
                    .where()
                    .eq(ContratoBaseDatos.LocationObject.FIELD_NAME_EMAIL, email)
                    .and()
                    .eq(ContratoBaseDatos.LocationObject.FIELD_NAME_ID_ELEM, id)
                    .query();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return locations;
    }
}
