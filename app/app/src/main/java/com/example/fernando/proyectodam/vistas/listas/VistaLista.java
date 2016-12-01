package com.example.fernando.proyectodam.vistas.listas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.fernando.proyectodam.R;
import com.example.fernando.proyectodam.adaptadores.AdaptadorVistaLista;
import com.example.fernando.proyectodam.adaptadores.ExpandableRecyclerView.AdaptadorItemMarcados;
import com.example.fernando.proyectodam.adaptadores.ExpandableRecyclerView.GrupoItemMarcados;
import com.example.fernando.proyectodam.adaptadores.ICheckItem;
import com.example.fernando.proyectodam.contrato.ContratoLista;
import com.example.fernando.proyectodam.databinding.ActivityListaBinding;
import com.example.fernando.proyectodam.dialogo.DialogoPonerNotificacion;
import com.example.fernando.proyectodam.dialogo.DialogoQuitarNotificacionLista;
import com.example.fernando.proyectodam.dialogo.OnEliminarNotificacionListaDialogListener;
import com.example.fernando.proyectodam.pojo.ElementoLista;
import com.example.fernando.proyectodam.pojo.Lista;
import com.example.fernando.proyectodam.pojo.LocationObject;
import com.example.fernando.proyectodam.util.Fecha.UtilFecha;
import com.example.fernando.proyectodam.util.Ficheros.AsyncResponse;
import com.example.fernando.proyectodam.util.Ficheros.UtilPdfAdapter;
import com.example.fernando.proyectodam.util.Mapas.MapService;
import com.example.fernando.proyectodam.vistas.mapas.VistaMapa;
import com.example.fernando.proyectodam.vistas.mapas.VistaMapaNota;
import com.example.fernando.proyectodam.vistas.notas.VistaNota;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import petrov.kristiyan.colorpicker.ColorPicker;

