package com.example.fernando.proyectodam.vistas.main;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fernando.proyectodam.adaptadores.ItemTouchHelperAdapter;
import com.example.fernando.proyectodam.adaptadores.SimpleItemTouchHelperCallback;
import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;
import com.example.fernando.proyectodam.gestion.GestionProviderElementos;
import com.example.fernando.proyectodam.util.Ficheros.AsyncResponse;
import com.example.fernando.proyectodam.util.Ficheros.UtilFicheros;
import com.example.fernando.proyectodam.util.Ficheros.UtilImage;
import com.example.fernando.proyectodam.util.Web.AsyncWebResponse;
import com.example.fernando.proyectodam.util.Web.UtilWeb;
import com.example.fernando.proyectodam.vistas.ayuda.VistaAyuda;
import com.example.fernando.proyectodam.vistas.mapas.VistaMapa;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.example.fernando.proyectodam.adaptadores.CursorAdaptadorQuip;
import com.example.fernando.proyectodam.contrato.ContratoMain;
import com.example.fernando.proyectodam.dialogo.DialogoBorrarLista;
import com.example.fernando.proyectodam.dialogo.DialogoRecuperarLista;
import com.example.fernando.proyectodam.dialogo.DialogoRecuperarNota;
import com.example.fernando.proyectodam.dialogo.OnBorrarDialogListener;
import com.example.fernando.proyectodam.dialogo.OnRecuperarDialogListener;
import com.example.fernando.proyectodam.pojo.Lista;
import com.example.fernando.proyectodam.pojo.Nota;
import com.example.fernando.proyectodam.dialogo.DialogoBorrarNota;
import com.example.fernando.proyectodam.vistas.notas.VistaNota;
import com.example.fernando.proyectodam.vistas.listas.VistaLista;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import com.example.fernando.proyectodam.R;

