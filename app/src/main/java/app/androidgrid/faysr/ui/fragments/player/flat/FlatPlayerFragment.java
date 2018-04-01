package app.androidgrid.faysr.ui.fragments.player.flat;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.kabouzeid.appthemehelper.util.ATHUtil;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.kabouzeid.appthemehelper.util.MaterialValueHelper;
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper;
import com.kabouzeid.appthemehelper.util.ViewUtil;

import app.androidgrid.faysr.DrawableGradient;
import app.androidgrid.faysr.R;
import app.androidgrid.faysr.dialogs.LyricsDialog;
import app.androidgrid.faysr.helper.MusicPlayerRemote;
import app.androidgrid.faysr.model.Song;
import app.androidgrid.faysr.model.lyrics.Lyrics;
import app.androidgrid.faysr.ui.fragments.player.AbsPlayerFragment;
import app.androidgrid.faysr.util.MusicUtil;
import app.androidgrid.faysr.util.NavigationUtil;
import app.androidgrid.faysr.util.PreferenceUtil;
import app.androidgrid.faysr.util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class FlatPlayerFragment extends AbsPlayerFragment implements FlatPlayerAlbumCoverFragment.Callbacks {

    @BindView(R.id.player_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.gradient_background)
    View colorBackground;
    @BindView(R.id.toolbar_container)
    FrameLayout toolbarContainer;
    private AsyncTask updateIsFavoriteTask;
    private AsyncTask updateLyricsAsyncTask;
    private FlatPlayerAlbumCoverFragment playerAlbumCoverFragment;
    private Unbinder unbinder;
    private ValueAnimator valueAnimator;
    private FlatPlayerPlaybackControlsFragment mFlatPlayerPlaybackControlsFragment;
    private int lastColor;
    private int iconColor;

    private void setUpSubFragments() {
        mFlatPlayerPlaybackControlsFragment = (FlatPlayerPlaybackControlsFragment) getChildFragmentManager().findFragmentById(R.id.playback_controls_fragment);

        playerAlbumCoverFragment = (FlatPlayerAlbumCoverFragment) getChildFragmentManager().findFragmentById(R.id.player_album_cover_fragment);
        playerAlbumCoverFragment.setCallbacks(this);
    }

    private void setUpPlayerToolbar() {
        mToolbar.inflateMenu(R.menu.menu_player);
        mToolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
        mToolbar.setOnMenuItemClickListener(this);
    }

    @Override
    public void onServiceConnected() {
        updateIsFavorite();
        updateLyrics();
    }

    private void colorize(int i) {
        if (valueAnimator != null) valueAnimator.cancel();

        valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), lastColor, i);
        valueAnimator.addUpdateListener(animation -> {
            GradientDrawable drawable = new DrawableGradient(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{(int) animation.getAnimatedValue(), android.R.color.transparent}, 0);
            if (colorBackground != null) {
                colorBackground.setBackground(drawable);
            }
        });
        valueAnimator.setDuration(app.androidgrid.faysr.util.ViewUtil.PHONOGRAPH_ANIM_TIME).start();
    }

    @Override
    public void onPlayingMetaChanged() {
        updateIsFavorite();
        updateLyrics();
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flat_player, container, false);
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
        setUpPlayerToolbar();
        setUpSubFragments();

    }

    @Override
    public int getPaletteColor() {
        return lastColor;
    }

    @Override
    public void onShow() {
        mFlatPlayerPlaybackControlsFragment.show();
    }

    @Override
    public void onHide() {
        mFlatPlayerPlaybackControlsFragment.hide();
        onBackPressed();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onColorChanged(int color) {
        mFlatPlayerPlaybackControlsFragment.setDark(color);
        getCallbacks().onPaletteColorChanged();
        lastColor = color;


        boolean isLight = ColorUtil.isColorLight(color);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(mToolbar);
        }
        iconColor =/* PreferenceUtil.getInstance(getContext()).getAdaptiveColor() ?*/
                MaterialValueHelper.getPrimaryTextColor(getContext(), isLight);
                /*ATHUtil.resolveColor(getContext(), R.attr.iconColor);*/

        //ToolbarContentTintHelper.colorizeToolbar(mToolbar, iconColor, getActivity());

        /*if (PreferenceUtil.getInstance(getContext()).getAdaptiveColor()) */colorize(color);
    }

    @Override
    public void onFavoriteToggled() {
        toggleFavorite(MusicPlayerRemote.getCurrentSong());
    }

    @Override
    public void onToolbarToggled() {
        //Toggle hiding toolbar for effect
        //toggleToolbar(toolbarContainer);
    }

    @Override
    protected void toggleFavorite(Song song) {
        super.toggleFavorite(song);
        if (song.id == MusicPlayerRemote.getCurrentSong().id) {
            if (MusicUtil.isFavorite(getActivity(), song)) {
                playerAlbumCoverFragment.showHeartAnimation();
            }
            updateIsFavorite();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void updateLyrics() {
        if (updateLyricsAsyncTask != null) updateLyricsAsyncTask.cancel(false);
        final Song song = MusicPlayerRemote.getCurrentSong();
        updateLyricsAsyncTask = new AsyncTask<Void, Void, Lyrics>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mToolbar.getMenu().removeItem(R.id.action_show_lyrics);
            }

            @Override
            protected Lyrics doInBackground(Void... params) {
                String data = MusicUtil.getLyrics(song);
                if (TextUtils.isEmpty(data)) {
                    return null;
                }
                return Lyrics.parse(song, data);

            }

            @Override
            protected void onPostExecute(Lyrics l) {
                mLyrics = l;
                playerAlbumCoverFragment.setLyrics(mLyrics);
                if (mLyrics == null) {
                    if (mToolbar != null) {
                        mToolbar.getMenu().removeItem(R.id.action_show_lyrics);
                    }
                } else {
                    Activity activity = getActivity();
                    if (mToolbar != null && activity != null)
                        if (mToolbar.getMenu().findItem(R.id.action_show_lyrics) == null) {
                            int color = ToolbarContentTintHelper.toolbarContentColor(activity, Color.TRANSPARENT);
                            Drawable drawable = Util.getTintedVectorDrawable(activity, R.drawable.ic_comment_text_outline_white_24dp, color);
                            mToolbar.getMenu()
                                    .add(Menu.NONE, R.id.action_show_lyrics, Menu.NONE, R.string.action_show_lyrics)
                                    .setIcon(drawable)
                                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                        }
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void updateIsFavorite() {
        if (updateIsFavoriteTask != null) updateIsFavoriteTask.cancel(false);
        updateIsFavoriteTask = new AsyncTask<Song, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Song... params) {
                Activity activity = getActivity();
                if (activity != null) {
                    return MusicUtil.isFavorite(getActivity(), params[0]);
                } else {
                    cancel(false);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Boolean isFavorite) {
                Activity activity = getActivity();
                if (activity != null) {
                    int res = isFavorite ? R.drawable.ic_favorite_white_24dp : R.drawable.ic_favorite_border_white_24dp;
                    Drawable drawable = Util.getTintedVectorDrawable(activity, res, iconColor);
                    mToolbar.getMenu().findItem(R.id.action_toggle_favorite)
                            .setIcon(drawable)
                            .setTitle(isFavorite ? getString(R.string.action_remove_from_favorites) : getString(R.string.action_add_to_favorites));
                }
            }
        }.execute(MusicPlayerRemote.getCurrentSong());
    }


    @Override
    public void onDestroyView() {
        if (updateLyricsAsyncTask != null && !updateLyricsAsyncTask.isCancelled()) {
            updateLyricsAsyncTask.cancel(true);
        }
        if (updateIsFavoriteTask != null && !updateIsFavoriteTask.isCancelled()) {
            updateIsFavoriteTask.cancel(true);
        }
        super.onDestroyView();
        unbinder.unbind();
    }
}