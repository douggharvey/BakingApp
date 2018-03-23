package com.douglasharvey.bakingapp.ui;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.douglasharvey.bakingapp.R;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

//Note: full screen exoplayer code taken from : https://geoffledak.com/blog/2017/09/11/how-to-add-a-fullscreen-toggle-button-to-exoplayer-in-android/

public class StepFragment extends Fragment {
    private static final String VIDEO_URL = "videoUrl";
    private static final String THUMBNAIL_URL = "thumbnailUrl";
    private static final String DESCRIPTION = "description";

    private final String STATE_RESUME_POSITION = "resumePosition";
    private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";
    private final String STATE_PLAYER_PLAY_WHEN_READY = "playWhenReady";

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.main_media_frame)
    FrameLayout mainMediaFrame;

    private boolean exoPlayerFullscreen = false;
    private boolean playWhenReady = true;

    private ImageView fullScreenIcon;
    private Dialog fullScreenDialog;
    private long resumePosition;

    @SuppressWarnings("WeakerAccess")

    PlayerView playerView;
    private SimpleExoPlayer player;

    @SuppressWarnings("WeakerAccess")
    Unbinder unbinder;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_step_long_description)
    TextView tvStepLongDescription;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.iv_thumb_nail)
    ImageView ivThumbNail;

    private String videoUrl;
    private String description;
    private String thumbnailUrl;


    public StepFragment() {
    }

    public static StepFragment newInstance(String videoUrl, String description, String thumbnailUrl) {
        StepFragment fragment = new StepFragment();
        Bundle args = new Bundle();
        args.putString(VIDEO_URL, videoUrl);
        args.putString(DESCRIPTION, description);
        args.putString(THUMBNAIL_URL, thumbnailUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            videoUrl = getArguments().getString(VIDEO_URL);
            description = getArguments().getString(DESCRIPTION);
            thumbnailUrl = getArguments().getString(THUMBNAIL_URL);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putLong(STATE_RESUME_POSITION, resumePosition);
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, exoPlayerFullscreen);
        outState.putBoolean(STATE_PLAYER_PLAY_WHEN_READY, playWhenReady);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        tvStepLongDescription.setText(description);

        if (playerView == null) {
            playerView = getView().findViewById(R.id.player_view);
            initFullscreenDialog();
            initFullscreenButton();
        }
        if ((videoUrl == null) || videoUrl.trim().isEmpty()) {
            mainMediaFrame.setVisibility(View.INVISIBLE);
            //Image only displayed if video does not exist and thumbnail is blank on json sample therefore decided not to use placeholder or error images
            if (thumbnailUrl != null && !thumbnailUrl.trim().isEmpty()) {
                Picasso.with(getContext())
                        .load(thumbnailUrl)
                        .into(ivThumbNail);
            }
        } else startVideoPlayer();

        if (exoPlayerFullscreen) {
            ((ViewGroup) playerView.getParent()).removeView(playerView);
            fullScreenDialog.addContentView(playerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            fullScreenIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_fullscreen_skrink));
            fullScreenDialog.show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (playerView != null && player != null) {
            playWhenReady = player.getPlayWhenReady();
            resumePosition = player.getCurrentPosition();
            player.stop();
            player.release();
        }

        if (fullScreenDialog != null)
            fullScreenDialog.dismiss();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step, container, false);
        unbinder = ButterKnife.bind(this, view);
        tvStepLongDescription.setMovementMethod(new ScrollingMovementMethod());

        if (savedInstanceState != null) {
            resumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
            exoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
            playWhenReady = savedInstanceState.getBoolean(STATE_PLAYER_PLAY_WHEN_READY);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void startVideoPlayer() {
        MediaSource videoSource;

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);

        Uri videoUri = Uri.parse(videoUrl);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(), Util.getUserAgent(getActivity(), getActivity().getApplication().getPackageName()));
        videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUri);

        playerView.setPlayer(player);
        player.prepare(videoSource);

        if (resumePosition != C.TIME_UNSET) {
            player.seekTo(resumePosition); //IMPORTANT: seek must be after prepare...
        }
        player.setPlayWhenReady(playWhenReady);
    }

    private void initFullscreenDialog() {
        fullScreenDialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (exoPlayerFullscreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }

    private void openFullscreenDialog() {

        ((ViewGroup) playerView.getParent()).removeView(playerView);
        fullScreenDialog.addContentView(playerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        fullScreenIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_fullscreen_skrink));
        exoPlayerFullscreen = true;
        fullScreenDialog.show();
    }

    private void closeFullscreenDialog() {

        ((ViewGroup) playerView.getParent()).removeView(playerView);
        mainMediaFrame.addView(playerView);
        exoPlayerFullscreen = false;
        fullScreenDialog.dismiss();
        fullScreenIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_fullscreen_expand));
    }

    private void initFullscreenButton() {

        PlayerControlView controlView = playerView.findViewById(R.id.exo_controller);
        fullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        FrameLayout fullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
        fullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!exoPlayerFullscreen)
                    openFullscreenDialog();
                else
                    closeFullscreenDialog();
            }
        });
    }

}
