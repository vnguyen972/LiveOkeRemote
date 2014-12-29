package com.vnguyen.liveokeremote;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.thedazzler.droidicon.IconicFontDrawable;
import com.vnguyen.liveokeremote.data.User;

import java.util.ArrayList;
import java.util.Iterator;

public class FriendListAdapter extends BaseSwipeAdapter {
    private MainActivity context;
    public ArrayList<User> friends;
    private SwipeLayout swipeLayout;

    public FriendListAdapter(Context context, ArrayList<User> list) {
        this.context = (MainActivity) context;
        Log.v(this.context.app.TAG, "New Adapter!");
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
        swipeLayout = (SwipeLayout) v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);
        setupActionButtonsBelow(swipeLayout);
        return v;
    }

    @Override
    public void fillValues(int i, View view) {
        final User friend = friends.get(i);
        FriendsViewHolder holder = (FriendsViewHolder) view.getTag();
        if (holder == null) {
            holder = new FriendsViewHolder();
            holder.icon = (ImageView) view.findViewById(R.id.friends_icon);
            holder.name = (TextView) view.findViewById(R.id.friends_name);
            view.setTag(holder);
        }
        holder.icon.setImageDrawable(friend.avatar);
        holder.name.setText(friend.name);
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

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setupActionButtonsBelow(final SwipeLayout swipeLayout) {
        ViewGroup vBottom = swipeLayout.getBottomView();
        ViewGroup vTop = swipeLayout.getSurfaceView();
        final TextView frName = (TextView) vTop.findViewById(R.id.friends_name);

        // Setup below layer for "actions"
        ImageView deleteImg = (ImageView) vBottom.findViewById(R.id.f_ic_delete_id);

//        DrawableHelper.getInstance().setIconAsBackground("fa-trash", R.color.white, deleteImg,context);
        IconicFontDrawable icon = new IconicFontDrawable(context);
        icon.setIcon("fa-trash");
        icon.setIconColor(context.getResources().getColor(R.color.white));
        icon.setIntrinsicHeight(30);
        icon.setIntrinsicWidth(30);
        deleteImg.setImageDrawable(null);
        deleteImg.setImageDrawable(icon);



        deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(context)
                        .title("Are you sure to delete " + frName.getText() + "?")
                        .theme(Theme.LIGHT)
                        .positiveText("OK")
                        .titleColor(R.color.half_black)
                        .negativeText("Cancel")
                        .callback(new MaterialDialog.Callback() {
                            @Override
                            public void onNegative(MaterialDialog materialDialog) {
                            }

                            @Override
                            public void onPositive(MaterialDialog materialDialog) {
                                int i = 0;
                                for (Iterator<User> it = friends.iterator();it.hasNext();i++) {
                                    User u = it.next();
                                    if (u.name.equalsIgnoreCase(frName.getText().toString())) {
                                        // delete
                                        PreferencesHelper.getInstance(context).removeFriend(u,i);
                                        it.remove();
                                        break;
                                    }
                                }
                                notifyDataSetChanged();
                            }
                        }).show();
            }
        });

    }

    private class FriendsViewHolder {
        ImageView icon;
        TextView name;
    }
}
