package com.douglasharvey.bakingapp.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.douglasharvey.bakingapp.R;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StepFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StepFragment extends Fragment {
    public static final String VIDEO_URL = "videoUrl";
    public static final String DESCRIPTION = "description";

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.player_view)
    PlayerView playerView;

    @SuppressWarnings("WeakerAccess")
    Unbinder unbinder;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_step_long_description)
    TextView tvStepLongDescription;

    // TODO: Rename and change types of parameters
    private String videoUrl;
    private String description;

    private OnFragmentInteractionListener mListener;

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
    }
//TODO HOW TO MAKE THE VIDEO LOOK BETTER /

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        MediaSource videoSource;
        super.onActivityCreated(savedInstanceState);
        tvStepLongDescription.setText(description);

        Timber.d("onActivityCreated:" + videoUrl + ":");

        if (videoUrl == null)
            playerView.setVisibility(View.GONE); //todo problems here, filenotfoundexception . do we need to release / what other solution?
        else startVideoPlayer();
    }

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
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        playerView.getPlayer().release();
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public interface OnFragmentInteractionListener {
        @SuppressWarnings("EmptyMethod")
        void onFragmentInteraction(Uri uri);
    }
}
