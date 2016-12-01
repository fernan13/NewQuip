package com.example.fernando.proyectodam.vistas.notas;

import android.content.Context;
import android.support.v7.app.AppCompatDialogFragment;

import com.example.fernando.proyectodam.contrato.ContratoNota;
import com.example.fernando.proyectodam.pojo.LocationObject;
import com.example.fernando.proyectodam.pojo.Nota;

public class PresentadorNota implements ContratoNota.InterfacePresentador {

    private ContratoNota.InterfaceModelo modelo;
    private ContratoNota.InterfaceVista vista;

    public PresentadorNota(ContratoNota.InterfaceVista vista) {

        this.vista = vista;
        this.modelo = new ModeloNota((Context)vista);
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {
    }

    @Override
    public long onSaveNota(Nota n) {

        return this.modelo.saveNota(n);
    }

    @Override
    public void saveLocation(LocationObject location) {

        this.modelo.saveLocation(location);
    }

    @Override
    public void onShowEliminarNotificacionNota() {

        this.vista.mostrarEliminarNotificacionNota();
    }

    @Override
    public void onEliminarNotificacionNota(Nota nota) {

        this.modelo.eliminarNotificacionNota(nota);
    }

    @Override
    public void onEliminarMapNotificacionNota(Nota nota) {

        this.modelo.eliminarMapNotificacionNota(nota);
    }


}