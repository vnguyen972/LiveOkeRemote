package com.vnguyen.liveokeremote.helper;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.ListView;

import com.vnguyen.liveokeremote.FriendListAdapter;
import com.vnguyen.liveokeremote.MainActivity;
import com.vnguyen.liveokeremote.R;
import com.vnguyen.liveokeremote.RsvpListAdapter;
import com.vnguyen.liveokeremote.data.ReservedListItem;

import java.util.ArrayList;

public class RsvpPanelHelper {
    private final MainActivity context;
    private RsvpListAdapter rsvpAdapter;
    private FriendListAdapter friendsAdapter;

    public RsvpPanelHelper(Context context) {
        this.context = (MainActivity) context;
    }


    public void updateAdapter(ArrayList<ReservedListItem> items) {
        if (rsvpAdapter != null) {
            rsvpAdapter.reloadData(items);
        }
    }
    public void refreshRsvpList(ArrayList<ReservedListItem> items) {
        if (rsvpAdapter == null) {
            LogHelper.v("RSVP New Adapter");
            rsvpAdapter = new RsvpListAdapter(context, items);
        } else {
            LogHelper.v("RSVP Reload Data");
            rsvpAdapter.reloadData(items);
        }
        ListView rsvpListView = (ListView) context.findViewById(R.id.rsvp_drawer);
        LogHelper.v("ListView found? " + rsvpListView);
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
    }
}
