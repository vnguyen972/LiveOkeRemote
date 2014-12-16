package com.vnguyen.liveokeremote;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;

public class RsvpPanelHelper {
    private final MainActivity context;
    private RsvpListAdapter rsvpAdapter;
    private FriendListAdapter friendsAdapter;

    public RsvpPanelHelper(Context context) {
        this.context = (MainActivity) context;
    }


    public void refreshFriendsList(ArrayList<User> friends) {
        if (friendsAdapter != null) {
            friendsAdapter = null;
        }
        friendsAdapter = new FriendListAdapter (context, friends);
        ListView rsvpListView = (ListView) context.findViewById(R.id.rsvp_drawer);
        rsvpListView.setAdapter(friendsAdapter);
        rsvpListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (friendsAdapter.getOpenItems().size() > 0) {
                    friendsAdapter.closeAllExcept(null);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        friendsAdapter.notifyDataSetChanged();
    }

    public void refreshRsvpList(ArrayList<ReservedListItem> items) {
        if (rsvpAdapter != null) {
            rsvpAdapter = null;
        }
        rsvpAdapter = new RsvpListAdapter(context, items);
        ListView rsvpListView = (ListView) context.findViewById(R.id.rsvp_drawer);
        rsvpListView.setAdapter(rsvpAdapter);
        rsvpListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (rsvpAdapter.getOpenItems().size() > 0) {
                    rsvpAdapter.closeAllExcept(null);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        rsvpAdapter.notifyDataSetChanged();
    }

}
