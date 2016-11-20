package com.example.fernando.proyectodam.contrato;

import com.example.fernando.proyectodam.pojo.LocationObject;

import java.util.List;

/**
 * Created by Fernando on 15/11/2016.
 */

public interface ContratoMapas {

    interface InterfaceModelo {

        void loadData(OnDataLoadListener listener);

        interface OnDataLoadListener {
            public void setLocations(List<LocationObject> list);
        }
    }

    interface InterfacePresentador {

        void pedirLocalizacion();
    }

    interface InterfaceVista {

        void mostrarLocalizacion(List<LocationObject> list);

    }
}
