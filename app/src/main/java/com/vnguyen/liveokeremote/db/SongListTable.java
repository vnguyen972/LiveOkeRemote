package com.vnguyen.liveokeremote.db;

import android.provider.BaseColumns;

public class SongListTable {
    public SongListTable() {}

    public static abstract class FavoriteSongConnection implements BaseColumns {
        public static final String TABLE_NAME = "favoritesongconnection";
        public static final String COLUMN_NAME_FAVORITE_ID = "favoriteID";
        public static final String COLUMN_NAME_CONNECTION_ID = "connectionID";
        public static final String COLUMN_NAME_SONG_ID = "songID";
    }
    public static abstract class FavoriteList implements BaseColumns {
        public static final String TABLE_NAME = "favoriteslist";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_ASCII_TITLE = "ascii_title";
    }
    public static abstract class K7Connection implements BaseColumns {
        public static final String TABLE_NAME = "k7connection";
        public static final String COLUMN_NAME_CONNECTION_NAME = "name";
        public static final String COLUMN_NAME_IP_ADDRESS = "ipAddress";
        public static final String COLUMN_NAME_ACTIVE_STATUS = "active";
    }
    public static abstract class SongList implements BaseColumns {
        public static final String TABLE_NAME = "songslist";
        public static final String COLUMN_NAME_SONG_ID = "songId";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_ASCII_TITLE = "ascii_title";
        public static final String COLUMN_NAME_QUICK_TITLE = "quick_title";
        public static final String COLUMN_NAME_SINGER = "singer";
        public static final String COLUMN_NAME_ASCII_SINGER = "ascii_singer";
        public static final String COLUMN_NAME_QUICK_SINGER = "quick_singer";
        public static final String COLUMN_NAME_SINGER_ICON = "singerIcon";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_ASCII_AUTHOR = "ascii_author";
        public static final String COLUMN_NAME_PRODUCER = "producer";
        public static final String COLUMN_NAME_ASCII_PRODUCER = "ascii_producer";
        public static final String COLUMN_NAME_SONG_PATH = "songPath";
        public static final String COLUMN_NAME_CLASSIFIED = "classified";
        public static final String COLUMN_NAME_TONE = "tone";
        public static final String COLUMN_NAME_TEMPO = "tempo";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_LANGUAGE = "language";
        public static final String COLUMN_NAME_FAVORITES = "favorites";
        public static final String COLUMN_NAME_SWAPPED = "swapped";
        public static final String COLUMN_NAME_POPULAR_RANK = "popularRank";
        public static final String COLUMN_NAME_LYRICS = "lyrics";
        public static final String COLUMN_NAME_VOLUME = "volume";
        public static final String COLUMN_NAME_REQUESTER = "requester";
    }
}
