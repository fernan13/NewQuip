package com.example.fernando.proyectodam.vistas.ayuda;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.fernando.proyectodam.R;

/**
 * Created by Fernando on 25/11/2016.
 */

public class VistaAyudaDetalle extends AppCompatActivity {

    private WebView view;
    private String html;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_description);

        init();
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
                    VistaAyudaDetalle.this.finish();
                    NavUtils.navigateUpTo(this, upIntent);
                }

                return true;

            }
        }

        return false;
    }


    private void init(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarNotaLista);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        view = (WebView)findViewById(R.id.webview);
        html = getIntent().getStringExtra("web");

        WebSettings settings = view.getSettings();
        settings.setJavaScriptEnabled(true);
        view.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        if ( !html.isEmpty() ) view.loadUrl(html);
    }
}
