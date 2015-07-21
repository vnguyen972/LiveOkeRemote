package com.vnguyen.liveokeremote;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.google.gson.Gson;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.vnguyen.liveokeremote.data.Song;
import com.vnguyen.liveokeremote.data.SongResult;
import com.vnguyen.liveokeremote.helper.AlertDialogHelper;
import com.vnguyen.liveokeremote.helper.LogHelper;
import com.vnguyen.liveokeremote.helper.SongHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class SongsListAdapter extends BaseSwipeAdapter {
    private MainActivity context;
    private ArrayList<Song> songs;
    private Typeface font;
    private Typeface font2;
    private SwipeLayout swipeLayout;

    public SongsListAdapter(Context context, ArrayList<Song> songs) {
        this.context = (MainActivity) context;
        this.songs = new ArrayList<>(songs.size());
        this.songs.addAll(songs);
        font = Typeface.createFromAsset(context.getAssets(),"fonts/NotoSans/NotoSans-Bold.ttf");
        font2 = Typeface.createFromAsset(context.getAssets(),"fonts/NotoSans/NotoSans-Italic.ttf");
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
        return v;
    }

    @Override
    public void fillValues(int position, View view) {
        SongListViewHolder holder = (SongListViewHolder) view.getTag();
        final Song song = songs.get(position);
        TextView smallLandscape = null;
        if (holder == null) {
            holder = new SongListViewHolder();
            holder.iconImgView = (ImageView) view.findViewById(R.id.songs_icon);
            holder.idTxtView = (TextView) view.findViewById(R.id.song_id);
            holder.titleTxtView = (TextView) view.findViewById(R.id.song_title);
            TextView tv = (TextView) view.findViewById(R.id.song_singer);
            if (tv != null) {
                holder.singerTxtView = tv;
            } else {
                smallLandscape = (TextView) view.findViewById(R.id.song_singer_land);
                holder.singerTxtView = smallLandscape;
            }
            holder.authorTxtView = (TextView) view.findViewById(R.id.song_author);
            holder.producerTxtView = (TextView) view.findViewById(R.id.song_producer);
            holder.position = position;
            view.setTag(holder);
        }
        holder.iconImgView.setImageDrawable(song.icon);
        holder.iconImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogHelper.i("CLICK ON SONG: " + song.title);
                try {
                    AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                        final AlertDialogHelper adh = new AlertDialogHelper(context);
                        ArrayList<SongResult> results = new ArrayList<>();
                        MaterialDialog dialog;
                        SongResultsAdapter adapter;

                        @Override
                        protected void onPreExecute() {
                            adh.popupProgress("Searching online for " + song.title);
                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                            String json = SongHelper.searchSong(song.title);
                            LogHelper.i("JSON = " + json);
                            try {
                                JSONArray jsonArray = new JSONArray(json);
                                Gson gson = new Gson();
                                for (int i = 0; i < jsonArray.length();i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    SongResult result = gson.fromJson(obj.toString(),SongResult.class);
                                    results.add(result);
                                }
                                LogHelper.i("RESULTS: " + results.size());
                                adapter = new SongResultsAdapter(context,results);

                            } catch (JSONException e) {
//                                e.printStackTrace();
                            }
                            return null;
                        }

                        @SuppressLint("NewApi")
                        @Override
                        protected void onPostExecute(Void aVoid) {
                            // @todo: research to fix this July 2015
                            dialog = new MaterialDialog.Builder(context)
                                    .title(Html.fromHtml("Found <font color='#009688'>" + song.title + "</font> Online: "))
                                    .adapter(adapter, new MaterialDialog.ListCallback() {
                                        @Override
                                        public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                                        }
                                    })
                                    .build();
                            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                @Override
                                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                                        if (adapter.myHandler != null) {
                                            adapter.myHandler.removeCallbacks(adapter.mProgressUpdater);
                                            context.mediaPlayer.stop();
                                        }
                                        dialog.dismiss();
                                    }
                                    // return false: telling Android that I ONLY handle the
                                    // keys specified here, Android does the rest.
                                    return false;
                                }
                            });
                            adh.dismissProgress();
                            dialog.show();
                        }

                    };
                    task.execute((Void[]) null);

