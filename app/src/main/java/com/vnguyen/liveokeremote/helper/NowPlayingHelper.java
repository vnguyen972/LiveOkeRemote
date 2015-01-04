package com.vnguyen.liveokeremote.helper;

import android.content.Context;
import android.text.Html;
import android.widget.TextView;

import com.vnguyen.liveokeremote.MainActivity;
import com.vnguyen.liveokeremote.R;

import java.util.Stack;

public class NowPlayingHelper {
    private MainActivity context;
    private Stack titleStack;

    public NowPlayingHelper(Context context) {
        this.context = (MainActivity) context;
        titleStack = new Stack();
    }

    public void pushTitle(String title) {
        String oldTitle = context.mNowPlayingTxtView.getText().toString();
        titleStack.push(oldTitle);
        context.mNowPlayingTxtView.setText(Html.fromHtml(title));
    }

    public void setTitle(String title) {
        if (!titleStack.isEmpty()) {
            titleStack.clear();
        }
        if (context.mNowPlayingTxtView == null ) {
            context.mNowPlayingTxtView = (TextView) context.findViewById(R.id.now_playing_text_view);
        }
        context.mNowPlayingTxtView.setText(Html.fromHtml(title));
    }


    public void popTitle() {
        if (!titleStack.isEmpty()) {
            String oldTitle = (String) titleStack.pop();
            context.mNowPlayingTxtView.setText(Html.fromHtml(oldTitle));
        }
    }
}
