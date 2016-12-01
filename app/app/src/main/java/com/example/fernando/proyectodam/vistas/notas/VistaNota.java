package com.example.fernando.proyectodam.vistas.notas;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import com.example.fernando.proyectodam.R;
import com.example.fernando.proyectodam.contrato.ContratoNota;
import com.example.fernando.proyectodam.databinding.ActivityNotaBinding;
import com.example.fernando.proyectodam.dialogo.DialogoCarga;
import com.example.fernando.proyectodam.dialogo.DialogoPonerNotificacion;
import com.example.fernando.proyectodam.dialogo.DialogoQuitarNotificacionNota;
import com.example.fernando.proyectodam.dialogo.OnEliminarNotificacionNotaDialogListener;
import com.example.fernando.proyectodam.pojo.LocationObject;
import com.example.fernando.proyectodam.pojo.Nota;
import com.example.fernando.proyectodam.util.Fecha.UtilFecha;
import com.example.fernando.proyectodam.util.Ficheros.AsyncResponse;
import com.example.fernando.proyectodam.util.Ficheros.UtilImage;
import com.example.fernando.proyectodam.util.Ficheros.UtilPdfAdapter;
import com.example.fernando.proyectodam.util.Mapas.MapService;
import com.example.fernando.proyectodam.vistas.mapas.VistaMapaNota;
import com.frosquivel.magicalcamera.MagicalCamera;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import petrov.kristiyan.colorpicker.ColorPicker;


