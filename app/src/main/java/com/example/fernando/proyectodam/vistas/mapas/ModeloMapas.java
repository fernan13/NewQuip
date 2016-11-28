package com.example.fernando.proyectodam.vistas.mapas;

import android.content.Context;

import com.example.fernando.proyectodam.contrato.ContratoMapas;
import com.example.fernando.proyectodam.gestion.GestionLocationObject;
import com.example.fernando.proyectodam.util.Ficheros.UtilFicheros;

/**
 * Created by Fernando on 15/11/2016.
 */

public class ModeloMapas implements ContratoMapas.InterfaceModelo {

    private GestionLocationObject glo;
    private Context c;

    public ModeloMapas( Context c ) {

        this.c      = c;
        this.glo    = new GestionLocationObject(c);
    }

    @Override
    public void loadData(OnDataLoadListener listener) {

        String idcorreo   = UtilFicheros.getValueFromSharedPreferences(c, "usuario", "id_email");
        listener.setLocations(glo.getAllLocationForEmail(Integer.parseInt(idcorreo)));
    }
}
