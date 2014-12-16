package com.vnguyen.liveokeremote;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

public class FloatingButtonsHelper {

    private final MainActivity context;

    public FloatingButtonsHelper(Context context) {
        this.context = (MainActivity) context;
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
                        .callback(new MaterialDialog.Callback() {
                            @Override
                            public void onNegative(MaterialDialog materialDialog) {
                            }

                            @Override
                            public void onPositive(MaterialDialog materialDialog) {
                                if (input.getEditableText().toString() != null && !input.getEditableText().toString().equals("")) {
                                    User u = new User(input.getEditableText().toString());
                                    context.friendsListHelper.adapter.friends.add(u);
                                    context.runOnUiThread(new Runnable() {
                                        public void run() {
                                            context.friendsListHelper.adapter.notifyDataSetChanged();
                                        }
                                    });
                                    PreferencesHelper.getInstance(context).addFriend(u);
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
                context.navigationDrawerHelper.showFriendsList = false;
                context.actionBarHelper.resetTitle();
            }
        });
    }
    public void setupActionButtons() {
        final IconDrawable playBtnIcon = new IconDrawable(context, Iconify.IconValue.md_play_arrow);
        playBtnIcon.sizeDp(40);
        playBtnIcon.colorRes(R.color.orange_800);
        final IconDrawable pauseBtnIcon = new IconDrawable(context, Iconify.IconValue.md_pause);
        pauseBtnIcon.sizeDp(40);
        pauseBtnIcon.colorRes(R.color.orange_800);
        IconDrawable prevBtnIcon = new IconDrawable(context, Iconify.IconValue.md_skip_previous);
        prevBtnIcon.sizeDp(40);
        prevBtnIcon.colorRes(R.color.orange_800);
        IconDrawable nextBtnIcon = new IconDrawable(context, Iconify.IconValue.md_skip_next);
        nextBtnIcon.sizeDp(40);
        nextBtnIcon.colorRes(R.color.orange_800);
        final IconDrawable micBtnIcon = new IconDrawable(context, Iconify.IconValue.md_mic);
        micBtnIcon.sizeDp(40);
        micBtnIcon.colorRes(R.color.orange_800);
        final IconDrawable micOffBtnIcon = new IconDrawable(context, Iconify.IconValue.md_mic_off);
        micOffBtnIcon.sizeDp(40);
        micOffBtnIcon.colorRes(R.color.orange_800);

        final FloatingActionButton playButton = (FloatingActionButton) context.findViewById(R.id.playBtn);
        playButton.setImageDrawable(playBtnIcon);
        playButton.setTag("PLAY");
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playButton.getTag().equals("PLAY")) {
                    playButton.setImageDrawable(pauseBtnIcon);
                    playButton.setTag("PAUSE");
                    ((MainActivity)context).updateNowPlaying("Now Playing:<br><b>Xin Loi Tinh Yeu</b>");
                } else if (playButton.getTag().equals("PAUSE")) {
                    playButton.setImageDrawable(playBtnIcon);
                    playButton.setTag("PLAY");
                    ((MainActivity)context).updateNowPlaying("<b>PAUSE<br>Press PLAY to resume</b>");
                }
                Log.v(context.app.TAG, "floating 1");
                context.viewFlipper.showPrevious();
                Log.v(context.app.TAG, "floating 2");
                context.navigationDrawerHelper.showFriendsList = true;
//                context.actionBarHelper.resetTitle();
            }
        });

        FloatingActionButton prevButton = (FloatingActionButton) context.findViewById(R.id.skipPreviousBtn);
        prevButton.setImageDrawable(prevBtnIcon);
        FloatingActionButton nextButton = (FloatingActionButton) context.findViewById(R.id.skipNextBtn);
        nextButton.setImageDrawable(nextBtnIcon);
        final FloatingActionButton switchAudioTrkButton = (FloatingActionButton) context.findViewById(R.id.switchAudioTrackBtn);
        switchAudioTrkButton.setImageDrawable(micBtnIcon);
        switchAudioTrkButton.setTag("MIC-ON");
        switchAudioTrkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchAudioTrkButton.getTag().equals("MIC-ON")) {
                    switchAudioTrkButton.setImageDrawable(micOffBtnIcon);
                    switchAudioTrkButton.setTag("MIC-OFF");
                } else if (switchAudioTrkButton.getTag().equals("MIC-OFF")) {
                    switchAudioTrkButton.setImageDrawable(micBtnIcon);
                    switchAudioTrkButton.setTag("MIC-ON");
                }
            }
        });
    }
}
