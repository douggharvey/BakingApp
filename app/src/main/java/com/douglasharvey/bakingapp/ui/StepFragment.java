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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

//Note: full screen exoplayer code taken from : https://geoffledak.com/blog/2017/09/11/how-to-add-a-fullscreen-toggle-button-to-exoplayer-in-android/

public class StepFragment extends Fragment {
    public static final String VIDEO_URL = "videoUrl";
    public static final String DESCRIPTION = "description";

    private final String STATE_RESUME_WINDOW = "resumeWindow";
    private final String STATE_RESUME_POSITION = "resumePosition";
    private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";
    private final String STATE_PLAYER_PLAY_WHEN_READY = "playWhenReady";

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.main_media_frame)
    FrameLayout mainMediaFrame;

    private boolean exoPlayerFullscreen = false;
    private boolean playWhenReady = true;

    private FrameLayout fullScreenButton;
    private ImageView fullScreenIcon;
    private Dialog fullScreenDialog;
    private int resumeWindow;
    private long resumePosition;

    @SuppressWarnings("WeakerAccess")

    PlayerView playerView;
    SimpleExoPlayer player;

    @SuppressWarnings("WeakerAccess")
    Unbinder unbinder;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_step_long_description)
    TextView tvStepLongDescription;

    private String videoUrl;
    private String description;


    public StepFragment() {
    }

    public static StepFragment newInstance(String videoUrl, String description) {
        StepFragment fragment = new StepFragment();
        Bundle args = new Bundle();
        args.putString(VIDEO_URL, videoUrl);
        args.putString(DESCRIPTION, description);
        fragment.setArguments(args);
        Timber.d("newInstance: set arguments" + videoUrl + description);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            videoUrl = getArguments().getString(VIDEO_URL);
            description = getArguments().getString(DESCRIPTION);
        }

    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(STATE_RESUME_WINDOW, resumeWindow);
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

        if ((videoUrl == null) || videoUrl.trim().length()==0 ){
            Timber.d("onResume: videoUrl is blank! - set player to gone");
            mainMediaFrame.setVisibility(View.INVISIBLE);
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
            resumeWindow = player.getCurrentWindowIndex(); //to do unnecessary?
            playWhenReady = player.getPlayWhenReady();
            resumePosition = player.getCurrentPosition();
            Timber.d("onPause: resumewindow:" + resumeWindow);
            Timber.d("onPause: resumePosition:" + resumePosition);
            player.stop();
            player.release();
        }

        if (fullScreenDialog != null)
            fullScreenDialog.dismiss();
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
//todo fix lint warning
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(), Util.getUserAgent(getActivity(), getActivity().getApplication().getPackageName()));
        videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUri);

        playerView.setPlayer(player);
        player.prepare(videoSource);

        Timber.d("startVideoPlayer: resumeposition"+resumePosition);
        Timber.d("startVideoPlayer: C.TIME_UNSET"+C.TIME_UNSET);
        if (resumePosition != C.TIME_UNSET) {
            player.seekTo(resumePosition); //important seek must be after prepare...
            Timber.d("startVideoPlayer: seekto done");
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
        fullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView: ");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_step, container, false);
        unbinder = ButterKnife.bind(this, view);
        tvStepLongDescription.setMovementMethod(new ScrollingMovementMethod());

        if (savedInstanceState != null) {
            resumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            resumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
            exoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
            playWhenReady = savedInstanceState.getBoolean(STATE_PLAYER_PLAY_WHEN_READY);
            Timber.d("onCreateView: resumewindow:" + resumeWindow);
            Timber.d("onCreateView: resumePosition:" + resumePosition);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.d("onDestroyView: ");
        unbinder.unbind();
    }
}
