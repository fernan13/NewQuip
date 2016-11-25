package com.example.fernando.proyectodam.basedatos;

import android.content.Context;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.example.fernando.proyectodam.R;
import com.example.fernando.proyectodam.pojo.LocationObject;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Created by Fernando on 15/11/2016.
 */

public class AyudanteOrm extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "locations.db";
    private static final int DATABASE_VERSION = 2;

    private Dao<LocationObject, Integer> mUserDao = null;

    public AyudanteOrm(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {

        try {

            TableUtils.createTable(connectionSource, LocationObject.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {

        try
        {
            //Obtenemos todas las localizaciones
            Dao<LocationObject, Integer> dao = getLocationDao();
            List<LocationObject> items = dao.queryForAll();

            TableUtils.dropTable(connectionSource, LocationObject.class, true);
            TableUtils.createTable(connectionSource, LocationObject.class);

            //Guardamos la informacion

            for ( LocationObject lo : items ) {

                dao.create(lo);
            }

        }
        catch (SQLException e) {

            throw new RuntimeException(e);
        }
    }

    /* User */

    public Dao<LocationObject, Integer> getLocationDao()  {

        if (mUserDao == null) {

            try
            {
                mUserDao = getDao(LocationObject.class);
            }
            catch( SQLException e ){}
        }

        return mUserDao;
    }

    @Override
    public void close() {

        mUserDao = null;

        super.close();
    }

}
