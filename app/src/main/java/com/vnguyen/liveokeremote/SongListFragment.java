package com.vnguyen.liveokeremote;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.vnguyen.liveokeremote.data.Song;
import com.vnguyen.liveokeremote.db.SongListDataSource;
import com.vnguyen.liveokeremote.helper.SongHelper;

import java.util.ArrayList;
import java.util.Collections;


public class SongListFragment extends Fragment {
    public static final String ARG_SECTION_NUMBER = "section_number";

    private ListView songListView;
    private SongsListAdapter adapter;
    private ArrayList<Song> songsList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(R.layout.song_list_fragment,container,false);
        final MainActivity ma = (MainActivity) getActivity();
        songListView = (ListView) rootView.findViewById(R.id.songs_list);
        ArrayList<String> sortedKeys = new ArrayList<>();
        sortedKeys.addAll(ma.pagerTitles.keySet());
        Collections.sort(sortedKeys);
        final String key = sortedKeys.get(getArguments().getInt(ARG_SECTION_NUMBER));
        Log.v(ma.app.TAG, "SongListFragment.key = " + key);
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                    }
                    Log.i(ma.app.TAG,"SongListFragment-listing by: " + ma.listingBy);
                    Log.i(ma.app.TAG,"Songs found: " + songsList.size());
                    adapter = new SongsListAdapter(ma, songsList);
                } catch (Exception ex) {
                    Log.e(ma.app.TAG,ex.getMessage(),ex);
                } finally {
                    db.close();
                }
                ma.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        songListView.setAdapter(adapter);
                    }
                });
            }
        }).start();
//        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected void onPreExecute() {
//            }
//
//            @Override
//            protected Void doInBackground(Void... params) {
//                SongListDataSource db = new SongListDataSource(ma);
//                try {
//                    db.open();
//                    if (ma.listingBy.equalsIgnoreCase("title")) {
//                        songsList = db.getSongByKeys(ma.listingBy, SongHelper.translateKey(key),ma.searchStr);
//                    } else if (ma.listingBy.equalsIgnoreCase("search") ||
//                            ma.listingBy.equalsIgnoreCase("favorites") ||
//                            ma.listingBy.equalsIgnoreCase("VN") ||
//                            ma.listingBy.equalsIgnoreCase("EN") ||
//                            ma.listingBy.equalsIgnoreCase("CN")) {
//                        songsList = db.getSongByKeys(ma.listingBy,key, ma.searchStr);
//                    }
//                    Log.i(ma.app.TAG,"SongListFragment-listing by: " + ma.listingBy);
//                    Log.i(ma.app.TAG,"Songs found: " + songsList.size());
//                    adapter = new SongsListAdapter(ma, songsList);
//                } catch (Exception ex) {
//                    Log.e(ma.app.TAG,ex.getMessage(),ex);
//                } finally {
//                    db.close();
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//               songListView.setAdapter(adapter);
//            }
//        };
//        task.execute((Void[])null);
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

}
