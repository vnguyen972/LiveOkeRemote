package com.vnguyen.liveokeremote.helper;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.vnguyen.liveokeremote.MainActivity;
import com.vnguyen.liveokeremote.R;

import java.util.Stack;

public class ActionBarHelper {

    private MainActivity context;
    private Stack titleStack;
    private Stack subTitleStack;

    public ActionBarHelper(Context context) {
        this.context = (MainActivity) context;
        titleStack = new Stack();
        subTitleStack = new Stack();
    }

    public Spannable formatActionBarTitle(String title) {
        Spannable newTitle = new SpannableString(title);
        newTitle.setSpan(new ForegroundColorSpan(Color.WHITE), 0, newTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return newTitle;
    }

    public void setSubTitle(String str) {
        context.getSupportActionBar().setSubtitle(formatActionBarTitle(str));
    }

    public void pushSub(String newSub) {
        String oldSub = context.getSupportActionBar().getSubtitle().toString();
        if (oldSub != null && !oldSub.equalsIgnoreCase(newSub)) {
            subTitleStack.push(oldSub);
        }
        context.getSupportActionBar().setSubtitle(formatActionBarTitle(newSub));
    }

    public void popSub() {
        if (!subTitleStack.isEmpty()) {
            String oldSub = (String) subTitleStack.pop();
            if (oldSub != null) {
                context.getSupportActionBar().setSubtitle(formatActionBarTitle(oldSub));
            }
        }
    }

    public void setTitle(String newTitle) {
        String oldTitle = context.getSupportActionBar().getTitle().toString();
        if (oldTitle != null && !oldTitle.equalsIgnoreCase(newTitle)) {
            titleStack.push(oldTitle);
        }
        context.getSupportActionBar().setTitle(formatActionBarTitle(newTitle));
    }

    public void resetTitle() {
        if (!titleStack.isEmpty()) {
            String oldTitle = (String) titleStack.pop();
            if (oldTitle != null) {
                context.getSupportActionBar().setTitle(formatActionBarTitle(oldTitle));
            }
        } else {
            context.getSupportActionBar().setTitle(formatActionBarTitle(context.getResources().getString(R.string.app_name)));
        }
    }

}
