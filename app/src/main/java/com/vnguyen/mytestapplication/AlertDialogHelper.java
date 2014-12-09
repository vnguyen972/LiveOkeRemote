package com.vnguyen.mytestapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

public class AlertDialogHelper {
    public void popupDialog(final Context context, String title, String dialogMsg) {
               /* Alert Dialog Code Start*/
        final EditText input = new EditText(context);

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
                            String srt = input.getEditableText().toString();
                            Toast.makeText(context, srt, Toast.LENGTH_LONG).show();
                        }
                    })
                .show();
    }
}
