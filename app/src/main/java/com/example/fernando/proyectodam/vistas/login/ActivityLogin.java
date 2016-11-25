package com.example.fernando.proyectodam.vistas.login;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.fernando.proyectodam.R;
import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;
import com.example.fernando.proyectodam.dialogo.DialogoCarga;
import com.example.fernando.proyectodam.gestion.GestionProviderUsuarios;
import com.example.fernando.proyectodam.util.Ficheros.UtilFicheros;
import com.example.fernando.proyectodam.util.Web.UtilWeb;
import com.example.fernando.proyectodam.vistas.main.VistaQuip;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.facebook.FacebookSdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Fernando on 05/11/2016.
 */

public class ActivityLogin extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    //Gestion de la cuenta con google
    private GoogleSignInOptions gso;
    private GoogleApiClient apiClient;
    private static final int RC_SIGN_IN   = 1;
    public static final int CHILD_INTENT  = 2;

    private SignInButton lgnG;

    //Dialogo para cargar usuario
    private DialogoCarga mProgressDialog;

    //Gestion de la cuenta con facebook
    private CallbackManager callbackManager;
    private LoginButton lgnFcb;

    private static final int PERMISSION = 1;
    private static final String[] permission = new String[]{Manifest.permission.READ_PHONE_STATE };

    //Elemento utilizado
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        //Si llega desde la actividad quip
        super.onCreate(savedInstanceState);

        //Los elementos de facebook deben de inicializarse antes
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);

        init();

    }

    private void init(){

        //Dialogo
        mProgressDialog = new DialogoCarga();
        mProgressDialog.setCancelable(false);
        mProgressDialog.setRetainInstance(true);

        //Google
        gso         =   new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestProfile()
                        .requestEmail()
                        .build();

        apiClient   =   new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();

        lgnG        = (SignInButton) findViewById(R.id.sign_in_button);

        lgnG.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Necesitamos conexion para subir la informacion al servidor
                if ( UtilWeb.getNetworkStatus(ActivityLogin.this) ) {

                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            }
        });

        //Facebook
        lgnFcb          = (LoginButton) findViewById(R.id.login_button);
        lgnFcb.setReadPermissions("email");
        lgnFcb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest graphrequest = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                if (object != null) {

                                    String email = "", imagen = "", nombre = "";

                                    try {

                                        email    = object.getString("email");
                                        imagen   = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                        nombre   = object.getString("name");

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                    if ( !email.isEmpty() ) {

                                        UtilFicheros.saveSharedPreferences(ActivityLogin.this, "usuario","nombre",nombre);

                                        String idcorreo = getIdCorreo(email);
                                        UtilFicheros.saveSharedPreferences(ActivityLogin.this, "usuario", "id_email",idcorreo);
                                        UtilFicheros.saveSharedPreferences(ActivityLogin.this, "usuario","email", email);
                                        UtilFicheros.saveSharedPreferences(ActivityLogin.this, "usuario","imagen",imagen);

                                        //Intenta registrar al usuario si no esta registrado
                                        pedirPermisosUsuario();
                                    }
                                    else
                                    {
                                        Snackbar.make(lgnFcb, getResources().getString(R.string.error_cuenta_fcb),
                                                     Snackbar.LENGTH_LONG).show();

                                        if ( AccessToken.getCurrentAccessToken() != null ) {

                                            LoginManager.getInstance().logOut();
                                        }
                                    }
                                }

                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,picture.type(large)");
                graphrequest.setParameters(parameters);
                graphrequest.executeAsync();
            }

            @Override
            public void onCancel() {

                Snackbar.make(lgnFcb, getResources().getString(R.string.cancel_login_fcb),
                              Snackbar.LENGTH_INDEFINITE)
                              .setAction("Conectar", new View.OnClickListener() {

                                  @Override
                                  public void onClick(View v) {

                                      LoginManager.getInstance().logInWithReadPermissions(
                                      ActivityLogin.this,Arrays.asList("public_profile") );

                                  }
                              }).show();

            }

            @Override
            public void onError(FacebookException error) {

                Snackbar.make(lgnFcb,getResources().getString(R.string.cancel_login_fcb),
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction("Conectar", new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {

                                    LoginManager.getInstance().logInWithReadPermissions(
                                            ActivityLogin.this,Arrays.asList("public_profile") );

                                }
                            }).show();
            }
        });
    }

    @Override
    protected void onStart() {

        super.onStart();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if ( accessToken == null ) {

            new AsyncTask< Void, Void, OptionalPendingResult<GoogleSignInResult> >(){

                @Override
                protected OptionalPendingResult doInBackground(Void... params) {

                    OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(apiClient);

                    return opr;
                }

                @Override
                protected void onPostExecute(OptionalPendingResult<GoogleSignInResult> opr) {

                    if (opr.isDone()) {

                        pedirPermisosUsuario();
                    }
                }
            }.execute();
        }
        else
        {
            pedirPermisosUsuario();
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == RESULT_OK ) {

            switch ( requestCode ) {

                case RC_SIGN_IN : {

                    new AsyncTask<Intent, Void, GoogleSignInResult>(){

                        @Override
                        protected GoogleSignInResult doInBackground(Intent... params) {

                            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(params[0]);

                            //Guardamos la informacion en las preferencias compartidas

                            GoogleSignInAccount acct = result.getSignInAccount();

                            if ( acct != null ) {

                                String usuario  = acct.getDisplayName();
                                Uri imagen      = acct.getPhotoUrl();
                                String email    = acct.getEmail();

                                UtilFicheros.saveSharedPreferences(ActivityLogin.this, "usuario", "email", email);

                                String idcorreo = getIdCorreo(email);
                                UtilFicheros.saveSharedPreferences(ActivityLogin.this, "usuario", "id_email",idcorreo);
                                UtilFicheros.saveSharedPreferences(ActivityLogin.this, "usuario", "nombre", usuario);

                                if ( imagen != null  ) {

                                    UtilFicheros.saveSharedPreferences(ActivityLogin.this, "usuario", "imagen",
                                            imagen.toString());
                                }
                                else
                                {
                                    UtilFicheros.saveSharedPreferences(ActivityLogin.this, "usuario", "imagen","");
                                }
                            }

                            return result;
                        }

                        @Override
                        protected void onPostExecute(GoogleSignInResult result) {

                            if ( result.isSuccess() ) {

                                pedirPermisosUsuario();
                            }
                            else
                            {
                                Snackbar.make(lgnG, getResources().getString(R.string.message_error_login),
                                        Snackbar.LENGTH_SHORT)
                                        .setAction( getResources().getString(R.string.message_reintentar_login),
                                                new View.OnClickListener() {

                                                    @Override
                                                    public void onClick(View v) {

                                                        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
                                                        startActivityForResult(signInIntent, RC_SIGN_IN);
                                                    }
                                                }).show();
                            }
                        }
                    }.execute(data);

                    break;

                }

                case CHILD_INTENT : {

                    if ( data.getBooleanExtra("cerrar", false) ) {

                        this.finish();
                    }

                    break;
                }

                default: {

                    callbackManager.onActivityResult(requestCode, resultCode, data);
                    break;
                }
            }

        }
    }


    //Interfaz que define un metodo mediante el cual se gestiona el fallo de inicio de sesion
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if ( mProgressDialog.isVisible() ) mProgressDialog.dismiss();

        Snackbar.make(lgnG, getResources().getString(R.string.message_error_login),
                Snackbar.LENGTH_SHORT)
                .setAction( getResources().getString(R.string.message_reintentar_login),
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
                                startActivityForResult(signInIntent, RC_SIGN_IN);
                            }
                        }).show();
    }

    private String getIdCorreo ( String correo ) {

        ContentResolver cr = getContentResolver();
        Uri uri = Uri.parse(GestionProviderUsuarios.URI_USUARIOS.toString() + "/" + correo);
        Cursor cursor = cr.query(uri, null, null, null, null);

        if ( cursor.moveToFirst() ) {

            long id =  cursor.getLong(cursor.getColumnIndex(ContratoBaseDatos.Usuarios._ID));
            return String.valueOf(id);
        }
        else
        {
            ContentValues cv = new ContentValues();
            cv.put(ContratoBaseDatos.Usuarios.CORREO, correo);

            String id = cr.insert(GestionProviderUsuarios.URI_USUARIOS, cv).getLastPathSegment();
            return id;
        }
    }

    private void pedirPermisosUsuario() {

        if (ContextCompat.checkSelfPermission(this, permission[0]) == PackageManager.PERMISSION_GRANTED ) {

            UtilWeb.subirInformacion(ActivityLogin.this, UtilWeb.URL_EMAIl, null);
            UtilWeb.bajarInformacion( ActivityLogin.this, null, UtilWeb.URL_UPDATE_SERVER, "actualizar");
            Intent i = new Intent(ActivityLogin.this, VistaQuip.class);
            startActivityForResult(i, CHILD_INTENT);
        }
        else
        {
            ActivityCompat.requestPermissions(this, permission, PERMISSION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case PERMISSION: {

                if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {

                    UtilWeb.subirInformacion(ActivityLogin.this, UtilWeb.URL_EMAIl, null);
                    UtilWeb.bajarInformacion( ActivityLogin.this, null, UtilWeb.URL_UPDATE_SERVER, "actualizar");
                    Intent i = new Intent(ActivityLogin.this, VistaQuip.class);
                    startActivityForResult(i, CHILD_INTENT);
                }
                else
                {
                    Snackbar.make(  lgnFcb, getResources().getString(R.string.aceptar_permisos),
                            Snackbar.LENGTH_INDEFINITE )
                            .setAction(R.string.conceder_permisos, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pedirPermisosUsuario();
                                }
                            }).show();
                }
                break;
            }
        }
    }
}
