package com.vnguyen.liveokeremote.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.thedazzler.droidicon.IconicFontDrawable;
import com.vnguyen.liveokeremote.LiveOkeRemoteApplication;
import com.vnguyen.liveokeremote.LiveOkeUDPClient;
import com.vnguyen.liveokeremote.MainActivity;
import com.vnguyen.liveokeremote.NavDrawerListAdapter;
import com.vnguyen.liveokeremote.R;
import com.vnguyen.liveokeremote.SongListRetriever;
import com.vnguyen.liveokeremote.data.NavDrawerItem;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class NavigationDrawerHelper {

    private final MainActivity context;
    public boolean showFriendsList;
    private Handler mHandler = new Handler();
    public NavDrawerListAdapter navAdapter;

    // MUST match the order of the text on strings.xml (nav_menu_items)
    public static final int HOME = 0;
    public static final int HEADER_1 = 1;
    public static final int FAVORITED_SONGS = 2;
    public static final int VN_SONGS = 3;
    public static final int EN_SONGS = 4;
    public static final int CN_SONGS = 5;
    public static final int HEADER_2 = 6;
    public static final int COMMENT_TO_SCREEN = 7;
    public static final int TOGGLE_FULL_SCREEN = 8;
    public static final int BACKUP_TO_SD = 9;
    public static final int UPDATE_SONGS_LIST = 10;
    public static final int HEADER_3 = 11;
    public static final int FRIENDS_LIST = 12;
    public static final int YOUR_PHOTO = 13;
    public static final int IP_ADDRESS = 14;
    public static final int MASTER_CODE = 15;
    public static final int HELP = 16;

    public NavigationDrawerHelper(Context context) {
        this.context = (MainActivity)context;
        showFriendsList = false;
    }

    public void setupSlidingNav(Toolbar toolbar) {
        // Setup Nav menu
        String[] navMenuTitles = context.getResources().getStringArray(R.array.nav_menu_items);
        final ListView mDrawerList = (ListView) context.findViewById(R.id.list_slidermenu);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(context, context.mDrawerLayout, toolbar, R.string.open, R.string.close){
            @Override
            public void onDrawerSlide(View view, float v) {
                super.onDrawerSlide(view,v);
            }

            @Override
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                //invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View view) {
//                final long startTime = System.currentTimeMillis();
//                context.friendsList = PreferencesHelper.getInstance(context).retrieveFriends();
                if (mDrawerList.getCheckedItemPosition() == HOME) {
                    context.getPagerTitles();
                    context.updateMainDisplay();
                    mDrawerList.setItemChecked(HOME, false);
                } else if (mDrawerList.getCheckedItemPosition() == FAVORITED_SONGS) {
                    context.getSupportActionBar().setTitle("FAVORITED SONGS");
                    context.getPagerFavorites();
                    context.updateMainDisplay();
                    mDrawerList.setItemChecked(FAVORITED_SONGS, false);
                } else if (mDrawerList.getCheckedItemPosition() == VN_SONGS) {
                    context.getSupportActionBar().setTitle("VIET SONGS");
                    context.getPagerLanguage("VN");
                    context.updateMainDisplay();
                    mDrawerList.setItemChecked(VN_SONGS, false);
                } else if (mDrawerList.getCheckedItemPosition() == EN_SONGS) {
                    context.getSupportActionBar().setTitle("ENGLISH SONGS");
                    context.getPagerLanguage("EN");
                    context.updateMainDisplay();
                    mDrawerList.setItemChecked(EN_SONGS, false);
                } else if (mDrawerList.getCheckedItemPosition() == CN_SONGS) {
                    context.getSupportActionBar().setTitle("CHINESE SONGS");
                    context.getPagerLanguage("CN");
                    context.updateMainDisplay();
                    mDrawerList.setItemChecked(CN_SONGS, false);
                } else if (mDrawerList.getCheckedItemPosition() == FRIENDS_LIST ) {
                    context.friendsListHelper.displayFriendsListPanel();
                    mDrawerList.setItemChecked(FRIENDS_LIST, false);
                } else if (mDrawerList.getCheckedItemPosition() == UPDATE_SONGS_LIST) {
                    new MaterialDialog.Builder(context)
                            .title("Update Song List.")
                            .theme(Theme.LIGHT)  // the default is light, so you don't need this line
                            .content("Do you want to update your songs list?")
                            .positiveText("OK")
                            .negativeText("CANCEL")
                            .callback(new MaterialDialog.Callback() {

                                @Override
                                public void onNegative(MaterialDialog materialDialog) {
                                }

                                @Override
                                public void onPositive(MaterialDialog materialDialog) {
                                    //if (context.webSocketHelper != null && context.webSocketHelper.isConnected()) {
                                    if (context.liveOkeUDPClient != null) {
                                        final long startTime = System.currentTimeMillis();
                                        final AlertDialogHelper adh = new AlertDialogHelper(context);
                                        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                                            @Override
                                            protected void onPreExecute() {
                                                try {
                                                    Log.v(LiveOkeRemoteApplication.TAG, "PRE-Exec: getsonglist");
                                                    //context.webSocketHelper.doneGettingSongList = false;
                                                    context.liveOkeUDPClient.doneGettingSongList = false;
                                                    Log.v(LiveOkeRemoteApplication.TAG, "PRE-Exec: set doneGettingSongList to false");
                                                    //context.webSocketHelper.gotTotalSongResponse = false;
                                                    context.liveOkeUDPClient.gotTotalSongResponse = false;
                                                    Log.v(LiveOkeRemoteApplication.TAG, "PRE-Exec: set gotTotalSongResponse to false");
                                                    adh.popupProgress("Updating song list...");
                                                } catch (Throwable t) {
                                                    t.printStackTrace();
                                                }
                                            }

                                            @Override
                                            protected Void doInBackground(Void... params) {
                                                try {
                                                    //context.webSocketHelper.sendMessage("getsonglist");
//                                                    context.liveOkeUDPClient.sendMessage("getsonglist",
//                                                            context.liveOkeUDPClient.liveOkeIPAddress,
//                                                            context.liveOkeUDPClient.LIVEOKE_UDP_PORT);
                                                    SongListRetriever slRetriever = new SongListRetriever(context);
                                                    slRetriever.getSongList();
                                                    Log.v(LiveOkeRemoteApplication.TAG, "PRE-Exec: sending 'getsonglist' command...");
                                                    //while (!context.webSocketHelper.gotTotalSongResponse) {
                                                    while (!context.liveOkeUDPClient.gotTotalSongResponse) {
                                                        long currTime = System.currentTimeMillis();
                                                        if (currTime - startTime > 10000) {
                                                            break;
                                                        }
                                                        Log.v(LiveOkeRemoteApplication.TAG, "PRE-Exec: waiting for total song");
                                                        try {
                                                            Thread.sleep(1000);
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                        // waits until got the total songs response
                                                    }
                                                    if (context.totalSong > 0) {
                                                        context.runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                adh.popupProgress("Downloading " + context.totalSong + " songs...");
                                                            }
                                                        });
                                                    }
                                                    if (context.totalSong > 0) {
                                                        Log.v(LiveOkeRemoteApplication.TAG, "Exec-BG: getsonglist");
                                                        //while (!context.webSocketHelper.doneGettingSongList) {
                                                        while (!context.liveOkeUDPClient.doneGettingSongList) {
                                                            Thread.sleep(1000);
                                                            // waits for all songs downloaded
                                                        }
                                                    }
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                return null;
                                            }

                                            @Override
                                            protected void onPostExecute(Void aVoid) {
                                                Log.v(LiveOkeRemoteApplication.TAG,"POST-Exec: getsonglist");
                                                long endTime = System.currentTimeMillis();
                                                long total = endTime - startTime;
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                                                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                                context.getPagerTitles();
                                                context.updateMainDisplay();
                                                adh.dismissProgress();
                                                SnackbarManager.show(Snackbar.with(context)
                                                        .type(SnackbarType.MULTI_LINE)
                                                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                                        .textColor(Color.WHITE)
                                                        .color(Color.BLACK)
                                                        .text(context.totalSong + " songs loaded in " + dateFormat.format(new Date(total))));
                                                try {
                                                    context.db.saveDB();
                                                } catch (IOException e) {
                                                    Log.e(LiveOkeRemoteApplication.TAG,e.getMessage(),e);
                                                }
                                            }
                                        };
                                        Log.v(LiveOkeRemoteApplication.TAG,"Exec: getsonglist");
                                        task.execute((Void[])null);
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
                    mDrawerList.setItemChecked(UPDATE_SONGS_LIST,false);

                } else if (mDrawerList.getCheckedItemPosition() == COMMENT_TO_SCREEN) {
                    // send comment to screen
                    final EditText input = new EditText(context);
                    new MaterialDialog.Builder(context)
                            .title("Send your comment to TV Screen:")
                            .theme(Theme.LIGHT)  // the default is light, so you don't need this line
                            .customView(input)
                            .positiveText("OK")
                            .negativeText("CANCEL")
                            .callback(new MaterialDialog.Callback() {

                                @Override
                                public void onNegative(MaterialDialog materialDialog) {
                                }

                                @Override
                                public void onPositive(MaterialDialog materialDialog) {
                                    String value = input.getEditableText().toString().trim();
                                    //if (context.webSocketHelper != null && context.webSocketHelper.isConnected()) {
                                    if (context.liveOkeUDPClient != null) {
                                        //context.webSocketHelper.sendMessage("msg," + value);
                                        context.liveOkeUDPClient.sendMessage("msg," + value,
                                                context.liveOkeUDPClient.liveOkeIPAddress,
                                                LiveOkeUDPClient.LIVEOKE_UDP_PORT);
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
                    mDrawerList.setItemChecked(COMMENT_TO_SCREEN,false);
                } else if (mDrawerList.getCheckedItemPosition() == MASTER_CODE) {
                    // master code
                    (new AlertDialogHelper(context)).
                            popupMasterCode("Enter Server Master Code");
                    mDrawerList.setItemChecked(MASTER_CODE, false);
                } else if (mDrawerList.getCheckedItemPosition() == TOGGLE_FULL_SCREEN) {
                    //if (context.webSocketHelper != null && context.webSocketHelper.isConnected()) {
                    if (context.liveOkeUDPClient != null) {
                        //context.webSocketHelper.sendMessage("togglefullscreen");
                        context.liveOkeUDPClient.sendMessage("togglefullscreen",
                                context.liveOkeUDPClient.liveOkeIPAddress,
                                LiveOkeUDPClient.LIVEOKE_UDP_PORT);
                    } else {
                        SnackbarManager.show(Snackbar.with(context)
                                .type(SnackbarType.MULTI_LINE)
                                .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                .textColor(Color.WHITE)
                                .color(Color.RED)
                                .text("ERROR: Not Connected"));
                    }
                    mDrawerList.setItemChecked(TOGGLE_FULL_SCREEN, false);
                } else if (mDrawerList.getCheckedItemPosition() == BACKUP_TO_SD) {
                    try {
                        context.db.saveDB();
                        SnackbarManager.show(Snackbar.with(context)
                                .type(SnackbarType.MULTI_LINE)
                                .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                .textColor(Color.WHITE)
                                .color(Color.BLACK)
                                .text("Successfully backup songslist.db to SD Card."));
                    } catch (IOException e) {
                        Log.e(LiveOkeRemoteApplication.TAG,e.getMessage(),e);
                        SnackbarManager.show(Snackbar.with(context)
                                .type(SnackbarType.MULTI_LINE)
                                .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                .textColor(Color.WHITE)
                                .color(Color.RED)
                                .text("ERROR: " + e.getMessage()));
                    }
                    mDrawerList.setItemChecked(BACKUP_TO_SD, false);
                }
            }

            @Override
            public void onDrawerStateChanged(int i) {
                super.onDrawerStateChanged(i);
            }
        };
        context.mDrawerLayout.setDrawerListener(mDrawerToggle);
        context.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context.getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

        ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<>();

        Drawable iconDrawable;
        boolean showCounter;
        String navCounter = "0";
        for (int i = 0; i < navMenuTitles.length;i++) {
            String title = null;
            switch (i) {
                case HOME:
                    // Home
//                    iconDrawable = new IconDrawable(getApplicationContext(), Iconify.IconValue.md_home).
//                            colorRes(R.color.primary).sizeDp(100);
                    IconicFontDrawable iconicFontDrawable = new IconicFontDrawable(context.getApplicationContext());
                    iconicFontDrawable.setIcon("gmd-home");
                    iconicFontDrawable.setIconColor(context.getResources().getColor(R.color.primary));
                    iconDrawable = iconicFontDrawable;
                    showCounter = true;
                    try {
                        context.db.open();
                        int count = context.db.getSongTotalNum();
                        navCounter = ""+count;
                        Log.v(LiveOkeRemoteApplication.TAG,"count = " + count);
                    } catch (Exception ex) {
                        Log.e(LiveOkeRemoteApplication.TAG,ex.getMessage(),ex);
                    } finally {
                        context.db.close();
                    }
                    break;
                case HEADER_1:
                case HEADER_2:
                case HEADER_3:
                    // Songs List header
                    iconDrawable = null;
                    showCounter = false;
                    break;
                case FAVORITED_SONGS:
                    IconicFontDrawable favIcon = new IconicFontDrawable(context.getApplicationContext());
                    favIcon.setIcon("fa-heart");
                    favIcon.setIconColor(context.getResources().getColor(R.color.primary));
                    iconDrawable = favIcon;
                    showCounter = false;
                    break;
                case VN_SONGS:
                    // Vietnamese Songs
                    Bitmap vnIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.vn);
                    iconDrawable = new BitmapDrawable(context.getResources(), vnIcon);
                    showCounter = true;
                    try {
                        context.db.open();
                        int count = context.db.getTotalLanguage("VN");
                        navCounter = ""+count;
                        Log.v(LiveOkeRemoteApplication.TAG,"count = " + count);
                    } catch (Exception ex) {
                        Log.e(LiveOkeRemoteApplication.TAG,ex.getMessage(),ex);
                    } finally {
                        context.db.close();
                    }

                    break;
                case EN_SONGS:
                    // English Songs
                    Bitmap usIcon = BitmapFactory.decodeResource(context.getResources(),R.drawable.us);
                    iconDrawable = new BitmapDrawable(context.getResources(), usIcon);
                    showCounter = true;
                    try {
                        context.db.open();
                        int count = context.db.getTotalLanguage("EN");
                        navCounter = ""+count;
                        Log.v(LiveOkeRemoteApplication.TAG,"count = " + count);
                    } catch (Exception ex) {
                        Log.e(LiveOkeRemoteApplication.TAG,ex.getMessage(),ex);
                    } finally {
                        context.db.close();
                    }
                    break;
                case CN_SONGS:
                    // Chinese Songs
                    Bitmap cnIcon = BitmapFactory.decodeResource(context.getResources(),R.drawable.cn);
                    iconDrawable = new BitmapDrawable(context.getResources(), cnIcon);
                    showCounter = true;
                    try {
                        context.db.open();
                        int count = context.db.getTotalLanguage("CN");
                        navCounter = ""+count;
                        Log.v(LiveOkeRemoteApplication.TAG,"count = " + count);
                    } catch (Exception ex) {
                        Log.e(LiveOkeRemoteApplication.TAG,ex.getMessage(),ex);
                    } finally {
                        context.db.close();
                    }
                    break;
                case IP_ADDRESS:
                    // IP Address
//                    iconDrawable = new com.joanzapata.android.iconify.IconDrawable(getApplicationContext(),
//                            com.joanzapata.android.iconify.Iconify.IconValue.fa_plug).colorRes(R.color.primary);
                    IconicFontDrawable ipAddrIcon = new IconicFontDrawable(context.getApplicationContext());
                    ipAddrIcon.setIcon("fa-plug");
                    ipAddrIcon.setIconColor(context.getResources().getColor(R.color.primary));
                    iconDrawable = ipAddrIcon;
                    showCounter = false;
                    String ip = PreferencesHelper.getInstance(context).getPreference(context.getResources().getString(R.string.ip_adress));
                    if (ip != null && !ip.equals("")) {
                        title = navMenuTitles[i] + " (" + ip  + ")";
                    }
                    break;
                case FRIENDS_LIST:
                    // Friends List
                    IconicFontDrawable peopleIcon = new IconicFontDrawable(context.getApplicationContext());
                    peopleIcon.setIcon("gmd-people");
                    peopleIcon.setIconColor(context.getResources().getColor(R.color.primary));
                    iconDrawable = peopleIcon;
                    showCounter = false;
                    break;
                case UPDATE_SONGS_LIST:
                    // Update Songs List
                    IconicFontDrawable updateSongsListIcon = new IconicFontDrawable(context.getApplicationContext());
                    updateSongsListIcon.setIcon("gmd-assignment-turned-in");
                    updateSongsListIcon.setIconColor(context.getResources().getColor(R.color.primary));
                    iconDrawable = updateSongsListIcon;
                    showCounter = false;
                    break;
                case COMMENT_TO_SCREEN:
                    // send comments to screen
                    IconicFontDrawable send2ScreenIcon = new IconicFontDrawable(context.getApplicationContext());
                    send2ScreenIcon.setIcon("gmd-insert-comment");
                    send2ScreenIcon.setIconColor(context.getResources().getColor(R.color.primary));
                    iconDrawable = send2ScreenIcon;
                    showCounter = false;
                    break;
                case TOGGLE_FULL_SCREEN:
                    IconicFontDrawable toggleScreenIcon = new IconicFontDrawable(context.getApplicationContext());
                    toggleScreenIcon.setIcon("gmd-aspect-ratio");
                    toggleScreenIcon.setIconColor(context.getResources().getColor(R.color.primary));
                    iconDrawable = toggleScreenIcon;
                    showCounter = false;
                    break;
                case BACKUP_TO_SD:
                    IconicFontDrawable backupSDIcon = new IconicFontDrawable(context.getApplicationContext());
                    backupSDIcon.setIcon("gmd-archive");
                    backupSDIcon.setIconColor(context.getResources().getColor(R.color.primary));
                    iconDrawable = backupSDIcon;
                    showCounter = false;
                    break;
                case YOUR_PHOTO:
                    // your photo
                    IconicFontDrawable profile = new IconicFontDrawable(context.getApplicationContext());
                    profile.setIcon("gmd-account-circle");
                    profile.setIconColor(context.getResources().getColor(R.color.primary));
                    iconDrawable = profile;
                    showCounter = false;
                    break;
                case MASTER_CODE:
                    // master code
                    IconicFontDrawable lockIcon = new IconicFontDrawable(context.getApplicationContext());
                    lockIcon.setIcon("gmd-lock");
                    lockIcon.setIconColor(context.getResources().getColor(R.color.primary));
                    iconDrawable = lockIcon;
                    showCounter = false;
                    break;
                case HELP:
                    // Help
                    IconicFontDrawable helpIcon = new IconicFontDrawable(context.getApplicationContext());
                    helpIcon.setIcon("gmd-help");
                    helpIcon.setIconColor(context.getResources().getColor(R.color.primary));
                    iconDrawable = helpIcon;
                    showCounter = false;
                    break;
                default:
                    continue;
            }
            navDrawerItems.add(new NavDrawerItem((title != null ? title : navMenuTitles[i]), iconDrawable, showCounter, navCounter));
        }
        navAdapter = new NavDrawerListAdapter(context.getApplicationContext());
        for (NavDrawerItem item : navDrawerItems) {
            navAdapter.addItem(item);
        }
        mDrawerList.setAdapter(navAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                Log.v(LiveOkeRemoteApplication.TAG, "Position = " + position);
                final NavDrawerListAdapter adapter = (NavDrawerListAdapter) mDrawerList.getAdapter();
                switch (position) {
                    case HOME:
                        // Home
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                context.mDrawerLayout.closeDrawers();
                            }
                        },10);
                        if (context.viewFlipper.getDisplayedChild() == 1) {
                            context.viewFlipper.showPrevious();
                        }
                        context.actionBarHelper.setTitle(context.getResources().getString(R.string.app_name));
                        break;
                    case IP_ADDRESS:
                        // IP Address
                        (new AlertDialogHelper(context)).
                                popupIPAddressDialog("LiveOke IP Address", "Enter IP Address",
                                        adapter.getItem(position),adapter);
                        break;
                    case YOUR_PHOTO:
                        // your photo
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                context.mDrawerLayout.closeDrawers();
                            }
                        },10);
                        CharSequence[] choices = {"Set new avatar","Reset to default"};
                        new MaterialDialog.Builder(context)
                                .title("Reserve for a friend")
                                .items(choices)
                                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallback() {
                                    @SuppressLint("NewApi")
                                    @Override
                                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                                        Log.v(context.app.TAG, "Selected: " + charSequence);
                                        if (charSequence.toString().equalsIgnoreCase("Set new avatar")) {
                                            (new AlertDialogHelper(context)).popupFileChooser(
                                                    context.mReservedCountImgView,
                                                    context.getResources().getString(R.string.myAvatarURI));
                                        } else {
                                            // reset to default avatar here
                                            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_profile);
                                            BitmapDrawable bd = new BitmapDrawable(context.drawableHelper.getCroppedBitmap(bm));
                                            context.mReservedCountImgView.setImageDrawable(bd);
                                            context.me.avatar =  bd;
                                            PreferencesHelper.getInstance(context).remoteMyAvatar();
                                        }
                                    }

                                })
                                .positiveText("Choose")
                                .negativeText("Cancel")
                                .show();

                        mDrawerList.setItemChecked(YOUR_PHOTO, false);
                        break;
                    default:
                        // just close the nav bar
                        mDrawerList.setItemChecked(position, true);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                context.mDrawerLayout.closeDrawers();
                            }
                        },10);
                        break;
                }
            }
        });

    }
}
