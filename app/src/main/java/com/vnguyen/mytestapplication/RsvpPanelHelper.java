package com.vnguyen.mytestapplication;


import android.content.Context;
import android.widget.ListView;

import java.util.ArrayList;

public class RsvpPanelHelper {
    private final MainActivity context;
    private RsvpListAdapter rsvpAdapter;

    public RsvpPanelHelper(Context context) {
        this.context = (MainActivity) context;
    }


    public void refreshRsvpList(ArrayList<ReservedListItem> items) {
        if (rsvpAdapter != null) {
            rsvpAdapter = null;
        }
        rsvpAdapter = new RsvpListAdapter(context, items);
        ListView rsvpListView = (ListView) context.findViewById(R.id.rsvp_drawer);
        rsvpListView.setAdapter(rsvpAdapter);
        rsvpAdapter.notifyDataSetChanged();
    }
}
