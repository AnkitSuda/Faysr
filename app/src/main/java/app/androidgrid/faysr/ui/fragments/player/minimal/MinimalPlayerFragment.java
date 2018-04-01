package app.androidgrid.faysr.ui.fragments.player.minimal;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.res.Configuration;
import android.databinding.adapters.FrameLayoutBindingAdapter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.kabouzeid.appthemehelper.util.ATHUtil;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.kabouzeid.appthemehelper.util.MaterialValueHelper;
import com.kabouzeid.appthemehelper.util.TintHelper;

import app.androidgrid.faysr.R;
import app.androidgrid.faysr.helper.MusicPlayerRemote;
import app.androidgrid.faysr.helper.MusicProgressViewUpdateHelper;
import app.androidgrid.faysr.helper.PlayPauseButtonOnClickHandler;
import app.androidgrid.faysr.interfaces.MusicServiceEventListener;
import app.androidgrid.faysr.service.MusicService;
import app.androidgrid.faysr.ui.fragments.player.AbsPlayerFragment;
import app.androidgrid.faysr.ui.fragments.player.NowPlayingScreen;
import app.androidgrid.faysr.ui.fragments.player.PlayerAlbumCoverFragment;
import app.androidgrid.faysr.ui.fragments.player.flat.FlatPlayerAlbumCoverFragment;
import app.androidgrid.faysr.ui.fragments.player.old_card.OldCardPlayerFragment;
import app.androidgrid.faysr.ui.fragments.player.old_card.OldCardPlayerPlaybackControlsFragment;
import app.androidgrid.faysr.util.ViewUtil;
import app.androidgrid.faysr.views.PlayPauseDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ankit on 21/2/18.
 */

public class MinimalPlayerFragment extends AbsPlayerFragment implements FlatPlayerAlbumCoverFragment.Callbacks  {

    @BindView(R.id.player_play_pause_fab)
    FloatingActionButton playPauseFab;
    @BindView(R.id.player_prev_button)
    ImageButton prevButton;
    @BindView(R.id.player_next_button)
    ImageButton nextButton;
    @BindView(R.id.player_repeat_button)
    ImageButton repeatButton;
    @BindView(R.id.player_shuffle_button)
    ImageButton shuffleButton;
    @BindView(R.id.root)
    FrameLayout root;

    private int lastPlaybackControlsColor;
    private int lastDisabledPlaybackControlsColor;

    private PlayPauseDrawable playerFabPlayPauseDrawable;

    private int lastColor;

    private FlatPlayerAlbumCoverFragment playerAlbumCoverFragment;


    private Unbinder unbinder;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_minimal_player, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;    
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

