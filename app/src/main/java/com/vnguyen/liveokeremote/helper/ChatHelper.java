package com.vnguyen.liveokeremote.helper;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.gson.Gson;
import com.vnguyen.liveokeremote.ChatAdapter;
import com.vnguyen.liveokeremote.LiveOkeRemoteApplication;
import com.vnguyen.liveokeremote.MainActivity;
import com.vnguyen.liveokeremote.R;
import com.vnguyen.liveokeremote.data.LiveOkeRemoteBroadcastMsg;
import com.vnguyen.liveokeremote.data.User;
import com.vnguyen.liveokeremote.service.UDPListenerService;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatHelper {
    private MainActivity context;
    public HashMap<String,MaterialDialog> chatMsgMap;

    public ChatHelper(Context context) {
        this.context = (MainActivity) context;
        chatMsgMap = new HashMap<>();
    }

    public MaterialDialog chat(User u, boolean showIt) {
        MaterialDialog dialog;
        if (chatMsgMap.containsKey(u.name)) {
            dialog = chatMsgMap.get(u.name);
        } else {
            dialog = popupChat(u);
            chatMsgMap.put(u.name, dialog);
        }
        if (showIt) {
            dialog.show();
            //dialog.getWindow().setLayout(900, 1000);
        }
        return dialog;
    }

    public MaterialDialog popupChat(User u) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title("LiveOke Chat - " + u.name)
                .theme(Theme.LIGHT)
                .titleColor(R.color.primary)
                .customView(R.layout.friend_tab, false)
                .build();

        ListView msgList = (ListView) dialog.getCustomView().findViewById(R.id.chat_message);

        final EditText edTxt = (EditText) dialog.getCustomView().findViewById(R.id.chat_text);
        Button sendButton = (Button) dialog.getCustomView().findViewById(R.id.send_button);

        final ChatAdapter chatAdapter = new ChatAdapter(context, new ArrayList<LiveOkeRemoteBroadcastMsg>());
        msgList.setAdapter(chatAdapter);
        //setListViewHeightBasedOnChildren(msgList);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = edTxt.getText().toString();
                LiveOkeRemoteBroadcastMsg msg = new LiveOkeRemoteBroadcastMsg("Chat", "LiveOke Remote", context.me.name);
                msg.message = str;
                Log.v(LiveOkeRemoteApplication.TAG, "Message SENT = " + msg.message);
                chatAdapter.messages.add(msg);
                chatAdapter.notifyDataSetChanged();
                context.liveOkeUDPClient.sendMessage((new Gson()).toJson(msg), msg.ipAddress, UDPListenerService.BROADCAST_PORT);
                Log.v(LiveOkeRemoteApplication.TAG, "CA Size = " + chatAdapter.getCount());
                edTxt.setText("");
            }
        });
        return dialog;
    }

}
