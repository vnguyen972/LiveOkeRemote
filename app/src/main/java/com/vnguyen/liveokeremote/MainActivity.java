package com.vnguyen.liveokeremote;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.androidquery.AQuery;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.vnguyen.liveokeremote.data.AquiredPhoto;
import com.vnguyen.liveokeremote.data.User;
import com.vnguyen.liveokeremote.data.WebSocketInfo;
import com.vnguyen.liveokeremote.db.SongListDataSource;
import com.vnguyen.liveokeremote.helper.ActionBarHelper;
import com.vnguyen.liveokeremote.helper.AlertDialogHelper;
import com.vnguyen.liveokeremote.helper.DrawableHelper;
import com.vnguyen.liveokeremote.helper.FloatingButtonsHelper;
import com.vnguyen.liveokeremote.helper.FriendsListHelper;
import com.vnguyen.liveokeremote.helper.NavigationDrawerHelper;
import com.vnguyen.liveokeremote.helper.NowPlayingHelper;
import com.vnguyen.liveokeremote.helper.PreferencesHelper;
import com.vnguyen.liveokeremote.helper.RsvpPanelHelper;
import com.vnguyen.liveokeremote.helper.UDPBroadcastHelper;
import com.vnguyen.liveokeremote.helper.WebSocketHelper;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import cat.lafosca.facecropper.FaceCropper;


public class MainActivity extends ActionBarActivity {

    public String serverMasterCode;
    public AQuery aq;
    public LiveOkeRemoteApplication app;

    public User me;
    public String myName;
    public ArrayList<User> friendsList;
    public ConcurrentHashMap<String, String> pagerTitles;
    public int totalSong;

    public Uri mImageCaptureUri;
    public AquiredPhoto aquiredPhoto;

    // Helpers
    public NavigationDrawerHelper navigationDrawerHelper;
    public FloatingButtonsHelper floatingButtonsHelper;
    public RsvpPanelHelper rsvpPanelHelper;
    public ActionBarHelper actionBarHelper;
    public FriendsListHelper friendsListHelper;
    public DrawableHelper drawableHelper;
    public WebSocketHelper webSocketHelper;
    public NowPlayingHelper nowPlayingHelper;
    public SongListDataSource db;
    public String searchStr;

    public Animation slide_in_left, slide_out_right;
    public ViewFlipper viewFlipper;

    // Variables to hold values from POPUP dialogs in SETTINGS
    public WebSocketInfo wsInfo;
    public String comment2Send2Screen;

    // GUI Components
    public DrawerLayout mDrawerLayout;
    public SlidingUpPanelLayout mSlidingPanel;
    public ImageView mReservedCountImgView;
    public TextView mNowPlayingTxtView;
    public MenuItem onOffSwitch;
    public ViewPager mViewPager;
    public SongsListPageAdapter mSongsListPagerAdapter;

    public OrientationEventListener myOrientationEventListener;
    public String listingBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        aq = new AQuery(getApplicationContext());
        app = (LiveOkeRemoteApplication) getApplication();
        aquiredPhoto = new AquiredPhoto();

        db = new SongListDataSource(MainActivity.this);

        pagerTitles = new ConcurrentHashMap<>();
        getPagerTitles();

        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        // Load Shared Preference
        if (wsInfo == null) {
            wsInfo = new WebSocketInfo();
            wsInfo.ipAddress = PreferencesHelper.getInstance(MainActivity.this).getPreference(
                    getResources().getString(R.string.ip_adress));
            wsInfo.port = PreferencesHelper.getInstance(MainActivity.this).getPreference(
                    getResources().getString(R.string.port));
            wsInfo.uri = "ws://"+wsInfo.ipAddress+":"+wsInfo.port;
        }
        if (myName == null) {
            myName = PreferencesHelper.getInstance(MainActivity.this).getPreference(
                    getResources().getString(R.string.myName));
        }
        if (rsvpPanelHelper == null) {
            rsvpPanelHelper = new RsvpPanelHelper(MainActivity.this);
        }

        // if myName is still not found (brand new use)
        if (myName == null || myName.equals("")) {
            new AlertDialogHelper(MainActivity.this).popupHello();
        } else {
            me = new User(myName);
        }

        // Init helpers
        if (drawableHelper == null) {
            drawableHelper = new DrawableHelper();
        }
        if (nowPlayingHelper == null) {
            nowPlayingHelper = new NowPlayingHelper(MainActivity.this);
        }


        viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        slide_in_left = AnimationUtils.loadAnimation(MainActivity.this,
                android.R.anim.slide_in_left);
        slide_out_right = AnimationUtils.loadAnimation(MainActivity.this,
                android.R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in_left);
        viewFlipper.setOutAnimation(slide_out_right);


        // setup toolbar as actionbar
        if (actionBarHelper == null) {
            actionBarHelper = new ActionBarHelper(MainActivity.this);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_TITLE);
        actionBarHelper.setTitle(getResources().getString(R.string.app_name));

        // setup sliding Navigation panel (hidden from left)
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        if (navigationDrawerHelper == null) {
            navigationDrawerHelper = new NavigationDrawerHelper(MainActivity.this);
        }
        navigationDrawerHelper.setupSlidingNav(toolbar);

        // setup floating action button(s)
        if (floatingButtonsHelper == null) {
            floatingButtonsHelper = new FloatingButtonsHelper(MainActivity.this);
        }
        floatingButtonsHelper.setupActionButtons();

        // setup sliding up panel layout
        mSlidingPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mSlidingPanel.setEnabled(false); // disable swiping to slide

        setupReservedPanel(); // setup reserved list panel
        floatingButtonsHelper.setupFriendsFloatingActionButtons();
        //setupFriendsListPanel();


        updateMainDisplay();
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            AlertDialogHelper ah = new AlertDialogHelper(MainActivity.this);

            @Override
            protected void onPreExecute() {
                ah.popupSplash();
            }

            @Override
            protected Void doInBackground(Void... params) {
                setupFriendsListPanel();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                ah.dismissSplash();
                getPagerTitles();
            }

        };
        task.execute((Void[])null);

    }

    public void setupFriendsListPanel() {
        if (friendsListHelper == null) {
            friendsListHelper = new FriendsListHelper(MainActivity.this);
        }
        //friendsListHelper.initFriendList(app.generateTestFriends());
        if (friendsList == null || friendsList.isEmpty()) {
            Log.v(app.TAG,friendsList+"-BEFORE");
            HashMap<String, Object> savedValues = (HashMap<String,Object>)getLastCustomNonConfigurationInstance();
            if (savedValues != null) {
                friendsList = (ArrayList<User>) savedValues.get("friendsList");
            }
            if (friendsList == null) {
                Log.v(app.TAG,"NULL from Retaining...");
                friendsList = PreferencesHelper.getInstance(MainActivity.this).retrieveFriends();
            }
            Log.v(app.TAG,friendsList+"-AFTER");
        }

        Log.v(app.TAG,"setupFriendsListPanel is called!!!");
        friendsListHelper.initFriendList(friendsList);
//        ListView friendList = (ListView) findViewById(R.id.friends_list);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        onOffSwitch = menu.findItem(R.id.on_off_switch);
        onOffSwitch.setActionView(R.layout.on_off_switch);
        final SwitchCompat switchButton = (SwitchCompat) onOffSwitch.getActionView().findViewById(R.id.switchForActionBar);
        if (webSocketHelper != null && webSocketHelper.isConnected()) {
            switchButton.setChecked(true);
        }
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.i(app.TAG,"SWITCHED ON");
                    if (wsInfo.ipAddress != null && !wsInfo.ipAddress.equals("")) {
                        // if there's an IP presents
                        wsInfo.port = "8181";
                        wsInfo.uri = "ws://" + wsInfo.ipAddress + ":" + wsInfo.port;
                        Log.v(app.TAG,"URI = " + wsInfo.uri);
                        if (webSocketHelper == null) {
                            webSocketHelper = new WebSocketHelper(MainActivity.this);
                        }
                        webSocketHelper.connect();
                    } else{
                        // if not, we will search the network for it
                        new Thread(new Runnable() {
                            //WebSocketInfo _wsInfo;
                            @Override
                            public void run() {
                                UDPBroadcastHelper udpHelper = new UDPBroadcastHelper();
                                wsInfo = udpHelper.findServer();
                                if (wsInfo != null) {
                                    if (webSocketHelper == null) {
                                        webSocketHelper = new WebSocketHelper(MainActivity.this);
                                    }
                                    webSocketHelper.connect();
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            switchButton.toggle();
                                            SnackbarManager.show(Snackbar.with(MainActivity.this)
                                                    .type(SnackbarType.MULTI_LINE)
                                                    .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                                    .textColor(Color.WHITE)
                                                    .color(Color.RED)
                                                    .text("ERROR: Unable to find LiveOke!"));
                                        }
                                    });
                                }
                            }
                        }).start();
                    }
                } else {
                    if (webSocketHelper != null) {
                        webSocketHelper.disconnect();
                    }
                    Log.i(app.TAG, "SWITCHED OFF");
                }
            }
        });


        IconDrawable searchIcon = new IconDrawable(getApplicationContext(), Iconify.IconValue.md_search);
        searchIcon.sizeDp(30);
        searchIcon.colorRes(R.color.white);
        MenuItem search = menu.findItem(R.id.menu_search);
        search.setIcon(searchIcon);
        MenuItemCompat.setOnActionExpandListener(search,new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // reset to master song list when back from searching
                getPagerTitles();
                updateMainDisplay();
                return true;
            }
        });
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        //searchView.setMaxWidth(800);
        Log.v(app.TAG, searchView + "");
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(true);
            SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener()
            {
                public boolean onQueryTextChange(String newText)
                {
                    // this is your adapter that will be filtered
                    //adapter.getFilter().filter(newText);
                    return true;
                }

                public boolean onQueryTextSubmit(final String query)
                {
                    // this is your adapter that will be filtered
                    //adapter.getFilter().filter(query);
                    Log.v(app.TAG,"SEARCH FOR = " + query);
                    getPagerSearch(query);
                    updateMainDisplay();
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.homeAsUp) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setupReservedPanel() {
        mNowPlayingTxtView = (TextView) findViewById(R.id.now_playing_text_view);
        mNowPlayingTxtView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/VPSLGAN.TTF"));
        mNowPlayingTxtView.setTextSize(15);

        mReservedCountImgView = (ImageView) findViewById(R.id.now_playing_image_view);
        mReservedCountImgView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mSlidingPanel.isPanelExpanded()) {
                    mSlidingPanel.collapsePanel();
                    actionBarHelper.resetTitle();
                    actionBarHelper.popSub();
                    //mReservedCountImgView.setImageDrawable(DrawableHelper.getInstance().buildDrawable(mNowPlayingTxtView.getText().charAt(0) + "", "roundrect"));
                } else {
                    mSlidingPanel.expandPanel();
                    if (webSocketHelper != null && !webSocketHelper.rsvpList.isEmpty()) {
                        actionBarHelper.setTitle(getResources().getString(R.string.rsvp_title));
                        actionBarHelper.pushSub(webSocketHelper.rsvpList.size() + " Songs.");
                    }
                }
            }
        });
