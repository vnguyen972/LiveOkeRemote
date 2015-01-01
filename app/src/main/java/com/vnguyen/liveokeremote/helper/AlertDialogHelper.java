package com.vnguyen.liveokeremote.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.vnguyen.liveokeremote.MainActivity;
import com.vnguyen.liveokeremote.NavDrawerListAdapter;
import com.vnguyen.liveokeremote.R;
import com.vnguyen.liveokeremote.RsvpListAdapter;
import com.vnguyen.liveokeremote.data.NavDrawerItem;
import com.vnguyen.liveokeremote.data.ReservedListItem;
import com.vnguyen.liveokeremote.data.User;


import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class AlertDialogHelper {

    private final MainActivity context;
    public static int FILE_PICK_FROM_CAMERA = 1;
    public static int FILE_PICK_FROM_FILE = 2;
    public static int FILE_PICK_FROM_FILE_KITKAT = 3;

    private MaterialDialog progressDialog;

    public AlertDialogHelper(Context context) {
        this.context = (MainActivity) context;
    }


    public void popupProgress(String message) {
        if (progressDialog == null) {
            progressDialog = new MaterialDialog.Builder(context)
                    .title("Please wait")
                    .customView(R.layout.progress_dialog)
                    .build();
        }
        View customView = progressDialog.getCustomView();
        TextView tvMessage = (TextView)customView.findViewById(R.id.progress_message);
        tvMessage.setText(message);
        progressDialog.show();
    }

    public void dismissProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = null;
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
                            context.nowPlayingHelper.popTitle();
                            context.setupReservedPanel();
                        }
                    }

                }).show();
    }

    public void popupIPAddressDialogGeneric() {
        final EditText input = new EditText(context);
        new MaterialDialog.Builder(context)
                .title("LiveOke IP Address")
                .content("Enter IP Address")
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
//                        if (input.getEditableText().toString() != null && !input.getEditableText().toString().equals("")) {
//                            String value = input.getEditableText().toString().trim();
//                            // store into Preference
//                            PreferencesHelper.getInstance(context).setStringPreference(
//                                    context.getResources().getString(R.string.ip_adress), value);
//                            context.ipAddress = value;
//                        }
                    }
                })
                .show();
    }
    public void popupMasterCode(String title) {
        final EditText input = new EditText(context);
        String code = PreferencesHelper.getInstance(context).getPreference("MasterCode");
        if (code != null && !code.equals("")) {
            input.setText(code);
        }
        new MaterialDialog.Builder(context)
                .title(title)
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
                        String value = input.getEditableText().toString().trim();
                        // store into Preference
                        PreferencesHelper.getInstance(context).setStringPreference(
                                "MasterCode", value);
                    }
                })
                .show();
    }

    public void popupIPAddressDialog(String title, String dialogMsg, final NavDrawerItem item, final NavDrawerListAdapter adapter) {
        final EditText input = new EditText(context);
        if (context.wsInfo != null && context.wsInfo.ipAddress != null && !context.wsInfo.ipAddress.equals("")) {
            input.setText(context.wsInfo.ipAddress);
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
                        String value = input.getEditableText().toString().trim();
                        // store into Preference
                        PreferencesHelper.getInstance(context).setStringPreference(
                                context.getResources().getString(R.string.ip_adress), value);
                        context.wsInfo.ipAddress = value;
                        Toast.makeText(context, "IP Address Set To: " + value, Toast.LENGTH_LONG).show();
                        // Change the list item by appending the IP to it
                        if (value != null && !value.equals("")) {
                            if (item.title.contains("(")) {
                                String hdr = item.title.substring(0, item.title.indexOf(" ("));
                                item.title = hdr + " (" + value + ")";
                            } else {
                                item.title = item.title + " (" + value + ")";
                            }
                        } else {
                            item.title = item.title.substring(0, item.title.indexOf(" ("));
                        }
                        // update the list
                        context.runOnUiThread(new Runnable() {
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                })
                .show();
    }

    public void popupFileChooser(final ImageView imgView, final String key) {
        final String [] items           = new String [] {"From Camera", "From SD Card"};
        ArrayAdapter<String> adapter  = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder     = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

        builder.setTitle("Select Image");
        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int item ) {
                context.aquiredPhoto.imgView = imgView;
                context.aquiredPhoto.prefKey = key;
                if (item == 0) {
                    Intent intent    = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file        = new File(Environment.getExternalStorageDirectory(),
                            "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                    context.aquiredPhoto.mImageCaptureUri = Uri.fromFile(file);

                    try {
                        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, context.mImageCaptureUri);
                        intent.putExtra("return-data", true);
                        context.startActivityForResult(intent, FILE_PICK_FROM_CAMERA);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    dialog.cancel();
                } else {
                    if (Build.VERSION.SDK_INT < 19) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        context.startActivityForResult(Intent.createChooser(intent, "Complete action using"), FILE_PICK_FROM_FILE);
                    } else {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        context.startActivityForResult(Intent.createChooser(intent, "Complete action using"), FILE_PICK_FROM_FILE_KITKAT);
                    }
                }
            }
        } );

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    public void popUpReservedListAction(final ArrayList<ReservedListItem> rItems,
                                        final String rsvpNumber, final RsvpListAdapter rsvpListAdapter,
                                        String dialogMsg, String dialogTitle, final String wsCommand) {
        new MaterialDialog.Builder(context)
                .title(dialogTitle)
                .content(dialogMsg)
                .theme(Theme.LIGHT)
                .positiveText("OK")
                .titleColor(R.color.black)
                .negativeText("Cancel")
                .callback(new MaterialDialog.Callback() {
                    @Override
                    public void onNegative(MaterialDialog materialDialog) {
                    }

                    @Override
                    public void onPositive(MaterialDialog materialDialog) {
                        int i = 0;
                        for (Iterator<ReservedListItem> it = rItems.iterator(); it.hasNext(); ) {
                            ReservedListItem item = it.next();
                            if (item.number.equalsIgnoreCase(rsvpNumber)) {
                                if (context.webSocketHelper != null && context.webSocketHelper.isConnected()) {
                                    String masterCode = PreferencesHelper.getInstance(context).getPreference("MasterCode");
                                    if (masterCode != null && !masterCode.equals("") && context.serverMasterCode != null &&
                                            !context.serverMasterCode.equals("") &&
                                            masterCode.equalsIgnoreCase(context.serverMasterCode)) {
                                        context.webSocketHelper.sendMessage(wsCommand + "," + rsvpNumber);
                                        if (wsCommand.equalsIgnoreCase("deleter")) {
                                            it.remove();
                                        }
                                    } else {
                                        SnackbarManager.show(Snackbar.with(context)
                                                .type(SnackbarType.MULTI_LINE)
                                                .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                                .textColor(Color.WHITE)
                                                .color(Color.RED)
                                                .text("ERROR: You do not have enough privileges to complete this action."));
                                    }
                                }
                                break;
                            }
                            i++;
                        }
                        rsvpListAdapter.notifyDataSetChanged();
                    }
                }).show();
    }
}