public class VistaNota extends AppCompatActivity implements ContratoNota.InterfaceVista,
                                                 OnEliminarNotificacionNotaDialogListener,
                                                 AsyncResponse,
                                                 GoogleApiClient.ConnectionCallbacks,
                                                 GoogleApiClient.OnConnectionFailedListener,
                                                 LocationListener {

    private PresentadorNota presentador;
    private Nota nota = new Nota();

    private Toolbar toolbar;

    //Identificadores para obtener las imagenes y el audio
    private static final int REQUEST_IMAGE_GALLERY  = 1;
    private static final int REQUEST_IMAGE_CAPTURE  = 2;
    private static final int SPEECH_REQUEST_CODE    = 3;
    private static final int MAP_REQUEST            = 4;

    private MagicalCamera camera;
    private static final int QUALITY                = 1500;

    //Dialogo utilizado para la carga de imagenes
    private DialogoCarga mProgressDialog;

    //Atributo utilizado para mostrar los botones con un layout personalizado
    private int alpha;

    //Binding que identifica los componentes del layout y une sus valores a los atributos de la nota
    private ActivityNotaBinding binding;

    //Elementos necesarios para almacenar la localicacion
    private GoogleApiClient mGoogleApiClient;
    private final String[] PERMISSIONS      = new String[]{ Manifest.permission.ACCESS_FINE_LOCATION,
                                                            Manifest.permission.ACCESS_COARSE_LOCATION};
    private final int LOCATION_PERMISSIONS  = 1;
    private LocationObject locationObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Siempre comprueba si estamos en una tablet

        if ( getResources().getBoolean(R.bool.isTablet) ) setWindowTablet();

        presentador = new PresentadorNota(this);

        if (savedInstanceState != null) {

            nota        = savedInstanceState.getParcelable("nota");
            alpha       = savedInstanceState.getInt("alpha");

        }
        else
        {
            Bundle b = getIntent().getExtras();

            if( b != null ) {

                nota        = b.getParcelable("nota");
                byte[] res  = b.getByteArray("imagen");
                alpha       = 0;

                if ( res != null ) {

                    Bitmap bmp  = UtilImage.getBitmapFromByteArray(res);
                    nota.setImagen(bmp);
                }
            }

        }

        //Debemos de agregar la direccion
        this.setPlace();

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView( this, R.layout.activity_nota);
        binding.setNota(nota);

        init();
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

        saveNota();
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

        outState.putParcelable("nota", nota);
        outState.putInt("alpha", alpha);
    }


    @Override
    public void mostrarEliminarNotificacionNota() {

        DialogoQuitarNotificacionNota dialog = DialogoQuitarNotificacionNota.newInstance(nota);
        dialog.show(getSupportFragmentManager(), "Eliminar");
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
                    VistaNota.this.finish();
                    NavUtils.navigateUpTo(this, upIntent);
                }

                return true;

            }

            case R.id.maps: {

                Intent i = new Intent(this, VistaMapaNota.class);

                //Antes de cargar generar la notificacion debemos de guardar los cambios
                saveNota();

                Bundle b = new Bundle();

                b.putLong("id", nota.getId());
                b.putBoolean("historial", false);
                i.putExtras(b);
                startActivityForResult(i, MAP_REQUEST);

                break;
            }

            case R.id.notification : {

                //Cerramos el teclado
                View view = VistaNota.this.getCurrentFocus();

                view.clearFocus();

                if (view != null) {

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                DialogoPonerNotificacion dialog = DialogoPonerNotificacion.newInstance(nota);
                dialog.show( getSupportFragmentManager(), "Fecha");

                break;
            }

            case R.id.share : {

                //Imprimimos PDF y lo compartimos
                nota.setTitulo(binding.etTitulo.getText().toString());
                nota.setNota(binding.etNota.getText().toString());

                new UtilPdfAdapter.ImprimirNota(this, item, true, this).execute(nota);

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

                        nota.setColor(color);
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

    private void saveNota() {

        long r = presentador.onSaveNota(nota);

        if(r > 0 & nota.getId() == 0){

            nota.setId(r);
        }

        //Asignamos el titulo
        locationObject.setIdE(nota.getId());
        locationObject.setTitulo(nota.getTitulo());
        locationObject.setEmail(nota.getCorreo());

    }


    private void init() {

        toolbar         = (Toolbar) findViewById(R.id.toolbarNotaLista);
        camera          = new MagicalCamera(this, QUALITY);

        //Dialogo modal que se muestra mientras se carga la imagen
        mProgressDialog = new DialogoCarga();
        mProgressDialog.setCancelable(false);
        mProgressDialog.setRetainInstance(true);

        //Objeto que trabaja con la localizacion
        locationObject = new LocationObject(0,0,"",0,"", 0);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.frameLayoutButton.getBackground().setAlpha(alpha);

        //Esta funcion se ejecuta cuando el dispositivo gira
        if ( alpha != 0 ) {

            binding.frameLayoutButton.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    binding.menuFab.collapse();
                    return true;
                }
            });

        }

        binding.menuFab.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.
                                                                OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {

                //Cerramos el teclado
                View view = VistaNota.this.getCurrentFocus();

                view.clearFocus();

                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                //Desplegamos la animacion del menu

                /*
                *   Agregamos un efecto circular a la aparicion del layout
                *   si la API es mayou o igual a la 21
                * */

                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {

                    View v = binding.frameLayoutButton;
                    int centerX = (v.getLeft() + v.getRight())/2;
                    int centerY = (v.getTop() + v.getBottom())/2;

                    int startRadius = 0;
                    // get the final radius for the clipping circle
                    int endRadius = Math.max(v.getWidth(), v.getHeight());

                    // create the animator for this view (the start radius is zero)
                    Animator anim = ViewAnimationUtils.createCircularReveal(v, centerX, centerY,
                                                                            startRadius, endRadius);

                    anim.setDuration(800);
                    anim.start();

                }

                // make the view visible and start the animation
                VistaNota.this.alpha = 230;

                binding.frameLayoutButton.getBackground().setAlpha(VistaNota.this.alpha);
                binding.frameLayoutButton.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        binding.menuFab.collapse();
                        return true;
                    }
                });
            }

            @Override
            public void onMenuCollapsed() {

                VistaNota.this.alpha = 0;
                binding.frameLayoutButton.getBackground().setAlpha(VistaNota.this.alpha);
                binding.frameLayoutButton.setOnTouchListener(null);
            }
        });

        binding.accionGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.setType("image/*");
                startActivityForResult(i.createChooser(i, "Seleccione una imagen"), REQUEST_IMAGE_GALLERY);

                //Cerramos el menu
                binding.menuFab.collapseImmediately();

            }
        });

        binding.accionCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.menuFab.collapseImmediately();

                camera.takePhoto();

                /*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }*/
            }
        });

        binding.accionSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showGoogleInputDialog();
                binding.menuFab.collapseImmediately();
            }
        });

        binding.ivImagenNota.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                nota.setImagen(null);
                return true;
            }
        });


        binding.fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VistaNota.this.presentador.onShowEliminarNotificacionNota();
            }
        });

        binding.notmap.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                presentador.onEliminarMapNotificacionNota(nota);
                Snackbar.make(binding.menuFab, getResources().getString(R.string.msg_notif_map_del),
                              Snackbar.LENGTH_SHORT ).show();
            }
        });

        binding.accionMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent( VistaNota.this, VistaMapaNota.class);

                Bundle b = new Bundle();

                b.putLong("id", nota.getId());
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

    public void showGoogleInputDialog() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getResources().getString(R.string.message_speech));

        try
        {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        }
        catch (ActivityNotFoundException a) {

            Snackbar.make(binding.menuFab, getResources().getString(R.string.message_error_speech),
                          Snackbar.LENGTH_SHORT).show();
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

        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == Activity.RESULT_OK ) {

            switch ( requestCode ) {

                case SPEECH_REQUEST_CODE : {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    nota.setNota(result.get(0));

                    break;
                }

                case REQUEST_IMAGE_GALLERY : {

                    new ImgHilo().execute(data);
                    break;
                }

                case MAP_REQUEST : {

                    String loc = data.getStringExtra("localizacion");

                    if( !loc.isEmpty() ) {

                        nota.setMap_not(loc);
                        setPlace();

                        saveNota();
                        startService(new Intent(this, MapService.class));
                    }

                    break;
                }
                default : {

                    camera.resultPhoto(requestCode, resultCode, data);

                    new ImgHilo().execute(camera.getMyPhoto());
                    break;
                }
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
    public void onEliminarPossitiveButtonCLick(Nota n) {

        this.presentador.onEliminarNotificacionNota(n);
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

    public class ImgHilo extends AsyncTask< Object, Void, Bitmap > {

        @Override
        protected void onPreExecute() {

            mProgressDialog.show( VistaNota.this.getSupportFragmentManager(), "Cargar");
        }

        @Override
        protected Bitmap doInBackground(Object... params) {

            Bitmap bmp = null;

            if ( params[0] instanceof Intent ) {

                try
                {
                    bmp =   MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),
                            ((Intent)params[0]).getData());

                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                bmp = (Bitmap) params[0];
            }



            return UtilImage.getImageResize(bmp, 250, 250 );
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            mProgressDialog.dismiss();
            nota.setImagen(bitmap);
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
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(500);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    private void setPlace() {

        String loc = nota.getMap_not();

        if ( !loc.isEmpty() ) {

            try
            {
                Geocoder geocoder;
                List<Address> direcciones;
                geocoder = new Geocoder( this, Locale.getDefault());

                LatLng lat = new GsonBuilder().create().fromJson( nota.getMap_not(), new TypeToken<LatLng>(){}.getType());
                direcciones= geocoder.getFromLocation( lat.latitude, lat.longitude, 1);

                if (direcciones.size() > 0) {

                    nota.setPlace(direcciones.get(0).getAddressLine(0));
                }
            }
            catch ( IOException e ){}
        }

    }
}