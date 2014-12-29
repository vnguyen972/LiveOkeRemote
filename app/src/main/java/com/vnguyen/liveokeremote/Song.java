package com.vnguyen.liveokeremote;

import android.graphics.drawable.Drawable;

import java.util.Comparator;
import java.util.Locale;

public class Song implements Comparable<Song> {

    private long dbID;
    private String id;
    private String title;
    private String convertedTitle;
    private String quickTitle;
    private String singer;
    private String convertedSinger;
    private String quickSinger;
    private String singerIcon;
    private String author;
    private String convertedAuthor;
    private String producer;
    private String convertProducer;
    private String songPath;
    private String classified;
    private String tone;
    private String tempo;
    private String type;
    private String language;
    private String favorites;
    private String swapped;
    private String popularRank;
    private String lyrics;
    private String volume;
    private String requester;
    private boolean favInSongList;

    private Drawable icon;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getSinger() {
        return singer;
    }
    public void setSinger(String singer) {
        this.singer = singer;
    }
    public String getSingerIcon() {
        return singerIcon;
    }
    public void setSingerIcon(String singerIcon) {
        this.singerIcon = singerIcon;
    }
    public String getProducer() {
        return producer;
    }
    public void setProducer(String producer) {
        this.producer = producer;
    }
    public String getSongPath() {
        return songPath;
    }
    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }
    public String getRequester() {
        return requester;
    }
    public void setRequester(String requester) {
        this.requester = requester;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getTone() {
        return tone;
    }
    public void setTone(String tone) {
        this.tone = tone;
    }
    public String getTempo() {
        return tempo;
    }
    public void setTempo(String tempo) {
        this.tempo = tempo;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getLyrics() {
        return lyrics;
    }
    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }
    public String getClassified() {
        return classified;
    }
    public void setClassified(String classified) {
        this.classified = classified;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public String getFavorites() {
        return favorites;
    }
    public void setFavorites(String favorites) {
        this.favorites = favorites;
    }
    public String getSwapped() {
        return swapped;
    }
    public void setSwapped(String swapped) {
        this.swapped = swapped;
    }
    public String getPopularRank() {
        return popularRank;
    }
    public void setPopularRank(String popularRank) {
        this.popularRank = popularRank;
    }
    public String getVolume() {
        return volume;
    }
    public void setVolume(String volume) {
        this.volume = volume;
    }
    @Override
    public int compareTo(Song compareSong) {
        String compareTitle = compareSong.getTitle().toUpperCase(Locale.US);
        return getTitle().toUpperCase(Locale.US).compareTo(compareTitle);
    }

    public String getConvertedTitle() {
        return convertedTitle;
    }
    public void setConvertedTitle(String convertedTitle) {
        this.convertedTitle = convertedTitle;
    }

    public String getConvertedSinger() {
        return convertedSinger;
    }
    public void setConvertedSinger(String convertedSinger) {
        this.convertedSinger = convertedSinger;
    }


    public long getDbID() {
        return dbID;
    }
    public void setDbID(long dbID) {
        this.dbID = dbID;
    }


    public String getConvertedAuthor() {
        return convertedAuthor;
    }
    public void setConvertedAuthor(String convertedAuthor) {
        this.convertedAuthor = convertedAuthor;
    }


    public String getConvertProducer() {
        return convertProducer;
    }
    public void setConvertProducer(String convertProducer) {
        this.convertProducer = convertProducer;
    }


    public boolean isFavInSongList() {
        return favInSongList;
    }
    public void setFavInSongList(boolean favInSongList) {
        this.favInSongList = favInSongList;
    }


    public String getQuickTitle() {
        return quickTitle;
    }
    public void setQuickTitle(String quickTitle) {
        this.quickTitle = quickTitle;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }


    public String getQuickSinger() {
        return quickSinger;
    }
    public void setQuickSinger(String quickSinger) {
        this.quickSinger = quickSinger;
    }


    public static Comparator<Song> SongSingersComparator =
            new Comparator<Song>() {

                public int compare(Song song1, Song song2) {

                    String singer1 = song1.getSinger().trim().toUpperCase(Locale.US);
                    String singer2 = song2.getSinger().trim().toUpperCase(Locale.US);

                    //ascending order
                    return singer1.compareTo(singer2);

                    //descending order
                    //return fruitName2.compareTo(fruitName1);
                }
            };

    public static Comparator<Song> SongIDComparator =
            new Comparator<Song>() {

                public int compare(Song song1, Song song2) {

                    int id1 = Integer.parseInt(song1.getId().trim());
                    int id2 = Integer.parseInt(song2.getId().trim());

                    //ascending order
                    return id1 - id2;

                    //descending order
                    //return id2 - id1;
                }
            };

}