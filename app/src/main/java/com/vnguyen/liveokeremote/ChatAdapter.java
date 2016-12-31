package com.vnguyen.liveokeremote;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vnguyen.liveokeremote.data.LiveOkeRemoteBroadcastMsg;
import com.vnguyen.liveokeremote.helper.PreferencesHelper;

import java.util.ArrayList;

public class ChatAdapter extends BaseAdapter {
    private MainActivity context;
    public ArrayList<LiveOkeRemoteBroadcastMsg> messages;


    public ChatAdapter(Context context,ArrayList<LiveOkeRemoteBroadcastMsg> messages) {
        this.context = (MainActivity) context;
        this.messages = messages;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LiveOkeRemoteBroadcastMsg message = (LiveOkeRemoteBroadcastMsg) this.getItem(position);
        ChatViewHolder holder;
        // not so optimized with view holder pattern but.. it works for this simple chat msg list :)
        if (message.ipAddress != null) {
            // other msg
            convertView = LayoutInflater.from(context).inflate(R.layout.im_row_other, parent, false);
        } else {
            // my msg
            convertView = LayoutInflater.from(context).inflate(R.layout.im_row, parent, false);
        }
        holder = (ChatViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ChatViewHolder();
            holder.message = (TextView) convertView.findViewById(R.id.message_text);
            holder.chatterIcon = (ImageView) convertView.findViewById(R.id.chatter_icon);
            holder.msgLayoutParams = (RelativeLayout.LayoutParams) holder.message.getLayoutParams();
            holder.iconLayoutParams = (RelativeLayout.LayoutParams) holder.chatterIcon.getLayoutParams();
            convertView.setTag(holder);
//        } else {
//            holder = (ChatViewHolder) convertView.getTag();
        }
        holder.message.setText(Html.fromHtml(message.time + "<BR>" + message.name + ": " + message.message + "   "));
//        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.message.getLayoutParams();
//        RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) holder.chatterIcon.getLayoutParams();
        //holder.message.setBackground(null);
        if (message.greeting.equalsIgnoreCase("Chat")) {
            if (message.ipAddress == null) {
                // this is my message
//                holder.message.setBackgroundResource(R.drawable.speech_bubble_green);
////                holder.msgLayoutParams.addRule(RelativeLayout.LEFT_OF, R.id.chatter_icon);
////                holder.iconLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, 1);
////                lp.gravity = Gravity.RIGHT;
////                lp2.gravity = Gravity.RIGHT;
//                holder.message.setTextColor(context.getResources().getColor(R.color.black));
                holder.chatterIcon.setImageDrawable(context.me.avatar);

            } else {
//                holder.message.setBackgroundResource(R.drawable.speech_bubble_orange);
////                lp.gravity = Gravity.LEFT;
////                lp2.gravity = Gravity.LEFT;
////                holder.msgLayoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.chatter_icon);
////                holder.iconLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, 1);
//                holder.message.setTextColor(context.getResources().getColor(R.color.black));
//                User u = context.friendsListHelper.findFriend(message.name);
                Drawable d = PreferencesHelper.getInstance(context).findFriendAvatar(message.name);
//                if (u != null) {
//                    u.avatar = d;
//                }
                holder.chatterIcon.setImageDrawable(d);
            }
        } else if (message.greeting.equalsIgnoreCase("Bye")){
            holder.message.setBackground(null);
            //lp.gravity = Gravity.LEFT;
            holder.message.setTextColor(context.getResources().getColor(R.color.half_black));
            holder.message.setText(Html.fromHtml(message.time + "<BR>" + message.name + " is off-line!"));
        } else if (message.greeting.equalsIgnoreCase("Hi")) {
            holder.message.setBackground(null);
            //lp.gravity = Gravity.LEFT;
            holder.message.setTextColor(context.getResources().getColor(R.color.half_black));
            holder.message.setText(Html.fromHtml(message.time + "<BR>" + message.name + " is on-line!"));
        } else if (message.greeting.equalsIgnoreCase("Pause")) {
            holder.message.setBackground(null);
            //lp.gravity = Gravity.LEFT;
            holder.message.setTextColor(context.getResources().getColor(R.color.half_black));
            holder.message.setText(Html.fromHtml(message.time + "<BR>" + message.name + " is not active and will NOT receive any messages!"));
        } else if (message.greeting.equalsIgnoreCase("Resume")) {
            holder.message.setBackground(null);
            //lp.gravity = Gravity.LEFT;
            holder.message.setTextColor(context.getResources().getColor(R.color.half_black));
            holder.message.setText(Html.fromHtml(message.time + "<BR>" + message.name + " is now active and will receive messages!"));
        }
        //holder.message.setLayoutParams(lp);
//        holder.message.setLayoutParams(holder.msgLayoutParams);
//        holder.chatterIcon.setLayoutParams(holder.iconLayoutParams);
        return convertView;
    }

    private static class ChatViewHolder {
        TextView message;
        ImageView chatterIcon;
        RelativeLayout.LayoutParams msgLayoutParams;
        RelativeLayout.LayoutParams iconLayoutParams;
    }
}
