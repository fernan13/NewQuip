package com.example.fernando.proyectodam.adaptadores;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;
import com.example.fernando.proyectodam.databinding.ItemQuipListaBinding;
import com.example.fernando.proyectodam.databinding.ItemQuipNotaBinding;
import com.example.fernando.proyectodam.pojo.ElementoLista;
import com.example.fernando.proyectodam.pojo.Lista;
import com.example.fernando.proyectodam.pojo.Nota;
import com.example.fernando.proyectodam.util.Ficheros.UtilImage;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by Fernando on 04/10/2016.
 */

public class CursorAdaptadorQuip extends MyCursorAdapter<RecyclerView.ViewHolder> {

    //Escuchadores
    private View.OnClickListener clickListener;
    private View.OnLongClickListener longClickListener;
    private boolean isEnabled;

    public CursorAdaptadorQuip(Context context, Cursor cursor){

        super(context,cursor);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater li = LayoutInflater.from(parent.getContext());

        View  v = null;
        RecyclerView.ViewHolder vh = null;

        switch( viewType ) {

            case 0 : {

                ItemQuipNotaBinding iqn = ItemQuipNotaBinding.inflate(li, parent, false);
                v   = iqn.getRoot();
                vh  = new ViewHolderNota(v);

                break;
            }

            case 1 : {

                ItemQuipListaBinding iql = ItemQuipListaBinding.inflate(li, parent, false);
                v   = iql.getRoot();
                vh  = new ViewHolderLista(v);

                break;
            }
        }

        //Indicamos a la vista quien gestiona su evento
        v.setOnClickListener(this);
        v.setOnLongClickListener(this);

        return vh;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {

        int tipo = cursor.getInt(cursor.getColumnIndex(ContratoBaseDatos.Elementos.TIPO));

        if ( tipo == 0 ) {

            Nota n = Nota.getNota(cursor);

            /*
            *   Reducimos el tamaño de la imagen a mostrar en la lista principal
            * */
            if ( n.getImagen() != null ) {

                n.setImagen(UtilImage.getImageResize(n.getImagen(), 150, 150));
            }

            ItemQuipNotaBinding binding = ((ViewHolderNota)viewHolder).binding;

            binding.setNota(n);
        }
        else
        {
            Lista l = Lista.getLista(cursor);
            ItemQuipListaBinding binding = ((ViewHolderLista)viewHolder).binding;

            binding.setLista(l);
            ((ViewHolderLista)viewHolder).viewLista(l);
        }
    }

    //Metodos para asignar desde la vista los eventos
    public void setOnClickListener( View.OnClickListener listener ) {

        this.clickListener = listener;
    }

    public void setOnLongClickListener ( View.OnLongClickListener longClickListener ) {

        this.longClickListener = longClickListener;
    }

    public void removeOnClickListener(){

        this.clickListener = null;
    }

    public void setEnabled( boolean isEnabled ) {

        this.isEnabled = isEnabled;
    }

    @Override
    public void onClick(View v) {

        if ( this.clickListener != null && this.isEnabled ) clickListener.onClick(v);
    }

    @Override
    public boolean onLongClick(View v) {

        if ( this.longClickListener != null && this.isEnabled ) {

            longClickListener.onLongClick(v);

            return true;
        }

        return false;
    }

    public static class ViewHolderNota extends RecyclerView.ViewHolder {

        public ItemQuipNotaBinding binding;

        public ViewHolderNota(View view) {

            super(view);
            binding = DataBindingUtil.bind(view);
        }
    }

    public static class ViewHolderLista extends RecyclerView.ViewHolder {

        public ItemQuipListaBinding binding;

        public ViewHolderLista( View view ) {

            super(view);

            binding = DataBindingUtil.bind(view);
        }

        public void viewLista( Lista lista ) {

            //Titulo lista
            if ( !lista.getTitulo().isEmpty() ) {

                binding.tvTitulo.setText(lista.getTitulo());
                binding.tvTitulo.setVisibility(View.VISIBLE);

            }

            //Elementos finitos de la lista
            ArrayList<ElementoLista> items = lista.getItems();

            for ( int i = 0; i < items.size(); i++ ) {

                ElementoLista el = items.get(i);

                switch ( i ) {

                    case 0 : {

                        binding.cbItem1.setClickable(false);
                        binding.cbItem1.setChecked(el.isCheck());
                        binding.tvItem1.setText(el.getTexto());

                        binding.cbItem1.setVisibility(View.VISIBLE);
                        binding.tvItem1.setVisibility(View.VISIBLE);

                        break;
                    }

                    case 1 : {

                        binding.cbItem2.setClickable(false);
                        binding.cbItem2.setChecked(el.isCheck());
                        binding.tvItem2.setText(el.getTexto());

                        binding.cbItem2.setVisibility(View.VISIBLE);
                        binding.tvItem2.setVisibility(View.VISIBLE);

                        break;
                    }

                    case 2 : {

                        binding.cbItem3.setClickable(false);
                        binding.cbItem3.setChecked(el.isCheck());
                        binding.tvItem3.setText(el.getTexto());

                        binding.cbItem3.setVisibility(View.VISIBLE);
                        binding.tvItem3.setVisibility(View.VISIBLE);

                        break;
                    }

                }

                //Solo mostramos dos elementos
                if ( i == 2 ) {

                    break;
                }
            }
        }

    }


}
