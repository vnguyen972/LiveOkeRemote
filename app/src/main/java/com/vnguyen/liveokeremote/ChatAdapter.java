package com.vnguyen.liveokeremote;

import android.annotation.SuppressLint;
import android.content.Context;
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
        holder.message.setText(message.name + ": " + message.message + "   ");
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.message.getLayoutParams();
        holder.message.setBackground(null);
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
        holder.message.setLayoutParams(lp);
        return convertView;
    }

    private static class ChatViewHolder {
        TextView message;
    }
}
