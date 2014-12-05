package com.vnguyen.mytestapplication;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;


public class MainActivity extends ActionBarActivity {

    public SlidingUpPanelLayout slidingPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        setupSlidingNav(toolbar);

        setupActionButtons();

        slidingPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingPanel.setEnabled(false);


        displayNowPlaying("Xin Loi Tinh Yeu");
        updateRsvpCount(7);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
                } else if (playButton.getTag().equals("PAUSE")) {
                    playButton.setImageDrawable(playBtnIcon);
                    playButton.setTag("PLAY");
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
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle("");
                invalidateOptionsMenu();
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
    public void updateRsvpCount(int count) {
        ColorGenerator generator = ColorGenerator.DEFAULT;
        // generate color based on a key (same key returns the same color), useful for list/grid views
        int color = generator.getColor(count+"");
        TextDrawable drawable = TextDrawable.builder().
                beginConfig()
                .withBorder(4) /* thickness in px */
                .endConfig()
                .buildRoundRect(count+"", color, 5);
        ImageView imageView = (ImageView) findViewById(R.id.now_playing_image_view);
        imageView.setImageDrawable(drawable);
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (slidingPanel.isPanelExpanded()) {
                    slidingPanel.collapsePanel();
                    getSupportActionBar().setTitle("");
                } else {
                    slidingPanel.expandPanel();
                    getSupportActionBar().setTitle("Reserved List");
                }
            }
        });
    }

    public void displayNowPlaying(String title) {
        TextView tv = (TextView) findViewById(R.id.now_playing_text_view);
        tv.setText(Html.fromHtml("Now Playing: <br><b>"+title+"</b>"));
    }

}
