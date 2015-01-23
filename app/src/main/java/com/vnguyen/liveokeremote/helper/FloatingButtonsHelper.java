package com.vnguyen.liveokeremote.helper;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.vnguyen.liveokeremote.MainActivity;
import com.vnguyen.liveokeremote.R;
import com.vnguyen.liveokeremote.RoundImgDrawable;
import com.vnguyen.liveokeremote.data.User;

public class FloatingButtonsHelper {

    private final MainActivity context;
    private IconDrawable playBtnIcon;
    private IconDrawable pauseBtnIcon;
    private IconDrawable micBtnIcon;
    private IconDrawable micOffBtnIcon;

    public FloatingActionButton playButton;
    public FloatingActionButton switchAudioTrkButton;

    public FloatingButtonsHelper(Context context) {
        this.context = (MainActivity) context;
        playBtnIcon = new IconDrawable(context, Iconify.IconValue.md_play_arrow);
        playBtnIcon.sizeDp(40);
        playBtnIcon.colorRes(R.color.orange_800);
        pauseBtnIcon = new IconDrawable(context, Iconify.IconValue.md_pause);
        pauseBtnIcon.sizeDp(40);
        pauseBtnIcon.colorRes(R.color.orange_800);
        micBtnIcon = new IconDrawable(context, Iconify.IconValue.md_mic);
        micBtnIcon.sizeDp(40);
        micBtnIcon.colorRes(R.color.orange_800);
        micOffBtnIcon = new IconDrawable(context, Iconify.IconValue.md_mic_off);
        micOffBtnIcon.sizeDp(40);
    }


