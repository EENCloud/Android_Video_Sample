package com.example.eagleeyenetworks.eagleeye_mediaplayer;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.util.Random;

public class VideoPlayerActivity extends AppCompatActivity {
    PlayerView playerView;
    ExoPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        Intent intent = getIntent();
        final String esn = intent.getStringExtra("esn");
        final String authToken = intent.getStringExtra("auth_token");

        playerView = findViewById(R.id.player_view);
        LoadControl loadControl = new DefaultLoadControl(new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE),
                25,
                25000,
                25,
                1000,
                10 * 1024 * 1024,
                true);

        player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), new DefaultTrackSelector(), loadControl);
        player.setPlayWhenReady(true);
        playerView.setPlayer(player);

        String url = String.format("https://login.eagleeyenetworks.com/asset/play/video.flv?c=%s;t=stream_%d;e=+300000;A=%s", esn, new Random().nextInt(), authToken);
        Log.e("URL", url);
        Uri uri = Uri.parse(url);
        MediaSource source = new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory("Eagle Eye Networks - Android")).createMediaSource(uri);
        player.prepare(source ,false, false);

    }
}
