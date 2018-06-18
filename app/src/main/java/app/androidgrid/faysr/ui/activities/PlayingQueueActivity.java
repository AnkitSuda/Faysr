package app.androidgrid.faysr.ui.activities;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.color.MaterialColor;
import com.kabouzeid.appthemehelper.util.ATHUtil;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.kabouzeid.appthemehelper.util.MaterialValueHelper;
import com.kabouzeid.appthemehelper.util.TintHelper;
import com.transitionseverywhere.TransitionManager;

import app.androidgrid.faysr.DrawableGradient;
import app.androidgrid.faysr.R;
import app.androidgrid.faysr.helper.MusicPlayerRemote;
import app.androidgrid.faysr.helper.MusicProgressViewUpdateHelper;
import app.androidgrid.faysr.helper.PlayPauseButtonOnClickHandler;
import app.androidgrid.faysr.misc.SimpleOnSeekbarChangeListener;
import app.androidgrid.faysr.transitions.ProgressTransition;
import app.androidgrid.faysr.ui.activities.base.AbsMusicServiceActivity;
import app.androidgrid.faysr.ui.fragments.PlayingQueueFragment;
import app.androidgrid.faysr.util.MusicUtil;
import app.androidgrid.faysr.views.PlayPauseDrawable;
import butterknife.BindAnim;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import me.zhanghai.android.materialprogressbar.internal.ThemeUtils;

public class PlayingQueueActivity extends AbsMusicServiceActivity implements MusicProgressViewUpdateHelper.Callback {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindDrawable(R.drawable.ic_close_white_24dp)
    Drawable mClose;
    @BindString(R.string.queue)
    String queue;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.progress_bar)
    MaterialProgressBar mProgressBar;
    @BindView(R.id.player_prev_button)
    ImageButton mPrevBtn;
    @BindView(R.id.player_next_button)
    ImageButton mNextBtn;
    @BindView(R.id.player_play_pause_button)
    ImageButton mPlayPauseBtn;
    @BindView(R.id.root)
    CoordinatorLayout root;
    @BindView(R.id.controllers)
    CardView controllersRoot;

    private MusicProgressViewUpdateHelper progressViewUpdateHelper;
    private PlayPauseDrawable playerFabPlayPauseDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_queue);
        overridePendingTransition(R.anim.slide_up_in, R.anim.do_not_move);
        ButterKnife.bind(this);

        setStatusbarColorAuto();
        setNavigationbarColorAuto();
        setTaskDescriptionColorAuto();

        TransitionManager.beginDelayedTransition(root);
        progressViewUpdateHelper = new MusicProgressViewUpdateHelper(this);

        setupToolbar();
        setUpMusicControllers();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PlayingQueueFragment())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.do_not_move, R.anim.slide_down_out);
    }

    protected String getUpNextAndQueueTime() {
        return getResources().getString(R.string.up_next) + "  •  " + MusicUtil.getReadableDurationString(MusicPlayerRemote.getQueueDurationMillis(MusicPlayerRemote.getPosition()));
    }

    private void setupToolbar() {
        mToolbar.setTitle(getUpNextAndQueueTime());
        mAppBarLayout.setBackgroundColor(ThemeStore.primaryColor(this));
        mToolbar.setBackgroundColor(ThemeStore.primaryColor(this));
        mToolbar.setNavigationIcon(mClose);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setUpMusicControllers() {
        setUpPlayPauseButton();
        setUpPrevNext();
        setUpProgressSlider();
    }

    private void setUpPrevNext() {
        mNextBtn.setColorFilter(ATHUtil.resolveColor(this, R.attr.iconColor, ThemeStore.textColorSecondary(this)), PorterDuff.Mode.SRC_IN);
        mPrevBtn.setColorFilter(ATHUtil.resolveColor(this, R.attr.iconColor, ThemeStore.textColorSecondary(this)), PorterDuff.Mode.SRC_IN);

        mNextBtn.setOnClickListener(v -> MusicPlayerRemote.playNextSong(PlayingQueueActivity.this));
        mPrevBtn.setOnClickListener(v -> MusicPlayerRemote.back(PlayingQueueActivity.this));
    }

    private void setUpPlayPauseButton() {
        playerFabPlayPauseDrawable = new PlayPauseDrawable(this);
        mPlayPauseBtn.setImageDrawable(playerFabPlayPauseDrawable); // Note: set the drawable AFTER TintHelper.setTintAuto() was called
        mPlayPauseBtn.setColorFilter(ThemeStore.accentColor(this), PorterDuff.Mode.SRC_IN);

        mPlayPauseBtn.setOnClickListener(new PlayPauseButtonOnClickHandler());
    }

    protected void updatePlayPauseDrawableState(boolean animate) {
        if (MusicPlayerRemote.isPlaying()) {
            playerFabPlayPauseDrawable.setPause(animate);
        } else {
            playerFabPlayPauseDrawable.setPlay(animate);
        }
    }

    private void setUpProgressSlider() {
        mProgressBar.setProgressTintList(ColorStateList.valueOf(ThemeStore.accentColor(this)));
    }

    @Override
    public void onQueueChanged() {
        super.onQueueChanged();
        mToolbar.setSubtitle(getUpNextAndQueueTime());
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        updatePlayPauseDrawableState(false);
    }

    @Override
    public void onPlayStateChanged() {
        super.onPlayStateChanged();
        updatePlayPauseDrawableState(true);
    }

    @Override
    public void onUpdateProgressViews(int progress, int total) {
        TransitionManager.beginDelayedTransition(controllersRoot, new ProgressTransition());

        mProgressBar.setMax(total);
        mProgressBar.setProgress(progress);
    }

    @Override
    public void onResume() {
        super.onResume();
        progressViewUpdateHelper.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        progressViewUpdateHelper.stop();
    }
}