//        Log.v(app.TAG,"Building the RSVP Panel");
//        rsvpPanelHelper.refreshRsvpList(app.generateTestRsvpList());
        if (me != null) {
            // Display the Avatar Photo
            Uri imgURI;
            Bitmap bm;
            String avatarURI = PreferencesHelper.getInstance(MainActivity.this).getPreference(
                    getResources().getString(R.string.myAvatarURI));
            Log.v(app.TAG, "Avatar from Pref. URI: " + avatarURI);
            if (avatarURI != null && !avatarURI.equals("")) {
                imgURI = Uri.parse(avatarURI);
                bm = uriToBitmap(imgURI);
            } else {
                bm = drawableHelper.drawableToBitmap(getResources().getDrawable(R.drawable.default_profile));
            }
            FaceCropper mFaceCropper = new FaceCropper();
            bm = mFaceCropper.getCroppedImage(bm);
            if (bm.getWidth() > 120 || bm.getHeight() > 120) {
                bm = Bitmap.createScaledBitmap(bm, 120, 120, false);
            }
            RoundImgDrawable img = new RoundImgDrawable(bm);
            mReservedCountImgView.setImageDrawable(img);
            nowPlayingHelper.setTitle("Welcome <b>" + me.name + "</b><br>Select and Reserve a song to sing!");
        }

    }

    public void updateNowPlaying(String title) {
        mNowPlayingTxtView.setText(Html.fromHtml(title));
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        Bitmap bitmap   = null;
        String path     = "";

        if (requestCode == AlertDialogHelper.FILE_PICK_FROM_FILE) {
            aquiredPhoto.mImageCaptureUri = data.getData();
        } else if (requestCode == AlertDialogHelper.FILE_PICK_FROM_FILE_KITKAT) {
            aquiredPhoto.mImageCaptureUri = data.getData();
            final int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(aquiredPhoto.mImageCaptureUri, takeFlags);
        }
        bitmap = uriToBitmap(aquiredPhoto.mImageCaptureUri);
        Log.v(app.TAG, "URL + " + aquiredPhoto.mImageCaptureUri.toString());
        Log.v(app.TAG,"Path = " + path);
        // Save to SharedPreference
        PreferencesHelper.getInstance(MainActivity.this).setStringPreference(
                aquiredPhoto.prefKey, aquiredPhoto.mImageCaptureUri.toString());
        if (bitmap != null) {
            FaceCropper mFaceCropper = new FaceCropper();
            bitmap = mFaceCropper.getCroppedImage(bitmap);
            if (bitmap.getHeight() > 120 && bitmap.getWidth() > 120) {
                bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false);
            }
            RoundImgDrawable img = new RoundImgDrawable(bitmap);
            //mReservedCountImgView.setImageDrawable(img);
            aquiredPhoto.imgView.setImageDrawable(img);
        } else {
            Toast.makeText(this, "Unable to find file. Path =  " + path, Toast.LENGTH_LONG).show();
        }
    }

    public Bitmap uriToBitmap(Uri imgUri) {
        Bitmap bitmap = null;
        try {
            if (imgUri != null) {
                if (imgUri.toString().startsWith("content://com.google.android.apps.photo.contents")) {
                    InputStream is = getContentResolver().openInputStream(Uri.parse(imgUri.toString()));
                    bitmap = BitmapFactory.decodeStream(is);
                } else {
                    ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(imgUri,"r");
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bitmap = BitmapFactory.decodeFileDescriptor(fd);
                    pfd.close();
                    //bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

//    @Override
//    public Object onRetainCustomNonConfigurationInstance() {
//        Log.v(app.TAG,"Saving configurations...");
//        HashMap<String, Object> savedValues = new HashMap<String, Object>();
//        savedValues.put("friendsList", friendsList);
//        savedValues.put("webSocketHelper", webSocketHelper);
//        savedValues.put("rsvpPanelHelper", rsvpPanelHelper);
//        return savedValues;
//    }

    public void getPagerTitles() {
//        pagerTitles.put("A","200");
//        pagerTitles.put("B","100");
//        pagerTitles.put("C","200");
        try {
            db.open();
            listingBy = "title";
            pagerTitles = db.getTitleKeysMap();
        } catch (Exception ex) {
            Log.e(app.TAG,ex.getMessage(),ex);
        } finally {
            db.close();
        }
    }
    public void getPagerSearch(String searchStr) {
        try {
            db.open();
            listingBy = "search";
            this.searchStr = searchStr;
            pagerTitles = db.getNewSearchKeysMap(searchStr);
        } catch (Exception e) {
            Log.e(app.TAG,e.getLocalizedMessage(),e);
        } finally {
            db.close();
        }
    }

    public void getPagerLanguage(String language) {
        try {
            db.open();
            listingBy = language;
            pagerTitles = db.getLanguageKeysMapNumber(language);
        } catch (Exception e) {
            Log.e(app.TAG, e.getLocalizedMessage(),e);
        } finally {
            db.close();
        }
    }
    public int getTotalSongs() {
        int count = 0;
        Collection<String> coll = pagerTitles.values();
        for (String s : coll) {
            count += Integer.parseInt(s);
        }
        return count;
    }

    public void updateMainDisplay() {
        mSongsListPagerAdapter = new SongsListPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSongsListPagerAdapter);
        mSongsListPagerAdapter.notifyDataSetChanged();
        actionBarHelper.setSubTitle(getTotalSongs()+" Songs.");
    }


    private class SongsListPageAdapter extends FragmentStatePagerAdapter {

        public SongsListPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            ArrayList<String> sortedKeys = new ArrayList<>(pagerTitles.size());
            sortedKeys.addAll(pagerTitles.keySet());
            Collections.sort(sortedKeys);
            String title = "";
            if (position < sortedKeys.size()) {
                String key = sortedKeys.get(position);
                title = key + " (" + pagerTitles.get(key) + " songs)";
            }
            return title;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new SongListFragment();
            Bundle args = new Bundle();
            args.putInt(SongListFragment.ARG_SECTION_NUMBER, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return pagerTitles.size();
        }
    }

}
