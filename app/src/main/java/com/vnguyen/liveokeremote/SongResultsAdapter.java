package com.vnguyen.liveokeremote;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.swipe.SwipeLayout;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.vnguyen.liveokeremote.data.SongResult;
import com.vnguyen.liveokeremote.helper.AlertDialogHelper;
import com.vnguyen.liveokeremote.helper.DrawableHelper;
import com.vnguyen.liveokeremote.helper.LogHelper;
import com.vnguyen.liveokeremote.helper.SongHelper;

import java.io.IOException;
import java.util.ArrayList;

public class SongResultsAdapter extends BaseAdapter {
    private static final int NOT_PLAYING = -1;
    private MainActivity context;
    private ArrayList<SongResult> results;
    private SwipeLayout swipeLayout;
    private double startTime;
    private double finalTime;
    public Handler myHandler;
    private int mPlayingPosition = NOT_PLAYING;
    public PlaybackUpdater mProgressUpdater = new PlaybackUpdater();
    IconDrawable playButton;
    IconDrawable stopButton;

    public SongResultsAdapter(Context context, ArrayList<SongResult> results) {
        this.context = (MainActivity) context;
        this.results = new ArrayList<>(results.size());
        this.results.addAll(results);
        playButton = new IconDrawable(context, Iconify.IconValue.md_play_arrow);
        stopButton = new IconDrawable(context, Iconify.IconValue.md_stop);
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public SongResult getItem(int position) {
        return results.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final SongResult result = results.get(position);
        View view = convertView;
        final ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.song_result_item, null);
            holder = new ViewHolder();
            holder.thumbNail = (ImageView) view.findViewById(R.id.result_image_view);
            holder.resultTitle = (TextView) view.findViewById(R.id.result_title);
            holder.resultArtist = (TextView) view.findViewById(R.id.result_artist);
            holder.resultHost = (TextView) view.findViewById(R.id.result_host);
            holder.mp3urlView = (TextView) view.findViewById(R.id.result_mp3_url);
            holder.lyricUrlView = (TextView) view.findViewById(R.id.result_lyric_url);
            holder.playView = (ImageView) view.findViewById(R.id.imageButton1);
            holder.playView.setTag("stop");
            holder.imgButton2 = (ImageView) view.findViewById(R.id.imageButton2);
            holder.seekBar = (SeekBar) view.findViewById(R.id.selected_seek_bar);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.playView.setImageDrawable(playButton);
        holder.imgButton2.setImageDrawable(new IconDrawable(context,Iconify.IconValue.md_subject));
        if (position == mPlayingPosition) {
            holder.seekBar.setVisibility(View.VISIBLE);
            holder.playView.setBackground(stopButton);
            mProgressUpdater.mBarToUpdate = holder.seekBar;
            myHandler.postDelayed(mProgressUpdater, 100);
        } else {
            holder.playView.setBackground(playButton);
            holder.seekBar.setVisibility(View.INVISIBLE);
            holder.seekBar.setProgress(0);
            if (mProgressUpdater.mBarToUpdate == holder.seekBar) {
                //this progress would be updated, but this is the wrong position
                mProgressUpdater.mBarToUpdate = null;
            }
        }
        if (result.Artist == null || result.Artist.equals("")) {
            result.Artist = "?Unknown";
        }
        final Drawable d = (new DrawableHelper()).buildDrawable(result.Artist.substring(0, 1), "rect");
        if (result.Avatar != null) {
            Ion.with(context).load(result.Avatar + "&code=" + SongHelper.JSEARCH_API_CODE)
                    .withBitmap().asBitmap()
                    .setCallback(new FutureCallback<Bitmap>() {
                        @Override
                        public void onCompleted(Exception e, Bitmap result) {
                            if (result != null) {
                                holder.thumbNail.setImageDrawable(new BitmapDrawable(result));
                            } else {
                                holder.thumbNail.setImageDrawable(d);
                            }
                        }
                    });
        } else {
            holder.thumbNail.setImageDrawable(d);
        }

        holder.resultTitle.setText(result.Title);
        holder.resultArtist.setText(result.Artist);
        holder.resultHost.setText(result.HostName);
        holder.mp3urlView.setText(result.UrlJunDownload);
        holder.lyricUrlView.setText(result.LyricsUrl);
        final String mp3Url = holder.mp3urlView.getText().toString() + "&code=" + SongHelper.JSEARCH_API_CODE;
        final String lyricUrl = holder.lyricUrlView.getText().toString() + "&code=" + SongHelper.JSEARCH_API_CODE;;
        holder.playView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                try {
                    LogHelper.d("play TAG = " + holder.playView.getTag());
                    if (holder.playView.getTag().equals("stop")) {
                        //holder.playView.setImageDrawable(stopButton);
                        myHandler = new Handler();
                        context.mediaPlayer.reset();
                        context.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        context.mediaPlayer.setDataSource(context, Uri.parse(mp3Url));
                        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                                .title("Please Wait..")
                                .content("Media is buffering...")
                                .build();
                        context.mediaPlayer.prepareAsync();
                        dialog.show();
                        //mediaPlayer.start();
                        context.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                LogHelper.i("DONE PLAYING?");
                                mPlayingPosition = NOT_PLAYING;
                                holder.playView.setBackground(playButton);
                                holder.playView.setTag("stop");
                                holder.seekBar.setProgress(0);
                                notifyDataSetChanged();
                            }
                        });
                        context.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                dialog.dismiss();
                                mp.start();
                                holder.playView.setBackground(stopButton);
                                finalTime = context.mediaPlayer.getDuration();
                                startTime = context.mediaPlayer.getCurrentPosition();
                                mPlayingPosition = position;
                                holder.seekBar.setClickable(false);
                                myHandler.postDelayed(mProgressUpdater, 100);
                                holder.playView.setTag("play");
                                notifyDataSetChanged();
                            }
                        });
                    } else {
                        context.mediaPlayer.stop();
                        holder.playView.setBackground(playButton);
                        holder.playView.setTag("stop");
                        notifyDataSetChanged();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        holder.imgButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                    String lyric;
                    AlertDialogHelper adh = new AlertDialogHelper(context);
                    @Override
                    protected void onPreExecute() {
                        adh.popupProgress("Downloading lyric...");
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        LogHelper.i("LYRIC URL = " + lyricUrl);
                        lyric = SongHelper.getLyric(lyricUrl);
                        return null;
                    }

                    @SuppressLint("NewApi")
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        adh.dismissProgress();
                        MaterialDialog dialog = new MaterialDialog.Builder(context)
                                .title(Html.fromHtml("Lyric for <font color='#009688'>" + result.Title + "</font> <font color='#808080'>(" + result.HostName + ")</font>"))
                                .content(Html.fromHtml(lyric))
                                .build();
                        dialog.show();
                    }

                };
                task.execute((Void[]) null);


            }
        });
        return view;
    }

    private class PlaybackUpdater implements Runnable {
        public SeekBar mBarToUpdate = null;

        @SuppressLint("NewApi")
        @Override
        public void run() {
            if ((mPlayingPosition != NOT_PLAYING) && (null != mBarToUpdate)) {
                mBarToUpdate.setProgress( (100*context.mediaPlayer.getCurrentPosition() / context.mediaPlayer.getDuration()) );    //Cast
                myHandler.postDelayed(this, 100);
            } else {
                //not playing so stop updating
            }
        }
    }
    public static class ViewHolder {
        ImageView thumbNail;
        TextView resultTitle;
        TextView resultArtist;
        TextView resultHost;
        TextView mp3urlView;
        TextView lyricUrlView;
        ImageView playView;
        ImageView imgButton2;
        SeekBar seekBar;
    }
}
