package com.vnguyen.liveokeremote;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.thedazzler.droidicon.IconicFontDrawable;

import java.util.ArrayList;
import java.util.Iterator;

import cat.lafosca.facecropper.FaceCropper;

public class FriendListAdapter extends BaseSwipeAdapter {
    private MainActivity context;
    public ArrayList<User> friends;
    private SwipeLayout swipeLayout;

    public FriendListAdapter(Context context, ArrayList<User> list) {
        this.context = (MainActivity) context;
        Log.v(this.context.app.TAG, "New Adapter!");
        friends = new ArrayList<>();
        friends.addAll(list);
    }

    public void clear() {
        friends.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.friends_swipe_layout;
    }

    @Override
    public View generateView(int position, ViewGroup viewGroup) {
        View v = LayoutInflater.from(context).inflate(R.layout.friends_list_item, null);
        swipeLayout = (SwipeLayout) v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);
        return v;
    }

    @Override
    public void fillValues(int i, View view) {
        final User friend = friends.get(i);
        final ImageView friendIcon = (ImageView) view.findViewById(R.id.friends_icon);
        Uri imgURI;
        Bitmap bm;
        Bitmap _bm;
        String avatarURI = PreferencesHelper.getInstance(context).getPreference(friend.getName()+"_avatar");
        Log.v(context.app.TAG, "Avatar from Pref. URI: " + avatarURI);
        if (avatarURI != null && !avatarURI.equals("")) {
            imgURI = Uri.parse(avatarURI);
            _bm = context.uriToBitmap(imgURI);
        } else {
            _bm = context.drawableHelper.drawableToBitmap(context.getResources().getDrawable(R.drawable.default_profile));
        }
        FaceCropper mFaceCropper = new FaceCropper();
        if (!_bm.isRecycled()) {
            bm = mFaceCropper.getCroppedImage(_bm);
            if (bm.getWidth() > 120 || bm.getHeight() > 120) {
                bm = Bitmap.createScaledBitmap(bm, 120, 120, false);
            }
            RoundImgDrawable img = new RoundImgDrawable(bm);
//
//        FaceCropper mFaceCropper = new FaceCropper();
//        Bitmap b = mFaceCropper.getCroppedImage(context,R.drawable.default_profile);
//        if (b.getWidth() > 120 || b.getHeight() > 120) {
//            b = Bitmap.createScaledBitmap(b, 120, 120, false);
//        }
//        RoundImgDrawable img = new RoundImgDrawable(b);
            friendIcon.setImageDrawable(img);
        }
        TextView fName = (TextView) view.findViewById(R.id.friends_name);
        fName.setText(friend.getName());
        setupActionButtonsBelow(swipeLayout);
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Object getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setupActionButtonsBelow(final SwipeLayout swipeLayout) {
        ViewGroup vBottom = swipeLayout.getBottomView();
        ViewGroup vTop = swipeLayout.getSurfaceView();
        final TextView frName = (TextView) vTop.findViewById(R.id.friends_name);

        // Setup below layer for "actions"
        ImageView deleteImg = (ImageView) vBottom.findViewById(R.id.f_ic_delete_id);

//        DrawableHelper.getInstance().setIconAsBackground("fa-trash", R.color.white, deleteImg,context);
        IconicFontDrawable icon = new IconicFontDrawable(context);
        icon.setIcon("fa-trash");
        icon.setIconColor(context.getResources().getColor(R.color.white));
        icon.setIntrinsicHeight(30);
        icon.setIntrinsicWidth(30);
        deleteImg.setImageDrawable(null);
        deleteImg.setImageDrawable(icon);



        deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(context)
                        .title("Are you sure to delete " + frName.getText() + "?")
                        .theme(Theme.LIGHT)
                        .positiveText("OK")
                        .titleColor(R.color.half_black)
                        .negativeText("Cancel")
                        .callback(new MaterialDialog.Callback() {
                            @Override
                            public void onNegative(MaterialDialog materialDialog) {
                            }

                            @Override
                            public void onPositive(MaterialDialog materialDialog) {
                                int i = 0;
                                for (Iterator<User> it = friends.iterator();it.hasNext();i++) {
                                    User u = it.next();
                                    if (u.getName().equalsIgnoreCase(frName.getText().toString())) {
                                        // delete
                                        PreferencesHelper.getInstance(context).removeFriend(u,i);
                                        it.remove();
                                        break;
                                    }
                                }
                                notifyDataSetChanged();
                            }
                        }).show();
            }
        });

    }
}
