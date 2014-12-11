package com.vnguyen.mytestapplication;


import android.content.Context;
import android.view.View;

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
        addFriendBtnIcon.colorRes(R.color.light_blue_400);

        final IconDrawable cancelBtnIcon = new IconDrawable(context, Iconify.IconValue.md_cancel);
        cancelBtnIcon.sizeDp(40);
        cancelBtnIcon.colorRes(R.color.light_blue_400);

        final FloatingActionButton addFriendButton = (FloatingActionButton) context.findViewById(R.id.add_new_friend);
        addFriendButton.setImageDrawable(addFriendBtnIcon);
        addFriendButton.setTag("Add a Friend");
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
