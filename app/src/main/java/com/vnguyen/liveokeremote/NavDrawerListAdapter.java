package com.vnguyen.liveokeremote;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.TreeSet;

public class NavDrawerListAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;

    private TreeSet<Integer> sectionHeader;

    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;

    public NavDrawerListAdapter(Context context) {
        this.context = context;
        navDrawerItems = new ArrayList<NavDrawerItem>();
        sectionHeader = new TreeSet<Integer>();
    }

    public void addItem(NavDrawerItem item) {
        navDrawerItems.add(item);
        if (item.icon == null) {
            sectionHeader.add(navDrawerItems.size()-1);
        }
        notifyDataSetChanged();
    }

    public void addHeader(NavDrawerItem item) {
        navDrawerItems.add(item);
        sectionHeader.add(navDrawerItems.size() - 1);
        notifyDataSetChanged();
    }


//    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems) {
//        this.context = context;
//        this.navDrawerItems = navDrawerItems;
//    }


    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public NavDrawerItem getItem(int position) {
        return navDrawerItems.get(position);
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NavViewHolder holder = null;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            holder  = new NavViewHolder();
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.nav_menu_item, null);
                    holder.imgIcon = (ImageView) convertView.findViewById(R.id.nav_icon);
                    holder.txtTitle = (TextView) convertView.findViewById(R.id.nav_title);
                    holder.txtCount = (TextView) convertView.findViewById(R.id.nav_counter);
                    break;
                case TYPE_HEADER:
                    convertView = mInflater.inflate(R.layout.nav_menu_header, null);
                    holder.txtTitle = (TextView) convertView.findViewById(R.id.nav_header);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (NavViewHolder) convertView.getTag();
        }

        if (holder.imgIcon != null) {
            int currentVersion = Build.VERSION.SDK_INT;
            if (currentVersion >= 16) {
                holder.imgIcon.setBackground(navDrawerItems.get(position).icon);
            } else {
                holder.imgIcon.setBackgroundDrawable(navDrawerItems.get(position).icon);
            }
        }
        holder.txtTitle.setText(navDrawerItems.get(position).title);

        // displaying count
        // check whether it set visible or not
        if (holder.txtCount != null) {
            if (navDrawerItems.get(position).counterVisible) {
                holder.txtCount.setText(navDrawerItems.get(position).count);
            } else {
                // hide the counter view
                holder.txtCount.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    public static class NavViewHolder {
        public ImageView imgIcon;
        public TextView txtTitle;
        public TextView txtCount;
    }
}
