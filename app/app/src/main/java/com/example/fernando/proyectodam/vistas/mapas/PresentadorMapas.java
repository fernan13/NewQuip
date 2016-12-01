package com.example.fernando.proyectodam.vistas.mapas;

import android.content.Context;

import com.example.fernando.proyectodam.contrato.ContratoMapas;
import com.example.fernando.proyectodam.pojo.LocationObject;

import java.util.List;

/**
 * Created by Fernando on 15/11/2016.
 */

public class PresentadorMapas implements ContratoMapas.InterfacePresentador {

    private ContratoMapas.InterfaceModelo modelo;
    private ContratoMapas.InterfaceVista vista;
    private ContratoMapas.InterfaceModelo.OnDataLoadListener oyente;

    public PresentadorMapas(ContratoMapas.InterfaceVista vista) {

        this.vista = vista;
        this.modelo = new ModeloMapas((Context)vista);
        oyente = new ContratoMapas.InterfaceModelo.OnDataLoadListener() {

            @Override
            public void setLocations(List<LocationObject> list) {

                PresentadorMapas.this.vista.mostrarInformacion(list);
            }
        };
    }

    @Override
    public void pedirLocalizacion() {

        modelo.loadData(oyente);
    }

    @Override
    public void pedirHistorial(int id) {

        modelo.loadData(oyente, id);
    }
}
