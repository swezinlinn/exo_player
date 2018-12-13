package com.frontiir.dasher;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.frontiir.dasher.listener.AudioEventListener;
import com.frontiir.dasher.listener.BandWidthListener;
import com.frontiir.dasher.listener.PlayerEventListener;
import com.frontiir.dasher.listener.VideoEventListener;
import com.frontiir.dasher.listener.ViewControlInterface;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;

import static com.google.android.exoplayer2.DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS;
import static com.google.android.exoplayer2.DefaultLoadControl.DEFAULT_MAX_BUFFER_MS;
import static com.google.android.exoplayer2.DefaultLoadControl.DEFAULT_TARGET_BUFFER_BYTES;

public class PlayerActivity extends AppCompatActivity implements ViewControlInterface {
    private static final String TAG = PlayerActivity.class.getName();
    private PlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;

    private Timeline.Window window;
    private DataSource.Factory mediaDataSourceFactory;
    private DefaultTrackSelector trackSelector;
    private boolean shouldAutoPlay;
    private BandwidthMeter bandwidthMeter;
    private PlayerEventListener playerEventListener;
    private AudioEventListener audioEventListener;
    private VideoEventListener videoEventListener;
    private String url;
    private AVLoadingIndicatorView loading;
    private long resume;
    private ImageView ivHideControllerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        if(getIntent()!= null){
            this.url = getIntent().getStringExtra("URL");
        }
        loading = (AVLoadingIndicatorView) findViewById(R.id.loading);
        simpleExoPlayerView = (PlayerView) findViewById(R.id.player_view);
        shouldAutoPlay = true;
        window = new Timeline.Window();
        ivHideControllerButton = (ImageView) findViewById(R.id.exo_controller);
        audioEventListener = new AudioEventListener(this);
        videoEventListener = new VideoEventListener(this);
    }

    private void initializePlayer() {
        simpleExoPlayerView.requestFocus();

        bandwidthMeter = new DefaultBandwidthMeter();
        bandwidthMeter.addEventListener(new Handler(), new BandWidthListener(this));

        TrackSelection.Factory adaptiveTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
//        trackSelector.init(bandwidthMeter);
        trackSelector.setParameters(new DefaultTrackSelector.ParametersBuilder().build());
        player = ExoPlayerFactory.newSimpleInstance(this,
                new DefaultRenderersFactory(this),
                trackSelector,
                new DefaultLoadControl(
                        new DefaultAllocator(true,C.DEFAULT_VIDEO_BUFFER_SIZE),
                        5 * 60 * 2000,
                        100 * 60 * 2000,
                        10000,
                         DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS,
                        -1,
                        true));

        simpleExoPlayerView.setPlayer(player);

        Uri uri = Uri.parse(url);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, buildHttpDataSourceFactory());

        if (url.endsWith(".m3u8")) {
            Log.d(TAG, "With hls");
            HlsMediaSource mediaSource = new HlsMediaSource(uri, dataSourceFactory, new Handler(), null);
            player.prepare(mediaSource, false, true);
        } else if (url.endsWith(".mpd")) {
            Log.d(TAG, "With dash");

            DashMediaSource mediaSource = new DashMediaSource(uri, dataSourceFactory,
                    new DefaultDashChunkSource.Factory(dataSourceFactory), null,null);
            player.prepare(mediaSource, false, true);
        } else {
            Log.d(TAG, "Unsupported url format");
        }


        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.d(TAG,"on tracks changed---"+trackGroups+"--------"+trackSelections);
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.d(TAG,"on player state changed---"+playWhenReady+"--------"+playbackState+"-----get buffer position-----"+ player.getBufferedPosition()+"------get real position-----"+player.getCurrentPosition());

                if (playWhenReady) {
                    switch (playbackState) {
                        case 2:
                            showLoading();

                            break;
                        case 3:
                            hideLoading();
                            break;
                        case 4:
                            new AlertDialog.Builder(PlayerActivity.this)
                                    .setTitle("Alert")
                                    .setMessage("Video Ended")
                                    .setCancelable(false)
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                            break;
                    }
                } else {
                    hideLoading();
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                new AlertDialog.Builder(PlayerActivity.this)
                        .setTitle("Alert")
                        .setMessage(error.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               finish();
                            }
                        }).show();
            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
    }

    private DataSource.Factory buildHttpDataSourceFactory() {
        DefaultHttpDataSourceFactory defaultHttpDataSource = new DefaultHttpDataSourceFactory(Util.getUserAgent(this,"MyanmarCast"), (TransferListener) bandwidthMeter,DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,true);
        defaultHttpDataSource.setDefaultRequestProperty("User-Agent","android");
        return new DefaultDataSourceFactory(this, (TransferListener) bandwidthMeter, defaultHttpDataSource);
    }

    private void releasePlayer() {
        if (player != null) {
            shouldAutoPlay = player.getPlayWhenReady();
            player.release();
            player = null;
            trackSelector = null;
        }
    }

    private void hideLoading() {
        loading.setVisibility(View.GONE);
    }


    private void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player == null) {
            initializePlayer();
        }
        player.seekTo(resume);
        player.setPlayWhenReady(shouldAutoPlay);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (player == null) {
            initializePlayer();
        }
        player.seekTo(resume);
        player.setPlayWhenReady(shouldAutoPlay);
    }

    @Override
    public void onPause() {
        super.onPause();
        resume = player.getCurrentPosition();
    }

    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

    @Override
    public void catchBufferPosition() {
//        if(player != null) {
//            if (player.getBufferedPosition() > 70000) {
//                player.setPlayWhenReady(shouldAutoPlay);
//            }
//        }
    }
}
