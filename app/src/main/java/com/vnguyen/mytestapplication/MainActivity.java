package com.vnguyen.mytestapplication;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;


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
        //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM);


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
                getSupportActionBar().setTitle("Opened");
                //invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle("");
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
                    updateRsvpCount("V");
                }
            }
        });
        updateRsvpCount("W");
        updateNowPlaying("<b>Welcome<br>Reserve a song and start singing</b>");
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
