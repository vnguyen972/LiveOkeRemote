package com.vnguyen.liveokeremote;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.vnguyen.liveokeremote.data.Song;

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
        View v = LayoutInflater.from(context).inflate(R.layout.songs_list_item, null);
        swipeLayout = (SwipeLayout) v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);
        return v;
    }

    @Override
    public void fillValues(int position, View view) {
        SongListViewHolder holder = (SongListViewHolder) view.getTag();
        Song song = songs.get(position);
        if (holder == null) {
            holder = new SongListViewHolder();
            holder.iconImgView = (ImageView) view.findViewById(R.id.songs_icon);
            holder.titleTxtView = (TextView) view.findViewById(R.id.song_title);
            holder.singerTxtView = (TextView) view.findViewById(R.id.song_singer);
            view.setTag(holder);
        }
        holder.iconImgView.setImageDrawable(song.icon);
        holder.titleTxtView.setTypeface(font);
        holder.titleTxtView.setText(song.title);
        holder.singerTxtView.setTypeface(font2);
        holder.singerTxtView.setText(song.singer);
//        View sv = swipeLayout.findViewById(R.id.surfaceView);
//        if (position % 2 == 0) {
//            sv.setBackgroundColor(Color.parseColor("#ffffff"));
//        } else {
//            sv.setBackgroundColor(Color.parseColor("#BCF7F0"));
//        }
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

    private class SongListViewHolder {
            ImageView iconImgView;
            TextView titleTxtView;
            TextView singerTxtView;
    }
}

