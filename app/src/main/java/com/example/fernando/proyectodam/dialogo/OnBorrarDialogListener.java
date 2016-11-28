package com.example.fernando.proyectodam.dialogo;

import com.example.fernando.proyectodam.pojo.Lista;
import com.example.fernando.proyectodam.pojo.Nota;

/**
 * Created by Pilar on 28/09/2016.
 */

public interface OnBorrarDialogListener {

    void onBorrarPossitiveButtonClick(Lista l);
    void onBorrarPossitiveButtonClick(Nota n);
    void onBorrarNegativeButtonClick();
}
