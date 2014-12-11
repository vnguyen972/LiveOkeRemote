package com.vnguyen.mytestapplication;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.callback.ImageOptions;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.thedazzler.droidicon.IconicFontDrawable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    public AQuery aq;
    public LiveOkeRemoteApplication app;

    public User me;
    public String myName;

    public Uri mImageCaptureUri;

    // Helpers
    public NavigationDrawerHelper navigationDrawerHelper;
    public FloatingButtonsHelper floatingButtonsHelper;
    public RsvpPanelHelper rsvpPanelHelper;
    public FriendsListHelper friendsListHelper;
    public ActionBarHelper actionBarHelper;

    public Animation slide_in_left, slide_out_right;
    public ViewFlipper viewFlipper;

    // Variables to hold values from POPUP dialogs in SETTINGS
    public String ipAddress;
    public String comment2Send2Screen;

    // GUI Components
    public DrawerLayout mDrawerLayout;
    public SlidingUpPanelLayout mSlidingPanel;
    public ImageView mReservedCountImgView;
    public TextView mNowPlayingTxtView;
    public MenuItem onOffSwitch;

    public OrientationEventListener myOrientationEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aq = new AQuery(getApplicationContext());
        app = (LiveOkeRemoteApplication) getApplication();

        // Load Shared Preference
        ipAddress = PreferencesHelper.getInstance(MainActivity.this).getPreference(
                getResources().getString(R.string.ip_adress));
        myName = PreferencesHelper.getInstance(MainActivity.this).getPreference(
                getResources().getString(R.string.myName));

        if (myName == null || myName.equals("")) {
            new AlertDialogHelper(MainActivity.this).popupHello();
        } else {
            me = new User(myName);
        }

        viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        slide_in_left = AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left);
        slide_out_right = AnimationUtils.loadAnimation(this,
                android.R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in_left);
        viewFlipper.setOutAnimation(slide_out_right);

        setupFriendsListPanel();

        // setup toolbar as actionbar
        if (actionBarHelper == null) {
            actionBarHelper = new ActionBarHelper(MainActivity.this);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_TITLE);
        actionBarHelper.setTitle("LiveOke Remote");

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

        final FloatingActionsMenu fam = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        fam.setTag("VERTICAL_DIRECTION");

        // FUTURE FEATURE
        // See if we could detect orientation change to expand the floating button accordingly
        myOrientationEventListener = new OrientationEventListener(getApplicationContext()) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (fam.getTag().equals("VERTICAL_DIRECTION")) {
                    fam.setTag("HORIZONTAL_DIRECTION");
                } else if (fam.getTag().equals("HORIZONTAL_DIRECTION")) {
                    fam.setTag("VERTICAL_DIRECTION");
                }
                Log.v(((LiveOkeRemoteApplication)getApplication()).TAG, fam.getTag()+"");
            }
        };

