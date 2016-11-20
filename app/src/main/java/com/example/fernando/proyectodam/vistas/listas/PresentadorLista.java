package com.example.fernando.proyectodam.vistas.listas;

import android.content.Context;

import com.example.fernando.proyectodam.contrato.ContratoLista;
import com.example.fernando.proyectodam.pojo.Lista;
import com.example.fernando.proyectodam.pojo.LocationObject;

public class PresentadorLista implements ContratoLista.InterfacePresentador {

    private ContratoLista.InterfaceModelo modelo;
    private ContratoLista.InterfaceVista vista;

    public PresentadorLista(ContratoLista.InterfaceVista vista) {

        this.vista = vista;
        this.modelo = new ModeloLista((Context)vista);
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {
    }

    @Override
    public long onSaveLista(Lista lista) {

        return this.modelo.saveLista(lista);
    }

    @Override
    public void saveLocation(LocationObject location) {

        this.modelo.saveLocation(location);
    }

    @Override
    public void onShowEliminarNotificacionLista(){

        this.vista.mostrarEliminarNotificacionLista();
    }

    @Override
    public void onEliminarNotificacionLista(Lista lista) {

        this.modelo.eliminarNotificacionLista(lista);
    }
}