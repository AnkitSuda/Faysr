package app.androidgrid.faysr.editor;

import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.kabouzeid.appthemehelper.ThemeStore;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import app.androidgrid.faysr.R;
import app.androidgrid.faysr.adapter.song.SongAdapter;
import app.androidgrid.faysr.loader.SongLoader;
import app.androidgrid.faysr.loader.TopAndRecentlyPlayedTracksLoader;
import app.androidgrid.faysr.model.Song;
import app.androidgrid.faysr.ui.activities.base.AbsBaseActivity;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SongTrimSelectActivity extends AbsBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.list)
    FastScrollRecyclerView list;
    @BindView(R.id.appbar)
    AppBarLayout mAppBarLayout;

    @BindDrawable(R.drawable.ic_close_white_24dp)
    Drawable mClose;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_trim_select_activity);
        unbinder = ButterKnife.bind(this);
        setStatusbarColorAuto();
        setNavigationbarColorAuto();
        setTaskDescriptionColorAuto();
        mAppBarLayout.setBackgroundColor(ThemeStore.primaryColor(this));
        mToolbar.setBackgroundColor(ThemeStore.primaryColor(this));
        mToolbar.setNavigationIcon(mClose);
        setSupportActionBar(mToolbar);
        setTitle(getString(R.string.choose_a_song));
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        setUpList();
    }

    private void setUpList() {
        list.setPopupBgColor(ThemeStore.accentColor(this));
        list.setThumbColor(ThemeStore.accentColor(this));
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setItemAnimator(new DefaultItemAnimator());
        list.setAdapter(new SongAdapter(this, (ArrayList<Song>) SongLoader.getAllSongs(this), R.layout.item_list, false, null));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
