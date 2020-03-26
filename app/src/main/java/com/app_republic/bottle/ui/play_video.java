package com.app_republic.bottle.ui;


import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.airbnb.lottie.LottieAnimationView;
import com.app_republic.bottle.R;

import static android.view.View.INVISIBLE;

/**
 * Created by elhadj on 07/09/2018.
 */

public class play_video extends DialogFragment {
    Dialog dialog;

    ProgressBar progressBar;
    VideoView videoView;
    RelativeLayout replay, animation;
    ImageView imageView;
    LottieAnimationView animationView;
    MyAsync task;
    FloatingActionButton close;

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        videoView.pause();
        videoView.suspend();
        if (task != null)
            task.cancel(true);

    }

    @Override
    public void onStart() {

        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().setBackgroundDrawableResource(R.color.cardview_dark_background);

        View view = inflater.inflate(R.layout.play_video, null);
        progressBar = view.findViewById(R.id.progress);
        progressBar.setScaleY(3f);
        progressBar.setMax(100);

        animation = view.findViewById(R.id.animation);

        videoView = view.findViewById(R.id.videoView);

        replay = view.findViewById(R.id.replay);
        imageView = view.findViewById(R.id.imageView);
        close = view.findViewById(R.id.close);
        animationView = view.findViewById(R.id.video_animation);


        //animationView.playAnimation();
        String str = getArguments().getString("video");
        Uri uri = Uri.parse(str);

        videoView.setVideoURI(uri);

        //progressBar.setVisibility(View.VISIBLE);


        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                imageView.setVisibility(View.VISIBLE);

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (task != null)
                    task.cancel(true);

                dismiss();
            }
        });

        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!videoView.isPlaying()) {
                    imageView.setVisibility(INVISIBLE);
                    videoView.start();
                } else {
                    imageView.setVisibility(INVISIBLE);
                    videoView.pause();
                }

            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //close the progress dialog when buffering is done

                //animationView.cancelAnimation();
                task = new MyAsync();
                task.execute();
                animation.setVisibility(INVISIBLE);


            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                return false;
            }
        });
        videoView.requestFocus();

        videoView.start();


        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        videoView.pause();
        videoView.suspend();
        if (task != null)
            task.cancel(true);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        videoView.pause();
        videoView.suspend();
        if (task != null)
            task.cancel(true);


    }

    @Override
    public void onPause() {
        super.onPause();
        videoView.pause();

    }




    private class MyAsync extends AsyncTask<Void, Integer, Void> {
        int duration = videoView.getDuration();

        int current = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setProgress(0);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setProgress(100);

        }

        @Override
        protected Void doInBackground(Void... params) {


            do {
                if (isCancelled())
                    break;


                try {
                    current = videoView.getCurrentPosition();
                    publishProgress((int) (current * 100 / duration));
                    if (progressBar.getProgress() >= 100) {
                        break;
                    }
                } catch (Exception e) {
                }
            } while (progressBar.getProgress() <= 100);

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
            //progressBar.setProgress(values[0]);
        }
    }


}

