package com.vnguyen.liveokeremote;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.google.gson.Gson;
import com.thedazzler.droidicon.IconicFontDrawable;
import com.vnguyen.liveokeremote.data.LiveOkeRemoteBroadcastMsg;
import com.vnguyen.liveokeremote.data.User;
import com.vnguyen.liveokeremote.helper.AlertDialogHelper;
import com.vnguyen.liveokeremote.helper.PreferencesHelper;
import com.vnguyen.liveokeremote.service.UDPListenerService;

import java.util.ArrayList;
import java.util.Iterator;

public class FriendListAdapter extends BaseSwipeAdapter {
    private MainActivity context;
    public ArrayList<User> friends;
    private IconicFontDrawable iconTrash;
    private IconicFontDrawable iconInfo;
    private SwipeLayout swipeLayout;
    private ChatAdapter ca;
    private ArrayList<LiveOkeRemoteBroadcastMsg> messages;

    public FriendListAdapter(Context context, ArrayList<User> list) {
        this.context = (MainActivity) context;
        Log.v(LiveOkeRemoteApplication.TAG, "New Adapter!");
        friends = new ArrayList<>();
        friends.addAll(list);
        iconTrash = new IconicFontDrawable(context);
        iconTrash.setIcon("fa-trash");
        iconTrash.setIconColor(context.getResources().getColor(R.color.white));
        iconTrash.setIntrinsicHeight(30);
        iconTrash.setIntrinsicWidth(30);

        iconInfo = new IconicFontDrawable(context);
        iconInfo.setIcon("fa-comment");
        iconInfo.setIconColor(context.getResources().getColor(R.color.white));
        iconInfo.setIntrinsicHeight(30);
        iconInfo.setIntrinsicWidth(30);
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
            holder.ipAddress = (TextView) view.findViewById(R.id.friends_ip);
            view.setTag(holder);
        }
        holder.icon.setImageDrawable(friend.avatar);
        holder.name.setText(friend.name);
        holder.ipAddress.setText(friend.ipAddress);
        setupActionButtonsBelow(swipeLayout);
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
        final TextView ipAddr = (TextView) vTop.findViewById(R.id.friends_ip);

        // Setup below layer for "actions"
        ImageView deleteImg = (ImageView) vBottom.findViewById(R.id.f_ic_delete_id);

//        DrawableHelper.getInstance().setIconAsBackground("fa-trash", R.color.white, deleteImg,context);
        deleteImg.setImageDrawable(null);
        if (ipAddr.getText() == null || ipAddr.getText().toString().equals("")) {
            deleteImg.setImageDrawable(iconTrash);
        } else {
            deleteImg.setImageDrawable(iconInfo);
        }



        deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ipAddr.getText() == null || ipAddr.getText().toString().equals("")) {
                    new MaterialDialog.Builder(context)
                            .title("Are you sure to delete " + frName.getText() + "?")
                            .theme(Theme.LIGHT)
                            .positiveText("OK")
                            .titleColor(R.color.half_black)
                            .negativeText("Cancel")
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onNegative(MaterialDialog materialDialog) {
                                }

                                @Override
                                public void onPositive(MaterialDialog materialDialog) {
                                    removeFriendFromAdapter(frName.getText().toString());
                                    swipeLayout.toggle();
                                    notifyDataSetChanged();
                                }
                            }).show();
                } else {
                    final User u = context.friendsListHelper.findFriend(frName.getText().toString());
                    MaterialDialog dialog;
                    if (u != null) {
                        if (context.chatMap.containsKey(u.name)) {
                            dialog = context.chatMap.get(u.name);
                        } else {
                            dialog = (new AlertDialogHelper(context)).popupChat(u);
                            context.chatMap.put(u.name, dialog);
                        }
                        dialog.show();
                        dialog.getWindow().setLayout(700, 1000);
                    }
                }
            }
        });

    }


    public void removeFriendFromAdapter(String frName) {
        int i = 0;
        for (Iterator<User> it = friends.iterator(); it.hasNext(); i++) {
            User u = it.next();
            if (u.name.equalsIgnoreCase(frName.toString())) {
                // delete
                PreferencesHelper.getInstance(context).removeFriend(u, i);
                it.remove();
                break;
            }
        }
        for (Iterator<User> it = context.friendsList.iterator(); it.hasNext(); ) {
            User u = it.next();
            if (u.name.equalsIgnoreCase(frName.toString())) {
                it.remove();
                break;
            }
        }
        context.actionBarHelper.pushSub(context.friendsList.size() + " Friends.");
        notifyDataSetChanged();
    }

    private class FriendsViewHolder {
        ImageView icon;
        TextView name;
        TextView ipAddress;
    }
}
