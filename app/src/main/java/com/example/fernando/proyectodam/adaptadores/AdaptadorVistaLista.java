package com.example.fernando.proyectodam.adaptadores;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.example.fernando.proyectodam.databinding.ItemListaBinding;
import com.example.fernando.proyectodam.pojo.ElementoLista;

import java.util.ArrayList;

public class AdaptadorVistaLista extends RecyclerView.Adapter<AdaptadorVistaLista.ViewHolder> {

    private ArrayList<ElementoLista> items;
    private ICheckItem icheck;

    public AdaptadorVistaLista( ArrayList<ElementoLista> items ) {

        this.items          = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater li       = LayoutInflater.from(parent.getContext());
        ItemListaBinding item   = ItemListaBinding.inflate( li, parent, false);

        return new ViewHolder(item.getRoot());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.binding.setElemento(items.get(position));
    }

    @Override
    public int getItemCount() {

        return items.size();
    }

    public void addNewView(){

        items.add( new ElementoLista());
        this.notifyItemInserted(items.size());
    }

    public void addNewItem(ElementoLista elementoLista){

        items.add(elementoLista);
        this.notifyItemInserted(items.size());
    }

    public void removeView( int position ){

        if ( position != -1 ) {

            items.remove(position);
            this.notifyItemRemoved(position);

        }
    }


    public ArrayList<ElementoLista> getItems(){

        return this.items;
    }

    public void setICheck( ICheckItem icheck ) {

        this.icheck = icheck;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ItemListaBinding binding;

        public ViewHolder(View itemView) {

            super(itemView);

            binding     = DataBindingUtil.bind(itemView);

            binding.cbDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = ViewHolder.super.getAdapterPosition();
                    ElementoLista item = items.get(position);

                    //Lo pasamos y lo eliminamos
                    icheck.checkItem(item);
                    AdaptadorVistaLista.this.removeView(position);

                }
            });


            //Controlamos que se actualize el texto como por la entrada por teclado o al cambiar el foco
            binding.edTarea.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                //Se registran las opciones IME para controlar la introduccion del texto
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if ( (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_NEXT) ) {

                        int position = ViewHolder.super.getAdapterPosition();
                        if ( actionId == EditorInfo.IME_ACTION_NEXT && position == items.size() - 1 ) {

                            addNewView();

                        }

                    }

                    //Devolvemos siempre falso para ocultar el teclado
                    return false;
                }
            });

            binding.edTarea.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    if ( hasFocus ) {

                        binding.cbRemove.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        binding.cbRemove.setVisibility(View.GONE);
                    }
                }
            });

            binding.cbRemove.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    removeView(ViewHolder.super.getAdapterPosition());
                }
            });

        }


    }
}