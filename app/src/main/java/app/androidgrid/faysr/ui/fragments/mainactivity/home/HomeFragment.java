package app.androidgrid.faysr.ui.fragments.mainactivity.home;


import android.content.Intent;
import android.icu.util.ValueIterator;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.common.ATHToolbarActivity;
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper;

import java.util.ArrayList;

import app.androidgrid.faysr.R;
import app.androidgrid.faysr.adapter.song.SongAdapter;
import app.androidgrid.faysr.editor.SongTrimSelectActivity;
import app.androidgrid.faysr.helper.MusicPlayerRemote;
import app.androidgrid.faysr.loader.SongLoader;
import app.androidgrid.faysr.loader.TopAndRecentlyPlayedTracksLoader;
import app.androidgrid.faysr.model.Song;
import app.androidgrid.faysr.ui.activities.MainActivity;
import app.androidgrid.faysr.ui.activities.SearchActivity;
import app.androidgrid.faysr.ui.activities.SettingsActivity;
import app.androidgrid.faysr.ui.fragments.mainactivity.AbsMainActivityFragment;
import app.androidgrid.faysr.ui.fragments.mainactivity.library.LibraryFragment;
import app.androidgrid.faysr.ui.fragments.mainactivity.library.pager.AbsLibraryPagerFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends AbsLibraryPagerFragment implements MainActivity.MainActivityFragmentCallbacks {
    public static final String TAG = HomeFragment.class.getSimpleName();

    @BindView(R.id.list0)
    RecyclerView suggestionsList;
    @BindView(R.id.list1)
    RecyclerView recentPlaysList;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.status_bar)
    View statusBar;

    private Unbinder unbinder;

    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setStatusbarColorAuto(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statusBar.setElevation(0f);
        }
        getMainActivity().setBottomBarVisibility(View.VISIBLE);
        setUpToolbar();
        setUpLists();
    }

    private void setUpToolbar() {
        int primaryColor = ThemeStore.primaryColor(getActivity());
        appBarLayout.setBackgroundColor(primaryColor);
        toolbar.setBackgroundColor(primaryColor);
        toolbar.setTitle(R.string.for_you);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        getMainActivity().setSupportActionBar(toolbar);

      /*  appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) appBarLayout.getLayoutParams();
                if (verticalOffset == 0) {
                    params.setMargins(16, 16, 16, 16);
                } else {
                    params.setMargins(0, 0, 0, 0);
                }
                appBarLayout.setLayoutParams(params);
            }
        });*/
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (getActivity() == null) {
            return;
        }
        ToolbarContentTintHelper.handleOnPrepareOptionsMenu(getActivity(), toolbar);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
        menu.getItem(0).setActionView(null);
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(getActivity(), toolbar, menu, ATHToolbarActivity.getToolbarBackgroundColor(toolbar));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.search:
                startActivity(new Intent(getActivity(), SearchActivity.class));
        }
        return true;
    }

    private void setUpLists() {
        suggestionsList.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        suggestionsList.setItemAnimator(new DefaultItemAnimator());
        suggestionsList.setAdapter(new SongAdapter((AppCompatActivity) getActivity(), (ArrayList<Song>)  TopAndRecentlyPlayedTracksLoader.getTopTracks(getActivity()), R.layout.item_image_1, true, null));

        recentPlaysList.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        recentPlaysList.setItemAnimator(new DefaultItemAnimator());
        recentPlaysList.setAdapter(new SongAdapter((AppCompatActivity) getActivity(), (ArrayList<Song>)  TopAndRecentlyPlayedTracksLoader.getRecentlyPlayedTracks(getActivity()), R.layout.item_image_1, true, null));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        menu.removeItem(R.id.action_grid_size);
        menu.removeItem(R.id.action_colored_footers);
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(getActivity(), mToolbar, menu, ATHToolbarActivity.getToolbarBackgroundColor(mToolbar));

    }*/

  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        int id = item.getItemId();
        switch (id) {
            case R.id.action_shuffle_all:
                MusicPlayerRemote.openAndShuffleQueue(SongLoader.getAllSongs(getActivity()), true);
                return true;
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
        }

        return true;
    }*/

    @OnClick({R.id.playSong, R.id.makeRing, R.id.makeRemix})
    void onClicks(View view) {
        switch (view.getId()) {
            case R.id.playSong:
                MusicPlayerRemote.openAndShuffleQueue(SongLoader.getAllSongs(getActivity()), true);
                break;
            case R.id.makeRing:
                startActivity(new Intent(getActivity(), SongTrimSelectActivity.class));
                break;
            case R.id.makeRemix:
                Toast.makeText(getActivity(), "This feature will come soon", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean handleBackPress() {
        return false;
    }


}
