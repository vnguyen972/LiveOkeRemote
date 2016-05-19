package com.vnguyen.liveokeremote;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.vnguyen.liveokeremote.data.ReservedListItem;
import com.vnguyen.liveokeremote.helper.AlertDialogHelper;

import java.util.ArrayList;

public class RsvpListAdapter extends BaseSwipeAdapter {

    private MainActivity mContext;
    private ArrayList<ReservedListItem> rItems;

    public RsvpListAdapter(Context context, ArrayList<ReservedListItem> itemList) {
        this.mContext = (MainActivity) context;
        rItems = new ArrayList<>();
        rItems.addAll(itemList);
    }

    public void reloadData(ArrayList<ReservedListItem> itemList) {
        rItems.clear();
        rItems.addAll(itemList);
        notifyDataSetChanged();
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
        RsvpViewHolder holder = (RsvpViewHolder) view.getTag();
        if (holder == null) {
            holder = new RsvpViewHolder();
            holder.icon = (ImageView) view.findViewById(R.id.rsvp_icon);
            holder.title = (TextView) view.findViewById(R.id.rsvp_title);
            holder.num = (TextView) view.findViewById(R.id.rsvp_number);
            view.setTag(holder);
        }
        if (item.requester.avatar == null) {
            holder.icon.setImageDrawable(item.icon);
            notifyDataSetChanged();
        } else {
            holder.icon.setImageDrawable(item.requester.avatar);
            notifyDataSetChanged();
        }
        holder.title.setText(Html.fromHtml(item.title + "<br><b>" + item.requester.name + "</b>"));
        holder.num.setText(item.number);
    }

    @Override
    public int getCount() {
        return rItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rItems.get(position);
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
    public void setupActionButtonsBelow(final SwipeLayout swipeLayout) {
        ViewGroup vBottom = swipeLayout.getBottomView();
        ViewGroup vTop = swipeLayout.getSurfaceView();
        final TextView rsvpNumber = (TextView) vTop.findViewById(R.id.rsvp_number);
        final TextView rsvpTitle = (TextView) vTop.findViewById(R.id.rsvp_title);

        // Setup below layer for "actions"
        ImageView moveTopImg = (ImageView) vBottom.findViewById(R.id.ic_move_to_top_id);
        ImageView moveUpImg = (ImageView) vBottom.findViewById(R.id.ic_move_up_id);
        ImageView moveDownImg = (ImageView) vBottom.findViewById(R.id.ic_move_down_id);
        ImageView moveBottomImg = (ImageView) vBottom.findViewById(R.id.ic_move_bottom_id);
        ImageView deleteImg = (ImageView) vBottom.findViewById(R.id.ic_delete_id);

        mContext.drawableHelper.setIconAsBackground("fa-angle-double-up", R.color.white, moveTopImg, mContext);
        mContext.drawableHelper.setIconAsBackground("fa-angle-up", R.color.white, moveUpImg, mContext);
        mContext.drawableHelper.setIconAsBackground("fa-angle-down", R.color.white, moveDownImg, mContext);
        mContext.drawableHelper.setIconAsBackground("fa-angle-double-down", R.color.white, moveBottomImg, mContext);
        mContext.drawableHelper.setIconAsBackground("fa-trash", R.color.white, deleteImg, mContext);

        final AlertDialogHelper alertDialogHelper = new AlertDialogHelper(mContext);
        moveTopImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeLayout.toggle();
                alertDialogHelper.popUpReservedListAction(rItems,
                        rsvpNumber.getText().toString(), RsvpListAdapter.this,
                        "Are you sure to move " + rsvpTitle.getText().toString() + " to the Top?",
                        "Move Reserved Song.", "tofront");
            }
        });

        moveUpImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeLayout.toggle();
                alertDialogHelper.popUpReservedListAction(rItems,
                        rsvpNumber.getText().toString(), RsvpListAdapter.this,
                        "Are you sure to Move '" + rsvpTitle.getText().toString() + "' up one position?",
                        "Move Reserved Song.", "upone");
            }
        });
        moveDownImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeLayout.toggle();
                alertDialogHelper.popUpReservedListAction(rItems,
                        rsvpNumber.getText().toString(), RsvpListAdapter.this,
                        "Are you sure to Move '" + rsvpTitle.getText().toString() + "' down one position?",
                        "Move Reserved Song.", "backone");
            }
        });
        moveBottomImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeLayout.toggle();
                alertDialogHelper.popUpReservedListAction(rItems,
                        rsvpNumber.getText().toString(), RsvpListAdapter.this,
                        "Are you sure to Move '" + rsvpTitle.getText().toString() + "' to the BOTTOM?",
                        "Move Reserved Song.", "tolast");
            }
        });
        deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeLayout.toggle();
                alertDialogHelper.popUpReservedListAction(rItems,
                        rsvpNumber.getText().toString(), RsvpListAdapter.this,
                        "Are you sure to delete '" + rsvpTitle.getText().toString() + "'?",
                        "Delete Reserved Song.", "deleter");
            }
        });

    }

    private class RsvpViewHolder {
        ImageView icon;
        TextView title;
        TextView num;
    }
}
