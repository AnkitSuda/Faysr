package app.androidgrid.faysr.ui.fragments.player.modern;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.color.MaterialColor;
import com.kabouzeid.appthemehelper.common.ATHToolbarActivity;
import com.kabouzeid.appthemehelper.util.ATHUtil;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import app.androidgrid.faysr.R;
import app.androidgrid.faysr.adapter.ModernAlbumCoverPagerAdapter;
import app.androidgrid.faysr.adapter.base.MediaEntryViewHolder;
import app.androidgrid.faysr.adapter.song.PlayingQueueAdapter;
import app.androidgrid.faysr.dialogs.LyricsDialog;
import app.androidgrid.faysr.dialogs.SongShareDialog;
import app.androidgrid.faysr.helper.MusicPlayerRemote;
import app.androidgrid.faysr.helper.menu.SongMenuHelper;
import app.androidgrid.faysr.model.Song;
import app.androidgrid.faysr.model.lyrics.Lyrics;
import app.androidgrid.faysr.ui.activities.base.AbsSlidingMusicPanelActivity;
import app.androidgrid.faysr.ui.fragments.player.AbsPlayerFragment;
import app.androidgrid.faysr.ui.fragments.player.PlayerAlbumCoverFragment;
import app.androidgrid.faysr.ui.fragments.player.modern.ModernPlayerFragment;
import app.androidgrid.faysr.ui.fragments.player.modern.ModernPlayerPlaybackControlsFragment;
import app.androidgrid.faysr.util.MusicUtil;
import app.androidgrid.faysr.util.NavigationUtil;
import app.androidgrid.faysr.util.Util;
import app.androidgrid.faysr.util.ViewUtil;
import app.androidgrid.faysr.views.WidthFitSquareLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ModernPlayerFragment extends AbsPlayerFragment implements ModernPlayerAlbumCoverFragment.Callbacks {

    public static final String TAG = ModernPlayerFragment.class.getSimpleName();

    private Unbinder unbinder;

    @BindView(R.id.player_status_bar)
    View playerStatusBar;
    @Nullable
    @BindView(R.id.toolbar_container)
    FrameLayout toolbarContainer;
    @BindView(R.id.root)
    FrameLayout root;
    //@BindView(R.id.player_toolbar)
    public static Toolbar toolbar;

    public static int lastColor;

    private ModernPlayerPlaybackControlsFragment playbackControlsFragment;
    private ModernPlayerAlbumCoverFragment playerAlbumCoverFragment;

    private AsyncTask updateIsFavoriteTask;
    private AsyncTask updateLyricsAsyncTask;

    private Lyrics lyrics;

    private ModernPlayerFragment.Impl impl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        if (Util.isLandscape(getResources())) {
//            impl = new ModernPlayerFragment.LandscapeImpl(this);
//        } else {
//            impl = new ModernPlayerFragment.PortraitImpl(this);
//        }

        View view = inflater.inflate(R.layout.fragment_modern_player, container, false);
        unbinder = ButterKnife.bind(this, view);
        toolbar = view.findViewById(R.id.player_toolbar);
        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //impl.init();

        setUpPlayerToolbar();
        setUpSubFragments();



        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                //impl.setUpPanelAndAlbumCoverHeight();
            }
        });
    }




    @Override
    public void onDestroyView() {


        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkToggleToolbar(toolbarContainer);
        //toolbar.setBackgroundColor(lastColor);
    }

    @Override
    public void onServiceConnected() {
        updateQueue();
        updateCurrentSong();
        updateIsFavorite();
        updateLyrics();
    }

    @Override
    public void onPlayingMetaChanged() {
        updateCurrentSong();
        updateIsFavorite();
        updateQueuePosition();
        updateLyrics();
    }

    @Override
    public void onQueueChanged() {
        updateQueue();
    }

    @Override
    public void onMediaStoreChanged() {
        updateQueue();
    }

    private void updateQueue() {

    }

    private void updateQueuePosition() {

    }

    @SuppressWarnings("ConstantConditions")
    private void updateCurrentSong() {
        //impl.updateCurrentSong(MusicPlayerRemote.getCurrentSong());
    }

    private void setUpSubFragments() {
        playbackControlsFragment = (ModernPlayerPlaybackControlsFragment) getChildFragmentManager().findFragmentById(R.id. modern_playback_controls_fragment);
        playerAlbumCoverFragment = (ModernPlayerAlbumCoverFragment) getChildFragmentManager().findFragmentById(R.id.player_album_cover_fragment);

        playerAlbumCoverFragment.setCallbacks(this);
    }

    private void setUpPlayerToolbar() {
        toolbar.inflateMenu(R.menu.menu_player);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        toolbar.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_lyrics:
                if (lyrics != null)
                    LyricsDialog.create(lyrics).show(getFragmentManager(), "LYRICS");
                return true;
            case R.id.action_open_playing_queue:
                NavigationUtil.goToPlayingQueue(getActivity());
                return true;
        }
        return super.onMenuItemClick(item);
    }



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
                    int color = ToolbarContentTintHelper.toolbarContentColor(activity, Color.TRANSPARENT);
                    Drawable drawable = Util.getTintedVectorDrawable(activity, res, color);
                    toolbar.getMenu().findItem(R.id.action_toggle_favorite)
                            .setIcon(drawable)
                            .setTitle(isFavorite ? getString(R.string.action_remove_from_favorites) : getString(R.string.action_add_to_favorites));
                }
            }
        }.execute(MusicPlayerRemote.getCurrentSong());
    }

    private void updateLyrics() {
        if (updateLyricsAsyncTask != null) updateLyricsAsyncTask.cancel(false);
        final Song song = MusicPlayerRemote.getCurrentSong();
        updateLyricsAsyncTask = new AsyncTask<Void, Void, Lyrics>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                lyrics = null;
                playerAlbumCoverFragment.setLyrics(null);

                toolbar.getMenu().removeItem(R.id.action_show_lyrics);
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
                lyrics = l;
                playerAlbumCoverFragment.setLyrics(lyrics);
                if (lyrics == null) {
                    if (toolbar != null) {
                        toolbar.getMenu().removeItem(R.id.action_show_lyrics);
                    }
                } else {
                    Activity activity = getActivity();
                    if (toolbar != null && activity != null)
                        if (toolbar.getMenu().findItem(R.id.action_show_lyrics) == null) {
                            int color = ToolbarContentTintHelper.toolbarContentColor(activity, Color.TRANSPARENT);
                            Drawable drawable = Util.getTintedVectorDrawable(activity, R.drawable.ic_comment_text_outline_white_24dp, color);
                            toolbar.getMenu()
                                    .add(Menu.NONE, R.id.action_show_lyrics, Menu.NONE, R.string.action_show_lyrics)
                                    .setIcon(drawable)
                                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                        }
                }
            }

            @Override
            protected void onCancelled(Lyrics s) {
                onPostExecute(null);
            }
        }.execute();
    }

    @Override
    @ColorInt
    public int getPaletteColor() {
        return lastColor;
    }

    private void animateColorChange(final int newColor) {
        //impl.animateColorChange(newColor);
        createDefaultColorChangeAnimatorSet(newColor).start();
        if (getActivity() == null) Log.i("modern_on_color_change", "null activity");
        lastColor = newColor;
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

    @Override
    public void onShow() {
        playbackControlsFragment.show();
    }

    @Override
    public void onHide() {
        playbackControlsFragment.hide();
        onBackPressed();
    }

//    @Override
//    public boolean onBackPressed() {
//        boolean wasExpanded = false;
//        if (slidingUpPanelLayout != null) {
//            wasExpanded = slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED;
//            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//        }
//
//        return wasExpanded;
//    }


    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onColorChanged(int color) {
        animateColorChange(color);
        //playerAlbumCoverFragment.updateAlbumArtShadow(color);
        playbackControlsFragment.setDark(ColorUtil.isColorLight(color));
        playbackControlsFragment.updateVisualizerColor(color);
        //ToolbarContentTintHelper.handleOnCreateOptionsMenu(getContext(), toolbar, toolbar.getMenu(), ATHToolbarActivity.getToolbarBackgroundColor(toolbar));
        int cc = color;
        if (ColorUtil.isColorLight(cc)) {
            cc = Color.BLACK;
        } else {
            cc = Color.WHITE;
        }
        playbackControlsFragment.setUpPlayPauseFab(cc);
        getCallbacks().onPaletteColorChanged();

    }

    @Override
    public void onFavoriteToggled() {
        toggleFavorite(MusicPlayerRemote.getCurrentSong());
    }

    @Override
    public void onToolbarToggled() {
        toggleToolbar(toolbarContainer);
        if (playerAlbumCoverFragment.artShadowSecond != null)
        playerAlbumCoverFragment.artShadowSecond.setVisibility(isToolbarShown() ? View.VISIBLE : View.GONE);
    }

//    @Override
//    public void onPanelSlide(View view, float slide) {
//    }
//
//    @Override
//    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
//        switch (newState) {
//            case COLLAPSED:
//                onPanelCollapsed(panel);
//                break;
//            case ANCHORED:
//                //noinspection ConstantConditions
//                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED); // this fixes a bug where the panel would get stuck for some reason
//                break;
//        }
//    }
//
//    public void onPanelCollapsed(View panel) {
//
//    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        playerAlbumCoverFragment.artShadowSecond.setMinimumHeight(toolbar.getHeight());
        /*if (playerAlbumCoverFragment.artShadowSecond != null)
            playerAlbumCoverFragment.artShadowSecond.setVisibility(isToolbarShown() ? View.VISIBLE : View.GONE);*/
    }

    public AnimatorSet createDefaultColorChangeAnimatorSet(int newColor) {
        Animator rootAnimator = ViewUtil.createBackgroundColorTransition(root, ColorUtil.shiftColor(lastColor, 0.8f), ColorUtil.shiftColor(newColor, 0.8f));
        Animator backgroundAnimator = ViewUtil.createBackgroundColorTransition(playbackControlsFragment.getView(), lastColor, newColor);
        Animator statusBarAnimator = ViewUtil.createBackgroundColorTransition(playerStatusBar, lastColor, newColor);
        //Animator toolbarAnimator = ViewUtil.createBackgroundColorTransition(toolbar, ColorUtil.shiftColor(lastColor, 0.8F), ColorUtil.shiftColor(newColor, 0.8F));
        Animator shadowAnimator = ViewUtil.createColorFilterTransition(playerAlbumCoverFragment.artShadow, lastColor, newColor);
        Animator sShadowAnimator = ViewUtil.createColorFilterTransition(playerAlbumCoverFragment.artShadowSecond, ColorUtil.shiftColor(lastColor, 0.8F), ColorUtil.shiftColor(newColor, 0.8F));
        Animator overlayAnimator = ViewUtil.createColorFilterTransition(playerAlbumCoverFragment.darkOverlay, ColorUtil.lightenColor(lastColor), ColorUtil.lightenColor(newColor));
        Animator visualizerAnimator = ViewUtil.createColorFilterTransition(playbackControlsFragment.visualizer, ColorUtil.shiftColor(lastColor, 0.8f), ColorUtil.shiftColor(newColor, 0.8f));

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(rootAnimator, backgroundAnimator, statusBarAnimator, visualizerAnimator, shadowAnimator, getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? sShadowAnimator : shadowAnimator, overlayAnimator);

        if (!ATHUtil.isWindowBackgroundDark(getActivity())) {
            int adjustedLastColor = ColorUtil.isColorLight(lastColor) ? ColorUtil.darkenColor(lastColor) : lastColor;
            int adjustedNewColor = ColorUtil.isColorLight(newColor) ? ColorUtil.darkenColor(newColor) : newColor;
        }

        animatorSet.setDuration(ViewUtil.PHONOGRAPH_ANIM_TIME);
        return animatorSet;
    }

    interface Impl {
        void init();

        void updateCurrentSong(Song song);

        void animateColorChange(final int newColor);

        void setUpPanelAndAlbumCoverHeight();
    }

    private static abstract class BaseImpl implements ModernPlayerFragment.Impl {
        protected ModernPlayerFragment fragment;

        public BaseImpl(ModernPlayerFragment fragment) {
            this.fragment = fragment;
        }

        public AnimatorSet createDefaultColorChangeAnimatorSet(int newColor) {
            Animator backgroundAnimator = ViewUtil.createBackgroundColorTransition(fragment.playbackControlsFragment.getView(), fragment.lastColor, newColor);
            Animator statusBarAnimator = ViewUtil.createBackgroundColorTransition(fragment.playerStatusBar, fragment.lastColor, newColor);
            Animator toolbarAnimator = ViewUtil.createBackgroundColorTransition(fragment.toolbar, fragment.lastColor, newColor);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(backgroundAnimator, statusBarAnimator, toolbarAnimator);

            if (!ATHUtil.isWindowBackgroundDark(fragment.getActivity())) {
                int adjustedLastColor = ColorUtil.isColorLight(fragment.lastColor) ? ColorUtil.darkenColor(fragment.lastColor) : fragment.lastColor;
                int adjustedNewColor = ColorUtil.isColorLight(newColor) ? ColorUtil.darkenColor(newColor) : newColor;
            }

            animatorSet.setDuration(ViewUtil.PHONOGRAPH_ANIM_TIME);
            return animatorSet;
        }

        @Override
        public void animateColorChange(int newColor) {
            if (ATHUtil.isWindowBackgroundDark(fragment.getActivity())) {
            }
        }
    }
