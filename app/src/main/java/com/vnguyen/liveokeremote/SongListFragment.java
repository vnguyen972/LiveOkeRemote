package com.vnguyen.liveokeremote;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.vnguyen.liveokeremote.data.Song;
import com.vnguyen.liveokeremote.db.SongListDataSource;
import com.vnguyen.liveokeremote.helper.DrawableHelper;
import com.vnguyen.liveokeremote.helper.LogHelper;
import com.vnguyen.liveokeremote.helper.SongHelper;
import com.vnguyen.liveokeremote.youtube.YTVideoItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;


public class SongListFragment extends Fragment {
    public static final String ARG_SECTION_NUMBER = "section_number";

    private ListView songListView;
    private SongsListAdapter adapter;
    private ArrayList<Song> songsList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(R.layout.song_list_fragment, container, false);
        final MainActivity ma = (MainActivity) getActivity();
        songListView = (ListView) rootView.findViewById(R.id.songs_list);
        ArrayList<String> sortedKeys = new ArrayList<>();
        sortedKeys.addAll(ma.pagerTitles.keySet());
        Collections.sort(sortedKeys);
        final String key = sortedKeys.get(getArguments().getInt(ARG_SECTION_NUMBER));
        //LogHelper.i("SongListFragment.key = " + key);
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Void doInBackground(Void... params) {
                SongListDataSource db = new SongListDataSource(ma);
                try {
                    db.open();
                    if (ma.listingBy.equalsIgnoreCase("title")) {
                        songsList = db.getSongByKeys(ma.listingBy, SongHelper.translateKey(key),ma.searchStr);
                    } else if (ma.listingBy.equalsIgnoreCase("search") ||
                            ma.listingBy.equalsIgnoreCase("favorites") ||
                            ma.listingBy.equalsIgnoreCase("VN") ||
                            ma.listingBy.equalsIgnoreCase("EN") ||
                            ma.listingBy.equalsIgnoreCase("CN")) {
                        songsList = db.getSongByKeys(ma.listingBy,key, ma.searchStr);
                    } else if (ma.listingBy.equalsIgnoreCase("youtube")) {
                        songsList = getYTSongByKeys(ma,key);
                    }
                    LogHelper.i("SongListFragment-listing by: " + ma.listingBy);
                    LogHelper.i("Songs found: " + songsList.size());
                    adapter = new SongsListAdapter(ma, songsList);
                } catch (Exception ex) {
                    LogHelper.e(ex.getMessage(),ex);
                } finally {
                    db.close();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
               songListView.setAdapter(adapter);
            }
        };
        task.execute((Void[])null);
        songListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (adapter.getOpenItems().size() > 0) {
                    adapter.closeAllExcept(null);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        return rootView;
    }

    private ArrayList<Song> getYTSongByKeys(MainActivity ma, String key) {
        ArrayList<Song> ytList = new ArrayList<Song>();
        ma.youtube.query.setFields(null);
        String token = ma.tokenMap.get(key);
        if (token != null) {
            if (token.equals("")) {
                ma.youtube.query.setPageToken(null);
            } else {
                ma.youtube.query.setPageToken(ma.tokenMap.get(key));
            }
        }
        LogHelper.i("YT Query = " + ma.youtube.query + "- Key = " + key);
        ma.ytSearchResults = ma.youtube.search(ma.searchStr);
        int nextKey = Integer.parseInt(key) + 1;
        String nextKeyStr = (nextKey < 10 ? "0"+nextKey : ""+nextKey);
        int prevKey = Integer.parseInt(key) - 1;
        String prevKeyStr = (prevKey < 10 ? "0"+prevKey : ""+prevKey);
        LogHelper.i("prevKey = " + prevKeyStr + " - nextKey = " + nextKeyStr);
        if (ma.youtube.nextPageToken != null) {
            if (!ma.tokenMap.containsKey(nextKeyStr)) {
                ma.tokenMap.put(nextKeyStr, ma.youtube.nextPageToken);
            }
        }
        if (prevKey > 0) {
            if (ma.tokenMap.containsKey(prevKeyStr)) {
                //LogHelper.i("In here?");
                ma.tokenMap.put(prevKeyStr, ma.youtube.prevPageToken);
            }
        }
//        Set keySet = ma.tokenMap.keySet();
//        for (Iterator<String> it = keySet.iterator();it.hasNext();) {
//            String k = it.next();
//            String val = ma.tokenMap.get(k);
//            LogHelper.i("k = " + k + "- v = " + val);
//        }
        for (YTVideoItem ytVideo : ma.ytSearchResults) {
            Song song = new Song();
            song.title = ytVideo.getTitle();
            song.id = ytVideo.getId();
            song.icon = (new DrawableHelper()).buildDrawable(song.id.substring(0, 1), "round");
            song.singer = "YouTube";
            song.producer = "YouTube";
            song.type = "online";
            ytList.add(song);
        }
        return ytList;
    }

}
