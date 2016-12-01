package com.example.fernando.proyectodam.dialogo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.fernando.proyectodam.R;
import com.example.fernando.proyectodam.pojo.Lista;

/**
 * Created by Fernando on 15/10/2016.
 */

public class DialogoRecuperarLista extends DialogFragment {

    private Lista l;

    // Interfaz de comunicación
    OnRecuperarDialogListener listener;

    public DialogoRecuperarLista() {
    }

    public static DialogoRecuperarLista newInstance(Lista l) {
        DialogoRecuperarLista fragment = new DialogoRecuperarLista();
        Bundle args = new Bundle();
        args.putParcelable("lista", l);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            l=getArguments().getParcelable("lista");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createDialogBorrar();
    }
    public AlertDialog createDialogBorrar() {
        String titulo_dialogo= String.format("%s %s", getString(R.string.etiqueta_dialogo_recuperar),l.getTitulo());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(titulo_dialogo);
        builder.setMessage(R.string.mensaje_confirm_recuperar_lista);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onRecuperarPossitiveButtonClick(l);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onRecuperarNegativeButtonClick();
            }
        });
        AlertDialog alertBorrar = builder.create();
        return alertBorrar;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (OnRecuperarDialogListener) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(
                    context.toString() +
                            " no implementó OnBorrarDialogListener");

        }
    }

}
