package com.vnguyen.liveokeremote;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.ArrayList;

public class SongsListAdapter extends BaseSwipeAdapter {
    private MainActivity context;
    private ArrayList<Song> songs;
    private SwipeLayout swipeLayout;
    private Typeface font;
    private Typeface font2;

    public SongsListAdapter(Context context, ArrayList<Song> songs) {
        this.context = (MainActivity) context;
        this.songs = new ArrayList<>(songs.size());
        this.songs.addAll(songs);
        font = Typeface.createFromAsset(context.getAssets(),"fonts/Vegur-B_0500.otf");
        font2 = Typeface.createFromAsset(context.getAssets(),"fonts/Vegur-R_0500.otf");
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.songs_swipe_layout;
    }

    @Override
    public View generateView(int position, ViewGroup viewGroup) {
        Log.i(context.app.TAG,"generateView?");
        View v = LayoutInflater.from(context).inflate(R.layout.songs_list_item, null);
        swipeLayout = (SwipeLayout) v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);
        return v;
    }

    @Override
    public void fillValues(int position, View view) {
        Log.i(context.app.TAG, "fillValues called?");
        Song song = songs.get(position);
        ImageView iconImg = (ImageView) view.findViewById(R.id.songs_icon);
        if (song.getIcon() == null) {
            iconImg.setImageDrawable(context.drawableHelper.buildDrawable(song.getTitle().substring(0, 1), "round"));
        } else {
            iconImg.setImageDrawable(song.getIcon());
        }
        if (position % 2 == 0) {
            view.setBackgroundColor(Color.parseColor("#ffffff"));
        } else {
            view.setBackgroundColor(Color.parseColor("#BCF7F0"));
        }
        TextView titleView = (TextView) view.findViewById(R.id.song_title);
        titleView.setTypeface(font);
        titleView.setText(song.getTitle());
        TextView singerView = (TextView) view.findViewById(R.id.song_singer);
        singerView.setTypeface(font2);
        singerView.setText(song.getSinger());
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
