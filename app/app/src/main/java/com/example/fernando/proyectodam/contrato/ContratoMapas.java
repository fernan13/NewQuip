package com.example.fernando.proyectodam.contrato;

import com.example.fernando.proyectodam.pojo.LocationObject;

import java.util.List;

/**
 * Created by Fernando on 15/11/2016.
 */

public interface ContratoMapas {

    interface InterfaceModelo {

        void loadData(OnDataLoadListener listener);

        void loadData(OnDataLoadListener listener, int id);

        interface OnDataLoadListener {
            public void setLocations(List<LocationObject> list);
        }
    }

    interface InterfacePresentador {

        void pedirLocalizacion();
        void pedirHistorial( int id );

    }

    interface InterfaceVista {

        void mostrarInformacion(List<LocationObject> list);

    }
}