//
//    @SuppressWarnings("ConstantConditions")
//    private static class PortraitImpl extends ModernPlayerFragment.BaseImpl {
//        //MediaEntryViewHolder currentSongViewHolder;
//        //Song currentSong = Song.EMPTY_SONG;
//
//        public PortraitImpl(ModernPlayerFragment fragment) {
//            super(fragment);
//        }
//
//        @Override
//        public void init() {
//            currentSongViewHolder = new MediaEntryViewHolder(fragment.getView().findViewById(R.id.current_song));
//
//            currentSongViewHolder.separator.setVisibility(View.VISIBLE);
//            currentSongViewHolder.shortSeparator.setVisibility(View.GONE);
//            //currentSongViewHolder.image.setScaleType(ImageView.ScaleType.CENTER);
//            currentSongViewHolder.image.setColorFilter(ATHUtil.resolveColor(fragment.getActivity(), R.attr.iconColor, ThemeStore.textColorSecondary(fragment.getActivity())), PorterDuff.Mode.SRC_IN);
//            currentSongViewHolder.image.setImageResource(R.drawable.ic_volume_up_white_24dp);
//            currentSongViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // toggle the panel
//                    if (fragment.slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
//                        fragment.slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
//                    } else if (fragment.slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
//                        fragment.slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//                    }
//                }
//            });
//            currentSongViewHolder.menu.setOnClickListener(new SongMenuHelper.OnClickSongMenu((AppCompatActivity) fragment.getActivity()) {
//                @Override
//                public Song getSong() {
//                    return currentSong;
//                }
//
//                public int getMenuRes() {
//                    return R.menu.menu_item_playing_queue_song;
//                }
//
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//                    switch (item.getItemId()) {
//                        case R.id.action_remove_from_playing_queue:
//                            MusicPlayerRemote.removeFromQueue(MusicPlayerRemote.getPosition());
//                            return true;
//                        case R.id.action_share:
//                            SongShareDialog.create(getSong()).show(fragment.getFragmentManager(), "SONG_SHARE_DIALOG");
//                            return true;
//                    }
//                    return super.onMenuItemClick(item);
//                }
//            });
//        }
//
//        @Override
//        public void setUpPanelAndAlbumCoverHeight() {
//            WidthFitSquareLayout albumCoverContainer = (WidthFitSquareLayout) fragment.getView().findViewById(R.id.album_cover_container);
//
//            final int availablePanelHeight = fragment.slidingUpPanelLayout.getHeight() - fragment.getView().findViewById(R.id.player_content).getHeight();
//            final int minPanelHeight = (int) ViewUtil.convertDpToPixel(8 + 72 + 24, fragment.getResources()) + fragment.getResources().getDimensionPixelSize(R.dimen.progress_container_height) + fragment.getResources().getDimensionPixelSize(R.dimen.media_controller_container_height);
//            if (availablePanelHeight < minPanelHeight) {
//                albumCoverContainer.getLayoutParams().height = albumCoverContainer.getHeight() - (minPanelHeight - availablePanelHeight);
//                albumCoverContainer.forceSquare(false);
//            }
//            fragment.slidingUpPanelLayout.setPanelHeight(Math.max(minPanelHeight, availablePanelHeight));
//
//            ((AbsSlidingMusicPanelActivity) fragment.getActivity()).setAntiDragView(fragment.slidingUpPanelLayout.findViewById(R.id.player_panel));
//        }
//
//        @Override
//        public void updateCurrentSong(Song song) {
//            currentSong = song;
//            currentSongViewHolder.title.setText(song.title);
//            currentSongViewHolder.text.setText(song.artistName);
//        }
//
//        @Override
//        public void animateColorChange(int newColor) {
//            super.animateColorChange(newColor);
//            createDefaultColorChangeAnimatorSet(newColor).start();
//            ToolbarContentTintHelper.handleOnCreateOptionsMenu(fragment.getContext(), fragment.toolbar, fragment.toolbar.getMenu(), ATHToolbarActivity.getToolbarBackgroundColor(fragment.toolbar));
//        }
//    }
//
//    @SuppressWarnings("ConstantConditions")
//    private static class LandscapeImpl extends ModernPlayerFragment.BaseImpl {
//        public LandscapeImpl(ModernPlayerFragment fragment) {
//            super(fragment);
//        }
//
//        @Override
//        public void init() {
//        }
//
//        @Override
//        public void setUpPanelAndAlbumCoverHeight() {
//            ((AbsSlidingMusicPanelActivity) fragment.getActivity()).setAntiDragView(fragment.getView().findViewById(R.id.player_panel));
//        }
//
//        @Override
//        public void updateCurrentSong(Song song) {
//            fragment.toolbar.setTitle(song.title);
//            fragment.toolbar.setSubtitle(song.artistName);
//        }
//
//        @Override
//        public void animateColorChange(int newColor) {
//            super.animateColorChange(newColor);
//
//            AnimatorSet animatorSet = createDefaultColorChangeAnimatorSet(newColor);
//            animatorSet.play(ViewUtil.createBackgroundColorTransition(fragment.toolbar, fragment.lastColor, newColor));
//            animatorSet.start();
//        }
//    }
}