public class VistaQuip extends AppCompatActivity implements ContratoMain.InterfaceVista ,
                                                            OnBorrarDialogListener,
                                                            OnRecuperarDialogListener,
                                                            AsyncResponse, AsyncWebResponse,
                                                            ItemTouchHelperAdapter,
                                                            LoaderManager.LoaderCallbacks<Cursor> {
    //Google
    private GoogleSignInOptions gso     = null;
    private GoogleApiClient apiClient   = null;

    //Gestion interfaz
    private SwipeRefreshLayout swipe;
    private CursorAdaptadorQuip adaptador;
    private PresentadorQuip presentador;
    private RecyclerView rv;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FloatingActionButton fab;

    //Elementos variantes segun el layout
    private boolean tablet;

    private BottomBar bb;
    private FloatingActionsMenu fabMenu;
    private com.getbase.floatingactionbutton.FloatingActionButton fabNota;
    private com.getbase.floatingactionbutton.FloatingActionButton fabLista;

    //Informacion Usuario Hader Navigation Drawer
    private CircleImageView circleImageView;
    private TextView textViewUser, textViewEmail;
    private int identificadorFiltro;

    //Identificador Loader
    private static final int IDENTIFICADOR_LOADER = 1;

    //Variable utilizada para mantener el layout asociado al recyclerview
    private boolean isLinearLayoutManager = true;

    //Listener
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongRecoverClickListener;
    private ItemTouchHelper.Callback callback;
    private ItemTouchHelper touchHelper;

    //Busqueda
    private SearchView search;
    private boolean isSearch;
    private String textSearch;


    private static final int MAPS_INT   = 1;
    private static final int HELP_INT   = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity_quiip);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbarUp);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24px);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if ( savedInstanceState != null ) {

            identificadorFiltro     = savedInstanceState.getInt("identificadorFiltro");
            isLinearLayoutManager   = savedInstanceState.getBoolean("isLinearLayoutManager");
            isSearch                = savedInstanceState.getBoolean("isSearch");
            textSearch              = savedInstanceState.getString("textSearch");

        }
        else
        {
            identificadorFiltro     = 0;
            isSearch                = true;
            textSearch              = "";
            /*
                Comprobamos si existe algun bundle porque si existe se ha iniciado la actividad
                a traves de una notificacion
            */

            Bundle b = getIntent().getExtras();

            if ( b != null ) {

                Object o = b.getParcelable("elemento");
                Intent i;
                Bundle bl;

                if ( o instanceof Nota ) {

                    i   = new Intent(this, VistaNota.class);
                    bl  = new Bundle();

                    Nota n = (Nota)o;

                    bl.putParcelable("nota", n);
                    bl.putByteArray("imagen", b.getByteArray("imagen"));

                    i.putExtras(bl);

                    startActivity(i);
                }

                if ( o instanceof Lista )
                {
                    i   = new Intent(this, VistaLista.class);
                    bl  = new Bundle();

                    bl.putParcelable("lista", (Lista)o);

                    i.putExtras(bl);
                    startActivity(i);
                }

            }
        }

        init();
    }

    @Override
    protected void onResume() {

        getSupportLoaderManager().restartLoader(IDENTIFICADOR_LOADER, null, this);
        super.onResume();
    }


    @Override
    protected void onPause() {

        presentador.onPause();
        super.onPause();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putInt("identificadorFiltro", identificadorFiltro);
        outState.putBoolean( "isLinearLayoutManager" , rv.getLayoutManager() instanceof LinearLayoutManager);

        if ( search != null ) {

            outState.putBoolean("isSearch", search.isIconified() );
            outState.putString( "textSearch", textSearch );
        }

    }

    //Asignamos menu contextual
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_activity_quip, menu);

        MenuItem item   = menu.findItem(R.id.action_search);
        search          = (SearchView) MenuItemCompat.getActionView(item);
        search.setQueryHint(getResources().getString(R.string.action_search));

        EditText et     = (EditText)  search.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        ImageView ivS   = (ImageView) search.findViewById(android.support.v7.appcompat.R.id.search_button);
        ImageView ivC   = (ImageView) search.findViewById(android.support.v7.appcompat.R.id.search_close_btn);

        et.setTextColor(Color.WHITE);
        et.setHintTextColor(Color.WHITE);
        ivS.setImageResource(R.drawable.ic_search_black_24px);
        ivC.setImageResource(R.drawable.ic_close_black_24px);

        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                getSupportLoaderManager().restartLoader(1, null, VistaQuip.this);

                return false;
            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                textSearch = query;

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                //Aqui debemos de realizar la consulta a la BBDD

                Bundle b = null;
                String selection = "";
                String[] selectionArgs = null;

                //Lo almacenamos por si gira el dispositivo guardar la informacion
                textSearch = newText;

                if ( !newText.isEmpty() ){

                    String like         = ContratoBaseDatos.Elementos.TITULO + " like '%" + newText + "%' OR " +
                                          ContratoBaseDatos.Elementos.CONTENIDO + " like '%" + newText + "%'";

                    String idcorreo     = UtilFicheros.getValueFromSharedPreferences( VistaQuip.this,
                                          "usuario", "id_email");

                    switch ( identificadorFiltro ) {

                        case 0 : {

                            selection =     ContratoBaseDatos.Elementos.PAPELERA + " = ? AND " +
                                            ContratoBaseDatos.Elementos.TABLA + "." +
                                            ContratoBaseDatos.Elementos.IDCORREO + " = ? AND (" +
                                            like + ")";

                            selectionArgs = new String[]{ "0", idcorreo};

                            break;
                        }

                        case 1 : {

                            selection =     ContratoBaseDatos.Elementos.TIPO + " = ? AND " +
                                            ContratoBaseDatos.Elementos.PAPELERA + " = ? AND " +
                                            ContratoBaseDatos.Elementos.TABLA + "." +
                                            ContratoBaseDatos.Elementos.IDCORREO + " = ? AND (" +
                                            like + ")";


                            selectionArgs = new String[]{ "0", "0", idcorreo};

                            break;
                        }

                        case 2 : {

                            selection =     ContratoBaseDatos.Elementos.TIPO + " = ? AND " +
                                            ContratoBaseDatos.Elementos.PAPELERA + " = ? AND " +
                                            ContratoBaseDatos.Elementos.TABLA + "." +
                                            ContratoBaseDatos.Elementos.IDCORREO + " = ? AND (" +
                                            like + ")";


                            selectionArgs = new String[]{ "1", "0", idcorreo};

                            break;
                        }


                        case 3 : {


                            selection =     ContratoBaseDatos.Elementos.FECHA_NOT + " != ? AND " +
                                            ContratoBaseDatos.Elementos.TABLA + "." +
                                            ContratoBaseDatos.Elementos.IDCORREO + " = ? AND (" +
                                            like + ")";

                            selectionArgs = new String[]{"", idcorreo};

                            break;
                        }

                    }

                    b = new Bundle();
                    b.putString("selection", selection);
                    b.putStringArray("args", selectionArgs);

                }

                getSupportLoaderManager().restartLoader(1, b, VistaQuip.this);


                return true;
            }
        });


        if ( !isSearch ) {

            search.setQuery(textSearch, true);
            search.setIconified(false);
        }
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.miChangeLayout : {

                if (rv.getLayoutManager() instanceof StaggeredGridLayoutManager) {

                    rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                    item.setIcon(R.drawable.ic_view_quilt_black_24px);
                    isLinearLayoutManager = true;

                } else {

                    rv.setLayoutManager(new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL));
                    item.setIcon(R.drawable.ic_view_stream_black_24px);
                    isLinearLayoutManager = false;

                }

                return true;

            }

            case android.R.id.home:{

                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }

            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ( keyCode == KeyEvent.KEYCODE_BACK ){


            if ( !search.isIconified() ) {

                search.setQuery("", false);
                search.setIconified(true);

            }
            else
            {

                Intent intent = getParentActivityIntent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("cerrar", true);
                setResult(RESULT_OK, intent);

                this.finish();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void mostrarAgregarNota() {

        Intent i = new Intent(this, VistaNota.class);
        startActivity(i);

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {

            //Transiciones para lanzar activities
            overridePendingTransition( R.anim.fade_in, R.anim.fade_out);
        }

        if ( !search.isIconified() ) {

            search.setQuery("", false);
            search.setIconified(true);

        }
    }

    @Override
    public void mostrarEditarNota(Nota n) {

        Intent i = new Intent(this, VistaNota.class);
        Bundle b = new Bundle();

        Bitmap bmp = n.getImagen();
        n.setImagen(null);
        byte[] res = null;

        if ( bmp != null ) {

            res = UtilImage.getByteArrayFromBitmap(bmp);
        }

        b.putParcelable("nota", n);
        b.putByteArray("imagen", res);

        i.putExtras(b);
        startActivity(i);

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {

            //Transiciones para lanzar activities
            overridePendingTransition( R.anim.fade_in, R.anim.fade_out);
        }

        if ( !search.isIconified() ) {

            search.setQuery("", false);
            search.setIconified(true);

        }
    }

    @Override
    public void mostrarConfirmarBorrarNota(Nota n) {

        DialogoBorrarNota fragmentBorrar = DialogoBorrarNota.newInstance(n);
        fragmentBorrar.show(getSupportFragmentManager(), "Dialogo borrar");

    }

    @Override
    public void onBorrarPossitiveButtonClick(final Nota n) {

        presentador.onPapeleraNota(n);

        Snackbar.make( fab, "Nota enviada a la papelera", Snackbar.LENGTH_LONG).setAction("Deshacer", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                presentador.onRecuperarNota(n);

                getSupportLoaderManager().restartLoader(IDENTIFICADOR_LOADER, null, VistaQuip.this);

            }
        }).show();

        getSupportLoaderManager().restartLoader(IDENTIFICADOR_LOADER, null, this);
    }


    @Override
    public void mostrarAgregarLista() {

        Intent i = new Intent(this, VistaLista.class);
        startActivity(i);

        //Transiciones para lanzar activities
        overridePendingTransition( R.anim.fade_in, R.anim.fade_out);

        if ( !search.isIconified() ) {

            search.setQuery("", false);
            search.setIconified(true);

        }

    }

    @Override
    public void mostrarEditarLista(Lista lista) {

        Intent i = new Intent(this, VistaLista.class);
        Bundle b = new Bundle();
        b.putParcelable("lista", lista);
        i.putExtras(b);
        startActivity(i);

        //Transiciones para lanzar activities
        overridePendingTransition( R.anim.fade_in, R.anim.fade_out);

        if ( !search.isIconified() ) {

            search.setQuery("", false);
            search.setIconified(true);

        }

    }

    @Override
    public void mostrarConfirmarBorrarLista(Lista lista) {

        DialogoBorrarLista fragmentBorrar = DialogoBorrarLista.newInstance(lista);
        fragmentBorrar.show(getSupportFragmentManager(), "Dialogo borrar");
    }

    @Override
    public void onBorrarPossitiveButtonClick( final Lista l) {

        presentador.onPapeleraLista(l);

        Snackbar.make(fab, "Lista enviada a la papelera", Snackbar.LENGTH_LONG).setAction("Deshacer", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                presentador.onRecuperarLista(l);
                getSupportLoaderManager().restartLoader(IDENTIFICADOR_LOADER, null, VistaQuip.this);

            }
        }).show();

        getSupportLoaderManager().restartLoader(IDENTIFICADOR_LOADER, null, this);
    }

    @Override
    public void onBorrarNegativeButtonClick() {

        getSupportLoaderManager().restartLoader(IDENTIFICADOR_LOADER, null, this);

    }


    private void init() {

        //Login
        if ( AccessToken.getCurrentAccessToken() == null ) {

            gso         =   new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestProfile()
                            .requestEmail()
                            .build();

            apiClient       =   new GoogleApiClient.Builder(this).enableAutoManage(this, null)
                                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                                .build();
        }
        //Presentador
        presentador     = new PresentadorQuip(this);

        //Drawer
        drawerLayout    = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView  = (NavigationView) findViewById(R.id.navview);

        //Header
        View view       = navigationView.getHeaderView(0);
        circleImageView = (CircleImageView) view.findViewById(R.id.circle_image);
        textViewUser    = (TextView) view.findViewById(R.id.username);
        textViewEmail   = (TextView) view.findViewById(R.id.email);

        //Cargamos la informacion del usuario
        mostrarUsuario();

        //FloatingActionButton papelera
        fab                 = (FloatingActionButton) findViewById(R.id.fab_papelera);

        //RecyclerView
        rv                  = (RecyclerView) findViewById(R.id.lvListaNotas);
        adaptador           = new CursorAdaptadorQuip(this, null);
        swipe               = (SwipeRefreshLayout) findViewById(R.id.swrfly);

        adaptador.setEnabled(true);
        rv.setHasFixedSize(true);
        rv.setAdapter(adaptador);

        getSupportLoaderManager().initLoader(IDENTIFICADOR_LOADER, null, this);

        //Swipe
        callback    = new SimpleItemTouchHelperCallback(this);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rv);

        //Evento al dslizar hacia abajo en el recyclerview
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            swipe.setColorSchemeColors(getResources().getColor(R.color.colorAccent, getTheme()));
        }

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                UtilWeb.bajarInformacion( VistaQuip.this, VistaQuip.this, UtilWeb.URL_UPDATE_SERVER, "actualizar");
            }
        });

        if ( isLinearLayoutManager ) {

            rv.setLayoutManager( new LinearLayoutManager( this, LinearLayoutManager.VERTICAL, false));
        }
        else
        {
            rv.setLayoutManager(new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL));
        }

        //Listeners
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int i       = rv.getChildAdapterPosition(v);
                int type    = adaptador.getType(i);

                if ( type == 0 ) {

                    presentador.onEditNota(i);
                }
                else
                {
                    presentador.onEditLista(i);
                }
            }
        };

        onLongRecoverClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                int i       = rv.getChildAdapterPosition(v);
                int type    = adaptador.getType(i);

                if ( type == 0 ) {

                    presentador.onShowRecuperarNota(i);
                }
                else
                {
                    presentador.onShowRecuperarLista(i);
                }

                return true;

            }
        };

        //Asignamos los listeners a nuestro adaptador
        adaptador.setOnClickListener(onClickListener);


        //Barra inferior
        bb              = (BottomBar) findViewById(R.id.bottomBar);
        tablet          = getResources().getBoolean(R.bool.isTablet);

        if ( !tablet) {

            bb.setOnTabReselectListener(new OnTabReselectListener() {
                @Override
                public void onTabReSelected( int tabId) {

                    switch( tabId ) {

                        case R.id.tab_add_nota : {

                            presentador.onAddNota();
                            break;
                        }

                        case R.id.tab_add_lista : {

                            presentador.onAddLista();

                            break;
                        }
                    }
                }
            });
        }
        else
        {
            fabMenu     = (FloatingActionsMenu) findViewById(R.id.menu_fab);
            fabNota     = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.add_nota);
            fabLista    = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.add_lista);

            fabNota.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    presentador.onAddNota();
                    fabMenu.collapseImmediately();
                }
            });

            fabLista.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    presentador.onAddLista();
                    fabMenu.collapseImmediately();
                }
            });


        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected( MenuItem item ) {

                identificadorFiltro = item.getItemId();
                if ( !search.isIconified() ) {

                    search.setQuery("", false);
                    search.setIconified(true);
                }

                if ( identificadorFiltro != R.id.drawer_papelera ) {

                    adaptador.setOnClickListener(onClickListener);
                    adaptador.setOnLongClickListener(null);
                    touchHelper.attachToRecyclerView(rv);
                    search.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.GONE);

                    if ( !tablet) {

                        bb.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        fabMenu.setVisibility(View.VISIBLE);
                    }

                }

                switch(identificadorFiltro) {

                    case R.id.drawer_todos: {

                        identificadorFiltro = 0;
                        break;
                    }

                    case R.id.drawer_notas : {

                        identificadorFiltro = 1;

                        break;
                    }

                    case R.id.drawer_list : {

                        identificadorFiltro = 2;

                        break;
                    }

                    case R.id.drawer_notification : {

                        identificadorFiltro = 3;

                        break;

                    }

                    case R.id.drawer_papelera : {

                        adaptador.removeOnClickListener();
                        adaptador.setOnLongClickListener(onLongRecoverClickListener);
                        touchHelper.attachToRecyclerView(null);
                        search.setVisibility(View.GONE);
                        fab.setVisibility(View.VISIBLE);

                        if ( !tablet) {

                            bb.setVisibility(View.GONE);
                        }
                        else
                        {
                            fabMenu.setVisibility(View.GONE);
                        }

                        identificadorFiltro = 4;

                        break;
                    }

                    case R.id.help_drawer : {

                        Intent i = new Intent(VistaQuip.this, VistaAyuda.class);
                        startActivityForResult(i, HELP_INT);

                        break;
                    }

                    case R.id.maps_drawer : {

                        Intent i = new Intent(VistaQuip.this, VistaMapa.class);
                        startActivityForResult(i ,MAPS_INT);

                        break;
                    }

                    case R.id.cerrar_sesion : {

                        //Comprobamos si la sesion es con Facebook o Google
                        if ( apiClient == null ) {

                            LoginManager.getInstance().logOut();
                        }
                        else
                        {
                            Auth.GoogleSignInApi.signOut(apiClient);
                        }

                        VistaQuip.this.finish();

                        break;

                    }


                }


                //Cerramos el despliegue del drawer
                drawerLayout.closeDrawers();
                getSupportLoaderManager().restartLoader(IDENTIFICADOR_LOADER, null, VistaQuip.this);

                return true;

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if ( adaptador.getItemCount() == 0 ) {

                    Snackbar.make(fab, getResources().getString(R.string.mensaje_papelera_vacia), Snackbar.LENGTH_LONG).show();
                }
                else
                {
                    presentador.onVaciarPapelera();
                    Snackbar.make(fab, "Vaciado completo", Snackbar.LENGTH_LONG).show();
                    getSupportLoaderManager().restartLoader(IDENTIFICADOR_LOADER, null, VistaQuip.this);
                }
            }
        });

    }

    //Papelera
    @Override
    public void mostrarRecuperarNota(Nota n) {

        DialogoRecuperarNota fragmentRecuperar = DialogoRecuperarNota.newInstance(n);
        fragmentRecuperar.show(getSupportFragmentManager(), "Dialogo recuperar");
    }

    @Override
    public void mostrarRecuperarLista(Lista lista) {

        DialogoRecuperarLista fragmentRecuperar = DialogoRecuperarLista.newInstance(lista);
        fragmentRecuperar.show(getSupportFragmentManager(), "Dialogo recuperar");
    }



    @Override
    public void onRecuperarPossitiveButtonClick(Lista l) {

        presentador.onRecuperarLista(l);
        getSupportLoaderManager().restartLoader(IDENTIFICADOR_LOADER, null, this);
    }

    @Override
    public void onRecuperarPossitiveButtonClick(Nota n) {

        presentador.onRecuperarNota(n);
        getSupportLoaderManager().restartLoader(IDENTIFICADOR_LOADER, null, this);
    }

    @Override
    public void onRecuperarNegativeButtonClick() {

        getSupportLoaderManager().restartLoader(IDENTIFICADOR_LOADER, null, this);
    }

    //Metodo que gestiona la informacion del inicio de sesion
    private void mostrarUsuario() {

        String[] atributos = new String[]{"nombre","imagen","email"};

        for ( String atributo : atributos ) {

            UtilFicheros.getValueFromSharedPreferences(this, this,"usuario", atributo);
        }

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader cursorLoader = null;
        String selection = null;
        String[] selectionArgs = null;

        if ( args != null ) {

            selection      = args.getString("selection");
            selectionArgs  = args.getStringArray("args");
        }

        switch( identificadorFiltro ) {

            case 0 : {

                cursorLoader = new CursorLoader(this, GestionProviderElementos.URI_CONTENIDO_ELEMENTOS,
                                                null, selection, selectionArgs, null);

                break;
            }

            case 1 : {

                cursorLoader = new CursorLoader(this, ContentUris.withAppendedId(
                               GestionProviderElementos.ELEMENTOS_ESPECIFICO, 0),
                               null, selection, selectionArgs, null);

                break;
            }

            case 2 : {

                cursorLoader = new CursorLoader(this, ContentUris.withAppendedId(
                               GestionProviderElementos.ELEMENTOS_ESPECIFICO, 1),
                               null, selection, selectionArgs, null);

                break;
            }

            case 3 : {

                cursorLoader = new CursorLoader(this, GestionProviderElementos.ELEMENTOS_NOTIFICACION,
                                                null, selection, selectionArgs, null);

                break;
            }

            case 4 : {

                cursorLoader = new CursorLoader(this, GestionProviderElementos.ELEMENTOS_PAPELERA,
                        null, selection, selectionArgs, null);

                break;
            }

        }

        return cursorLoader;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        System.out.println(requestCode);
        if ( resultCode == Activity.RESULT_OK ) {
            if ( requestCode == MAPS_INT || requestCode == HELP_INT ) {

                this.recreate();
            }
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        presentador.onRestartCursor(data);
        adaptador.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        adaptador.swapCursor(null);
    }

    @Override
    public void enviarInformacion(Object output) {

        String info = (String) output;

        if ( !info.isEmpty() ) {

            if ( info.contains("@") ) {

                //Es el e-mail
                textViewEmail.setText(info);
            }
            else
            {
                //Nombre completo del usuario
                if ( info.contains(" ") ) {

                    textViewUser.setText(info);
                }
                else
                {
                    Picasso.with(this)
                                .load(info)
                                .placeholder(R.drawable.img_user)
                                .error(R.drawable.img_user)
                                .into(circleImageView);


                }

            }
        }

        //Si llega vacia significa que no existe imagen
        Bitmap bmp = BitmapFactory.decodeResource( getResources(), R.drawable.img_user);
        circleImageView.setImageBitmap(bmp);
    }

    @Override
    public void sendRequest() {

        navigationView.setActivated(false);
        adaptador.setEnabled(false);
        if ( bb != null) {

            bb.setActivated(false);
        }
        else
        {
            fabMenu.setEnabled(false);
        }
    }

    @Override
    public void getRequest() {

        getSupportLoaderManager().restartLoader(IDENTIFICADOR_LOADER, null, this);

        swipe.setRefreshing(false);
        navigationView.setActivated(true);
        adaptador.setEnabled(true);

        if ( bb != null) {

            bb.setActivated(true);
        }
        else
        {
            fabMenu.setEnabled(true);
        }
    }

    @Override
    public void onItemDismiss(int position) {

        //Evento para deslizar el item del recyclerview
        int type    = adaptador.getType(position);

        if ( type == 0 ) {

            presentador.onShowBorrarNota(position);
        }
        else
        {
            presentador.onShowBorrarLista(position);
        }


    }

}