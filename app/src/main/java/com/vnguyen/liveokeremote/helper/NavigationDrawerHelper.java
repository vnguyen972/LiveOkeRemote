package com.vnguyen.liveokeremote.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.thedazzler.droidicon.IconicFontDrawable;
import com.vnguyen.liveokeremote.MainActivity;
import com.vnguyen.liveokeremote.NavDrawerListAdapter;
import com.vnguyen.liveokeremote.PreferencesHelper;
import com.vnguyen.liveokeremote.R;
import com.vnguyen.liveokeremote.data.NavDrawerItem;
import com.vnguyen.liveokeremote.data.ReservedListItem;
import com.vnguyen.liveokeremote.data.User;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class NavigationDrawerHelper {

    private final MainActivity context;
    public boolean showFriendsList;
    private Handler mHandler = new Handler();
    public NavDrawerListAdapter navAdapter;
    private ConcurrentHashMap<String, String> dataMap;

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
                if (mDrawerList.getCheckedItemPosition() == 0) {
                    context.getPagerTitles();
                    context.updateMainDisplay();
                    mDrawerList.setItemChecked(0, false);
                } else if (mDrawerList.getCheckedItemPosition() == 2) {
                    context.getPagerLanguage("VN");
                    context.updateMainDisplay();
                    mDrawerList.setItemChecked(2, false);
                } else if (mDrawerList.getCheckedItemPosition() == 3) {
                    context.getPagerLanguage("EN");
                    context.updateMainDisplay();
                    mDrawerList.setItemChecked(3, false);
                } else if (mDrawerList.getCheckedItemPosition() == 4) {
                    context.getPagerLanguage("CN");
                    context.updateMainDisplay();
                    mDrawerList.setItemChecked(4, false);
                } else if (mDrawerList.getCheckedItemPosition() == 7 ) {
                    //context.rsvpPanelHelper.refreshFriendsList(context.app.generateTestFriends());
                    final ArrayList<User> friends = new ArrayList<User>();
                    final ArrayList<ReservedListItem> rsvpItems = new ArrayList<ReservedListItem>();
                    AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                        ProgressDialog pd;
                        AlertDialogHelper ah = new AlertDialogHelper(context);

                        @Override
                        protected void onPreExecute() {
                            ah.popupProgress("Loading friends...");
                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                context.friendsList = PreferencesHelper.getInstance(context).retrieveFriends();
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        context.friendsListHelper.initFriendList(context.friendsList);
                                        if (context.viewFlipper.getDisplayedChild() == 0) {
                                            context.viewFlipper.showNext();
                                        }
                                        context.actionBarHelper.setTitle(context.getResources().getString(R.string.friends_title));
                                        mDrawerList.setItemChecked(7, false);
                                        ah.popupProgress(context.friendsList.size() + " friends loaded.");
                                    }
                                });
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            ah.dismissProgress();
                        }
                    };
                    task.execute((Void[])null);
                } else if (mDrawerList.getCheckedItemPosition() == 8) {
                    if (context.webSocketHelper != null && context.webSocketHelper.isConnected()) {
                        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                            ProgressDialog pd;
                            AlertDialogHelper ah = new AlertDialogHelper(context);

                            @Override
                            protected void onPreExecute() {
                                context.webSocketHelper.sendMessage("getsonglist");
                                ah.popupProgress("Updating songs list...");
                            }

                            @Override
                            protected Void doInBackground(Void... params) {
                                try {
                                    while (!context.webSocketHelper.gotTotalSongResponse) {
                                        Thread.sleep(1000);
                                        // waits until got the total songs response
                                    }
                                    ah.popupProgress("Downloading " + context.totalSong + " songs...");
                                    while (!context.webSocketHelper.doneGettingSongList) {
                                        Thread.sleep(1000);
                                        // waits for all songs downloaded
                                    }

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                context.getPagerTitles();
                                ah.dismissProgress();
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        context.updateMainDisplay();
                                        mDrawerList.setItemChecked(8,false);
                                    }
                                });
                            }
                        };
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

        Drawable iconDrawable = null;
        boolean showCounter = false;
        String navCounter = "0";
        for (int i = 0; i < navMenuTitles.length;i++) {
            String title = null;
            switch (i) {
                case 0:
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
                        Log.v(context.app.TAG,"count = " + count);
                    } catch (Exception ex) {
                        Log.e(context.app.TAG,ex.getMessage(),ex);
                    } finally {
                        context.db.close();
                    }
                    break;
                case 1:
                    // Songs List header
                    iconDrawable = null;
                    showCounter = false;
                    break;
                case 2:
                    // Vietnamese Songs
                    Bitmap vnIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.vn);
                    iconDrawable = new BitmapDrawable(context.getResources(), vnIcon);
                    showCounter = true;
                    try {
                        context.db.open();
                        int count = context.db.getTotalLanguage("VN");
                        navCounter = ""+count;
                        Log.v(context.app.TAG,"count = " + count);
                    } catch (Exception ex) {
                        Log.e(context.app.TAG,ex.getMessage(),ex);
                    } finally {
                        context.db.close();
                    }

                    break;
                case 3:
                    // English Songs
                    Bitmap usIcon = BitmapFactory.decodeResource(context.getResources(),R.drawable.us);
                    iconDrawable = new BitmapDrawable(context.getResources(), usIcon);
                    showCounter = true;
                    try {
                        context.db.open();
                        int count = context.db.getTotalLanguage("EN");
                        navCounter = ""+count;
                        Log.v(context.app.TAG,"count = " + count);
                    } catch (Exception ex) {
                        Log.e(context.app.TAG,ex.getMessage(),ex);
                    } finally {
                        context.db.close();
                    }
                    break;
                case 4:
                    // Chinese Songs
                    Bitmap cnIcon = BitmapFactory.decodeResource(context.getResources(),R.drawable.cn);
                    iconDrawable = new BitmapDrawable(context.getResources(), cnIcon);
                    showCounter = true;
                    try {
                        context.db.open();
                        int count = context.db.getTotalLanguage("CN");
                        navCounter = ""+count;
                        Log.v(context.app.TAG,"count = " + count);
                    } catch (Exception ex) {
                        Log.e(context.app.TAG,ex.getMessage(),ex);
                    } finally {
                        context.db.close();
                    }
                    break;
                case 5:
                    // Settings header
                    iconDrawable = null;
                    showCounter = false;
                    break;
                case 6:
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
                case 7:
                    // Friends List
                    IconicFontDrawable peopleIcon = new IconicFontDrawable(context.getApplicationContext());
                    peopleIcon.setIcon("gmd-people");
                    peopleIcon.setIconColor(context.getResources().getColor(R.color.primary));
                    iconDrawable = peopleIcon;
                    showCounter = false;
                    break;
                case 8:
                    // Update Songs List
                    IconicFontDrawable updateSongsListIcon = new IconicFontDrawable(context.getApplicationContext());
                    updateSongsListIcon.setIcon("gmd-assignment-turned-in");
                    updateSongsListIcon.setIconColor(context.getResources().getColor(R.color.primary));
                    iconDrawable = updateSongsListIcon;
                    showCounter = false;
                    break;
                case 9:
                    // send comments to screen
                    IconicFontDrawable send2ScreenIcon = new IconicFontDrawable(context.getApplicationContext());
                    send2ScreenIcon.setIcon("gmd-insert-comment");
                    send2ScreenIcon.setIconColor(context.getResources().getColor(R.color.primary));
                    iconDrawable = send2ScreenIcon;
                    showCounter = false;
                    break;
                case 10:
                    // your photo
                    IconicFontDrawable profile = new IconicFontDrawable(context.getApplicationContext());
                    profile.setIcon("gmd-account-circle");
                    profile.setIconColor(context.getResources().getColor(R.color.primary));
                    iconDrawable = profile;
                    showCounter = false;
                    break;
                case 11:
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
                Log.v(context.app.TAG, "Position = " + position);
                final NavDrawerListAdapter adapter = (NavDrawerListAdapter) mDrawerList.getAdapter();
                NavDrawerListAdapter.NavViewHolder holder = (NavDrawerListAdapter.NavViewHolder) view.getTag();
                FragmentManager manager;
                android.support.v4.app.FragmentTransaction transaction;
                switch (position) {
                    case 0:
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
                    case 6:
                        // IP Address
                        (new AlertDialogHelper(context)).
                                popupIPAddressDialog("LiveOke IP Address", "Enter IP Address",
                                        adapter.getItem(position),adapter);
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 7:
                    case 8:
                        // Friends List
                        mDrawerList.setItemChecked(position, true);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                context.mDrawerLayout.closeDrawers();
                            }
                        },10);
                        break;
                    case 10:
                        // your photo
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                context.mDrawerLayout.closeDrawers();
                            }
                        },10);
                        (new AlertDialogHelper(context)).popupFileChooser(
                                context.mReservedCountImgView,
                                context.getResources().getString(R.string.myAvatarURI));
                        break;
                }
            }
        });



    }
}
