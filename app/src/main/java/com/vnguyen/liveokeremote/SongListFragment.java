package com.vnguyen.liveokeremote;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;


public class SongListFragment extends Fragment {
    public static final String ARG_SECTION_NUMBER = "section_number";

    private ListView songListView;
    private SongsListAdapter adapter;
    private ArrayList<Song> songsList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(R.layout.song_list_fragment,container,false);
        songListView = (ListView) rootView.findViewById(R.id.songs_list);
        songsList = ((MainActivity) getActivity()).app.generateTestSongs();
        adapter = new SongsListAdapter(getActivity(),songsList);
        songListView.setAdapter(adapter);
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