    public void setupFriendsFloatingActionButtons() {
        final IconDrawable addFriendBtnIcon = new IconDrawable(context, Iconify.IconValue.md_person_add);
        addFriendBtnIcon.sizeDp(40);
        addFriendBtnIcon.colorRes(R.color.white);

        final IconDrawable cancelBtnIcon = new IconDrawable(context, Iconify.IconValue.md_undo);
        cancelBtnIcon.sizeDp(40);
        cancelBtnIcon.colorRes(R.color.white);

        final FloatingActionButton addFriendButton = (FloatingActionButton) context.findViewById(R.id.add_new_friend);
        addFriendButton.setImageDrawable(addFriendBtnIcon);
        addFriendButton.setTag("Add a Friend");
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(context);
                new MaterialDialog.Builder(context)
                        .title("Add a new friend")
                        .theme(Theme.LIGHT)
                        .customView(input)
                        .positiveText("OK")
                        .titleColor(R.color.half_black)
                        .negativeText("Cancel")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onNegative(MaterialDialog materialDialog) {
                            }

                            @Override
                            public void onPositive(MaterialDialog materialDialog) {
                                if (input.getEditableText().toString() != null && !input.getEditableText().toString().equals("")) {
                                    User u = new User(input.getEditableText().toString());
                                    //Bitmap bm = context.drawableHelper.drawableToBitmap(context.getResources().getDrawable(R.drawable.default_profile));
                                    Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_profile);
                                    u.avatar =  new RoundImgDrawable(bm);
                                    context.friendsListHelper.adapter.friends.add(u);
                                    context.friendsList.add(u);
                                    context.friendsListHelper.adapter.notifyDataSetChanged();
                                    PreferencesHelper.getInstance(context).addFriend(u);
                                    context.actionBarHelper.setSubTitle(context.friendsListHelper.adapter.friends.size() + " Friends.");
                                }
                            }
                        }).show();
            }
        });

        final FloatingActionButton cancelButton = (FloatingActionButton) context.findViewById(R.id.close_friends_list);
        cancelButton.setImageDrawable(cancelBtnIcon);
        cancelButton.setTag("Cancel");
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.viewFlipper.showNext();
                context.actionBarHelper.popSub();
                context.navigationDrawerHelper.showFriendsList = false;
                context.actionBarHelper.resetTitle();
            }
        });
    }

    public void togglePlayBtn() {
        Log.v(context.app.TAG, "Playbutton TAG = " + playButton.getTag());
        if (playButton.getTag().equals("PLAY")) {
            playButton.setImageDrawable(pauseBtnIcon);
            playButton.setTag("PAUSE");
            context.nowPlayingHelper.popTitle();
        } else if (playButton.getTag().equals("PAUSE")) {
            playButton.setImageDrawable(playBtnIcon);
            playButton.setTag("PLAY");
        }
    }

    public void setupActionButtons() {
        IconDrawable prevBtnIcon = new IconDrawable(context, Iconify.IconValue.md_skip_previous);
        prevBtnIcon.sizeDp(40);
        prevBtnIcon.colorRes(R.color.orange_800);
        IconDrawable nextBtnIcon = new IconDrawable(context, Iconify.IconValue.md_skip_next);
        nextBtnIcon.sizeDp(40);
        IconDrawable swapAudioBtnIcon = new IconDrawable(context, Iconify.IconValue.md_sync);
        swapAudioBtnIcon.sizeDp(40);
        swapAudioBtnIcon.colorRes(R.color.orange_800);
        nextBtnIcon.colorRes(R.color.orange_800);
        micOffBtnIcon.colorRes(R.color.orange_800);

        playButton = (FloatingActionButton) context.findViewById(R.id.playBtn);
        playButton.setImageDrawable(playBtnIcon);
        playButton.setTag("PLAY");
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context.liveOkeUDPClient != null) {
                    context.liveOkeUDPClient.sendMessage("play",
                            context.liveOkeUDPClient.liveOkeIPAddress,
                            context.liveOkeUDPClient.LIVEOKE_UDP_PORT);
                } else {
                    SnackbarManager.show(Snackbar.with(context)
                            .type(SnackbarType.MULTI_LINE)
                            .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                            .textColor(Color.WHITE)
                            .color(Color.RED)
                            .text("ERROR: Unable to connect to UDPClient Service!"));
                }
            }
        });

        FloatingActionButton prevButton = (FloatingActionButton) context.findViewById(R.id.skipPreviousBtn);
        prevButton.setImageDrawable(prevBtnIcon);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (context.webSocketHelper != null && context.webSocketHelper.isConnected()) {
                if (context.liveOkeUDPClient != null) {
                    new MaterialDialog.Builder(context)
                            .title("RESTART THIS SONG?")
                            .content("Make sure everyone is OK before continue...")
                            .theme(Theme.LIGHT)  // the default is light, so you don't need this line
                            .positiveText("OK")
                            .negativeText("CANCEL")
                            .callback(new MaterialDialog.ButtonCallback() {

                                @Override
                                public void onNegative(MaterialDialog materialDialog) {
                                }

                                @Override
                                public void onPositive(MaterialDialog materialDialog) {
                                    //context.webSocketHelper.sendMessage("begine");
                                    context.liveOkeUDPClient.sendMessage("begine",
                                            context.liveOkeUDPClient.liveOkeIPAddress,
                                            context.liveOkeUDPClient.LIVEOKE_UDP_PORT);
                                }
                            })
                            .show();

                } else {
                    SnackbarManager.show(Snackbar.with(context)
                            .type(SnackbarType.MULTI_LINE)
                            .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                            .textColor(Color.WHITE)
                            .color(Color.RED)
                            .text("ERROR: Not Connected to UDPClient Service"));
                }
            }
        });
        FloatingActionButton nextButton = (FloatingActionButton) context.findViewById(R.id.skipNextBtn);
        nextButton.setImageDrawable(nextBtnIcon);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (context.webSocketHelper != null && context.webSocketHelper.isConnected()) {
                if (context.liveOkeUDPClient != null) {
                    new MaterialDialog.Builder(context)
                            .title("SKIP THIS SONG?")
                            .content("Make sure everyone is OK before continue...")
                            .theme(Theme.LIGHT)  // the default is light, so you don't need this line
                            .positiveText("OK")
                            .negativeText("CANCEL")
                            .callback(new MaterialDialog.ButtonCallback() {

                                @Override
                                public void onNegative(MaterialDialog materialDialog) {
                                }

                                @Override
                                public void onPositive(MaterialDialog materialDialog) {
                                    //context.webSocketHelper.sendMessage("next");
                                    context.liveOkeUDPClient.sendMessage("next",
                                            context.liveOkeUDPClient.liveOkeIPAddress,
                                            context.liveOkeUDPClient.LIVEOKE_UDP_PORT);
                                    togglePlayBtn();
                                }
                            })
                            .show();

                } else {
                    SnackbarManager.show(Snackbar.with(context)
                            .type(SnackbarType.MULTI_LINE)
                            .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                            .textColor(Color.WHITE)
                            .color(Color.RED)
                            .text("ERROR: Not Connected to UDPClient Service"));
                }
            }
        });

        switchAudioTrkButton = (FloatingActionButton) context.findViewById(R.id.switchAudioTrackBtn);
        switchAudioTrkButton.setImageDrawable(micBtnIcon);
        switchAudioTrkButton.setTag("MIC-ON");
        switchAudioTrkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (context.webSocketHelper != null && context.webSocketHelper.isConnected()) {
                if (context.liveOkeUDPClient != null) {
                        context.liveOkeUDPClient.sendMessage("toggleaudio",
                                context.liveOkeUDPClient.liveOkeIPAddress,
                                context.liveOkeUDPClient.LIVEOKE_UDP_PORT);

                } else {
                    SnackbarManager.show(Snackbar.with(context)
                            .type(SnackbarType.MULTI_LINE)
                            .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                            .textColor(Color.WHITE)
                            .color(Color.RED)
                            .text("ERROR: Not Connected to UDPClient Service"));
                }
            }
        });
        FloatingActionButton swapAudioButton = (FloatingActionButton) context.findViewById(R.id.swapAudioBtn);
        swapAudioButton.setImageDrawable(swapAudioBtnIcon);
        swapAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (context.webSocketHelper != null && context.webSocketHelper.isConnected()) {
                if (context.liveOkeUDPClient != null) {
                    new MaterialDialog.Builder(context)
                            .title("MARK THIS AUDIO TRACK SWAPPED?")
                            .content("This will ensure next time the song plays the right audio track.")
                            .theme(Theme.LIGHT)  // the default is light, so you don't need this line
                            .positiveText("OK")
                            .negativeText("CANCEL")
                            .callback(new MaterialDialog.ButtonCallback() {

                                @Override
                                public void onNegative(MaterialDialog materialDialog) {
                                }

                                @Override
                                public void onPositive(MaterialDialog materialDialog) {
                                    //context.webSocketHelper.sendMessage("swap");
                                    context.liveOkeUDPClient.sendMessage("swap",
                                            context.liveOkeUDPClient.liveOkeIPAddress,
                                            context.liveOkeUDPClient.LIVEOKE_UDP_PORT);
                                }
                            })
                            .show();

                } else {
                    SnackbarManager.show(Snackbar.with(context)
                            .type(SnackbarType.MULTI_LINE)
                            .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                            .textColor(Color.WHITE)
                            .color(Color.RED)
                            .text("ERROR: Not Connected to UDPClient Service"));
                }
            }
        });
    }

    public void micOn() {
        switchAudioTrkButton.setImageDrawable(micBtnIcon);
        switchAudioTrkButton.setTag("MIC-ON");
    }

    public void micOff() {
        switchAudioTrkButton.setImageDrawable(micOffBtnIcon);
        switchAudioTrkButton.setTag("MIC-OFF");
    }
}
