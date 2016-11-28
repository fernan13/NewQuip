package com.example.fernando.proyectodam.util.Backup;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.os.ParcelFileDescriptor;

import com.example.fernando.proyectodam.contrato.ContratoBaseDatos;

import java.io.IOException;

/**
 * Created by Fernando on 23/10/2016.
 */

public class MyBackupAgent extends BackupAgentHelper {

    public static final Object sDataLock    = new Object();

    // A key to uniquely identify the set of backup data
    static final String FILES_BACKUP_KEY    = "database";

    // Allocate a helper and add it to the backup agent
    @Override
    public void onCreate() {

        FileBackupHelper helper = new FileBackupHelper(this,this.getDatabasePath(ContratoBaseDatos.BASEDATOS).getAbsolutePath());
        addHelper(FILES_BACKUP_KEY, helper);
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {

        synchronized (MyBackupAgent.sDataLock) {

            super.onBackup(oldState, data, newState);
        }

    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {

        synchronized (MyBackupAgent.sDataLock) {

            super.onRestore(data, appVersionCode, newState);
        }
    }
}
