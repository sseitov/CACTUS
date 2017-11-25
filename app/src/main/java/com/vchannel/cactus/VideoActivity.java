package com.vchannel.cactus;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    VideoView videoview;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video);
        videoview = (VideoView) findViewById(R.id.VideoView);

        Uri video = getIntent().getData();

        progressDialog = new ProgressDialog(VideoActivity.this);
        progressDialog.setTitle(R.string.app_title);
        progressDialog.setMessage("Buffering...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(VideoActivity.this);
            mediacontroller.setAnchorView(videoview);
            // Get the URL from String VideoURL
            videoview.setMediaController(mediacontroller);
            videoview.setVideoURI(video);

        } catch (Exception e) {
            e.printStackTrace();
        }

        videoview.requestFocus();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                progressDialog.dismiss();
                videoview.start();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
