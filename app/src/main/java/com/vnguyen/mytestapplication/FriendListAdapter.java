package com.vnguyen.mytestapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.ArrayList;

public class FriendListAdapter extends BaseSwipeAdapter {
    private MainActivity context;
    private ArrayList<User> friends;

    public FriendListAdapter(Context context, ArrayList<User> list) {
        this.context = (MainActivity) context;
        friends = new ArrayList<>();
        friends.addAll(list);
    }

    public void clear() {
        friends.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.friends_swipe_layout;
    }

    @Override
    public View generateView(int position, ViewGroup viewGroup) {
        View v = LayoutInflater.from(context).inflate(R.layout.friends_list_item, null);
        SwipeLayout swipeLayout = (SwipeLayout) v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);
        //setupActionButtonsBelow(swipeLayout);
        return v;
    }

    @Override
    public void fillValues(int i, View view) {
        User friend = friends.get(i);
        ImageView friendIcon = (ImageView) view.findViewById(R.id.friends_icon);
        if (friend.getPhotoURL() == null || friend.getPhotoURL().equals("")) {
            Bitmap genericFrBM = BitmapFactory.decodeResource(context.getResources(), R.drawable.generic_friend);
            //BitmapDrawable iconDrawable = new BitmapDrawable(context.getResources(), genericFrBM);
            friendIcon.setImageBitmap(genericFrBM);
        }

        TextView fName = (TextView) view.findViewById(R.id.friends_name);
        fName.setText(friend.getName());
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Object getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
