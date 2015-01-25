package com.vnguyen.liveokeremote;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vnguyen.liveokeremote.data.LiveOkeRemoteBroadcastMsg;

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
        if (convertView == null) {
            holder = new ChatViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.im_row, parent, false);
            holder.message = (TextView) convertView.findViewById(R.id.message_text);
            convertView.setTag(holder);
        } else {
            holder = (ChatViewHolder) convertView.getTag();
        }
        holder.message.setText(Html.fromHtml(message.getDateTime() + "<BR>" + message.name + ": " + message.message + "   "));
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.message.getLayoutParams();
        holder.message.setBackground(null);
        if (message.greeting.equalsIgnoreCase("Chat")) {
            if (message.ipAddress == null) {
                // this is my message
                holder.message.setBackgroundResource(R.drawable.speech_bubble_green);
                lp.gravity = Gravity.RIGHT;
                holder.message.setTextColor(context.getResources().getColor(R.color.black));
            } else {
                holder.message.setBackgroundResource(R.drawable.speech_bubble_orange);
                lp.gravity = Gravity.LEFT;
                holder.message.setTextColor(context.getResources().getColor(R.color.black));
            }
        } else if (message.greeting.equalsIgnoreCase("Bye")){
            holder.message.setBackground(null);
            lp.gravity = Gravity.LEFT;
            holder.message.setTextColor(context.getResources().getColor(R.color.half_black));
            holder.message.setText(Html.fromHtml(message.getDateTime() + "<BR>" + message.name + " is off-line!"));
        } else if (message.greeting.equalsIgnoreCase("Hi")) {
            holder.message.setBackground(null);
            lp.gravity = Gravity.LEFT;
            holder.message.setTextColor(context.getResources().getColor(R.color.half_black));
            holder.message.setText(Html.fromHtml(message.getDateTime() + "<BR>" + message.name + " is on-line!"));
        } else if (message.greeting.equalsIgnoreCase("Pause")) {
            holder.message.setBackground(null);
            lp.gravity = Gravity.LEFT;
            holder.message.setTextColor(context.getResources().getColor(R.color.half_black));
            holder.message.setText(Html.fromHtml(message.getDateTime() + "<BR>" + message.name + " is not active and will NOT receive any messages!"));
        } else if (message.greeting.equalsIgnoreCase("Resume")) {
            holder.message.setBackground(null);
            lp.gravity = Gravity.LEFT;
            holder.message.setTextColor(context.getResources().getColor(R.color.half_black));
            holder.message.setText(Html.fromHtml(message.getDateTime() + "<BR>" + message.name + " is now active and will receive messages!"));
        }
        holder.message.setLayoutParams(lp);
        return convertView;
    }

    private static class ChatViewHolder {
        TextView message;
    }
}
