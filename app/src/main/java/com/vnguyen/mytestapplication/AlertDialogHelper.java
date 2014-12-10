package com.vnguyen.mytestapplication;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.thedazzler.droidicon.IconicFontDrawable;

public class AlertDialogHelper {

    private final MainActivity context;

    public AlertDialogHelper(Context context) {
        this.context = (MainActivity) context;
    }

    public void popupHello() {
        final EditText input = new EditText(context);
        new MaterialDialog.Builder(context)
                .title("Hello, what is your name?")
                .theme(Theme.LIGHT)
                .positiveText("OK")
                .customView(input)
                .titleColor(R.color.half_black)
                .callback(new MaterialDialog.Callback(){
                    @Override
                    public void onNegative(MaterialDialog materialDialog) {
                    }

                    @Override
                    public void onPositive(MaterialDialog materialDialog) {
                        if (input.getEditableText().toString() != null && !input.getEditableText().toString().equals("")) {
                            String value = input.getEditableText().toString().trim();
                            // store into Preference
                            PreferencesHelper.getInstance(context).setStringPreference(
                                    context.getResources().getString(R.string.myName), value);
                            context.me = new User(value);
                            Toast.makeText(context, "Hello "+ value, Toast.LENGTH_LONG).show();
                            context.updateNowPlaying("Welcome " + value + "<br>Reserve a song and start singing");
                        }
                    }

                }).show();
    }

    public void popupIPAddressDialog(String title, String dialogMsg, final NavDrawerItem item, final NavDrawerListAdapter adapter) {
        final EditText input = new EditText(context);
        if (context.ipAddress != null && !context.ipAddress.equals("")) {
            input.setText(context.ipAddress);
        }
        new MaterialDialog.Builder(context)
                .title(title)
                .content(dialogMsg)
                .theme(Theme.LIGHT)  // the default is light, so you don't need this line
                .positiveText("OK")
                .customView(input)
                .titleColor(R.color.half_black)
                .negativeText("CANCEL")
                .callback(new MaterialDialog.Callback() {

                    @Override
                    public void onNegative(MaterialDialog materialDialog) {
                    }

                    @Override
                    public void onPositive(MaterialDialog materialDialog) {
                        if (input.getEditableText().toString() != null && !input.getEditableText().toString().equals("")) {
                            String value = input.getEditableText().toString().trim();
                            // store into Preference
                            PreferencesHelper.getInstance(context).setStringPreference(
                                    context.getResources().getString(R.string.ip_adress), value);
                            context.ipAddress = value;
                            Toast.makeText(context, "IP Address Set To: "+ value, Toast.LENGTH_LONG).show();
                            // Change the list item by appending the IP to it
                            if (item.getTitle().contains("(")) {
                                String hdr = item.getTitle().substring(0, item.getTitle().indexOf(" ("));
                                item.setTitle(hdr + " (" + value + ")");
                            } else {
                                item.setTitle(item.getTitle() + " (" + value + ")");
                            }
                            // update the list
                            context.runOnUiThread(new Runnable() {
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                })
                .show();
    }
}
