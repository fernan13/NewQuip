package com.example.fernando.proyectodam.adaptadores.ExpandableRecyclerView;

import com.example.fernando.proyectodam.pojo.ElementoLista;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by Fernando on 13/11/2016.
 */

    public class GrupoItemMarcados extends ExpandableGroup<ElementoLista>{

        public GrupoItemMarcados(String title, List<ElementoLista> items) {
            super(title, items);
        }

    }
