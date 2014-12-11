package com.vnguyen.mytestapplication;

import android.content.Context;
import android.widget.ListView;

import java.util.ArrayList;

public class FriendsListHelper {
    private MainActivity context;
    private FriendListAdapter adapter;

    public FriendsListHelper(Context context) {
        this.context = (MainActivity) context;
    }

    public void initFriendList(ArrayList<User> friends) {
        if (adapter != null) {
            adapter = null;
        }
        adapter = new FriendListAdapter(context, friends);
        ListView friendsListView = (ListView) context.findViewById(R.id.friends_list);
        friendsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

}
