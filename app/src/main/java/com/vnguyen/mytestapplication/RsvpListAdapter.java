package com.vnguyen.mytestapplication;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.ArrayList;

public class RsvpListAdapter extends BaseSwipeAdapter {

    private Context mContext;
    private ArrayList<ReservedListItem> rItems;

    public RsvpListAdapter(Context context, ArrayList<ReservedListItem> itemList) {
        this.mContext = context;
        rItems = new ArrayList<>();
        rItems.addAll(itemList);
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.swipe_layout;
    }

    @Override
    public View generateView(int position, ViewGroup viewGroup) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.rsvp_list_item, null);
        SwipeLayout swipeLayout = (SwipeLayout) v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
        return v;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void fillValues(int i, View view) {
        ReservedListItem item = rItems.get(i);
        ImageView iconImg = (ImageView) view.findViewById(R.id.rsvp_icon);
        if (item.getIcon() == null) {
            iconImg.setImageDrawable(DrawableHelper.getInstance().buildDrawable(item.getTitle().substring(0, 1), "round"));
        } else {
            iconImg.setImageDrawable(item.getIcon());
        }
        TextView txtView = (TextView) view.findViewById(R.id.rsvp_title);
        txtView.setText(item.getTitle() + " - " + item.getRequester());
    }

    @Override
    public int getCount() {
        return 50;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clearData() {
        rItems.clear();
    }
}
