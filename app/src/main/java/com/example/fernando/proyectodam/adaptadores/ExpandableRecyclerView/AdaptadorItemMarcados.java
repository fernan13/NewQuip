package com.example.fernando.proyectodam.adaptadores.ExpandableRecyclerView;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fernando.proyectodam.R;
import com.example.fernando.proyectodam.adaptadores.ICheckItem;
import com.example.fernando.proyectodam.databinding.ItemListaBinding;
import com.example.fernando.proyectodam.pojo.ElementoLista;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

import static android.view.animation.Animation.RELATIVE_TO_SELF;


/**
 * Created by Fernando on 13/11/2016.
 */

public class AdaptadorItemMarcados extends ExpandableRecyclerViewAdapter<AdaptadorItemMarcados.GrupoViewHolder,
                                                                         AdaptadorItemMarcados.ItemViewHolder> {

    private static Context c;
    private ICheckItem ichekc;

    public AdaptadorItemMarcados(Context c, List<? extends ExpandableGroup> groups) {
        super(groups);

        this.c = c;
    }

    @Override
    public GrupoViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(c);
        View v = inflater.inflate(R.layout.lista_item_marcado_titulo, parent, false);

        return new GrupoViewHolder(v);
    }

    @Override
    public ItemViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater li       = LayoutInflater.from(c);
        ItemListaBinding item   = ItemListaBinding.inflate( li, parent, false);

        return new ItemViewHolder(item.getRoot());
    }

    @Override
    public void onBindChildViewHolder(ItemViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {

        Object o = group.getItems().get(childIndex);
        holder.binding.setElemento((ElementoLista)o);
    }

    @Override
    public void onBindGroupViewHolder(GrupoViewHolder holder, int flatPosition, ExpandableGroup group) {

        cambiarTitulo();
    }

    public void cambiarTitulo() {

        ExpandableGroup group       = AdaptadorItemMarcados.super.getGroups().get(0);
        List<ElementoLista> items   = group.getItems();

        if ( items.isEmpty() ) {

            GrupoViewHolder.itemView.setVisibility(View.GONE);
        }
        else
        {
            GrupoViewHolder.itemView.setVisibility(View.VISIBLE);

            GrupoViewHolder.tvTitulo.setHint(items.size() + " " +
                    c.getResources().getString(R.string.numero_elementos));
        }
    }

    public void addNewItem( ElementoLista el ) {

        ExpandableGroup group       = AdaptadorItemMarcados.super.getGroups().get(0);
        List<ElementoLista> items   = group.getItems();

        items.add(el);
        this.notifyItemInserted(items.size());
        cambiarTitulo();
    }


    public static class GrupoViewHolder extends GroupViewHolder {

        public static TextView tvTitulo;
        public static ImageView arrow;
        public static View itemView;

        public GrupoViewHolder(View itemView) {

            super(itemView);

            this.tvTitulo    = (TextView) itemView.findViewById(R.id.tituloGrupo);
            this.arrow       = (ImageView) itemView.findViewById(R.id.flechaGrupo);
            this.itemView    = itemView;

            //Background transparente
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                arrow.setBackgroundColor(c.getResources().getColor(android.R.color.transparent, c.getTheme()));
            }
        }

        @Override
        public void expand() {
            animateExpand();
        }

        @Override
        public void collapse() {
            animateCollapse();
        }

        private void animateExpand() {
            RotateAnimation rotate = new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            arrow.setAnimation(rotate);
        }

        private void animateCollapse() {
            RotateAnimation rotate =  new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            arrow.setAnimation(rotate);
        }
    }

    public class ItemViewHolder extends ChildViewHolder {

        public ItemListaBinding binding;

        public ItemViewHolder(View itemView) {
            super(itemView);

            binding     = DataBindingUtil.bind(itemView);

            binding.cbDone.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //Obtenemos a nuestro pare de grupo
                    int position                = ItemViewHolder.super.getAdapterPosition() - 1;

                    ExpandableGroup group       = AdaptadorItemMarcados.super.getGroups().get(0);
                    List<ElementoLista> items   = group.getItems();

                    ElementoLista el = items.get(position);
                    ichekc.unCheckItem(el);

                    //Eliminamos el elemento
                    items.remove(position);
                    AdaptadorItemMarcados.this.notifyItemRemoved(position + 1);
                    AdaptadorItemMarcados.this.cambiarTitulo();
                }
            });

            binding.edTarea.setEnabled(false);
            binding.edTarea.setPaintFlags(binding.edTarea.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    public void setICheck(ICheckItem icheck){

        this.ichekc = icheck;
    }
}
