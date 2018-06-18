package app.androidgrid.faysr.ui.activities.base;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.LayoutRes;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.PathInterpolator;

import app.androidgrid.faysr.R;
import app.androidgrid.faysr.helper.MusicPlayerRemote;
import app.androidgrid.faysr.ui.fragments.player.AbsPlayerFragment;
import app.androidgrid.faysr.ui.fragments.player.MiniPlayerFragment;
import app.androidgrid.faysr.ui.fragments.player.NowPlayingScreen;
import app.androidgrid.faysr.ui.fragments.player.flat.FlatPlayerFragment;
import app.androidgrid.faysr.ui.fragments.player.minimal.MinimalPlayerFragment;
import app.androidgrid.faysr.ui.fragments.player.old_card.OldCardPlayerFragment;
import app.androidgrid.faysr.ui.fragments.player.old_flat.OldFlatPlayerFragment;
import app.androidgrid.faysr.ui.fragments.player.modern.ModernPlayerFragment;
import app.androidgrid.faysr.util.PreferenceUtil;
import app.androidgrid.faysr.util.ViewUtil;

import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import app.androidgrid.faysr.views.BottomNavigationViewEx;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Karim Abou Zeid (kabouzeid)
 *         <p/>
 *         Do not use {@link #setContentView(int)}. Instead wrap your layout with
 *         {@link #wrapSlidingMusicPanel(int)} first and then return it in {@link #createContentView()}
 */
public abstract class AbsSlidingMusicPanelActivity extends AbsMusicServiceActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        SlidingUpPanelLayout.PanelSlideListener, OldCardPlayerFragment.Callbacks {
    public static final String TAG = AbsSlidingMusicPanelActivity.class.getSimpleName();

    @BindView(R.id.bottom_navigation)
    BottomNavigationViewEx mBottomNavigationView;
    @BindView(R.id.sliding_layout)
    SlidingUpPanelLayout slidingUpPanelLayout;
    @BindView(R.id.root_layout)
    ViewGroup mViewGroup;

    private int navigationbarColor;
    private int taskColor;
    private boolean lightStatusbar;

    private NowPlayingScreen currentNowPlayingScreen;
    private AbsPlayerFragment playerFragment;
    private MiniPlayerFragment miniPlayerFragment;

    private ValueAnimator navigationBarColorAnimator;
    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(createContentView());
        ButterKnife.bind(this);

        currentNowPlayingScreen = PreferenceUtil.getInstance(this).getNowPlayingScreen();
        Fragment fragment; // must implement AbsPlayerFragment
        switch (currentNowPlayingScreen) {
            case MODERN:
                fragment = new ModernPlayerFragment();
                break;
            case FLAT:
                fragment = new FlatPlayerFragment();
                break;
            case Minimal:
                fragment = new MinimalPlayerFragment();
                break;
            case OLD_CARD:
                fragment = new OldCardPlayerFragment();
                break;
            case OLD_FLAT:
                fragment = new OldFlatPlayerFragment();
                break;
            default:
                fragment = new ModernPlayerFragment();
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.player_fragment_container, fragment).commit();
        getSupportFragmentManager().executePendingTransactions();

        playerFragment = (AbsPlayerFragment) getSupportFragmentManager().findFragmentById(R.id.player_fragment_container);
        miniPlayerFragment = (MiniPlayerFragment) getSupportFragmentManager().findFragmentById(R.id.mini_player_fragment);

        //noinspection ConstantConditions
        miniPlayerFragment.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandPanel();
            }
        });

        slidingUpPanelLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                slidingUpPanelLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                if (getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    onPanelSlide(slidingUpPanelLayout, 1);
                    onPanelExpanded(slidingUpPanelLayout);
                } else if (getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    onPanelCollapsed(slidingUpPanelLayout);
                } else {
                    playerFragment.onHide();
                }
            }
        });

        setupBottomView();
        slidingUpPanelLayout.addPanelSlideListener(this);
    }

    private void setupBottomView() {
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        mBottomNavigationView.enableAnimation(false);
        mBottomNavigationView.enableItemShiftingMode(false);
        mBottomNavigationView.enableShiftingMode(false);
        mBottomNavigationView.setTextVisibility(true);
        mBottomNavigationView.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentNowPlayingScreen != PreferenceUtil.getInstance(this).getNowPlayingScreen()) {
            postRecreate();
        }
    }

    public void setAntiDragView(View antiDragView) {
        slidingUpPanelLayout.setAntiDragView(antiDragView);
    }

    protected abstract View createContentView();

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        if (!MusicPlayerRemote.getPlayingQueue().isEmpty()) {
            slidingUpPanelLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    slidingUpPanelLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    hideBottomBar(false);
                }
            });
        } // don't call hideBottomBar(true) here as it causes a bug with the SlidingUpPanelLayout
    }

    @Override
    public void onQueueChanged() {
        super.onQueueChanged();
        hideBottomBar(MusicPlayerRemote.getPlayingQueue().isEmpty());
    }

    @Override
    public void onPanelSlide(View panel, @FloatRange(from = 0, to = 1) float slideOffset) {
        mBottomNavigationView.setTranslationY(slideOffset * 300);
        setMiniPlayerAlphaProgress(slideOffset);
        if (navigationBarColorAnimator != null) navigationBarColorAnimator.cancel();
        super.setNavigationbarColor((int) argbEvaluator.evaluate(slideOffset, ColorUtil.shiftColor(navigationbarColor, 0.8f), ColorUtil.shiftColor(playerFragment.getPaletteColor(), 0.8f)));
    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
        switch (newState) {
            case COLLAPSED:
                onPanelCollapsed(panel);
                break;
            case EXPANDED:
                onPanelExpanded(panel);
                break;
            case ANCHORED:
                collapsePanel(); // this fixes a bug where the panel would get stuck for some reason
                break;
        }
    }

    public void onPanelCollapsed(View panel) {
        // restore values
        super.setLightStatusbar(lightStatusbar);
        super.setTaskDescriptionColor(taskColor);
        super.setNavigationbarColor(ColorUtil.shiftColor(navigationbarColor, 0.8f));

        playerFragment.setMenuVisibility(false);
        playerFragment.setUserVisibleHint(false);
        playerFragment.onHide();
    }

    public void onPanelExpanded(View panel) {
        // setting fragments values
        int playerFragmentColor = playerFragment.getPaletteColor();
        super.setLightStatusbar(false);
        super.setTaskDescriptionColor(playerFragmentColor);
        super.setNavigationbarColor(ColorUtil.shiftColor(playerFragmentColor, 0.8f));

        playerFragment.setMenuVisibility(true);
        playerFragment.setUserVisibleHint(true);
        playerFragment.onShow();
    }

    private void setMiniPlayerAlphaProgress(@FloatRange(from = 0, to = 1) float progress) {
        if (miniPlayerFragment.getView() == null) return;
        float alpha = 1 - progress;
        miniPlayerFragment.getView().setAlpha(alpha);
        // necessary to make the views below clickable
        miniPlayerFragment.getView().setVisibility(alpha == 0 ? View.GONE : View.VISIBLE);
    }


    public SlidingUpPanelLayout.PanelState getPanelState() {
        return slidingUpPanelLayout == null ? null : slidingUpPanelLayout.getPanelState();
    }

    public void collapsePanel() {
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    public void expandPanel() {
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    public void hideBottomBar(final boolean hide) {
        if (hide) {
            slidingUpPanelLayout.setPanelHeight(0);
            collapsePanel();
        } else {
            if (!MusicPlayerRemote.getPlayingQueue().isEmpty())
                if (mBottomNavigationView.getVisibility() == View.VISIBLE) {
                    slidingUpPanelLayout.setPanelHeight(getResources().getDimensionPixelSize(R.dimen.mini_player_height_expanded));
                } else {
                    slidingUpPanelLayout.setPanelHeight(getResources().getDimensionPixelSize(R.dimen.mini_player_height));
                }
        }
    }

    public void setBottomBarVisibility(int gone) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (mBottomNavigationView != null) {
                TransitionManager.beginDelayedTransition(mBottomNavigationView);
                mBottomNavigationView.setVisibility(gone);
                hideBottomBar(false);
            }
        }
    }

    protected View wrapSlidingMusicPanel(@LayoutRes int resId) {
        @SuppressLint("InflateParams")
        View slidingMusicPanelLayout = getLayoutInflater().inflate(R.layout.sliding_music_panel_layout, null);
        ViewGroup contentContainer = ButterKnife.findById(slidingMusicPanelLayout, R.id.content_container);
        getLayoutInflater().inflate(resId, contentContainer);
        return slidingMusicPanelLayout;
    }

    @Override
    public void onBackPressed() {
        if (!handleBackPress())
            super.onBackPressed();
    }

    public boolean handleBackPress() {
        if (slidingUpPanelLayout.getPanelHeight() != 0 && playerFragment.onBackPressed())
            return true;
        if (getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            collapsePanel();
            return true;
        }
        return false;
    }

    @Override
    public void onPaletteColorChanged() {
        if (getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            int playerFragmentColor = playerFragment.getPaletteColor();
            super.setTaskDescriptionColor(playerFragmentColor);
            animateNavigationBarColor(playerFragmentColor);
        }
    }

    @Override
    public void setLightStatusbar(boolean enabled) {
        lightStatusbar = enabled;
        if (getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            super.setLightStatusbar(enabled);
        }
    }

    @Override
    public void setNavigationbarColor(int color) {
        if (currentNowPlayingScreen == NowPlayingScreen.MODERN) {
            color = ColorUtil.shiftColor(color, 0.8f);
            this.navigationbarColor = ColorUtil.shiftColor(color, 0.8f);
        } else {
            this.navigationbarColor = color;
        }
        if (getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            if (navigationBarColorAnimator != null) navigationBarColorAnimator.cancel();
            super.setNavigationbarColor(color);
        }
    }

    private void animateNavigationBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (currentNowPlayingScreen == NowPlayingScreen.MODERN) color = ColorUtil.shiftColor(color, 0.8f);
            if (navigationBarColorAnimator != null) navigationBarColorAnimator.cancel();
            navigationBarColorAnimator = ValueAnimator
                    .ofArgb(getWindow().getNavigationBarColor(), color)
                    .setDuration(ViewUtil.PHONOGRAPH_ANIM_TIME);
            navigationBarColorAnimator.setInterpolator(new PathInterpolator(0.4f, 0f, 1f, 1f));
            navigationBarColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    AbsSlidingMusicPanelActivity.super.setNavigationbarColor((Integer) animation.getAnimatedValue());
                }
            });
            navigationBarColorAnimator.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (navigationBarColorAnimator != null) navigationBarColorAnimator.cancel(); // just in case
    }

    @Override
    public void setTaskDescriptionColor(@ColorInt int color) {
        this.taskColor = color;
        if (getPanelState() == null || getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            super.setTaskDescriptionColor(color);
        }
    }

    @Override
    protected View getSnackBarContainer() {
        return findViewById(R.id.content_container);
    }

    public SlidingUpPanelLayout getSlidingUpPanelLayout() {
        return slidingUpPanelLayout;
    }

    public MiniPlayerFragment getMiniPlayerFragment() {
        return miniPlayerFragment;
    }

    public AbsPlayerFragment getPlayerFragment() {
        return playerFragment;
    }

    public BottomNavigationView getBottomNavigationView() {
        return mBottomNavigationView;
    }
}