//        SwipeLayout swipeLayout = (SwipeLayout) findViewById(R.id.swipe_layout);
//        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
//        swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);

        if (rsvpPanelHelper == null) {
            rsvpPanelHelper = new RsvpPanelHelper(MainActivity.this);
        }
        rsvpPanelHelper.refreshRsvpList(app.generateTestRsvpList());
        if (friendsListHelper == null) {
            friendsListHelper = new FriendsListHelper(MainActivity.this);
        }
        friendsListHelper.initFriendList(app.generateTestFriends());
    }

    public void setupFriendsListPanel() {
        ListView friendList = (ListView) findViewById(R.id.friends_list);
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

        IconDrawable searchIcon = new IconDrawable(getApplicationContext(), Iconify.IconValue.md_search);
        searchIcon.sizeDp(30);
        searchIcon.colorRes(R.color.white);
        MenuItem search = menu.findItem(R.id.menu_search);
        search.setIcon(searchIcon);
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

                public boolean onQueryTextSubmit(String query)
                {
                    // this is your adapter that will be filtered
                    //adapter.getFilter().filter(query);
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
        if (id == R.id.on_off_switch) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setupReservedPanel() {
        mNowPlayingTxtView = (TextView) findViewById(R.id.now_playing_text_view);

        mReservedCountImgView = (ImageView) findViewById(R.id.now_playing_image_view);
        mReservedCountImgView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mSlidingPanel.isPanelExpanded()) {
                    mSlidingPanel.collapsePanel();
                    actionBarHelper.resetTitle();
                    //mReservedCountImgView.setImageDrawable(DrawableHelper.getInstance().buildDrawable(mNowPlayingTxtView.getText().charAt(0) + "", "roundrect"));
                } else {
                    mSlidingPanel.expandPanel();
                    actionBarHelper.setTitle(getResources().getString(R.string.rsvp_title));
                }
            }
        });


        if (me != null) {
            // Display the Avatar Photo
            if (me.getPhotoURL() != null && !me.getPhotoURL().equals("")) {
                aq.id(R.id.now_playing_image_view).image(me.getPhotoURL(), true, false, 0, 0, new BitmapAjaxCallback() {
                    public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                        RoundImgDrawable img = new RoundImgDrawable(bm);
                        iv.setImageDrawable(img);
                    }
                });
            } else {
                Uri imgURI;
                Bitmap bm;
                String avatarURI = PreferencesHelper.getInstance(MainActivity.this).getPreference(
                        getResources().getString(R.string.myAvatarURI));
                Log.v(app.TAG, "Avatar from Pref. URI: " + avatarURI);
                if (avatarURI != null && !avatarURI.equals("")) {
                    imgURI = Uri.parse(avatarURI);
                    bm = uriToBitmap(imgURI);
                } else {
                    bm = DrawableHelper.getInstance().drawableToBitmap(getResources().getDrawable(R.drawable.default_profile));
                }
                if (bm.getWidth() > 120 || bm.getHeight() > 120) {
                    bm = Bitmap.createScaledBitmap(bm, 120, 120, false);
                }
                RoundImgDrawable img = new RoundImgDrawable(bm);
                mReservedCountImgView.setImageDrawable(img);
            }
            updateNowPlaying("Welcome " + me.getName() + "<br>Reserve a song and start singing");
//        } else {
//            Bitmap bm = DrawableHelper.getInstance().drawableToBitmap(getResources().getDrawable(R.drawable.default_profile));
//            RoundImgDrawable img = new RoundImgDrawable(bm);
//            mReservedCountImgView.setImageDrawable(img);
        }

    }

    public void updateNowPlaying(String title) {
        mNowPlayingTxtView.setText(Html.fromHtml(title));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        Bitmap bitmap   = null;
        String path     = "";

        if (requestCode == AlertDialogHelper.FILE_PICK_FROM_FILE) {
            mImageCaptureUri = data.getData();
        }
        bitmap = uriToBitmap(mImageCaptureUri);
        Log.v(app.TAG, "URL + " + mImageCaptureUri.toString());
        Log.v(app.TAG,"Path = " + path);
        // Save to SharedPreference
        PreferencesHelper.getInstance(MainActivity.this).setStringPreference(
                getResources().getString(R.string.myAvatarURI), mImageCaptureUri.toString());
        if (bitmap != null) {
            Bitmap bm = Bitmap.createScaledBitmap(bitmap, 120, 120, false);
            RoundImgDrawable img = new RoundImgDrawable(bm);
            mReservedCountImgView.setImageDrawable(img);
        } else {
            Toast.makeText(this, "Unable to find file. Path =  " + path, Toast.LENGTH_LONG).show();
        }
    }

    private Bitmap uriToBitmap(Uri imgUri) {
        Bitmap bitmap = null;
        try {
            if (imgUri != null) {
                if (imgUri.toString().startsWith("content://com.google.android.apps.photo.contents")) {
                    InputStream is = getContentResolver().openInputStream(Uri.parse(imgUri.toString()));
                    bitmap = BitmapFactory.decodeStream(is);
                } else {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
