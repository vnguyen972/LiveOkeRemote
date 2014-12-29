package com.vnguyen.liveokeremote.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.vnguyen.liveokeremote.MainActivity;
import com.vnguyen.liveokeremote.db.SongListTable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SongSQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "songslist.db";

    private static final int DATABASE_VERSION = 1;

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    // Database creation sql statement
    private static final String FAVORITE_SONG_CONNECTION_DB_CREATE =
            "create table " + SongListTable.FavoriteSongConnection.TABLE_NAME + " (" +
                    SongListTable.FavoriteSongConnection._ID + " INTEGER PRIMARY KEY," +
                    SongListTable.FavoriteSongConnection.COLUMN_NAME_FAVORITE_ID + INTEGER_TYPE + " NOT NULL " + COMMA_SEP +
                    SongListTable.FavoriteSongConnection.COLUMN_NAME_CONNECTION_ID + INTEGER_TYPE + " NOT NULL " + COMMA_SEP +
                    SongListTable.FavoriteSongConnection.COLUMN_NAME_SONG_ID + INTEGER_TYPE + " NOT NULL);";

    private static final String FAVORITE_LIST_DB_CREATE =
            "create table " + SongListTable.FavoriteList.TABLE_NAME + " (" +
                    SongListTable.FavoriteList._ID + " INTEGER PRIMARY KEY," +
                    SongListTable.FavoriteList.COLUMN_NAME_TITLE + TEXT_TYPE + " NOT NULL " + COMMA_SEP +
                    SongListTable.FavoriteList.COLUMN_NAME_ASCII_TITLE + TEXT_TYPE + " NOT NULL);";

    private static final String K7CONNECTION_DB_CREATE =
            "create table " + SongListTable.K7Connection.TABLE_NAME + " (" +
                    SongListTable.K7Connection._ID + " INTEGER PRIMARY KEY," +
                    SongListTable.K7Connection.COLUMN_NAME_CONNECTION_NAME + TEXT_TYPE + " NOT NULL " + COMMA_SEP +
                    SongListTable.K7Connection.COLUMN_NAME_IP_ADDRESS + TEXT_TYPE + " NOT NULL " + COMMA_SEP +
                    SongListTable.K7Connection.COLUMN_NAME_ACTIVE_STATUS + TEXT_TYPE + " NOT NULL);";

    private static final String DATABASE_CREATE =
            "create table " + SongListTable.SongList.TABLE_NAME + " (" +
                    SongListTable.SongList._ID + " INTEGER PRIMARY KEY," +
                    SongListTable.SongList.COLUMN_NAME_SONG_ID + TEXT_TYPE + " NOT NULL " + COMMA_SEP +
                    SongListTable.SongList.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    SongListTable.SongList.COLUMN_NAME_ASCII_TITLE + TEXT_TYPE + COMMA_SEP +
                    SongListTable.SongList.COLUMN_NAME_QUICK_TITLE + TEXT_TYPE + COMMA_SEP +
                    SongListTable.SongList.COLUMN_NAME_SINGER + TEXT_TYPE + COMMA_SEP +
                    SongListTable.SongList.COLUMN_NAME_ASCII_SINGER + TEXT_TYPE + COMMA_SEP +
                    SongListTable.SongList.COLUMN_NAME_QUICK_SINGER + TEXT_TYPE + COMMA_SEP +
                    SongListTable.SongList.COLUMN_NAME_SINGER_ICON + TEXT_TYPE + COMMA_SEP +
                    SongListTable.SongList.COLUMN_NAME_AUTHOR + TEXT_TYPE + COMMA_SEP +
                    SongListTable.SongList.COLUMN_NAME_ASCII_AUTHOR + TEXT_TYPE + COMMA_SEP +
                    SongListTable.SongList.COLUMN_NAME_PRODUCER +  TEXT_TYPE + COMMA_SEP +
                    SongListTable.SongList.COLUMN_NAME_ASCII_PRODUCER + TEXT_TYPE + COMMA_SEP +
                    SongListTable.SongList.COLUMN_NAME_SONG_PATH + TEXT_TYPE + COMMA_SEP +
                    SongListTable.SongList.COLUMN_NAME_CLASSIFIED + TEXT_TYPE + COMMA_SEP +
                    SongListTable.SongList.COLUMN_NAME_TONE + TEXT_TYPE + COMMA_SEP +
                    SongListTable.SongList.COLUMN_NAME_TEMPO + TEXT_TYPE + COMMA_SEP +
                    SongListTable.SongList.COLUMN_NAME_TYPE + TEXT_TYPE + COMMA_SEP +
                    SongListTable.SongList.COLUMN_NAME_LANGUAGE + TEXT_TYPE + COMMA_SEP +
                    SongListTable.SongList.COLUMN_NAME_FAVORITES + TEXT_TYPE + COMMA_SEP +
                    SongListTable.SongList.COLUMN_NAME_SWAPPED + TEXT_TYPE + COMMA_SEP +
                    SongListTable.SongList.COLUMN_NAME_POPULAR_RANK + TEXT_TYPE + COMMA_SEP +
                    SongListTable.SongList.COLUMN_NAME_LYRICS + TEXT_TYPE + COMMA_SEP +
                    SongListTable.SongList.COLUMN_NAME_VOLUME + TEXT_TYPE + COMMA_SEP +
                    SongListTable.SongList.COLUMN_NAME_REQUESTER + TEXT_TYPE 	+ ");"
            ;

    private static final String SQL_DELETE_ENTRIES = "drop table if exists " +
            SongListTable.SongList.TABLE_NAME;

    private static final String SQL_CREATE_INDEX = "create index index1 on " +
            SongListTable.SongList.TABLE_NAME + " (songid,ascii_title, ascii_singer,ascii_author,ascii_producer);";
    private static final String SQL_CREATE_FAVORITE_INDEX = "create index index2 on " +
            SongListTable.FavoriteList.TABLE_NAME + " (ascii_title);";

    private MainActivity ma;
    public SongSQLiteHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        ma = (MainActivity) context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.d("K7", "SongSQLLiteHelper.onCreate");
        database.execSQL(DATABASE_CREATE);
        database.execSQL(K7CONNECTION_DB_CREATE);
        database.execSQL(FAVORITE_LIST_DB_CREATE);
        Log.d("K7", "FSC QUERY = " + FAVORITE_SONG_CONNECTION_DB_CREATE);
        database.execSQL(FAVORITE_SONG_CONNECTION_DB_CREATE);
        database.execSQL(SQL_CREATE_FAVORITE_INDEX);
        database.execSQL(SQL_CREATE_INDEX);
    }

    public boolean importDatabase(String dbPath) throws IOException {

        // Close the SQLiteOpenHelper so it will commit the created empty
        // database to internal storage.
        close();
        File newDb = new File(dbPath);
        File dataDir = new File("/data/data/"+ma.getPackageName());
        File dbDir = new File(dataDir,"databases");
        if (!dbDir.exists()) {
            dbDir.mkdir();
        }
        File dbFile = new File(dbDir,getDatabaseName());
        if (newDb.exists()) {
            FileUtilHelper.copyFile(new FileInputStream(newDb), new FileOutputStream(dbFile));
            // Access the copied database so SQLiteHelper will cache it and mark
            // it as created.
            getWritableDatabase().close();
            return true;
        }
        return false;
    }
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVerson, int newVersion) {
        database.execSQL(SQL_DELETE_ENTRIES);
        database.execSQL(DATABASE_CREATE);
        database.execSQL(SQL_CREATE_INDEX);
    }

    public void resetDB(SQLiteDatabase database) {
        database.execSQL(SQL_DELETE_ENTRIES);
        database.execSQL(DATABASE_CREATE);
        database.execSQL(SQL_CREATE_INDEX);
    }

    @Override
    public void onDowngrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL(SQL_DELETE_ENTRIES);
        database.execSQL(DATABASE_CREATE);
        database.execSQL(SQL_CREATE_INDEX);
    }

    public String getDatabaseName() {
        return DATABASE_NAME;
    }

}
