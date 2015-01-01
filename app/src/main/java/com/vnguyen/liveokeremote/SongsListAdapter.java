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

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.vnguyen.liveokeremote.data.Song;
import com.vnguyen.liveokeremote.data.User;
import com.vnguyen.liveokeremote.helper.PreferencesHelper;

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
        font2 = Typeface.createFromAsset(context.getAssets(),"fonts/VPSLGAN.TTF");
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
        setupActionButtonsBelow(swipeLayout);
        return v;
    }

    @Override
    public void fillValues(int position, View view) {
        SongListViewHolder holder = (SongListViewHolder) view.getTag();
        Song song = songs.get(position);
        if (holder == null) {
            holder = new SongListViewHolder();
            holder.iconImgView = (ImageView) view.findViewById(R.id.songs_icon);
            holder.idTxtView = (TextView) view.findViewById(R.id.song_id);
            holder.titleTxtView = (TextView) view.findViewById(R.id.song_title);
            holder.singerTxtView = (TextView) view.findViewById(R.id.song_singer);
            view.setTag(holder);
        }
        holder.iconImgView.setImageDrawable(song.icon);
        holder.idTxtView.setText(song.id);
        holder.titleTxtView.setTypeface(font);
//        holder.titleTxtView.setTextSize(30);
        holder.titleTxtView.setText(song.title);
        holder.singerTxtView.setTypeface(font2);
//        holder.singerTxtView.setTextSize(30);
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

    public void setupActionButtonsBelow(final SwipeLayout swipeLayout) {
        ViewGroup vBottom = swipeLayout.getBottomView();
        ViewGroup vTop = swipeLayout.getSurfaceView();
        final TextView idNumber = (TextView) vTop.findViewById(R.id.song_id);
        final TextView songTitle = (TextView) vTop.findViewById(R.id.song_title);

        ImageView rsvp4MeImgView = (ImageView) vBottom.findViewById(R.id.reserve_for_me_id);
        ImageView rsvp4FriendsImgView = (ImageView) vBottom.findViewById(R.id.reserve_for_friends_id);
        ImageView add2FavImgView = (ImageView) vBottom.findViewById(R.id.add_to_favorites_id);

        context.drawableHelper.setIconAsBackground("fa-user", R.color.white, rsvp4MeImgView, context);
        context.drawableHelper.setIconAsBackground("fa-group", R.color.white, rsvp4FriendsImgView, context);
        context.drawableHelper.setIconAsBackground("fa-heart", R.color.white, add2FavImgView, context);

        rsvp4MeImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(context)
                        .title("Reserve This Song.")
                        .content("Do you want to reserve this song for YOU?")
                        .theme(Theme.LIGHT)  // the default is light, so you don't need this line
                        .positiveText("OK")
                        .negativeText("CANCEL")
                        .callback(new MaterialDialog.Callback() {

                            @Override
                            public void onNegative(MaterialDialog materialDialog) {
                            }

                            @Override
                            public void onPositive(MaterialDialog materialDialog) {
                                if (context.webSocketHelper != null && context.webSocketHelper.isConnected()) {
                                    String cmd = "reserve," + idNumber.getText() + "," + context.me.name;
                                    Log.v(context.app.TAG, "cmd = " + cmd);
                                    context.webSocketHelper.sendMessage(cmd);
                                    swipeLayout.toggle();
                                } else {
                                    SnackbarManager.show(Snackbar.with(context)
                                            .type(SnackbarType.MULTI_LINE)
                                            .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                            .textColor(Color.WHITE)
                                            .color(Color.RED)
                                            .text("ERROR: Not Connected"));
                                }
                            }
                        })
                        .show();
            }
        });

        rsvp4FriendsImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<User> friends = PreferencesHelper.getInstance(context).retrieveFriendsList();
                String[] frNames = new String[friends.size()];
                for (int i = 0; i < frNames.length;i++) {
                    frNames[i] = friends.get(i).name;
                }
                new MaterialDialog.Builder(context)
                        .title("Reserve for a friend")
                        .items(frNames)
                        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                                Log.v(context.app.TAG,"Selected: " + charSequence);
                                if (context.webSocketHelper != null && context.webSocketHelper.isConnected()) {
                                    context.webSocketHelper.sendMessage("reserve," + idNumber.getText() + "," + charSequence);
                                    SnackbarManager.show(Snackbar.with(context)
                                            .type(SnackbarType.MULTI_LINE)
                                            .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                            .textColor(Color.WHITE)
                                            .color(Color.BLACK)
                                            .text("'" + songTitle.getText() + "' is reserved for " + charSequence));
                                } else {
                                    SnackbarManager.show(Snackbar.with(context)
                                            .type(SnackbarType.MULTI_LINE)
                                            .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                            .textColor(Color.WHITE)
                                            .color(Color.RED)
                                            .text("ERROR: Not Connected"));
                                }
                                swipeLayout.toggle();
                            }
                        })
                        .positiveText("Choose")
                        .negativeText("Cancel")
                        .show();
            }
        });

        add2FavImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

        private class SongListViewHolder {
            ImageView iconImgView;
            TextView idTxtView;
            TextView titleTxtView;
            TextView singerTxtView;
    }

}

