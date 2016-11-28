package com.example.fernando.proyectodam.dialogo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.fernando.proyectodam.R;
import com.example.fernando.proyectodam.pojo.Nota;

/**
 * Created by Fernando on 23/10/2016.
 */

public class DialogoQuitarNotificacionNota extends DialogFragment {

    private Nota nota;
    private OnEliminarNotificacionNotaDialogListener listener;

    public static DialogoQuitarNotificacionNota newInstance(Nota nota) {

        DialogoQuitarNotificacionNota fragment = new DialogoQuitarNotificacionNota();
        Bundle args = new Bundle();
        args.putParcelable("nota", nota);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ( getArguments() != null ) {

            nota = getArguments().getParcelable("nota");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getResources().getString(R.string.titulo_eliminar_notificacion));
        builder.setMessage(getResources().getString(R.string.mensaje_eliminar_notificacion));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onEliminarPossitiveButtonCLick(nota);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onEliminarNegativeButtonClick();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (OnEliminarNotificacionNotaDialogListener) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(
                    context.toString() +
                            " no implementó OnEliminarNotificacionNotaDialogListener");

        }
    }
}