public class VistaLista extends AppCompatActivity implements ContratoLista.InterfaceVista,
                                                  OnEliminarNotificacionListaDialogListener,
                                                  AsyncResponse, ICheckItem,
                                                  GoogleApiClient.ConnectionCallbacks,
                                                  GoogleApiClient.OnConnectionFailedListener,
                                                  LocationListener {

    private Lista lista = new Lista();
    private PresentadorLista presentador;
    private Toolbar toolbar;
    //Componentes Necesarios Para el Trabajo con Las Listas

    //Componentes Varios
    private ActivityListaBinding binding;

    //RecyclerView
    private RecyclerView rv;
    private AdaptadorVistaLista adapter;

    //RecyclerViewMarcados
    private RecyclerView rvMarcados;
    private AdaptadorItemMarcados adapterMarcados;

    //Elementos necesarios para almacenar la localicacion
    private GoogleApiClient mGoogleApiClient;
    private final String[] PERMISSIONS      = new String[]{ Manifest.permission.ACCESS_FINE_LOCATION,
                                                            Manifest.permission.ACCESS_COARSE_LOCATION};
    private final int LOCATION_PERMISSIONS  = 1;
    private static final int MAP_REQUEST    = 2;
    private LocationObject locationObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if ( getResources().getBoolean(R.bool.isTablet)) {

            setWindowTablet();
        }

        presentador = new PresentadorLista(this);

        if (savedInstanceState != null) {

            lista = savedInstanceState.getParcelable("lista");
        }
        else
        {
            Bundle b = getIntent().getExtras();

            if( b != null ) {

                lista   = b.getParcelable("lista");
            }
        }

        setPlace();

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView( this, R.layout.activity_lista);
        binding.setLista(lista);

        init();
        mostrarLista(lista);

    }

    //Metodo utilizado para mostrar una activity como un dialogo utilizando el theme declarado en la activity
    private void setWindowTablet(){

        if ( getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_0 ) {

            requestWindowFeature(Window.FEATURE_ACTION_BAR);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                                 WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.height = 800; //fixed height
            params.width = 900; //fixed width
            params.alpha = 1.0f;
            params.dimAmount = 0.5f;
            getWindow().setAttributes(params);

        }
    }


    @Override
    protected void onPause() {

        saveLista();
        presentador.saveLocation(locationObject);
        presentador.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {

        presentador.onResume();
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("lista", lista);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // Respond to the action bar's Up/Home button
            case android.R.id.home : {

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
                    NavUtils.navigateUpTo(this, upIntent);
                }

                return true;
            }

            case R.id.maps: {

                Intent i = new Intent(this, VistaMapa.class);

                //Antes de cargar generar la notificacion debemos de guardar los cambios
                saveLista();

                Bundle b = new Bundle();
                b.putLong("id", lista.getId());

                i.putExtras(b);
                startActivityForResult(i, MAP_REQUEST);

                break;
            }

            case R.id.notification : {


                DialogoPonerNotificacion dialog = DialogoPonerNotificacion.newInstance(lista);
                dialog.show( getSupportFragmentManager(), "Fecha");

                break;
            }

            case R.id.share : {

                //Imprimimos PDF y lo compartimos
                lista.setTitulo(binding.edTitLista.getText().toString());
                lista.setItems(adapter.getItems());

                item.setEnabled(false);
                new UtilPdfAdapter.ImprimirLista(this, item, true, this).execute(lista);

                break;

            }

            case R.id.color_picker : {

                //Dialogo modal que nos permite cambiar el color de nuestra nota
                final ColorPicker mColorDialog    = new ColorPicker(this);
                mColorDialog.setTitle(getResources().getString(R.string.color_dialog));
                mColorDialog.setRoundColorButton(true);
                mColorDialog.setColors(getResources().getIntArray(R.array.colors));
                mColorDialog.setColorButtonTickColor(Color.parseColor("#A0A0A0"));
                mColorDialog.setColumns(4);

                mColorDialog.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {

                        lista.setColor(color);
                    }

                    @Override
                    public void onCancel() {

                        mColorDialog.dismissDialog();
                    }
                });

                mColorDialog.show();

                break;
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void mostrarLista(Lista lista) {

        if ( lista.getItems().isEmpty() ) {

            adapter.addNewView();
        }
    }

    @Override
    public void mostrarEliminarNotificacionLista() {

        DialogoQuitarNotificacionLista dialog = DialogoQuitarNotificacionLista.newInstance(lista);
        dialog.show( getSupportFragmentManager(), "Eliminar");
    }

    private void saveLista() {

        Set<ElementoLista> set= new HashSet<ElementoLista>();

        set.addAll(adapter.getItems());
        set.addAll(adapterMarcados.getGroups().get(0).getItems());

        lista.setItems(new ArrayList(set));

        long r = presentador.onSaveLista(lista);

        if ( r > 0 & lista.getId() == 0 ) {

            lista.setId(r);
        }

        //Obtenemos el titulo de la lista
        locationObject.setIdE(lista.getId());
        locationObject.setTitulo(lista.getTitulo());
        locationObject.setEmail(lista.getCorreo());
    }

    private void init() {

        toolbar             = (Toolbar) findViewById(R.id.toolbarNotaLista);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Objeto que trabaja con la localizacion
        locationObject = new LocationObject(0,0,"",0, "", 0);

        //Adaptadores

        ArrayList<ElementoLista> items = lista.getItems();
        ArrayList<ElementoLista> itemsMarcados      = new ArrayList();
        ArrayList<ElementoLista> itemsNoMarcados    = new ArrayList();

        if ( !items.isEmpty() ) {

            for ( ElementoLista el : items ) {

                if ( el.isCheck() ) {

                    itemsMarcados.add(el);
                }
                else
                {
                    itemsNoMarcados.add(el);
                }
            }
        }

        //Generamos los adaptaroes
        adapter = new AdaptadorVistaLista(itemsNoMarcados);
        adapter.setICheck(this);

        GrupoItemMarcados group     = new GrupoItemMarcados("",itemsMarcados);
        ArrayList<GrupoItemMarcados> groups = new ArrayList();
        groups.add(group);

        adapterMarcados             = new AdaptadorItemMarcados(this, groups);
        adapterMarcados.setICheck(this);

        //RecyclerView
        rv                  = binding.rvActivityLista;
        rvMarcados          = binding.rvMarcados;

        rv.setLayoutManager( new LinearLayoutManager( this, LinearLayoutManager.VERTICAL, false));
        rvMarcados.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        rv.setAdapter(adapter);
        rvMarcados.setAdapter(adapterMarcados);

        binding.lyAddItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                VistaLista.this.adapter.addNewView();
            }
        });

        binding.fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VistaLista.this.presentador.onShowEliminarNotificacionLista();
            }
        });

        binding.mapLista.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                presentador.onEliminarMapNotificacionLista(lista);
                Snackbar.make(rvMarcados, getResources().getString(R.string.msg_notif_map_del),
                              Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.accionMap.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent( VistaLista.this, VistaMapaNota.class);

                Bundle b = new Bundle();

                b.putLong("id", lista.getId());
                b.putBoolean("historial", true);
                i.putExtras(b);
                startActivityForResult(i, MAP_REQUEST);
            }
        });

        //Instancia del objeto que gestiona la api de mapas
        if ( mGoogleApiClient == null ) {

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ( resultCode == RESULT_OK && requestCode == MAP_REQUEST ) {

            String loc = data.getStringExtra("localizacion");
            System.out.println(loc);
            if( !loc.isEmpty() ) {

                lista.setMap_not(loc);
                setPlace();

                saveLista();
                startService( new Intent( this, MapService.class));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if ( requestCode == LOCATION_PERMISSIONS ) {

            comprobarPermisos();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        comprobarPermisos();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        locationObject.setLatitud(location.getLatitude());
        locationObject.setLongitud(location.getLongitude());
        locationObject.setFecha(UtilFecha.formatDate(Calendar.getInstance().getTime()));
    }


    @Override
    public void onEliminarPossitiveButtonCLick(Lista l) {

        this.presentador.onEliminarNotificacionLista(l);
    }

    @Override
    public void onEliminarNegativeButtonClick() {

    }

    @Override
    public void enviarInformacion(Object output) {

        //Aqui compartimos la informacion
        Uri file = (Uri) output;

        Intent waIntent = new Intent(Intent.ACTION_SEND);

        waIntent.setType("application/pdf");
        waIntent.putExtra(Intent.EXTRA_STREAM, file);
        waIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.compartir));
        startActivity(Intent.createChooser(waIntent, getResources().getString(R.string.compartir)));

    }

    @Override
    public void checkItem(ElementoLista el) {

        adapterMarcados.addNewItem(el);
    }

    @Override
    public void unCheckItem(ElementoLista el) {

        adapter.addNewItem(el);

    }

    private void comprobarPermisos(){

        //Comprobamos permisos
        boolean permiso1 = ActivityCompat.checkSelfPermission(this, PERMISSIONS[0])!= PackageManager.PERMISSION_GRANTED;
        boolean permiso2 = ActivityCompat.checkSelfPermission(this, PERMISSIONS[1])!= PackageManager.PERMISSION_GRANTED;

        if ( permiso1 || permiso2 ) {

            ActivityCompat.requestPermissions(this, PERMISSIONS,LOCATION_PERMISSIONS );
        }
        else
        {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(500);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    private void setPlace() {

        String loc = lista.getMap_not();

        if ( !loc.isEmpty() ) {

            try
            {
                Geocoder geocoder;
                List<Address> direcciones;
                geocoder = new Geocoder( this, Locale.getDefault());

                LatLng lat = new GsonBuilder().create().fromJson( lista.getMap_not(), new TypeToken<LatLng>(){}.getType());
                direcciones= geocoder.getFromLocation( lat.latitude, lat.longitude, 1);

                if (direcciones.size() > 0) {

                    lista.setPlace(direcciones.get(0).getAddressLine(0));
                }
            }
            catch ( IOException e ){}
        }

    }

}