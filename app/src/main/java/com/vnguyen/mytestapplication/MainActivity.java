package com.vnguyen.mytestapplication;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.callback.ImageOptions;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    public AQuery aq;
    public LiveOkeRemoteApplication app;

    public User me;
    public String myName;

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

        // setup toolbar as actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tool-Bar");
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM);

        // setup sliding Navigation panel (hidden from left)
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        NavigationDrawerHelper.getInstance(MainActivity.this).setupSlidingNav(toolbar);

        // setup floating action button(s)
        FloatingButtonsHelper.getInstance(MainActivity.this).setupActionButtons();

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

        RsvpPanelHelper rph = RsvpPanelHelper.getInstance(MainActivity.this);
        rph.refreshRsvpList(app.generateTestRsvpList());
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
                    getSupportActionBar().setTitle("");
                    //mReservedCountImgView.setImageDrawable(DrawableHelper.getInstance().buildDrawable(mNowPlayingTxtView.getText().charAt(0) + "", "roundrect"));
                } else {
                    mSlidingPanel.expandPanel();
                    getSupportActionBar().setTitle(R.string.rsvp_title);
                }
            }
        });


        if (me.getPhotoURL() != null && !me.getPhotoURL().equals("")) {
            aq.id(R.id.now_playing_image_view).image(me.getPhotoURL(), true, false, 0, 0, new BitmapAjaxCallback() {
                public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                    RoundImgDrawable img = new RoundImgDrawable(bm);
                    iv.setImageDrawable(img);
                }
            });
        } else {
            Bitmap bm = DrawableHelper.getInstance().drawableToBitmap(getResources().getDrawable(R.drawable.default_profile));
            RoundImgDrawable img = new RoundImgDrawable(bm);
            mReservedCountImgView.setImageDrawable(img);
        }
        if (myName != null && !myName.equals("")) {
            updateNowPlaying("Welcome " + myName + "<br>Reserve a song and start singing");
        }
    }

    public void updateNowPlaying(String title) {
        mNowPlayingTxtView.setText(Html.fromHtml(title));
    }

}
