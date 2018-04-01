package app.androidgrid.faysr.ui.fragments.player.modern;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.heinrichreimersoftware.materialintro.util.AnimUtils;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.util.ATHUtil;
import com.kabouzeid.appthemehelper.util.ColorUtil;

import org.jaudiotagger.audio.AudioFileIO;

import java.io.File;

import app.androidgrid.faysr.R;
import app.androidgrid.faysr.adapter.ModernAlbumCoverPagerAdapter;
import app.androidgrid.faysr.glide.FaysrColoredTarget;
import app.androidgrid.faysr.glide.SongGlideRequest;
import app.androidgrid.faysr.helper.MusicPlayerRemote;
import app.androidgrid.faysr.helper.MusicProgressViewUpdateHelper;
import app.androidgrid.faysr.lastfm.rest.model.LastFmAlbum;
import app.androidgrid.faysr.loader.SongLoader;
import app.androidgrid.faysr.misc.SimpleAnimatorListener;
import app.androidgrid.faysr.model.lyrics.AbsSynchronizedLyrics;
import app.androidgrid.faysr.model.lyrics.Lyrics;
import app.androidgrid.faysr.service.MusicService;
import app.androidgrid.faysr.ui.fragments.AbsMusicServiceFragment;
import app.androidgrid.faysr.ui.fragments.player.PlayerAlbumCoverFragment;
import app.androidgrid.faysr.util.FileUtil;
import app.androidgrid.faysr.util.MusicUtil;
import app.androidgrid.faysr.util.PreferenceUtil;
import app.androidgrid.faysr.util.ViewUtil;
import app.androidgrid.faysr.views.PlayerVisualizerView;
import app.androidgrid.faysr.visualizer.view.BarVisualizer;
import app.androidgrid.faysr.visualizer.view.CircleBarVisualizer;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import eu.gsottbauer.equalizerview.EqualizerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ModernPlayerAlbumCoverFragment extends AbsMusicServiceFragment implements ViewPager.OnPageChangeListener, MusicProgressViewUpdateHelper.Callback {
    public static final String TAG = PlayerAlbumCoverFragment.class.getSimpleName();

    public static final int VISIBILITY_ANIM_DURATION = 300;

    private Unbinder unbinder;

    @BindView(R.id.modern_player_placeholder_art)
    ImageView albumArt;
    public ImageView artShadow;
    public ImageView artShadowSecond;
    public ImageView darkOverlay;
    @BindView(R.id.modern_player_album_cover_viewpager)
    ViewPager viewPager;
    @BindView(R.id.player_favorite_icon)
    ImageView favoriteIcon;
    @BindView(R.id.player_lyrics)
    FrameLayout lyricsLayout;
    @BindView(R.id.player_lyrics_line1)
    TextView lyricsLine1;
    @BindView(R.id.player_lyrics_line2)
    TextView lyricsLine2;



    private Animation ROTATE_ANIM;

    private Callbacks callbacks;
    private int currentPosition;

    private Lyrics lyrics;
    private MusicProgressViewUpdateHelper progressViewUpdateHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modern_player_album_cover, container, false);
        unbinder = ButterKnife.bind(this, view);
        artShadow = view.findViewById(R.id.modern_player_art_shadow);
        artShadowSecond = view.findViewById(R.id.modern_player_art_shadow_second);
        darkOverlay = view.findViewById(R.id.modern_player_tint_overlay);
        ROTATE_ANIM = AnimationUtils.loadAnimation(getContext(), R.anim.album_rotate);



        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    if (callbacks != null) {
                        callbacks.onToolbarToggled();
                        return true;
                    }
                    return super.onSingleTapConfirmed(e);
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
        progressViewUpdateHelper = new MusicProgressViewUpdateHelper(this, 500, 1000);
        progressViewUpdateHelper.start();
        if (ModernPlayerFragment.toolbar != null)
        if (ModernPlayerFragment.toolbar.getVisibility() == View.GONE) {
            artShadowSecond.setVisibility(View.GONE);
        } else {
            artShadowSecond.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onPlayStateChanged() {
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewPager.removeOnPageChangeListener(this);
        progressViewUpdateHelper.stop();
        unbinder.unbind();
    }

    @Override
    public void onServiceConnected() {
        //toggleWave();
        updateAlbumArt();
        updatePlayingQueue();
    }

    @Override
    public void onPlayingMetaChanged() {
        viewPager.setCurrentItem(MusicPlayerRemote.getPosition());
        updateAlbumArt();
    }

    @Override
    public void onQueueChanged() {
        updatePlayingQueue();
    }



    private void updatePlayingQueue() {
        viewPager.setAdapter(new ModernAlbumCoverPagerAdapter(getFragmentManager(), MusicPlayerRemote.getPlayingQueue()));
        viewPager.setCurrentItem(MusicPlayerRemote.getPosition());
        updateAlbumArt();
        onPageSelected(MusicPlayerRemote.getPosition());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        currentPosition = position;
        ((ModernAlbumCoverPagerAdapter) viewPager.getAdapter()).receiveColor(colorReceiver, position);
        if (position != MusicPlayerRemote.getPosition()) {
            MusicPlayerRemote.playSongAt(position);
        }
    }



    private ModernAlbumCoverPagerAdapter.ModernAlbumCoverFragment.ColorReceiver colorReceiver = new ModernAlbumCoverPagerAdapter.ModernAlbumCoverFragment.ColorReceiver() {
        @Override
        public void onColorReady(int color, int requestCode) {
            if (currentPosition == requestCode) {
                notifyColorChange(color);
            }
        }
    };

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public void updateAlbumArt() {
        SongGlideRequest.Builder.from(Glide.with(this), MusicPlayerRemote.getCurrentSong())
                .checkIgnoreMediaStore(getActivity())
                .generatePalette(getActivity()).build()
                //.centerCrop()
                //.animate(android.R.anim.fade_in)
                .into(new FaysrColoredTarget(albumArt) {
                    @Override
                    public void onColorReady(int color) {
                        //updateAlbumArtShadow(color);
                    }
                });
    }

    public void updateAlbumArtShadow(int color) {
        artShadow.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        darkOverlay.setColorFilter(ColorUtil.lightenColor(color), PorterDuff.Mode.SRC_IN);
    }


    public AnimatorSet createShadowColorChangeAnimatorSet(int newColor) {
        Animator backgroundAnimator = ViewUtil.createBackgroundColorTransition(artShadow, ModernPlayerFragment.lastColor, newColor);
        Animator statusBarAnimator = ViewUtil.createBackgroundColorTransition(darkOverlay, ModernPlayerFragment.lastColor, newColor);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(backgroundAnimator, statusBarAnimator);

        if (!ATHUtil.isWindowBackgroundDark(getActivity())) {
            int adjustedLastColor = ColorUtil.isColorLight(ModernPlayerFragment.lastColor) ? ColorUtil.darkenColor(ModernPlayerFragment.lastColor) : ModernPlayerFragment.lastColor;
            int adjustedNewColor = ColorUtil.isColorLight(newColor) ? ColorUtil.darkenColor(newColor) : newColor;
        }

        animatorSet.setDuration(ViewUtil.PHONOGRAPH_ANIM_TIME);
        return animatorSet;
    }

    public void animateColorChange(int color) {
        createShadowColorChangeAnimatorSet(color).start();
    }


    public void showHeartAnimation() {
        favoriteIcon.clearAnimation();

        favoriteIcon.setAlpha(0f);
        favoriteIcon.setScaleX(0f);
        favoriteIcon.setScaleY(0f);
        favoriteIcon.setVisibility(View.VISIBLE);
        favoriteIcon.setPivotX(favoriteIcon.getWidth() / 2);
        favoriteIcon.setPivotY(favoriteIcon.getHeight() / 2);

        favoriteIcon.animate()
                .setDuration(ViewUtil.PHONOGRAPH_ANIM_TIME / 2)
                .setInterpolator(new DecelerateInterpolator())
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setListener(new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        favoriteIcon.setVisibility(View.INVISIBLE);
                    }
                })
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        favoriteIcon.animate()
                                .setDuration(ViewUtil.PHONOGRAPH_ANIM_TIME / 2)
                                .setInterpolator(new AccelerateInterpolator())
                                .scaleX(0f)
                                .scaleY(0f)
                                .alpha(0f)
                                .start();
                    }
                })
                .start();
    }

    private boolean isLyricsLayoutVisible() {
        return lyrics != null && lyrics.isSynchronized() && lyrics.isValid() && PreferenceUtil.getInstance(getActivity()).synchronizedLyricsShow();
    }

    private boolean isLyricsLayoutBound() {
        return lyricsLayout != null && lyricsLine1 != null && lyricsLine2 != null;
    }

    private void hideLyricsLayout() {
        lyricsLayout.animate().alpha(0f).setDuration(PlayerAlbumCoverFragment.VISIBILITY_ANIM_DURATION).withEndAction(new Runnable() {
            @Override
            public void run() {
                if (!isLyricsLayoutBound()) return;
                lyricsLayout.setVisibility(View.GONE);
                lyricsLine1.setText(null);
                lyricsLine2.setText(null);
            }
        });
    }

    public void setLyrics(Lyrics l) {
        lyrics = l;

        if (!isLyricsLayoutBound()) return;

        if (!isLyricsLayoutVisible()) {
            hideLyricsLayout();
            return;
        }

        lyricsLine1.setText(null);
        lyricsLine2.setText(null);

        lyricsLayout.setVisibility(View.VISIBLE);
        lyricsLayout.animate().alpha(1f).setDuration(PlayerAlbumCoverFragment.VISIBILITY_ANIM_DURATION);
    }

    private void notifyColorChange(int color) {
        if (callbacks != null) callbacks.onColorChanged(color);
    }

    public void setCallbacks(Callbacks listener) {
        callbacks = listener;
    }

    @Override
    public void onUpdateProgressViews(int progress, int total) {
        if (!isLyricsLayoutBound()) return;

        if (!isLyricsLayoutVisible()) {
            hideLyricsLayout();
            return;
        }


        if (!(lyrics instanceof AbsSynchronizedLyrics)) return;
        AbsSynchronizedLyrics synchronizedLyrics = (AbsSynchronizedLyrics) lyrics;

        lyricsLayout.setVisibility(View.VISIBLE);
        lyricsLayout.setAlpha(1f);

        String oldLine = lyricsLine2.getText().toString();
        String line = synchronizedLyrics.getLine(progress);

        if (!oldLine.equals(line) || oldLine.isEmpty()) {
            lyricsLine1.setText(oldLine);
            lyricsLine2.setText(line);

            lyricsLine1.setVisibility(View.VISIBLE);
            lyricsLine2.setVisibility(View.VISIBLE);

            lyricsLine2.measure(View.MeasureSpec.makeMeasureSpec(lyricsLine2.getMeasuredWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.UNSPECIFIED);
            int h = lyricsLine2.getMeasuredHeight();

            lyricsLine1.setAlpha(1f);
            lyricsLine1.setTranslationY(0f);
            lyricsLine1.animate().alpha(0f).translationY(-h).setDuration(PlayerAlbumCoverFragment.VISIBILITY_ANIM_DURATION);

            lyricsLine2.setAlpha(0f);
            lyricsLine2.setTranslationY(h);
            lyricsLine2.animate().alpha(1f).translationY(0f).setDuration(PlayerAlbumCoverFragment.VISIBILITY_ANIM_DURATION);
        }
    }

    public interface Callbacks {
        void onColorChanged(int color);

        void onFavoriteToggled();

        void onToolbarToggled();
    }

}
