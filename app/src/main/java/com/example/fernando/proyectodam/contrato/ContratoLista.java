package com.example.fernando.proyectodam.contrato;


import com.example.fernando.proyectodam.pojo.Lista;
import com.example.fernando.proyectodam.pojo.LocationObject;

/**
 * Created by Fernando on 09/10/2016.
 */

public interface ContratoLista {

    interface InterfaceModelo {

        Lista getLista(long id);

        long saveLista(Lista lista);

        void saveLocation(LocationObject location);

        void eliminarNotificacionLista(Lista lista);

        void eliminarMapNotificacionLista(Lista lista);
    }

    interface InterfacePresentador {

        void onPause();

        void onResume();

        long onSaveLista(Lista n);

        void saveLocation(LocationObject location);

        void onShowEliminarNotificacionLista();

        void onEliminarNotificacionLista(Lista lista);

        void onEliminarMapNotificacionLista( Lista lista);
    }

    interface InterfaceVista {

        void mostrarLista(Lista n);

        void mostrarEliminarNotificacionLista();

    }

}
