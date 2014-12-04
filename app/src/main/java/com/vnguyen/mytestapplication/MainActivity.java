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
                } else {
                    slidingPanel.expandPanel();
                }
            }
        });
    }

    public void displayNowPlaying(String title) {
        TextView tv = (TextView) findViewById(R.id.now_playing_text_view);
        tv.setText(Html.fromHtml("Now Playing: <br><b>"+title+"</b>"));
    }

}
