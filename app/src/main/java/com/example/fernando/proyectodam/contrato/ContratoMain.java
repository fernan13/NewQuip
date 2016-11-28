package com.example.fernando.proyectodam.contrato;

import android.database.Cursor;

import com.example.fernando.proyectodam.pojo.Lista;
import com.example.fernando.proyectodam.pojo.Nota;

public interface ContratoMain {

    interface InterfaceModelo {

        long deleteNota(int position);

        long deleteNota(Nota n);

        long papeleraNota(Nota n);

        Nota getNota(int position);

        long deleteLista(int position);

        long deleteLista(Lista l);

        long papeleraLista(Lista l);

        Lista getLista(int position);

        long recuperarNota( Nota n );

        long recuperarNota( int position );

        long recuperarLista( Lista l );

        long recuperarLista( int position );

        void vaciarPapelera();

        void setCursor( Cursor c );
    }

    interface InterfacePresentador {

        void onAddNota();

        void onDeleteNota(int position);

        void onDeleteNota(Nota n);

        void onPapeleraNota(Nota n );

        void onEditNota(int position);

        void onEditNota(Nota n);

        void onPause();

        void onAddLista();

        void onShowBorrarNota(int position);

        void onDeleteLista(int position);

        void onDeleteLista(Lista lista);

        void onEditLista(int position);

        void onEditLista(Lista lista);

        void onShowBorrarLista(int position);

        void onPapeleraLista( Lista l);

        void onRecuperarNota( int position );

        void onRecuperarNota ( Nota n );

        void onRecuperarLista( int position );

        void onRecuperarLista( Lista l );

        void onShowRecuperarNota(int position);

        void onShowRecuperarLista(int position);

        void onVaciarPapelera();

        void onRestartCursor(Cursor c );
    }

    interface InterfaceVista {

        void mostrarAgregarNota();

        void mostrarEditarNota(Nota n);

        void mostrarConfirmarBorrarNota(Nota n);

        void mostrarAgregarLista();

        void mostrarEditarLista(Lista lista);

        void mostrarConfirmarBorrarLista(Lista lista);

        void mostrarRecuperarNota(Nota n);

        void mostrarRecuperarLista(Lista lista);


    }


}