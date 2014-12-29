package com.vnguyen.liveokeremote.data;

import android.graphics.drawable.Drawable;

import java.util.Comparator;
import java.util.Locale;

public class Song implements Comparable<Song> {

    public long dbID;
    public String id;
    public String title;
    public String convertedTitle;
    public String quickTitle;
    public String singer;
    public String convertedSinger;
    public String quickSinger;
    public String singerIcon;
    public String author;
    public String convertedAuthor;
    public String producer;
    public String convertProducer;
    public String songPath;
    public String classified;
    public String tone;
    public String tempo;
    public String type;
    public String language;
    public String favorites;
    public String swapped;
    public String popularRank;
    public String lyrics;
    public String volume;
    public String requester;
    public boolean favInSongList;

    public Drawable icon;

    @Override
    public int compareTo(Song compareSong) {
        String compareTitle = compareSong.title.toUpperCase(Locale.US);
        return title.toUpperCase(Locale.US).compareTo(compareTitle);
    }

    public static Comparator<Song> SongSingersComparator =
            new Comparator<Song>() {

                public int compare(Song song1, Song song2) {

                    String singer1 = song1.singer.trim().toUpperCase(Locale.US);
                    String singer2 = song2.singer.trim().toUpperCase(Locale.US);

                    //ascending order
                    return singer1.compareTo(singer2);

                    //descending order
                    //return fruitName2.compareTo(fruitName1);
                }
            };

    public static Comparator<Song> SongIDComparator =
            new Comparator<Song>() {

                public int compare(Song song1, Song song2) {

                    int id1 = Integer.parseInt(song1.id.trim());
                    int id2 = Integer.parseInt(song2.id.trim());

                    //ascending order
                    return id1 - id2;

                    //descending order
                    //return id2 - id1;
                }
            };

}