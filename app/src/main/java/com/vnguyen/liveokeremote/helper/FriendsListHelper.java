package com.vnguyen.liveokeremote.helper;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.daimajia.swipe.SwipeLayout;
import com.vnguyen.liveokeremote.FriendListAdapter;
import com.vnguyen.liveokeremote.LiveOkeRemoteApplication;
import com.vnguyen.liveokeremote.MainActivity;
import com.vnguyen.liveokeremote.R;
import com.vnguyen.liveokeremote.data.User;


import java.util.ArrayList;
import java.util.Iterator;

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
        } else {
            adapter.friends.addAll(friends);
        }
        adapter.notifyDataSetChanged();
    }

    public void displayFriendsListPanel() {
        if (context.friendsList != null) {
            Log.v(LiveOkeRemoteApplication.TAG, "friends.list.here = " + context.friendsList.size());
            context.actionBarHelper.pushSub(context.friendsList.size() + " Friends.");
            context.friendsListHelper.initFriendList(context.friendsList);
        } else {
            context.actionBarHelper.pushSub("0 Friends.");
        }
        if (context.viewFlipper.getDisplayedChild() == 0) {
            context.viewFlipper.showNext();
        }
        context.actionBarHelper.setTitle(context.getResources().getString(R.string.friends_title));
    }

    public User findFriend(String frName) {
        User u = null;
        for (Iterator<User> it = context.friendsList.iterator(); it.hasNext();) {
            u = it.next();
            if (u.name.equalsIgnoreCase(frName.toString())) {
                break;
            }
        }
        return u;
    }

}
