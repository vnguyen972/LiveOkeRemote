package com.vnguyen.liveokeremote.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.vnguyen.liveokeremote.LiveOkeRemoteApplication;
import com.vnguyen.liveokeremote.MainActivity;
import com.vnguyen.liveokeremote.data.Song;
import com.vnguyen.liveokeremote.helper.DrawableHelper;
import com.vnguyen.liveokeremote.helper.SongHelper;
import com.vnguyen.liveokeremote.helper.SongSQLiteHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SongListDataSource {
    private SQLiteDatabase database;

    private SongSQLiteHelper dbHelper;
    private MainActivity context;

    public SongSQLiteHelper getDbHelper() {
        return dbHelper;
    }

    private String[] displayColumns = {
            SongListTable.SongList.COLUMN_NAME_SONG_ID,
            SongListTable.SongList.COLUMN_NAME_TITLE,
            SongListTable.SongList.COLUMN_NAME_ASCII_TITLE,
            SongListTable.SongList.COLUMN_NAME_QUICK_TITLE,
            SongListTable.SongList.COLUMN_NAME_SINGER,
            SongListTable.SongList.COLUMN_NAME_ASCII_SINGER,
            SongListTable.SongList.COLUMN_NAME_QUICK_SINGER,
            SongListTable.SongList.COLUMN_NAME_SONG_PATH,
            SongListTable.SongList.COLUMN_NAME_TYPE
    };

    private String[] allColumns = {
            SongListTable.SongList.COLUMN_NAME_SONG_ID,
            SongListTable.SongList.COLUMN_NAME_TITLE,
            SongListTable.SongList.COLUMN_NAME_ASCII_TITLE,
            SongListTable.SongList.COLUMN_NAME_QUICK_TITLE,
            SongListTable.SongList.COLUMN_NAME_SINGER,
            SongListTable.SongList.COLUMN_NAME_ASCII_SINGER,
            SongListTable.SongList.COLUMN_NAME_QUICK_SINGER,
            SongListTable.SongList.COLUMN_NAME_SINGER_ICON,
            SongListTable.SongList.COLUMN_NAME_AUTHOR,
            SongListTable.SongList.COLUMN_NAME_ASCII_AUTHOR,
            SongListTable.SongList.COLUMN_NAME_PRODUCER,
            SongListTable.SongList.COLUMN_NAME_ASCII_PRODUCER,
            SongListTable.SongList.COLUMN_NAME_SONG_PATH,
            SongListTable.SongList.COLUMN_NAME_CLASSIFIED,
            SongListTable.SongList.COLUMN_NAME_TONE,
            SongListTable.SongList.COLUMN_NAME_TEMPO,
            SongListTable.SongList.COLUMN_NAME_TYPE,
            SongListTable.SongList.COLUMN_NAME_LANGUAGE,
            SongListTable.SongList.COLUMN_NAME_FAVORITES,
            SongListTable.SongList.COLUMN_NAME_SWAPPED,
            SongListTable.SongList.COLUMN_NAME_POPULAR_RANK,
            SongListTable.SongList.COLUMN_NAME_LYRICS,
            SongListTable.SongList.COLUMN_NAME_VOLUME,
            SongListTable.SongList.COLUMN_NAME_REQUESTER
    };


    public SongListDataSource(Context context) {
        dbHelper = new SongSQLiteHelper(context);
        this.context = (MainActivity) context;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean importDB(String dbPath) throws IOException {
        return dbHelper.importDatabase(dbPath);
    }

    public void saveDB() throws IOException {
        dbHelper.saveDB2SD();
    }
    public String getDBName() {
        return dbHelper.getDatabaseName();
    }

    /**
     *
     * @param songList
     * @throws Exception
     */
    public void insertAll(ArrayList<Song> songList) throws Exception {
        Song _song = null;
        try {
            String sql = "insert into " + SongListTable.SongList.TABLE_NAME +
                    "(" + allColumns[0] + "," + allColumns[1] + "," +
                    allColumns[2] + "," + allColumns[3] + "," +
                    allColumns[4] + "," + allColumns[5] + "," +
                    allColumns[6] + "," + allColumns[7] + "," +
                    allColumns[8] + "," + allColumns[9] + "," +
                    allColumns[10] + "," + allColumns[11] + "," +
                    allColumns[12] + "," + allColumns[13] + "," +
                    allColumns[14] + "," + allColumns[15] + "," +
                    allColumns[16] + "," + allColumns[17] + "," +
                    allColumns[18] + "," + allColumns[19] + "," +
                    allColumns[20] + "," + allColumns[21] + "," +
                    allColumns[22] + "," + allColumns[23] +
                    ") values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?)";
            Log.d(LiveOkeRemoteApplication.TAG, "sql = " + sql);
            database.beginTransaction();
            //getDatabase().delete(SongListTable.SongList.TABLE_NAME, null, null);
            final SQLiteStatement insert = getDatabase().compileStatement(sql);
            for (Song song : songList) {
                _song = song;
                insert.bindString(1, song.id);
                insert.bindString(2, song.title);
                insert.bindString(3, song.convertedTitle);
                insert.bindString(4, song.quickTitle);
                insert.bindString(5, song.singer);
                insert.bindString(6, song.convertedSinger);
                insert.bindString(7, song.quickSinger);
                insert.bindString(8, song.singerIcon);
                insert.bindString(9, song.author);
                insert.bindString(10, song.convertedAuthor);
                insert.bindString(11, song.producer);
                insert.bindString(12, song.convertProducer);
                insert.bindString(13, song.songPath);
                insert.bindString(14, song.classified);
                insert.bindString(15, song.tone);
                insert.bindString(16, song.tempo);
                insert.bindString(17, song.type);
                insert.bindString(18, (song.language));
                insert.bindString(19, song.favorites);
                insert.bindString(20, song.swapped);
                insert.bindString(21, song.popularRank);
                insert.bindString(22, song.lyrics);
                insert.bindString(23, song.volume);
                insert.bindString(24, song.requester);
                insert.execute();
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(LiveOkeRemoteApplication.TAG, "Exception on song: " + _song.title);
            throw new Exception(e);
        } finally {
            getDatabase().endTransaction();
        }
    }

    public boolean isConnectionExisted(String wifiName, String ipAddress) {
        String query = "select count(*) from k7connection where name = '" +
                wifiName + ":" + ipAddress + "' and ipAddress = '"+ ipAddress+"'";
        return runExistedQuery(query);
    }

    private int runQueryRetrieveID(String query) {
        Cursor cursor = database.rawQuery(query, null);
        int id = 0;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                id = cursor.getInt(0);
            }
            cursor.close();
        }
        return id;
    }
    public int queryCurrentK7ConnectionID(String wifiName, String ipAddress) {
        String query = "select _id from k7connection where name = '" + wifiName +":"+ipAddress+"' and ipAddress = '" + ipAddress + "';";
        Log.d(LiveOkeRemoteApplication.TAG,"queryCurrentK7Connection: " + query);
        return runQueryRetrieveID(query);
    }

    public long createK7Connection(String wifiName,String ipAddress) {
        deactivateOtherConnections(wifiName, ipAddress);
        if (!isConnectionExisted(wifiName, ipAddress)) {
            ContentValues values = new ContentValues();
            values.put(SongListTable.K7Connection.COLUMN_NAME_CONNECTION_NAME, wifiName+":"+ipAddress);
            values.put(SongListTable.K7Connection.COLUMN_NAME_IP_ADDRESS, ipAddress);
            values.put(SongListTable.K7Connection.COLUMN_NAME_ACTIVE_STATUS, "Y");
            return database.insert(SongListTable.K7Connection.TABLE_NAME, null, values);
        } else {
            updateK7Connection(wifiName, ipAddress, "Y");
            return queryCurrentK7ConnectionID(wifiName, ipAddress);
        }
    }

    public void deactivateOtherConnections(String wifiName, String ipAddress) {
        ContentValues value = new ContentValues();
        value.put(SongListTable.K7Connection.COLUMN_NAME_ACTIVE_STATUS, "N");
        int rowsUpdated = database.update(SongListTable.K7Connection.TABLE_NAME, value,
                SongListTable.K7Connection.COLUMN_NAME_CONNECTION_NAME + " <>'" + wifiName + ":" + ipAddress + "' and " +
                        SongListTable.K7Connection.COLUMN_NAME_IP_ADDRESS + " <> '" + ipAddress + "'", null);
        Log.d(LiveOkeRemoteApplication.TAG,"deactivating-rows updated: " + rowsUpdated);
    }

    private boolean runExistedQuery(String query) {
        Cursor cursor = database.rawQuery(query, null);
        int count = 0;
        boolean existed = false;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
            cursor.close();
            if (count > 0) {
                existed = true;
            }
        }
        return existed;
    }
    public boolean favoriteExisted(Song song) {
        String query = "select count(*) from favoriteslist where ascii_title = '" + song.convertedTitle.replaceAll("'", "''") + "'";
        Log.d(LiveOkeRemoteApplication.TAG, query);
        return runExistedQuery(query);
    }
    public long save2Favorite(Song song) {
        if (!favoriteExisted(song)) {
            ContentValues value = new ContentValues();
            value.put(SongListTable.FavoriteList.COLUMN_NAME_TITLE,song.title.replaceAll("'", "\'"));
            value.put(SongListTable.FavoriteList.COLUMN_NAME_ASCII_TITLE, song.convertedTitle.replaceAll("'", "\'"));
            return database.insert(SongListTable.FavoriteList.TABLE_NAME, null, value);
        } else {
            return queryFavorite(song);
        }
    }
    public long queryActiveFavorite(Song song,String wifi,String ipAddr) {
        String query = "select a._id from favoriteslist a, favoritesongconnection b, " +
                "k7connection c where a._id = b.favoriteID and songID = "+song.id+
                " and b.connectionID = c._id and c.name = '"+wifi+":"+ipAddr+"'";
        //Log.d("K7","queryActiveFavorite: " + query);
        return runQueryRetrieveID(query);
    }
    public long queryFavorite(Song song) {
        String aTitle = song.convertedTitle.replaceAll("'", "''");
        //Log.d("K7","aTitle = " + aTitle);
        String query = "select _id from favoriteslist where ascii_title = '" + aTitle + "'";
        return runQueryRetrieveID(query);
    }

    public boolean favoriteLinkExisted(long favID, long activeK7ConnectionID, String songID) {
        String query = "select count(*) from favoritesongconnection where favoriteID = " +
                favID + " and connectionID = "+ activeK7ConnectionID +" and songID = " + songID;
        return runExistedQuery(query);
    }

    public void link2Connection(long favID, long activeK7ConnectionID, String songID) {
        Log.d(LiveOkeRemoteApplication.TAG, "link2Connection: " + favID + "-" + activeK7ConnectionID + "-" + songID);
        if (!favoriteLinkExisted(favID, activeK7ConnectionID, songID)) {
            ContentValues value = new ContentValues();
            value.put(SongListTable.FavoriteSongConnection.COLUMN_NAME_FAVORITE_ID, favID);
            value.put(SongListTable.FavoriteSongConnection.COLUMN_NAME_CONNECTION_ID, activeK7ConnectionID);
            value.put(SongListTable.FavoriteSongConnection.COLUMN_NAME_SONG_ID, songID);
            database.insert(SongListTable.FavoriteSongConnection.TABLE_NAME, null, value);
            Log.d(LiveOkeRemoteApplication.TAG, "insert new link...");
        } else {
            //unFavorite(favID,activeK7ConnectionID,songID);
            long row = updateFavSongCollection(favID, activeK7ConnectionID, songID);
            Log.d(LiveOkeRemoteApplication.TAG, "update existing link: " + row);
        }
    }

    public void deleteFavorite(String asciiTitle) {
        int rows = database.delete(SongListTable.FavoriteList.TABLE_NAME,"ascii_title = '" + asciiTitle + "'", null);
    }

    public void unFavorite(long favID, long activeK7ConnectionID, String songID) {
        Log.d(LiveOkeRemoteApplication.TAG,"unFavorite: favID = " + favID + ",activeID = " + activeK7ConnectionID + ",songID = " + songID);
        int rows = database.delete(SongListTable.FavoriteSongConnection.TABLE_NAME,
                "favoriteID = " + favID + " and connectionID = " + activeK7ConnectionID + " and songID = " + songID, null);
        Log.d(LiveOkeRemoteApplication.TAG,"unFavorite:link:rows deleted: " + rows);
        rows = database.delete(SongListTable.FavoriteList.TABLE_NAME, "_id = " + favID, null);
        Log.d(LiveOkeRemoteApplication.TAG,"unFavorite:rows deleted: " + rows);
    }

    public long updateFavSongCollection(long favID, long activeK7ConnID, String songID) {
        ContentValues values = new ContentValues();
        values.put(SongListTable.FavoriteSongConnection.COLUMN_NAME_CONNECTION_ID, activeK7ConnID);
        int numRowsUpdated = database.update(SongListTable.FavoriteSongConnection.TABLE_NAME, values,
                SongListTable.FavoriteSongConnection.COLUMN_NAME_SONG_ID+"='" + songID + "' and " +
                        SongListTable.FavoriteSongConnection.COLUMN_NAME_FAVORITE_ID+"=" + favID, null);
        return numRowsUpdated;
    }
    public long updateK7Connection(String wifiName, String ipAddress, String activeStatus) {
        ContentValues values = new ContentValues();
        values.put(SongListTable.K7Connection.COLUMN_NAME_ACTIVE_STATUS, activeStatus);
        int numRowUpdated = database.update(SongListTable.K7Connection.TABLE_NAME, values,
                SongListTable.K7Connection.COLUMN_NAME_CONNECTION_NAME+"='" + wifiName + ":" + ipAddress + "' and " +
                        SongListTable.K7Connection.COLUMN_NAME_IP_ADDRESS + "= '" + ipAddress + "'", null);
        Log.d(LiveOkeRemoteApplication.TAG, numRowUpdated + " rows updated active status to " + activeStatus);
        return numRowUpdated;
    }

    public long createSong(Song song) {
        ContentValues values = new ContentValues();
        values.put(SongListTable.SongList.COLUMN_NAME_SONG_ID, song.id);
        values.put(SongListTable.SongList.COLUMN_NAME_TITLE,song.title);
        values.put(SongListTable.SongList.COLUMN_NAME_ASCII_TITLE, song.convertedTitle);
        values.put(SongListTable.SongList.COLUMN_NAME_SINGER,song.singer);
        values.put(SongListTable.SongList.COLUMN_NAME_ASCII_SINGER,song.convertedSinger);
        values.put(SongListTable.SongList.COLUMN_NAME_SINGER_ICON,song.singerIcon);
        values.put(SongListTable.SongList.COLUMN_NAME_AUTHOR,song.author);
        values.put(SongListTable.SongList.COLUMN_NAME_ASCII_AUTHOR,song.convertedAuthor);
        values.put(SongListTable.SongList.COLUMN_NAME_PRODUCER,song.producer);
        values.put(SongListTable.SongList.COLUMN_NAME_ASCII_PRODUCER,song.convertProducer);
        values.put(SongListTable.SongList.COLUMN_NAME_SONG_PATH,song.songPath);
        values.put(SongListTable.SongList.COLUMN_NAME_CLASSIFIED,song.classified);
        values.put(SongListTable.SongList.COLUMN_NAME_TONE,song.tone);
        values.put(SongListTable.SongList.COLUMN_NAME_TEMPO,song.tempo);
        values.put(SongListTable.SongList.COLUMN_NAME_TYPE,song.type);
        values.put(SongListTable.SongList.COLUMN_NAME_LANGUAGE,song.language);
        values.put(SongListTable.SongList.COLUMN_NAME_FAVORITES,song.favorites);
        values.put(SongListTable.SongList.COLUMN_NAME_SWAPPED,song.swapped);
        values.put(SongListTable.SongList.COLUMN_NAME_POPULAR_RANK,song.popularRank);
        values.put(SongListTable.SongList.COLUMN_NAME_LYRICS,song.lyrics);
        values.put(SongListTable.SongList.COLUMN_NAME_VOLUME,song.volume);
        values.put(SongListTable.SongList.COLUMN_NAME_REQUESTER,song.requester);

        long insertId = database.insert(SongListTable.SongList.TABLE_NAME,
                null, values);
//		Cursor cursor = database.query(SongListTable.SongList.TABLE_NAME,
//				allColumns, SongListTable.SongList._ID + " = " + insertId, null,
//				null, null, null);
//		cursor.moveToFirst();
//		Song newSong = cursorToSong(cursor);
//		return newSong;
        return insertId;
    }

    public void deleteAll() {
        //not implemented
    }

    public void deleteSong(Song song) {
        //not implemented yet
    }

    /**
     * @deprecated
     */
    public List<Song> getAllSongs() throws Exception {
        List<Song> songList = new ArrayList<Song>();
        Cursor cursor = database.query(SongListTable.SongList.TABLE_NAME,
                displayColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Song song = cursorToSongDisplay(cursor);
            songList.add(song);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return songList;
    }

    /**
     * getSongBy(letter)
     *
     * Get all the songs that starts with a letter
     *
     * @param letter
     * @return
     * @deprecated
     */
    public ArrayList<Song> getSongsBy(String letter, String field) {
        ArrayList<Song> songs = new ArrayList<Song>();
        String query = "select songId,title,ascii_title,singer,ascii_singer,songPath,type from songslist where substr("+field+",1,1) in ('" + letter.charAt(0) + "')";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                songs.add(cursorToSongDisplay(cursor));
            }
            cursor.close();
        }
        return songs;
    }

    /**
     * getSongByNumeric
     *
     * Build the page where contains all that starts with a numeric
     * in the title.
     *
     * @return
     */
    public ArrayList<Song> getSongByKeys(String field, String keys,String searchStr) {
        ArrayList<Song> list = new ArrayList<Song>();
        //String _key = "('0','1','2','3','4','5','6','7','8','9','0')";
        String query = "";
        if (field.equalsIgnoreCase("search")) {
            int offset = (Integer.parseInt(keys) * 100) - 100;
            query = "select songId,title,ascii_title,singer,ascii_singer,songPath,type,author,producer from songslist where " +
                    "(upper(ascii_singer) like upper('%"+ searchStr +"%') or upper(ascii_title) like upper('%"+searchStr+"%') " +
                    "or upper(quick_title) like upper('"+ searchStr +"') or upper(quick_singer) like upper('%"+searchStr+"%') " +
                    "or upper(ascii_author) like upper('%"+ searchStr +"%') or upper(ascii_producer) like upper('%"+ searchStr +"%') " +
                    "or songid like '%"+searchStr+"%') order by ascii_title limit 100 offset " + offset;
        } else if (field.equalsIgnoreCase("favorites")) {
            int offset = (Integer.parseInt(keys) * 100) - 100;
            query = "select * from favoriteslist limit 100 offset " + offset;
        } else if (field.equalsIgnoreCase("EN") ||
                field.equalsIgnoreCase("VN") ||
                field.equalsIgnoreCase("CN")) {
            int offset = (Integer.parseInt(keys) * 100) - 100;
            //query = "select songId,title,ascii_title,singer,ascii_singer,songPath,type from songslist where upper(substr(title,1,1)) in " + keys + " and language='"+field+"'";// order by ascii_title asc";
            query = "select songId,title,ascii_title,singer,ascii_singer,songPath,type,author,producer from songslist where language='"+field+"' limit 100 offset " + offset;
        } else {
            query = "select songId,title,ascii_title,singer,ascii_singer,songPath,type,author,producer from songslist where upper(substr("+field+",1,1)) in " + keys;// + " order by ascii_title asc";
        }
        Log.d(LiveOkeRemoteApplication.TAG,"Query = " + query);
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null) {
            //Log.d("K7","cursor size = " + cursor.getCount());
            while (cursor.moveToNext()) {
                Song song = null;
                if (field.equalsIgnoreCase("favorites")) {
                    song = cursorToFavoriteSongDisplay(cursor);
                    song.icon = (new DrawableHelper()).buildDrawable(song.title.substring(0, 1), "round");
                    //list.add(cursorToFavoriteSongDisplay(cursor));
                } else {
                    song = cursorToSongDisplay(cursor);
                    if (context.app.songInitialIconBy.equalsIgnoreCase("Title")) {
                        song.icon = (new DrawableHelper()).buildDrawable(song.title.substring(0, 1), "round");
                    } else if (context.app.songInitialIconBy.equalsIgnoreCase("Singer")) {
                        song.icon = (new DrawableHelper()).buildDrawable(song.singer.substring(0, 1), "round");
                    } else if (context.app.songInitialIconBy.equalsIgnoreCase("Author")) {
                        if (song.author != null && !song.author.equals("")) {
                            song.icon = (new DrawableHelper()).buildDrawable(song.author.substring(0, 1), "round");
//                        } else {
//                            song.icon = (new DrawableHelper()).buildDrawable("?Unknown".substring(0, 1), "round");
                        }
                    } else if (context.app.songInitialIconBy.equalsIgnoreCase("Producer")) {
                        if (song.producer != null && !song.producer.equals("")) {
                            song.icon = (new DrawableHelper()).buildDrawable(song.producer.substring(0, 1), "round");
//                        } else {
//                            song.icon = (new DrawableHelper()).buildDrawable("?Unknown".substring(0, 1), "round");
                        }
                    }
                    //Log.d("K7","song: " + song.getTitle());
                    //list.add(song);
                }
                if (song != null) {
                    list.add(song);
                }
            }
            cursor.close();
        }
        //Log.d("K7","Found: " + list.size() + " songs.");
        return list;
    }


    /**
     * getKeyList
     *
     * Retrieve from sqlite all the initials of the songs
     * to build the title for the Pager Viewer
     *
     * @return
     * @throws Exception
     */
    public ConcurrentHashMap<String, String> getTitleKeysMap() throws Exception {
        //String query = "select distinct upper(substr(title,1,1)) from songslist";
        String query = "select distinct upper(substr(title,1,1)) as key, count(*) from songslist group by key";
//		ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
//		Log.d("K7","getTitleKeysMap: " + query);
//		Cursor cursor = database.rawQuery(query, null);
//		if (cursor != null) {
//		    while (cursor.moveToNext()) {
//				String s = cursor.getString(0);
//				if (s == null || s.equals("")) {
//					s = "?Unavailable";
//				}
//				String key = "?";
//				//if (Character.isLetterOrDigit(s.charAt(0))) {
//					key = SongUtil.getTheKey(s);
//				//}
//				//Log.d("K7","key: " + key);
//				int value = cursor.getInt(1);
//				if (!map.containsKey(key)) {
//					map.put(key, value+"");
//				} else {
//					int prev = Integer.parseInt(map.get(key));
//					prev += value;
//					map.put(key, prev+"");
//
//				}
//				//Log.d("K7","value: " + map.get(key));
//		    }
//		    cursor.close();
//		}
//		//Log.d("K7", "getKeyMap = " + map.size());
//		return map;
        return getTheTitleKeysMap(query);
    }

    private ConcurrentHashMap<String, String> getTheTitleKeysMap(String query) throws Exception {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
        Log.d(LiveOkeRemoteApplication.TAG,"getTitleKeysMap: " + query);
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String s = cursor.getString(0);
                if (s == null || s.equals("")) {
                    s = "?Unavailable";
                }
                String key = "?";
                //if (Character.isLetterOrDigit(s.charAt(0))) {
                key = SongHelper.getTheKey(s);
                //}
                //Log.d("K7","key: " + key);
                int value = cursor.getInt(1);
                if (!map.containsKey(key)) {
                    map.put(key, value+"");
                } else {
                    int prev = Integer.parseInt(map.get(key));
                    prev += value;
                    map.put(key, prev+"");

                }
                //Log.d("K7","value: " + map.get(key));
            }
            cursor.close();
        }
        //Log.d("K7", "getKeyMap = " + map.size());
        return map;
    }

    public ConcurrentHashMap<String, String> getNewSearchKeysMap(String searchStr) throws Exception {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
        String query = "select count(*) from songslist where " +
                "(upper(ascii_singer) like upper('%"+searchStr+"%') or upper(ascii_title) like upper('%"+
                searchStr+"%') or upper(ascii_author) like upper('%"+ searchStr +"%') or upper(quick_title) like upper('" + searchStr + "') " +
                "or upper(quick_singer) like upper('%" + searchStr + "%') or upper(ascii_producer) like upper('%"+ searchStr +"%') " +
                "or songId like '%"+searchStr+"%' )";
        Log.d(LiveOkeRemoteApplication.TAG, "Search Query = " + query);
        return queryTotalPages(query);
    }

    private ConcurrentHashMap<String, String> queryTotalPages(String query) {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
        Cursor cursor = database.rawQuery(query, null);
        int numKeys = 0;
        int total = 0;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                total = cursor.getInt(0);
                if (total % 100 != 0) {
                    numKeys = (total / 100) + 1;
                } else {
                    numKeys = (total / 100);
                }
                Log.d(LiveOkeRemoteApplication.TAG,"value: " + numKeys);
            }
            cursor.close();
            int size = 100;
            int num = total;
            for (int i = 1; i <= numKeys; i++)  {
                num = num - size;
                // if total = 663
                // i=1 num=563
                // i=2 num=463
                // i=3 num=363
                // i=4 num=263
                // i=5 num=163
                // i=6 num=63
                // i=7 num=-37
                if (num < 0) {
                    if (i < 10) {
                        map.put("0"+i, (num+100)+"");
                    } else {
                        map.put(i+"",(num+100)+"");
                    }
                } else {
                    if (i < 10) {
                        map.put("0"+i,size+"");
                    } else {
                        map.put(i+"",size+"");
                    }
                }

            }
        }
        //Log.d("K7", "getKeyMap = " + map.size());
        return map;
    }
    /**
     * @deprecated
     * @param searchStr
     * @return
     * @throws Exception
     */
    public ConcurrentHashMap<String, String> getSearchKeysMap(String searchStr) throws Exception {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
        String query = "select distinct substr(title,1,1) as key, count(*) from songslist where " +
                "(upper(ascii_singer) like upper('%"+searchStr+"%') or upper(ascii_title) like upper('%"+
                searchStr+"%') or upper(ascii_author) like upper('%"+ searchStr +"%') or upper(ascii_producer) like upper('%"+ searchStr +"%') " +
                "or songId like '%"+searchStr+"%' ) " + "group by key";
        Log.d(LiveOkeRemoteApplication.TAG, "Search Query = " + query);
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String s = cursor.getString(0);
                if (s == null || s.equals("")) {
                    s = "?Unavailable";
                }
                String key = "?";
                if (Character.isLetterOrDigit(s.charAt(0))) {
                    key = SongHelper.getTheKey(s);
                }
                //Log.d("K7","key: " + key);
                int value = cursor.getInt(1);
                if (!map.containsKey(key)) {
                    map.put(key, value+"");
                } else {
                    int prev = Integer.parseInt(map.get(key));
                    prev += value;
                    map.put(key, prev+"");

                }
                //Log.d("K7","value: " + map.get(key));
            }
            cursor.close();
        }
        //Log.d("K7", "getKeyMap = " + map.size());
        return map;
    }

    public ConcurrentHashMap<String, String> getLanguageKeysMap(String language) throws Exception {
        //ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
        String query = "select distinct upper(substr(title,1,1)) as key, count(*) from songslist where language='"+language+"' group by key";
        return getTheTitleKeysMap(query);
    }

    public ConcurrentHashMap<String, String> getLanguageKeysMapNumber(String language) throws Exception {
        //ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
        String query = "select count(*) from songslist where language='"+language+"'";
        Log.v(LiveOkeRemoteApplication.TAG,"query = " + query);
        return queryTotalPages(query);
    }

    public int getTotalLanguage(String language) throws Exception {
        int count = 0;
        String query = "select count(*) from songslist where language='"+language+"'";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    public int getTotalFavorites() throws Exception {
        int count = 0;
        String query = "select count(*) from favoriteslist";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    public int getSongTotalNum() throws Exception {
        int count = 0;
        String query = "select count(*) from songslist";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    public ConcurrentHashMap<String, String> getFavoriteKeysMap() throws Exception {
        //ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
        //String query = "select distinct upper(substr(title,1,1)) from songslist";
        //String query = "select distinct substr(title,1,1) as key, count(*) from favoriteslist group by key";
        String query = "select count(*) from favoriteslist";
//		Cursor cursor = database.rawQuery(query, null);
//		if (cursor != null) {
//		    while (cursor.moveToNext()) {
//				String s = cursor.getString(0);
//				if (s == null || s.equals("")) {
//					s = "?Unavailable";
//				}
//				String key = "?";
//				if (Character.isLetterOrDigit(s.charAt(0))) {
//					key = SongUtil.getTheKey(s);
//				}
//				//Log.d("K7","key: " + key);
//				int value = cursor.getInt(1);
//				if (!map.containsKey(key)) {
//					map.put(key, value+"");
//				} else {
//					int prev = Integer.parseInt(map.get(key));
//					prev += value;
//					map.put(key, prev+"");
//
//				}
//				//Log.d("K7","value: " + map.get(key));
//		    }
//		    cursor.close();
//		}
//		//Log.d("K7", "getKeyMap = " + map.size());
//		return map;
        return queryTotalPages(query);
    }

    public ConcurrentHashMap<String, String> getSingerKeysMap() throws Exception {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
        //String query = "select distinct upper(substr(title,1,1)) from songslist";
        String query = "select distinct substr(singer,1,1) as key, count(*) from songslist group by key";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String s = cursor.getString(0);
                if (s == null || s.equals("")) {
                    s = "?Unavailable";
                }
                String key = "?";
                if (Character.isLetterOrDigit(s.charAt(0))) {
                    key = SongHelper.getTheKey(s);
                }
                //Log.d("K7","key: " + key);
                int value = cursor.getInt(1);
                if (!map.containsKey(key)) {
                    map.put(key, value+"");
                } else {
                    int prev = Integer.parseInt(map.get(key));
                    prev += value;
                    map.put(key, prev+"");

                }
                //Log.d("K7","value: " + map.get(key));
            }
            cursor.close();
        }
        //Log.d("K7", "getKeyMap = " + map.size());
        return map;
    }

    /**
     * @deprecated
     * @return
     * @throws Exception
     */
    public ConcurrentHashMap<String, ArrayList<Song>> getAllSongMap() throws Exception {
        ConcurrentHashMap<String, ArrayList<Song>> map = new
                ConcurrentHashMap<String, ArrayList<Song>>();
        ArrayList<Song> list = new ArrayList<Song>();
        Cursor cursor = database.query(SongListTable.SongList.TABLE_NAME,
                displayColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Song song = cursorToSongDisplay(cursor);
            String title = song.title.trim();
            if (title.equals("")) {
                title = "?Unavailable";
            }
            String s = title.substring(0, 1);
            String key = "?";
            if (Character.isLetterOrDigit(s.charAt(0))) {
                key = SongHelper.getTheKey(s);
            }
            list = map.putIfAbsent(key, new ArrayList<Song>());
            if (list == null) {
                list = map.get(key);
            }
            list.add(song);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return map;
    }

    /**
     *
     * @param searchStr
     * @return
     */
    public List<Song> findSong(String searchStr) {
        List<Song> songList = new ArrayList<Song>();
        Cursor cursor = database.query(SongListTable.SongList.TABLE_NAME,
                displayColumns,"songId like '%" + searchStr +
                        "%' or ascii_title like '%" + searchStr +
                        "%' or quick_title like '%" + searchStr +
                        "%' or ascii_singer like '%" + searchStr +
                        "%' or quick_singer like '%" + searchStr +
                        "%' or ascii_author like '%" + searchStr +
                        "%' or ascii_producer like '%"+ searchStr +"%'", null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Song song = cursorToSongDisplay(cursor);
            songList.add(song);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return songList;
    }

    /**
     *
     * @param id
     * @return
     */
    public Song findSongByID(String id) {
        Song song = null;
        Cursor cursor = database.query(SongListTable.SongList.TABLE_NAME,
                displayColumns,"songId = " + id, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            song = cursorToSongDisplay(cursor);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return song;
    }

    private Song cursorToFavoriteSongDisplay(Cursor cursor) {
        Song song = new Song();
        song.id = cursor.getString(0);
        song.title = cursor.getString(1);
        song.convertedTitle = cursor.getString(2);
        return song;
    }
    /**
     *
     * @param cursor
     * @return
     */
    private Song cursorToSongDisplay(Cursor cursor) {
        Song song = new Song();
        song.id = cursor.getString(0);
        song.title = cursor.getString(1);
        song.convertedTitle = cursor.getString(2);
        song.singer = cursor.getString(3);
        song.convertedSinger = cursor.getString(4);
        song.songPath = cursor.getString(5);
        song.type = cursor.getString(6);
        String author = cursor.getString(7);
        song.author = (author != null && !author.equals("") ? author : "?Unknown");
        String producer = cursor.getString(8);
        song.producer = (producer != null && !producer.equals("") ? producer : "?Unknown");
        return song;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void setDatabase(SQLiteDatabase database) {
        this.database = database;
    }

    public void deleteAllFavorites() {
        database.execSQL("delete from " + SongListTable.FavoriteList.TABLE_NAME);
        database.execSQL("delete from " + SongListTable.FavoriteSongConnection.TABLE_NAME);
    }


}
