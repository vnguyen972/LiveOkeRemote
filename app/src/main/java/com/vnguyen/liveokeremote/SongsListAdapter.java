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
import com.vnguyen.liveokeremote.helper.SongHelper;

import java.util.ArrayList;
import java.util.Iterator;

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
        //font = Typeface.createFromAsset(context.getAssets(),"fonts/Vegur-B_0500.otf");
        //font2 = Typeface.createFromAsset(context.getAssets(),"fonts/VPSLGAN.TTF");
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.songs_swipe_layout;
    }

    @Override
    public View generateView(int position, ViewGroup viewGroup) {
        View v = LayoutInflater.from(context).inflate(R.layout.songs_list_item, null);
        swipeLayout = (SwipeLayout) v.findViewById(R.id.songs_swipe_layout);
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);
        setupActionButtonsBelow(swipeLayout);
//        View sv = swipeLayout.findViewById(R.id.surfaceView);
//        if (position % 2 == 0) {
//            sv.setBackgroundColor(Color.parseColor("#ffffff"));
//        } else {
//            sv.setBackgroundColor(Color.parseColor("#BCF7F0"));
//        }
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
            holder.position = position;
            view.setTag(holder);
        }
        holder.iconImgView.setImageDrawable(song.icon);
        if (context.listingBy.equalsIgnoreCase("favorites")) {
            holder.idTxtView.setText(song.convertedTitle);
        } else {
            holder.idTxtView.setText(song.id);
        }
//        holder.titleTxtView.setTypeface(font);
//        holder.titleTxtView.setTextSize(21);
        holder.titleTxtView.setText(song.title);
