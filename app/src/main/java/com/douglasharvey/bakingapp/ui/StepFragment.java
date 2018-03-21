package com.douglasharvey.bakingapp.ui;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.main_media_frame)
    FrameLayout mainMediaFrame;

    private boolean exoPlayerFullscreen = false;
    private FrameLayout fullScreenButton;
    private ImageView fullScreenIcon;
    private Dialog fullScreenDialog;
    private int resumeWindow;
    private long resumePosition;

    @SuppressWarnings("WeakerAccess")

    //  @BindView(R.id.player_view)

    PlayerView playerView;

    @SuppressWarnings("WeakerAccess")
    Unbinder unbinder;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_step_long_description)
    TextView tvStepLongDescription;

    private String videoUrl;
    private String description;

 //   private OnFragmentInteractionListener mListener;

    public StepFragment() {
        // Required empty public constructor
    }

    public static StepFragment newInstance(String videoUrl, String description) {
        StepFragment fragment = new StepFragment();
        Bundle args = new Bundle();
        args.putString(VIDEO_URL, videoUrl);
        args.putString(DESCRIPTION, description);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            videoUrl = getArguments().getString(VIDEO_URL);
            description = getArguments().getString(DESCRIPTION);
        }
        Timber.d("onCreate: ");

        if (savedInstanceState != null) {
            resumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            resumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
            exoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
            Timber.d("onCreate: resumewindow:"+resumeWindow);
            Timber.d("onCreate: resumePosition:"+resumePosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //todo not being called on rotation?
        Timber.d("onSaveInstanceState: resumewindow:"+resumeWindow);
        Timber.d("onSaveInstanceState: resumePosition:"+resumePosition);
        outState.putInt(STATE_RESUME_WINDOW, resumeWindow);
        outState.putLong(STATE_RESUME_POSITION, resumePosition);
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, exoPlayerFullscreen);

        super.onSaveInstanceState(outState);
    }


    @Override
    public void onResume() {
        super.onResume();
        tvStepLongDescription.setText(description);

        Timber.d("onResume:" + videoUrl + ":");

        if (playerView == null) {
            playerView = getView().findViewById(R.id.player_view);
            initFullscreenDialog();
            initFullscreenButton();
            Timber.d("onResume: dialog/full screenbutton initialized");
        }

        if (videoUrl == null)
            playerView.setVisibility(View.GONE); //todo problems here, filenotfoundexception . do we need to release / what other solution?
        else startVideoPlayer();

        if (exoPlayerFullscreen) {
            Timber.d("onResume: full screen true");
            ((ViewGroup) playerView.getParent()).removeView(playerView);
            fullScreenDialog.addContentView(playerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            fullScreenIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_fullscreen_skrink));
            fullScreenDialog.show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (playerView != null && playerView.getPlayer() != null) {
            resumeWindow = playerView.getPlayer().getCurrentWindowIndex();
            resumePosition = Math.max(0, playerView.getPlayer().getContentPosition());
            Timber.d("onPause: resumewindow:"+resumeWindow);
            Timber.d("onPause: resumePosition:"+resumePosition);
            playerView.getPlayer().release();
        }

        if (fullScreenDialog != null)
            fullScreenDialog.dismiss();
    }

    //TODO HOW TO MAKE THE VIDEO LOOK BETTER /

    private void startVideoPlayer() {
        MediaSource videoSource;//exoplayer

        Handler mainHandler = new Handler();

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
//todo check/clean up this code.

        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);
// 2. Create the player
        SimpleExoPlayer player =
                ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);

        Uri videoUri = Uri.parse(videoUrl);
        Timber.d("onActivityCreated: %s", videoUrl);
//todo fix lint warning
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(), Util.getUserAgent(getActivity(), getActivity().getApplication().getPackageName()));

        videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUri);


        playerView.setPlayer(player);
//TODO TRYING https://stackoverflow.com/questions/45481775/exoplayer-restore-state-when-resumed
        boolean haveResumePosition = resumeWindow != C.INDEX_UNSET; //TODO CHECK THIS

    //    if (haveResumePosition) {
//            playerView.getPlayer().seekTo(resumeWindow, resumePosition);
    //    }

        if (resumePosition != C.TIME_UNSET) player.seekTo(resumePosition);

        player.prepare(videoSource);
        player.setPlayWhenReady(true);
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
        return view;
    }

/*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
      */

/*
    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }
*/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
//TODO POSSIBLE TO REMOVE THIS?
 /*   public interface OnFragmentInteractionListener {
        @SuppressWarnings("EmptyMethod")
        void onFragmentInteraction(Uri uri);
    }
    */
}
