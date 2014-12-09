package com.vnguyen.mytestapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thedazzler.droidicon.IconicFontDrawable;

import java.util.ArrayList;

public class NavigationDrawerHelper {

    private final MainActivity context;

    public NavigationDrawerHelper(Context context) {
        this.context = (MainActivity)context;
    }

    public void setupSlidingNav(Toolbar toolbar) {

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
                super.onDrawerClosed(view);
                //invalidateOptionsMenu();
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

        // Setup Nav menu
        String[] navMenuTitles = context.getResources().getStringArray(R.array.nav_menu_items);
        final ListView mDrawerList = (ListView) context.findViewById(R.id.list_slidermenu);

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
                    showCounter = false;
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
                    navCounter = "2000";
                    break;
                case 3:
                    // English Songs
                    Bitmap usIcon = BitmapFactory.decodeResource(context.getResources(),R.drawable.us);
                    iconDrawable = new BitmapDrawable(context.getResources(), usIcon);
                    showCounter = true;
                    navCounter = "1000";
                    break;
                case 4:
                    // Chinese Songs
                    Bitmap cnIcon = BitmapFactory.decodeResource(context.getResources(),R.drawable.cn);
                    iconDrawable = new BitmapDrawable(context.getResources(), cnIcon);
                    showCounter = true;
                    navCounter = "1000";
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
                    // send comments to screen
                    IconicFontDrawable send2ScreenIcon = new IconicFontDrawable(context.getApplicationContext());
                    send2ScreenIcon.setIcon("gmd-insert-comment");
                    send2ScreenIcon.setIconColor(context.getResources().getColor(R.color.primary));
                    iconDrawable = send2ScreenIcon;
                    showCounter = false;
                    break;
                case 9:
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

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                Log.v(context.TAG, "Position = " + position);
                final NavDrawerListAdapter adapter = (NavDrawerListAdapter) mDrawerList.getAdapter();
                NavDrawerListAdapter.NavViewHolder holder = (NavDrawerListAdapter.NavViewHolder) view.getTag();
                switch (position) {
                    case 6:
                        // IP Address
                        (new AlertDialogHelper(context)).
                                popupIPAddressDialog("Karaoke4Pro IP Address", "Enter IP Address",
                                        adapter.getItem(position),adapter);
                        break;
                    case 7:
                        // Friends List
                        context.mDrawerLayout.closeDrawers();
                        context.viewFlipper.showNext();
                        break;
                }
            }
        });

        NavDrawerListAdapter navAdapter = new NavDrawerListAdapter(context.getApplicationContext());
        for (int i = 0; i < navDrawerItems.size();i++) {
            navAdapter.addItem(navDrawerItems.get(i));
        }
        mDrawerList.setAdapter(navAdapter);

    }
}
