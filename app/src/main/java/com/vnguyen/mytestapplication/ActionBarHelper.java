package com.vnguyen.mytestapplication;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import java.util.Stack;

public class ActionBarHelper {

    private MainActivity context;
    private Stack titleStack;

    public ActionBarHelper(Context context) {
        this.context = (MainActivity) context;
        titleStack = new Stack();
    }

    public Spannable formatActionBarTitle(String title) {
        Spannable newTitle = new SpannableString(title);
        newTitle.setSpan(new ForegroundColorSpan(Color.WHITE), 0, newTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return newTitle;
    }

    public void setTitle(String newTitle) {
        String oldTitle = context.getSupportActionBar().getTitle().toString();
        if (oldTitle != null) {
            titleStack.push(oldTitle);
        }
        context.getSupportActionBar().setTitle(formatActionBarTitle(newTitle));
    }

    public void resetTitle() {
        String oldTitle = (String) titleStack.pop();
        if (oldTitle != null) {
            context.getSupportActionBar().setTitle(formatActionBarTitle(oldTitle));
        }
    }

}
