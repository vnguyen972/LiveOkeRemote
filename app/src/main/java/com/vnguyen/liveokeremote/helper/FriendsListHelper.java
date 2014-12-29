package com.vnguyen.liveokeremote.helper;

import android.content.Context;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.daimajia.swipe.SwipeLayout;
import com.vnguyen.liveokeremote.FriendListAdapter;
import com.vnguyen.liveokeremote.MainActivity;
import com.vnguyen.liveokeremote.R;
import com.vnguyen.liveokeremote.data.User;


import java.util.ArrayList;

public class FriendsListHelper {
    private MainActivity context;
    public FriendListAdapter adapter;

    public FriendsListHelper(Context context) {
        this.context = (MainActivity) context;
    }

    public void initFriendList(ArrayList<User> friends) {
        if (adapter == null) {
            adapter = new FriendListAdapter(context, friends);
            ListView friendsListView = (ListView) context.findViewById(R.id.friends_list);
            friendsListView.setAdapter(adapter);
            friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SwipeLayout swipeLayout = (SwipeLayout) view;
                    final User friend = adapter.friends.get(position);
                    final ImageView friendIcon = (ImageView) view.findViewById(R.id.friends_icon);

                    if (swipeLayout.getOpenStatus() == SwipeLayout.Status.Close) {
                        (new AlertDialogHelper(context)).popupFileChooser(friendIcon, friend.name.trim()+"_avatar");
                    }
                }
            });
            friendsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (adapter.getOpenItems().size() > 0) {
                        adapter.closeAllExcept(null);
                    }

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });
            adapter.notifyDataSetChanged();
        }
    }

}
