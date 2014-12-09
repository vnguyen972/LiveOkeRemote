package com.vnguyen.mytestapplication;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.thedazzler.droidicon.IconicFontDrawable;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    public String TAG = "-MainActivity-";
    public SlidingUpPanelLayout mSlidingPanel;
    public ImageView mReservedCountImgView;
    public TextView mNowPlayingTxtView;
    public OrientationEventListener myOrientationEventListener;
    public MenuItem onOffSwitch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setup toolbar as actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tool-Bar");
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM);



        // setup sliding Navigation panel (hidden from left)
        setupSlidingNav(toolbar);

        // setup floating action button(s)
        setupActionButtons();

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
                Log.v(TAG, fam.getTag()+"");
            }
        };

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
        Log.v(TAG, searchView + "");
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

    public void setupActionButtons() {
        final IconDrawable playBtnIcon = new IconDrawable(getApplicationContext(), Iconify.IconValue.md_play_arrow);
        playBtnIcon.sizeDp(40);
        playBtnIcon.colorRes(R.color.orange_800);
        final IconDrawable pauseBtnIcon = new IconDrawable(getApplicationContext(), Iconify.IconValue.md_pause);
        pauseBtnIcon.sizeDp(40);
        pauseBtnIcon.colorRes(R.color.orange_800);
        IconDrawable prevBtnIcon = new IconDrawable(getApplicationContext(), Iconify.IconValue.md_skip_previous);
        prevBtnIcon.sizeDp(40);
        prevBtnIcon.colorRes(R.color.orange_800);
        IconDrawable nextBtnIcon = new IconDrawable(getApplicationContext(), Iconify.IconValue.md_skip_next);
        nextBtnIcon.sizeDp(40);
        nextBtnIcon.colorRes(R.color.orange_800);
        final IconDrawable micBtnIcon = new IconDrawable(getApplicationContext(), Iconify.IconValue.md_mic);
        micBtnIcon.sizeDp(40);
        micBtnIcon.colorRes(R.color.orange_800);
        final IconDrawable micOffBtnIcon = new IconDrawable(getApplicationContext(), Iconify.IconValue.md_mic_off);
        micOffBtnIcon.sizeDp(40);
        micOffBtnIcon.colorRes(R.color.orange_800);

        final FloatingActionButton playButton = (FloatingActionButton) findViewById(R.id.playBtn);
        playButton.setImageDrawable(playBtnIcon);
        playButton.setTag("PLAY");
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playButton.getTag().equals("PLAY")) {
                    playButton.setImageDrawable(pauseBtnIcon);
                    playButton.setTag("PAUSE");
                    updateNowPlaying("Now Playing:<br><b>Xin Loi Tinh Yeu</b>");
                } else if (playButton.getTag().equals("PAUSE")) {
                    playButton.setImageDrawable(playBtnIcon);
                    playButton.setTag("PLAY");
                    updateNowPlaying("<b>PAUSE<br>Press PLAY to resume</b>");
                }
            }
        });

        FloatingActionButton prevButton = (FloatingActionButton) findViewById(R.id.skipPreviousBtn);
        prevButton.setImageDrawable(prevBtnIcon);
        FloatingActionButton nextButton = (FloatingActionButton) findViewById(R.id.skipNextBtn);
        nextButton.setImageDrawable(nextBtnIcon);
        final FloatingActionButton switchAudioTrkButton = (FloatingActionButton) findViewById(R.id.switchAudioTrackBtn);
        switchAudioTrkButton.setImageDrawable(micBtnIcon);
        switchAudioTrkButton.setTag("MIC-ON");
        switchAudioTrkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchAudioTrkButton.getTag().equals("MIC-ON")) {
                    switchAudioTrkButton.setImageDrawable(micOffBtnIcon);
                    switchAudioTrkButton.setTag("MIC-OFF");
                } else if (switchAudioTrkButton.getTag().equals("MIC-OFF")) {
                    switchAudioTrkButton.setImageDrawable(micBtnIcon);
                    switchAudioTrkButton.setTag("MIC-ON");
                }
            }
        });
    }

    public void setupSlidingNav(Toolbar toolbar) {
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close){
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
                super.onDrawerClosed(view);
                //invalidateOptionsMenu();
            }

            @Override
            public void onDrawerStateChanged(int i) {
                super.onDrawerStateChanged(i);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

        // Setup Nav menu


        String[] navMenuTitles = getResources().getStringArray(R.array.nav_menu_items);
        ListView mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<NavDrawerItem>();

        Drawable iconDrawable = null;
        boolean showCounter = false;
        String navCounter = "0";
        for (int i = 0; i < navMenuTitles.length;i++) {
            switch (i) {
                case 0:
                    // Home
//                    iconDrawable = new IconDrawable(getApplicationContext(), Iconify.IconValue.md_home).
//                            colorRes(R.color.primary).sizeDp(100);
                    IconicFontDrawable iconicFontDrawable = new IconicFontDrawable(getApplicationContext());
                    iconicFontDrawable.setIcon("gmd-home");
                    iconicFontDrawable.setIconColor(getResources().getColor(R.color.primary));
                    iconDrawable = iconicFontDrawable;
                    showCounter = false;
                    break;
                case 1:
                    // header
                    iconDrawable = null;
                    showCounter = false;
                    break;
                case 2:
                    Bitmap vnIcon = BitmapFactory.decodeResource(getResources(),R.drawable.vn);
                    iconDrawable = new BitmapDrawable(getResources(), vnIcon);
                    showCounter = true;
                    navCounter = "2000";
                    break;
                case 3:
                    Bitmap usIcon = BitmapFactory.decodeResource(getResources(),R.drawable.us);
                    iconDrawable = new BitmapDrawable(getResources(), usIcon);
                    showCounter = true;
                    navCounter = "1000";
                    break;
                case 4:
                    Bitmap cnIcon = BitmapFactory.decodeResource(getResources(),R.drawable.cn);
                    iconDrawable = new BitmapDrawable(getResources(), cnIcon);
                    showCounter = true;
                    navCounter = "1000";
                    break;
                case 5:
                    //header
                    iconDrawable = null;
                    showCounter = false;
                    break;
                case 6:
                    iconDrawable = new com.joanzapata.android.iconify.IconDrawable(getApplicationContext(),
                            com.joanzapata.android.iconify.Iconify.IconValue.fa_plug).colorRes(R.color.primary);
                    showCounter = false;
                    break;
                case 7:
                    IconicFontDrawable peopleIcon = new IconicFontDrawable(getApplicationContext());
                    peopleIcon.setIcon("gmd-people");
                    peopleIcon.setIconColor(getResources().getColor(R.color.primary));
                    iconDrawable = peopleIcon;
                    showCounter = false;
                    break;
                case 8:
                    IconicFontDrawable send2ScreenIcon = new IconicFontDrawable(getApplicationContext());
                    send2ScreenIcon.setIcon("gmd-open-in-browser");
                    send2ScreenIcon.setIconColor(getResources().getColor(R.color.primary));
                    iconDrawable = send2ScreenIcon;
                    showCounter = false;
                    break;
                default:
                    continue;
            }
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], iconDrawable, showCounter, navCounter));
        }

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                Log.v(TAG,"Position = " + position);
                //NavDrawerListAdapter.NavViewHolder holder = (NavDrawerListAdapter.NavViewHolder) view.getTag();
                switch (position) {
                    case 6:
                        // IP Address
                        (new AlertDialogHelper()).popupDialog(MainActivity.this,"Karaoke4Pro IP Address","Enter IP Address");
                        break;
                }
            }
        });

        NavDrawerListAdapter navAdapter = new NavDrawerListAdapter(getApplicationContext());
        for (int i = 0; i < navDrawerItems.size();i++) {
            navAdapter.addItem(navDrawerItems.get(i));
        }
        mDrawerList.setAdapter(navAdapter);

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
                    updateRsvpCount(mNowPlayingTxtView.getText().charAt(0) + "");
                } else {
                    mSlidingPanel.expandPanel();
                    getSupportActionBar().setTitle(R.string.rsvp_title);
                }
            }
        });
        updateRsvpCount("W");
        updateNowPlaying("Welcome<br>Reserve a song and start singing");
    }

    public void updateRsvpCount(String value) {
        ColorGenerator generator = ColorGenerator.DEFAULT;
        int color = generator.getColor(value);
        TextDrawable drawable = TextDrawable.builder().
                beginConfig()
                .withBorder(4) /* thickness in px */
                .endConfig()
                .buildRoundRect(value, color, 5);
        mReservedCountImgView.setImageDrawable(drawable);
    }

    public void updateNowPlaying(String title) {
        mNowPlayingTxtView.setText(Html.fromHtml(title));
    }

}
