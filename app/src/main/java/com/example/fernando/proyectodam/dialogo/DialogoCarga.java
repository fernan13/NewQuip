package com.example.fernando.proyectodam.dialogo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.example.fernando.proyectodam.R;

/**
 * Created by Fernando on 28/10/2016.
 */

public class DialogoCarga extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Caracteristicas dialogo imagen
        ProgressDialog mProgressDialog = new ProgressDialog(getContext());

        mProgressDialog.setMessage(getResources().getString(R.string.message_progress_dialog));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);

        return mProgressDialog;
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();

        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }
}