//                            context.liveOkeUDPClient.sendMessage("getlyric,"+song.id,
//                                    context.liveOkeUDPClient.liveOkeIPAddress,
//                                    context.liveOkeUDPClient.LIVEOKE_UDP_PORT);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (context.listingBy.equalsIgnoreCase("favorites")) {
            holder.idTxtView.setText(song.convertedTitle.trim());
        } else {
            holder.idTxtView.setText(song.id.trim());
        }
        holder.titleTxtView.setTypeface(font);
//        holder.titleTxtView.setTextSize(21);
        holder.titleTxtView.setText(song.title.trim());
        holder.singerTxtView.setTypeface(font2);
        if (context.app.landscapeOriented) {
            if (context.listingBy.equalsIgnoreCase("favorites")) {
                holder.singerTxtView.setText("");
            } else {
                if (holder.authorTxtView != null) {
                    holder.authorTxtView.setText(song.author.trim());
                }
                if (holder.producerTxtView != null) {
                    holder.producerTxtView.setText(song.producer.trim());
                }
                if (smallLandscape == null) {
                    // large landscape mode
                    holder.singerTxtView.setText(song.singer.trim());
                } else {
                    // small landscape
                    //holder.singerTxtView.setText(song.singer.trim() + " - " + song.author.trim() + " - " + song.producer.trim());
                    String desc2Display = "";
                    for (String desc : context.app.displaySongDescFrom) {
                        switch (desc) {
                            case "Author":
                                desc2Display += (desc2Display.length() > 0 ? "-" + song.author : song.author);
                                break;
                            case "Producer":
                                desc2Display += (desc2Display.length() > 0 ? "-" + song.producer : song.producer);
                                break;
                            default:
                                desc2Display += (desc2Display.length() > 0 ? "-" + song.singer : song.singer);
                                break;
                        }
                    }
                    holder.singerTxtView.setText(desc2Display);
                }
            }
        } else {
            // portrait mode (all modes)
            String desc2Display = "";
            if (!context.listingBy.equalsIgnoreCase("favorites")) {
                for (String desc : context.app.displaySongDescFrom) {
                    switch (desc) {
                        case "Author":
                            desc2Display += (desc2Display.length() > 0 ? "-" + song.author : song.author);
                            break;
                        case "Producer":
                            desc2Display += (desc2Display.length() > 0 ? "-" + song.producer : song.producer);
                            break;
                        default:
                            desc2Display += (desc2Display.length() > 0 ? "-" + song.singer : song.singer);
                            break;
                    }
                }
            } else {
                desc2Display = "";
            }
            holder.singerTxtView.setText(desc2Display);
        }
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
                            .callback(new MaterialDialog.ButtonCallback() {

                                @Override
                                public void onNegative(MaterialDialog materialDialog) {
                                }

                                @Override
                                public void onPositive(MaterialDialog materialDialog) {
                                    //if (context.webSocketHelper != null && context.webSocketHelper.isConnected()) {
                                    if (context.liveOkeUDPClient != null) {
                                        String cmd = "reserve," + idNumber.getText() + "," + context.me.name;
                                        LogHelper.v("cmd = " + cmd);
                                        //context.webSocketHelper.sendMessage(cmd);
                                        context.liveOkeUDPClient.sendMessage(cmd,
                                                context.liveOkeUDPClient.liveOkeIPAddress,
                                                context.liveOkeUDPClient.LIVEOKE_UDP_PORT);
                                        swipeLayout.toggle();
                                        if (context.interstitialAd.isLoaded()) {
                                            try {
                                                Thread.sleep(500);
                                            } catch (InterruptedException e) {

                                            }
                                            context.interstitialAd.show();
                                            // update the reserved count notification
                                            //context.reservedCount++;
                                            //context.notifCountButton.setText(String.valueOf(ma.reservedCount));
                                        } else {
                                            // Toast.makeText(listView.getContext(),
                                            // "Ad not ready",
                                            // Toast.LENGTH_SHORT).show();
                                            LogHelper.i(
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
                            .callback(new MaterialDialog.ButtonCallback() {

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
                    //ArrayList<User> friends = PreferencesHelper.getInstance(context).retrieveFriendsList();
                    String[] frNames = new String[context.friendsList.size()];
                    for (int i = 0; i < frNames.length; i++) {
                        frNames[i] = context.friendsList.get(i).name;
                    }
                    if (frNames.length > 0) {
                        new MaterialDialog.Builder(context)
                                .title("Reserve for a friend")
                                .items(frNames)
                                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                                        LogHelper.i("Selected: " + charSequence);
                                        //if (context.webSocketHelper != null && context.webSocketHelper.isConnected()) {
                                        if (context.liveOkeUDPClient != null) {
                                            //context.webSocketHelper.sendMessage("reserve," + idNumber.getText() + "," + charSequence);
                                            context.liveOkeUDPClient.sendMessage("reserve," + idNumber.getText() + "," + charSequence,
                                                    context.liveOkeUDPClient.liveOkeIPAddress,
                                                    context.liveOkeUDPClient.LIVEOKE_UDP_PORT);
                                            if (context.interstitialAd.isLoaded()) {
                                                try {
                                                    Thread.sleep(500);
                                                } catch (InterruptedException e) {

                                                }
                                                context.interstitialAd.show();
                                                // update the reserved count notification
                                                //context.reservedCount++;
                                                //context.notifCountButton.setText(String.valueOf(ma.reservedCount));
                                            } else {
                                                // Toast.makeText(listView.getContext(),
                                                // "Ad not ready",
                                                // Toast.LENGTH_SHORT).show();
                                                LogHelper.i(
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
                                        return true;
                                    }
                                })
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onNeutral(MaterialDialog dialog) {
                                        context.friendsListHelper.displayFriendsListPanel();
                                    }
                                })
                                .positiveText("Choose")
                                .negativeText("Cancel")
                                .neutralText("Add New Friend")
                                .show();
                    } else {
                        // no friends yet
                        new MaterialDialog.Builder(context)
                                .title("No Friends Yet")
                                .content("Add new friend?")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog materialDialog) {
                                        context.friendsListHelper.displayFriendsListPanel();
                                    }
                                })
                                .positiveText("OK")
                                .negativeText("Cancel")
                                .show();
                    }
                } else {
                    new MaterialDialog.Builder(context)
                            .title("Delete Favorite Song.")
                            .content("Do you want to delete this song from your favorite list?")
                            .theme(Theme.LIGHT)  // the default is light, so you don't need this line
                            .positiveText("OK")
                            .negativeText("CANCEL")
                            .callback(new MaterialDialog.ButtonCallback() {

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
                                        context.getPagerFavorites();
                                        context.updateMainDisplay();
                                        SnackbarManager.show(Snackbar.with(context)
                                                .type(SnackbarType.MULTI_LINE)
                                                .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                                .textColor(Color.WHITE)
                                                .color(Color.BLACK)
                                                .text("Favorite Item Removed."));
                                        context.navigationDrawerHelper.refreshDrawer();
                                        context.db.saveDB();
                                    } catch (Exception ex) {
                                        LogHelper.e(ex.getMessage(),ex);

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
                            .callback(new MaterialDialog.ButtonCallback() {

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
                                        context.getPagerFavorites();
                                        context.updateMainDisplay();
                                        context.navigationDrawerHelper.refreshDrawer();
                                        context.db.saveDB();
                                    } catch (Exception ex) {
                                        LogHelper.e(ex.getMessage(), ex);
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
            TextView producerTxtView;
            TextView authorTxtView;
            int position;
    }

}

