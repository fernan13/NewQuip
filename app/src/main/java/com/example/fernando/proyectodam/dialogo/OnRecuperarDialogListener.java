package com.example.fernando.proyectodam.dialogo;


import com.example.fernando.proyectodam.pojo.Lista;
import com.example.fernando.proyectodam.pojo.Nota;

/**
 * Created by Fernando on 15/10/2016.
 */

public interface OnRecuperarDialogListener {

    void onRecuperarPossitiveButtonClick(Lista l);
    void onRecuperarPossitiveButtonClick(Nota n);
    void onRecuperarNegativeButtonClick();
}
