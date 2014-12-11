package com.vnguyen.mytestapplication;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.thedazzler.droidicon.IconicFontDrawable;

import java.util.ArrayList;

public class RsvpListAdapter extends BaseSwipeAdapter {

    private MainActivity mContext;
    private ArrayList<ReservedListItem> rItems;

    public RsvpListAdapter(Context context, ArrayList<ReservedListItem> itemList) {
        this.mContext = (MainActivity) context;
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
        setupActionButtonsBelow(swipeLayout);
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
        TextView rsvpNbr = (TextView) view.findViewById(R.id.rsvp_number);
        rsvpNbr.setText(item.getNumber());
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

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setupActionButtonsBelow(SwipeLayout swipeLayout) {
        ViewGroup vBottom = swipeLayout.getBottomView();
        ViewGroup vTop = swipeLayout.getSurfaceView();
        final TextView rsvpNumber = (TextView) vTop.findViewById(R.id.rsvp_number);

        // Setup below layer for "actions"
        ImageView moveTopImg = (ImageView) vBottom.findViewById(R.id.ic_move_to_top_id);
        ImageView moveUpImg = (ImageView) vBottom.findViewById(R.id.ic_move_up_id);
        ImageView moveDownImg = (ImageView) vBottom.findViewById(R.id.ic_move_down_id);
        ImageView moveBottomImg = (ImageView) vBottom.findViewById(R.id.ic_move_bottom_id);
        ImageView deleteImg = (ImageView) vBottom.findViewById(R.id.ic_delete_id);

        mContext.iconImageHelper.setIconAsBackground("fa-angle-double-up",R.color.white,moveTopImg);
        mContext.iconImageHelper.setIconAsBackground("fa-angle-up",R.color.white,moveUpImg);
        mContext.iconImageHelper.setIconAsBackground("fa-angle-down", R.color.white, moveDownImg);
        mContext.iconImageHelper.setIconAsBackground("fa-angle-double-down", R.color.white, moveBottomImg);
        mContext.iconImageHelper.setIconAsBackground("fa-trash", R.color.white, deleteImg);

        moveTopImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "MoveToTOP itemNo = " + rsvpNumber.getText(), Toast.LENGTH_LONG).show();
            }
        });
        moveUpImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "MoveUP itemNo = " + rsvpNumber.getText(), Toast.LENGTH_LONG).show();
            }
        });
        moveDownImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "MoveDOWN itemNo = " + rsvpNumber.getText(), Toast.LENGTH_LONG).show();
            }
        });
        moveBottomImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "MoveToBOTTOM itemNo = " + rsvpNumber.getText(), Toast.LENGTH_LONG).show();
            }
        });
        deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Delete itemNo = " + rsvpNumber.getText(), Toast.LENGTH_LONG).show();
            }
        });

    }

}