//        holder.singerTxtView.setTypeface(font2);
//        holder.singerTxtView.setTextSize(30);
        holder.singerTxtView.setText(song.singer);
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

        final TextView songTitle = (TextView) vTop.findViewById(R.id.song_title);

        final TextView idNumber = (TextView) vTop.findViewById(R.id.song_id);

        ImageView rsvp4MeImgView = (ImageView) vBottom.findViewById(R.id.reserve_for_me_id);
        ImageView rsvp4FriendsImgView = (ImageView) vBottom.findViewById(R.id.reserve_for_friends_id);
        ImageView add2FavImgView = (ImageView) vBottom.findViewById(R.id.add_to_favorites_id);

        if (!context.listingBy.equalsIgnoreCase("favorites")) {
            context.drawableHelper.setIconAsBackground("fa-user", R.color.white, rsvp4MeImgView, context);
            context.drawableHelper.setIconAsBackground("fa-group", R.color.white, rsvp4FriendsImgView, context);
            context.drawableHelper.setIconAsBackground("fa-heart", R.color.white, add2FavImgView, context);
        } else {
            context.drawableHelper.setIconAsBackground("fa-search", R.color.white, rsvp4MeImgView, context);
            context.drawableHelper.setIconAsBackground("fa-trash", R.color.white, rsvp4FriendsImgView, context);
            add2FavImgView.setImageResource(android.R.color.transparent);
        }

        rsvp4MeImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!context.listingBy.equalsIgnoreCase("favorites")) {
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
                                        if (context.interstitialAd.isLoaded()) {
                                            context.interstitialAd.show();
                                            // update the reserved count notification
                                            //context.reservedCount++;
                                            //context.notifCountButton.setText(String.valueOf(ma.reservedCount));
                                        } else {
                                            // Toast.makeText(listView.getContext(),
                                            // "Ad not ready",
                                            // Toast.LENGTH_SHORT).show();
                                            Log.d(context.app.TAG,
                                                    "Ad is not ready to display, getting new Ad...");
                                            context.getNewAd();
                                        }
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
                } else {
                    new MaterialDialog.Builder(context)
                            .title("Search Favorite Song.")
                            .content("Do you want to search this song on your songlist?")
                            .theme(Theme.LIGHT)  // the default is light, so you don't need this line
                            .positiveText("OK")
                            .negativeText("CANCEL")
                            .callback(new MaterialDialog.Callback() {

                                @Override
                                public void onNegative(MaterialDialog materialDialog) {
                                }

                                @Override
                                public void onPositive(MaterialDialog materialDialog) {
                                    // perform search here
                                    context.getPagerSearch(idNumber.getText().toString());
                                    context.actionBarHelper.resetTitle();
                                    context.updateMainDisplay();
                                }
                            })
                            .show();
                }
            }
        });

        rsvp4FriendsImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!context.listingBy.equalsIgnoreCase("favorites")) {
                    ArrayList<User> friends = PreferencesHelper.getInstance(context).retrieveFriendsList();
                    String[] frNames = new String[friends.size()];
                    for (int i = 0; i < frNames.length; i++) {
                        frNames[i] = friends.get(i).name;
                    }
                    new MaterialDialog.Builder(context)
                            .title("Reserve for a friend")
                            .items(frNames)
                            .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                                    Log.v(context.app.TAG, "Selected: " + charSequence);
                                    if (context.webSocketHelper != null && context.webSocketHelper.isConnected()) {
                                        context.webSocketHelper.sendMessage("reserve," + idNumber.getText() + "," + charSequence);
                                        if (context.interstitialAd.isLoaded()) {
                                            context.interstitialAd.show();
                                            // update the reserved count notification
                                            //context.reservedCount++;
                                            //context.notifCountButton.setText(String.valueOf(ma.reservedCount));
                                        } else {
                                            // Toast.makeText(listView.getContext(),
                                            // "Ad not ready",
                                            // Toast.LENGTH_SHORT).show();
                                            Log.d(context.app.TAG,
                                                    "Ad is not ready to display, getting new Ad...");
                                            context.getNewAd();
                                        }

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
                } else {
                    new MaterialDialog.Builder(context)
                            .title("Delete Favorite Song.")
                            .content("Do you want to delete this song from your favorite list?")
                            .theme(Theme.LIGHT)  // the default is light, so you don't need this line
                            .positiveText("OK")
                            .negativeText("CANCEL")
                            .callback(new MaterialDialog.Callback() {

                                @Override
                                public void onNegative(MaterialDialog materialDialog) {
                                }

                                @Override
                                public void onPositive(MaterialDialog materialDialog) {
                                    // perform detete favorites here
                                    try {
                                        context.db.open();
                                        context.db.deleteFavorite(idNumber.getText().toString());
                                        for (Iterator<Song> it = songs.iterator();it.hasNext();) {
                                            Song song = it.next();
                                            if (song.convertedTitle.equalsIgnoreCase(idNumber.getText().toString())) {
                                                it.remove();
                                                notifyDataSetChanged();
                                                break;
                                            }
                                        }
                                        context.actionBarHelper.setSubTitle(songs.size() + " Songs.");
                                        SnackbarManager.show(Snackbar.with(context)
                                                .type(SnackbarType.MULTI_LINE)
                                                .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                                .textColor(Color.WHITE)
                                                .color(Color.BLACK)
                                                .text("Favorite Item Removed."));
                                    } catch (Exception ex) {
                                        Log.e(context.app.TAG,ex.getMessage(),ex);

                                    } finally {
                                        context.db.close();
                                    }
                                }
                            })
                            .show();
                }
            }
        });

        add2FavImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!context.listingBy.equalsIgnoreCase("favorites")) {
                    new MaterialDialog.Builder(context)
                            .title("Favorite This Song.")
                            .content("Do you want to save this song to your favorites?")
                            .theme(Theme.LIGHT)  // the default is light, so you don't need this line
                            .positiveText("OK")
                            .negativeText("CANCEL")
                            .callback(new MaterialDialog.Callback() {

                                @Override
                                public void onNegative(MaterialDialog materialDialog) {
                                }

                                @Override
                                public void onPositive(MaterialDialog materialDialog) {
                                    try {
                                        String sTitle = songTitle.getText().toString();
                                        String asciiTittle = SongHelper.convertAllChars(sTitle);
                                        Song song = new Song();
                                        song.title = sTitle;
                                        song.convertedTitle = asciiTittle;
                                        context.db.open();
                                        context.db.save2Favorite(song);
                                        SnackbarManager.show(Snackbar.with(context)
                                                .type(SnackbarType.MULTI_LINE)
                                                .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                                .textColor(Color.WHITE)
                                                .color(Color.BLACK)
                                                .text("Saved '" + sTitle + "' to your favorites successfully."));
                                    } catch (Exception ex) {
                                        Log.e(context.app.TAG, ex.getMessage(), ex);
                                    } finally {
                                        context.db.close();
                                    }
                                }
                            })
                            .show();
                } else {
                    // do nothing
                }
            }
        });
    }

        private class SongListViewHolder {
            ImageView iconImgView;
            TextView idTxtView;
            TextView titleTxtView;
            TextView singerTxtView;
            int position;
    }

}

