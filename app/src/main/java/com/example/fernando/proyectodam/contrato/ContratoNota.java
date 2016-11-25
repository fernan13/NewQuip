package com.example.fernando.proyectodam.contrato;


import com.example.fernando.proyectodam.pojo.LocationObject;
import com.example.fernando.proyectodam.pojo.Nota;

public interface ContratoNota {

    interface InterfaceModelo {

        Nota getNota(long id);

        long saveNota(Nota n);

        void eliminarNotificacionNota(Nota nota);

        void saveLocation(LocationObject location );

        void eliminarMapNotificacionNota(Nota nota);

    }

    interface InterfacePresentador {

        void onPause();

        void onResume();

        long onSaveNota(Nota n);

        void saveLocation(LocationObject location);

        void onShowEliminarNotificacionNota();

        void onEliminarNotificacionNota(Nota nota);

        void onEliminarMapNotificacionNota(Nota nota);
    }

    interface InterfaceVista {

        void mostrarEliminarNotificacionNota();

    }

}