package com.example.fernando.proyectodam.vistas.mapas;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.fernando.proyectodam.R;
import com.example.fernando.proyectodam.contrato.ContratoMapas;
import com.example.fernando.proyectodam.pojo.LocationObject;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


public class VistaMapa extends FragmentActivity implements ContratoMapas.InterfaceVista,
                                                            OnMapReadyCallback,
                                                            GoogleApiClient.ConnectionCallbacks,
                                                            GoogleApiClient.OnConnectionFailedListener,
                                                            LocationListener {

    private PresentadorMapas presentador;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private final String[] PERMISSIONS      = new String[]{ Manifest.permission.ACCESS_FINE_LOCATION,
                                                            Manifest.permission.ACCESS_COARSE_LOCATION};
    private final int LOCATION_PERMISSIONS  = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_mapa);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        init();
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    private void init() {

        presentador = new PresentadorMapas(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    /* metodos de las interfaces */

    //OnMapReadyCallback
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        presentador.pedirLocalizacion();

    }

    //GoogleApiClient.ConnectionCallbacks Conexion
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        comprobarPermisos();
    }

    //GoogleApiClient.ConnectionCallbacks Desconexion
    @Override
    public void onConnectionSuspended(int i) {

    }


    //GoogleApiClient.OnConnectionFailedListener Falla la conexion con la API
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if ( requestCode == LOCATION_PERMISSIONS ) {

            comprobarPermisos();
        }
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
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void mostrarLocalizacion(List<LocationObject> list) {

        //Debemos de pintar cada localizacion

        LatLng punto = null;

        for ( LocationObject locationObject : list ) {

            punto = new LatLng(locationObject.getLatitud(), locationObject.getLongitud());
            mMap.addMarker(new MarkerOptions().position(punto).title(locationObject.getTitulo()));
        }

        if ( punto != null ) {

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(punto, 15));

        }
    }
}
