package app.androidgrid.faysr.ui.activities;

import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.animation.Animation;
import android.widget.TextView;

import com.kabouzeid.appthemehelper.ThemeStore;

import app.androidgrid.faysr.R;
import app.androidgrid.faysr.helper.MusicPlayerRemote;
import app.androidgrid.faysr.ui.activities.base.AbsMusicServiceActivity;
import app.androidgrid.faysr.ui.fragments.PlayingQueueFragment;
import app.androidgrid.faysr.util.MusicUtil;
import butterknife.BindAnim;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayingQueueActivity extends AbsMusicServiceActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindDrawable(R.drawable.ic_close_white_24dp)
    Drawable mClose;
    @BindView(R.id.player_queue_sub_header)
    TextView mPlayerQueueSubHeader;
    @BindString(R.string.queue)
    String queue;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_queue);
        overridePendingTransition(R.anim.slide_up_in, R.anim.do_not_move);
        ButterKnife.bind(this);

        setStatusbarColorAuto();
        setNavigationbarColorAuto();
        setTaskDescriptionColorAuto();

        setupToolbar();
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
        mPlayerQueueSubHeader.setText(getUpNextAndQueueTime());
        mPlayerQueueSubHeader.setTextColor(ThemeStore.accentColor(this));
        mAppBarLayout.setBackgroundColor(ThemeStore.primaryColor(this));
        mToolbar.setBackgroundColor(ThemeStore.primaryColor(this));
        mToolbar.setNavigationIcon(mClose);
        setSupportActionBar(mToolbar);
        setTitle(queue);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
}