   /*     *//*Hide status bar view for !full screen mode*//*
        if (PreferenceUtil.getInstance(getContext()).getFullScreenMode()) {
            view.findViewById(R.id.status_bar).setVisibility(View.GONE);
        }*/
        setUpSubFragments();
        setUpMusicControllers();
    }
    
    private void setUpSubFragments() {
        playerAlbumCoverFragment = (FlatPlayerAlbumCoverFragment) getChildFragmentManager().findFragmentById(R.id.player_album_cover_fragment);

        playerAlbumCoverFragment.setCallbacks(this);
    }

    @Override
    @ColorInt
    public int getPaletteColor() {
        return lastColor;
    }

    

    @Override
    public void onShow() {

    }

    @Override
    public void onHide() {

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        updatePlayPauseDrawableState(false);
        updateRepeatState();
        updateShuffleState();
    }

    @Override
    public void onShuffleModeChanged() {
        super.onShuffleModeChanged();
        updateShuffleState();
    }

    @Override
    public void onRepeatModeChanged() {
        super.onRepeatModeChanged();
        updateRepeatState();
    }

    @Override
    public void onColorChanged(int color) {
        animateColorChange(color);
        getCallbacks().onPaletteColorChanged();
    }

    @Override
    public void onPlayStateChanged() {
        super.onPlayStateChanged();
        updatePlayPauseDrawableState(true);
    }

    @Override
    public void onFavoriteToggled() {

    }

    @Override
    public void onToolbarToggled() {

    }

    private void animateColorChange(final int newColor) {
        lastColor = newColor;
        createDefaultColorChangeAnimatorSet(newColor).start();
        setDark(ColorUtil.isColorLight(newColor));
    }

    public void setDark(boolean dark) {
        if (dark) {
            lastPlaybackControlsColor = MaterialValueHelper.getSecondaryTextColor(getActivity(), true);
            lastDisabledPlaybackControlsColor = MaterialValueHelper.getSecondaryDisabledTextColor(getActivity(), true);
        } else {
            lastPlaybackControlsColor = MaterialValueHelper.getPrimaryTextColor(getActivity(), false);
            lastDisabledPlaybackControlsColor = MaterialValueHelper.getPrimaryDisabledTextColor(getActivity(), false);
        }

        updateRepeatState();
        updateShuffleState();
        updatePrevNextColor();
    }

    private void setUpPlayPauseFab() {
        final int fabColor = Color.WHITE;
        TintHelper.setTintAuto(playPauseFab, fabColor, true);

        playerFabPlayPauseDrawable = new PlayPauseDrawable(getActivity());

        playPauseFab.setImageDrawable(playerFabPlayPauseDrawable); // Note: set the drawable AFTER TintHelper.setTintAuto() was called
        playPauseFab.setColorFilter(MaterialValueHelper.getPrimaryTextColor(getContext(), ColorUtil.isColorLight(fabColor)), PorterDuff.Mode.SRC_IN);
        playPauseFab.setOnClickListener(new PlayPauseButtonOnClickHandler());
        playPauseFab.post(new Runnable() {
            @Override
            public void run() {
                if (playPauseFab != null) {
                    playPauseFab.setPivotX(playPauseFab.getWidth() / 2);
                    playPauseFab.setPivotY(playPauseFab.getHeight() / 2);
                }
            }
        });
    }

    protected void updatePlayPauseDrawableState(boolean animate) {
        if (MusicPlayerRemote.isPlaying()) {
            playerFabPlayPauseDrawable.setPause(animate);
        } else {
            playerFabPlayPauseDrawable.setPlay(animate);
        }
    }

    private void setUpMusicControllers() {
        setUpPlayPauseFab();
        setUpPrevNext();
        setUpRepeatButton();
        setUpShuffleButton();

    }

    private void setUpPrevNext() {
        updatePrevNextColor();
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayerRemote.playNextSong(getContext());
            }
        });
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayerRemote.back(getContext());
            }
        });
    }


    private void updatePrevNextColor() {
        nextButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN);
        prevButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN);
    }

    private void setUpShuffleButton() {
        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayerRemote.toggleShuffleMode();
            }
        });
    }

    private void updateShuffleState() {
        switch (MusicPlayerRemote.getShuffleMode()) {
            case MusicService.SHUFFLE_MODE_SHUFFLE:
                shuffleButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN);
                break;
            default:
                shuffleButton.setColorFilter(lastDisabledPlaybackControlsColor, PorterDuff.Mode.SRC_IN);
                break;
        }
    }

    private void setUpRepeatButton() {
        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayerRemote.cycleRepeatMode();
            }
        });
    }

    private void updateRepeatState() {
        switch (MusicPlayerRemote.getRepeatMode()) {
            case MusicService.REPEAT_MODE_NONE:
                repeatButton.setImageResource(R.drawable.ic_repeat_white_24dp);
                repeatButton.setColorFilter(lastDisabledPlaybackControlsColor, PorterDuff.Mode.SRC_IN);
                break;
            case MusicService.REPEAT_MODE_ALL:
                repeatButton.setImageResource(R.drawable.ic_repeat_white_24dp);
                repeatButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN);
                break;
            case MusicService.REPEAT_MODE_THIS:
                repeatButton.setImageResource(R.drawable.ic_repeat_one_white_24dp);
                repeatButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN);
                break;
        }
    }

    public void show() {
        playPauseFab.animate()
                .scaleX(1f)
                .scaleY(1f)
                .rotation(360f)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    public void hide() {
        if (playPauseFab != null) {
            playPauseFab.setScaleX(0f);
            playPauseFab.setScaleY(0f);
            playPauseFab.setRotation(0f);
        }
    }

    public Animator createDefaultColorChangeAnimatorSet(int newColor) {
        Animator rootAnimator = ViewUtil.createBackgroundColorTransition(root, ColorUtil.shiftColor(lastColor, 0.8f), ColorUtil.shiftColor(newColor, 0.8f));

        if (!ATHUtil.isWindowBackgroundDark(getActivity())) {
            int adjustedLastColor = ColorUtil.isColorLight(lastColor) ? ColorUtil.darkenColor(lastColor) : lastColor;
            int adjustedNewColor = ColorUtil.isColorLight(newColor) ? ColorUtil.darkenColor(newColor) : newColor;
        }

        rootAnimator.setDuration(ViewUtil.PHONOGRAPH_ANIM_TIME);
        return rootAnimator;
    }
}
