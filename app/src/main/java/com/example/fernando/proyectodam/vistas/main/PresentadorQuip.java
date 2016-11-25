package com.example.fernando.proyectodam.vistas.main;

import android.content.Context;
import android.database.Cursor;

import com.example.fernando.proyectodam.contrato.ContratoMain;
import com.example.fernando.proyectodam.pojo.Lista;
import com.example.fernando.proyectodam.pojo.Nota;

public class PresentadorQuip implements ContratoMain.InterfacePresentador{

    private ContratoMain.InterfaceModelo modelo;
    private ContratoMain.InterfaceVista vista;

    public PresentadorQuip(ContratoMain.InterfaceVista vista) {

        this.vista = vista;
        this.modelo = new ModeloQuip((Context)vista);
    }

    @Override
    public void onAddNota() {

        this.vista.mostrarAgregarNota();
    }

    @Override
    public void onDeleteNota(Nota n) {

        this.modelo.deleteNota(n);

    }

    @Override
    public void onPapeleraNota(Nota n) {

        this.modelo.papeleraNota(n);
    }

    @Override
    public void onEditNota(Nota n) {
        this.vista.mostrarEditarNota(n);
    }

    @Override
    public void onDeleteNota(int position) {

        this.modelo.deleteNota(position);
    }

    @Override
    public void onEditNota(int position) {
        Nota n = this.modelo.getNota(position);
        this.onEditNota(n);
    }


    @Override
    public void onShowBorrarNota(int position) {
        Nota n = this.modelo.getNota(position);
        this.vista.mostrarConfirmarBorrarNota(n);
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onAddLista() {

        this.vista.mostrarAgregarLista();

    }

    @Override
    public void onEditLista(int position) {

        Lista lista = this.modelo.getLista(position);
        this.onEditLista(lista);
    }

    @Override
    public void onEditLista(Lista lista) {

        this.vista.mostrarEditarLista(lista);
    }

    @Override
    public void onDeleteLista(int position) {

        this.modelo.deleteLista(position);
    }


    @Override
    public void onPapeleraLista(Lista lista ) {

           this.modelo.papeleraLista(lista);
    }

    @Override
    public void onDeleteLista(Lista lista) {

        this.modelo.deleteLista(lista);
    }


    @Override
    public void onShowBorrarLista(int position) {

        Lista lista = this.modelo.getLista(position);
        this.vista.mostrarConfirmarBorrarLista(lista);

    }


    @Override
    public void onRecuperarNota(int position) {

        this.modelo.recuperarNota(position);
    }

    @Override
    public void onRecuperarNota(Nota n ) {

        this.modelo.recuperarNota(n);
    }

    @Override
    public void onRecuperarLista(int position) {

        this.modelo.recuperarLista(position);

    }

    @Override
    public void onRecuperarLista(Lista l) {

        this.modelo.recuperarLista(l);
    }

    @Override
    public void onShowRecuperarNota(int position) {
        Nota n = this.modelo.getNota(position);
        this.vista.mostrarRecuperarNota(n);
    }

    @Override
    public void onShowRecuperarLista(int position) {
        Lista lista = this.modelo.getLista(position);
        this.vista.mostrarRecuperarLista(lista);
    }

    @Override
    public void onVaciarPapelera() {

        this.modelo.vaciarPapelera();
    }

    @Override
    public void onRestartCursor(Cursor c) {

        this.modelo.setCursor(c);
    }

}
