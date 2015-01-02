package com.vnguyen.liveokeremote.helper;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.vnguyen.liveokeremote.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class BackupHelper {

    private MainActivity context;
    private String songsDBFile = "songslist.db";

    public BackupHelper(Context context) {
        this.context = (MainActivity) context;
    }

    public void copyDatabase(String command) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = new File(Environment.getDataDirectory(),"data/"+context.getPackageName());

            if (sd.canWrite()) {
                File databaseDir = new File(data,"databases");
                if (!databaseDir.exists()) {
                    databaseDir.mkdir();
                }
                String currentDBPath = songsDBFile;
                File sdDir = new File(sd,"liveokeremote");
                if (!sdDir.exists()) {
                    sdDir.mkdir();
                }
                String backupDBPath = "songslist.db";
                File currentDB;
                File backupDB;
                Log.d(context.app.TAG, "currentDBPath = " + databaseDir + "/" + currentDBPath);
                if (command.equals("export")) {
                    currentDB = new File(databaseDir, currentDBPath);
                    backupDB = new File(sdDir, backupDBPath);
                } else {
                    backupDB = new File(databaseDir, currentDBPath);
                    currentDB = new File(sdDir, backupDBPath);
                }

                if (currentDB.exists()) {
                    Log.d(context.app.TAG,"Copying databases to SD card...");
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            Log.e(context.app.TAG,e.getLocalizedMessage(),e);
        }
    }


}
