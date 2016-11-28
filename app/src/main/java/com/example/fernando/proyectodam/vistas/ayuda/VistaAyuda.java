package com.example.fernando.proyectodam.vistas.ayuda;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.example.fernando.proyectodam.R;
import com.example.fernando.proyectodam.util.Web.UtilWeb;
import com.example.fernando.proyectodam.vistas.notas.VistaNota;

/**
 * Created by Fernando on 25/11/2016.
 */

public class VistaAyuda extends AppCompatActivity {

    private LinearLayout ll1;
    private LinearLayout ll2;
    private LinearLayout ll3;
    private LinearLayout ll4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);

        init();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ( keyCode == KeyEvent.KEYCODE_BACK ){

            Intent upIntent = NavUtils.getParentActivityIntent(this);

            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {

                // This activity is NOT part of this app's task, so create a new task
                // when navigating up, with a synthesized back stack.
                TaskStackBuilder.create(this)
                        // Add all of this activity's parents to the back stack
                        .addNextIntentWithParentStack(upIntent)
                        // Navigate up to the closest parent
                        .startActivities();
            } else {
                // This activity is part of this app's task, so simply
                // navigate up to the logical parent activity.
                VistaAyuda.this.finish();
                NavUtils.navigateUpTo(this, upIntent);
            }

        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){


            case android.R.id.home: {

                Intent upIntent = NavUtils.getParentActivityIntent(this);

                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {

                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    VistaAyuda.this.finish();
                    NavUtils.navigateUpTo(this, upIntent);
                }

                return true;

            }
        }

        return false;
    }

    private void init() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarNotaLista);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ll1 = (LinearLayout) findViewById(R.id.nota);
        ll2 = (LinearLayout) findViewById(R.id.lista);
        ll3 = (LinearLayout) findViewById(R.id.compartir);
        ll4 = (LinearLayout) findViewById(R.id.sugerencias);

        ll1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(VistaAyuda.this, VistaAyudaDetalle.class);
                i.putExtra("web", UtilWeb.URL_NOTA_HELP);

                startActivity(i);
            }
        });

        ll2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(VistaAyuda.this, VistaAyudaDetalle.class);
                i.putExtra("web", UtilWeb.URL_LISTA_HELP);

                startActivity(i);
            }
        });

        ll3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(VistaAyuda.this, VistaAyudaDetalle.class);
                i.putExtra("web", UtilWeb.URL_SHARE_HELP);

                startActivity(i);
            }
        });

        ll4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(VistaAyuda.this, VistaAyudaDetalle.class);
                i.putExtra("web", UtilWeb.URL_SUG_HELP);

                startActivity(i);
            }
        });
    }
}
