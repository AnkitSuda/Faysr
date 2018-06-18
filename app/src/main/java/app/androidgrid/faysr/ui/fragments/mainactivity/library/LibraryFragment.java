package app.androidgrid.faysr.ui.fragments.mainactivity.library;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import app.androidgrid.faysr.R;
import app.androidgrid.faysr.dialogs.CreatePlaylistDialog;
import app.androidgrid.faysr.dialogs.SleepTimerDialog;
import app.androidgrid.faysr.helper.MusicPlayerRemote;
import app.androidgrid.faysr.interfaces.CabHolder;
import app.androidgrid.faysr.loader.SongLoader;
import app.androidgrid.faysr.ui.activities.MainActivity;
import app.androidgrid.faysr.ui.activities.SearchActivity;
import app.androidgrid.faysr.ui.activities.SettingsActivity;
import app.androidgrid.faysr.ui.fragments.mainactivity.AbsMainActivityFragment;
import app.androidgrid.faysr.ui.fragments.mainactivity.library.pager.AbsLibraryPagerRecyclerViewCustomGridSizeFragment;
import app.androidgrid.faysr.ui.fragments.mainactivity.library.pager.PlaylistsFragment;
import app.androidgrid.faysr.ui.fragments.mainactivity.library.pager.SongsFragment;
import app.androidgrid.faysr.ui.fragments.mainactivity.library.pager.creators.CreatorsFragment;
import app.androidgrid.faysr.util.FaysrColorUtil;
import app.androidgrid.faysr.util.NavigationUtil;
import app.androidgrid.faysr.util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


import com.afollestad.materialcab.MaterialCab;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.common.ATHToolbarActivity;
import com.kabouzeid.appthemehelper.util.ATHUtil;
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper;
import com.transitionseverywhere.TransitionManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.util.Objects;

public class LibraryFragment extends AbsMainActivityFragment implements CabHolder,
        MainActivity.MainActivityFragmentCallbacks {

    private static final String TAG = "LibraryFragment";
    private static final String CURRENT_TAB_ID = "current_tab_id";

    public static Toolbar toolbar;
    public static AppBarLayout appbar;
    public static TabLayout tabs;
    @BindView(R.id.root_1)
    CoordinatorLayout root;
    private Unbinder unBinder;
    private MaterialCab cab;
    private FragmentManager fragmentManager;

    public static Fragment newInstance(int tab) {
        Bundle args = new Bundle();
        args.putInt(CURRENT_TAB_ID, tab);
        LibraryFragment fragment = new LibraryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void addOnAppBarOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener onOffsetChangedListener) {
        appbar.addOnOffsetChangedListener(onOffsetChangedListener);
    }

    public void removeOnAppBarOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener onOffsetChangedListener) {
        appbar.removeOnOffsetChangedListener(onOffsetChangedListener);
    }

    public int getTotalAppBarScrollingRange() {
        return appbar.getTotalScrollRange();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        unBinder = ButterKnife.bind(this, view);
        appbar = view.findViewById(R.id.appbar);
        toolbar = view.findViewById(R.id.toolbar);
        tabs = view.findViewById(R.id.tabs);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setStatusbarColorAuto(view);

        TransitionManager.beginDelayedTransition(root);

        getMainActivity().setBottomBarVisibility(View.VISIBLE);
        getMainActivity().getSlidingUpPanelLayout().setShadowHeight(6);
        setupToolbar();
        if (getArguments() == null) {
            selectedFragment(SongsFragment.newInstance());
        } else {
            switch (getArguments().getInt(CURRENT_TAB_ID)) {
                default:
                case R.id.action_song:
                    selectedFragment(SongsFragment.newInstance());
                    break;
                case R.id.action_creators:
                    selectedFragment(CreatorsFragment.newInstance());
                    break;
                case R.id.action_playlist:
                    selectedFragment(PlaylistsFragment.newInstance());
                    break;
            }
        }
    }

    public static Toolbar getToolbar() {
        return toolbar;
    }

    public static void setToolbarTitle(String str) {
        if (getToolbar() != null)
        getToolbar().setTitle(str);
    }


    private void setupToolbar() {
        //noinspection ConstantConditions
        int primaryColor = ThemeStore.primaryColor(getContext());
        appbar.setBackgroundColor(primaryColor);
        toolbar.setBackgroundColor(primaryColor);
        toolbar.setTitle(R.string.library);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) ->
                getMainActivity().setLightStatusbar(!ATHUtil.isWindowBackgroundDark(getContext())));
        Objects.requireNonNull(getActivity()).setTitle(R.string.app_name);
        getMainActivity().setSupportActionBar(toolbar);
    }

    public Fragment getCurrentFragment() {
        if (fragmentManager == null) {
            return SongsFragment.newInstance();
        }
        return fragmentManager.findFragmentByTag(LibraryFragment.TAG);
    }

    public static void refreshTabVisibility(Fragment fragment) {
        if (fragment instanceof CreatorsFragment) {
            tabs.setVisibility(View.VISIBLE);
        } else {
            tabs.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unBinder.unbind();
    }

    @Override
    public boolean handleBackPress() {
        if (cab != null && cab.isActive()) {
            cab.finish();
            return true;
        }
        return false;
    }

    public void selectedFragment(Fragment fragment) {
        fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction
                .replace(R.id.fragment_container, fragment, TAG)
                .commit();

        refreshTabVisibility(fragment);
    }

    @NonNull
    @Override
    public MaterialCab openCab(int menuRes, MaterialCab.Callback callback) {
        if (cab != null && cab.isActive()) {
            cab.finish();
        }
        //noinspection ConstantConditions
        cab = new MaterialCab(getMainActivity(), R.id.cab_stub)
                .setMenu(menuRes)
                .setCloseDrawableRes(R.drawable.ic_close_white_24dp)
                .setBackgroundColor(
                        FaysrColorUtil.shiftBackgroundColorForLightText(ThemeStore.primaryColor(getActivity())))
                .start(callback);
        return cab;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);

        Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof AbsLibraryPagerRecyclerViewCustomGridSizeFragment
                && currentFragment.isAdded()) {
            AbsLibraryPagerRecyclerViewCustomGridSizeFragment fragment = (AbsLibraryPagerRecyclerViewCustomGridSizeFragment) currentFragment;

            MenuItem gridSizeItem = menu.findItem(R.id.action_grid_size);
            if (Util.isLandscape(getResources())) {
                gridSizeItem.setTitle(R.string.action_grid_size_land);
            }
            setUpGridSizeMenu(fragment, gridSizeItem.getSubMenu());

            //setUpSortOrderMenu(fragment, menu.findItem(R.id.action_sort_order).getSubMenu());

        } else {
            menu.add(0, R.id.action_new_playlist, 0, R.string.new_playlist_title);
            menu.removeItem(R.id.action_grid_size);
        }
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(getActivity(), toolbar, menu,
                ATHToolbarActivity.getToolbarBackgroundColor(toolbar));
    }

  /*  private void setUpSortOrderMenu(
            @NonNull AbsLibraryPagerRecyclerViewCustomGridSizeFragment fragment,
            @NonNull SubMenu sortOrderMenu) {
        String currentSortOrder = fragment.getSortOrder();
        sortOrderMenu.clear();

        if (fragment instanceof AlbumsFragment) {
            sortOrderMenu.add(0, R.id.action_album_sort_order_asc, 0, R.string.sort_order_a_z)
                    .setChecked(currentSortOrder.equals(SortOrder.AlbumSortOrder.ALBUM_A_Z));
            sortOrderMenu.add(0, R.id.action_album_sort_order_desc, 1, R.string.sort_order_z_a)
                    .setChecked(currentSortOrder.equals(SortOrder.AlbumSortOrder.ALBUM_Z_A));
            sortOrderMenu.add(0, R.id.action_album_sort_order_artist, 2, R.string.sort_order_artist)
                    .setChecked(currentSortOrder.equals(SortOrder.AlbumSortOrder.ALBUM_ARTIST));
            sortOrderMenu.add(0, R.id.action_album_sort_order_year, 3, R.string.sort_order_year)
                    .setChecked(currentSortOrder.equals(SortOrder.AlbumSortOrder.ALBUM_YEAR));
        } else if (fragment instanceof ArtistsFragment) {
            sortOrderMenu.add(0, R.id.action_artist_sort_order_asc, 0, R.string.sort_order_a_z)
                    .setChecked(currentSortOrder.equals(SortOrder.ArtistSortOrder.ARTIST_A_Z));
            sortOrderMenu.add(0, R.id.action_artist_sort_order_desc, 1, R.string.sort_order_z_a)
                    .setChecked(currentSortOrder.equals(SortOrder.ArtistSortOrder.ARTIST_Z_A));
        } else if (fragment instanceof SongsFragment) {
            sortOrderMenu.add(0, R.id.action_song_sort_order_asc, 0, R.string.sort_order_a_z)
                    .setChecked(currentSortOrder.equals(SortOrder.SongSortOrder.SONG_A_Z));
            sortOrderMenu.add(0, R.id.action_song_sort_order_desc, 1, R.string.sort_order_z_a)
                    .setChecked(currentSortOrder.equals(SortOrder.SongSortOrder.SONG_Z_A));
            sortOrderMenu.add(0, R.id.action_song_sort_order_artist, 2, R.string.sort_order_artist)
                    .setChecked(currentSortOrder.equals(SortOrder.SongSortOrder.SONG_ARTIST));
            sortOrderMenu.add(0, R.id.action_song_sort_order_album, 3, R.string.sort_order_album)
                    .setChecked(currentSortOrder.equals(SortOrder.SongSortOrder.SONG_ALBUM));
            sortOrderMenu.add(0, R.id.action_song_sort_order_year, 4, R.string.sort_order_year)
                    .setChecked(currentSortOrder.equals(SortOrder.SongSortOrder.SONG_YEAR));
            sortOrderMenu.add(0, R.id.action_song_sort_order_date, 4, R.string.sort_order_date)
                    .setChecked(currentSortOrder.equals(SortOrder.SongSortOrder.SONG_DATE));
        }

        sortOrderMenu.setGroupCheckable(0, true, true);
    }

    private boolean handleSortOrderMenuItem(
            @NonNull AbsLibraryPagerRecyclerViewCustomGridSizeFragment fragment, @NonNull MenuItem item) {
        String sortOrder = null;
        if (fragment instanceof AlbumsFragment) {
            switch (item.getItemId()) {
                case R.id.action_album_sort_order_asc:
                    sortOrder = SortOrder.AlbumSortOrder.ALBUM_A_Z;
                    break;
                case R.id.action_album_sort_order_desc:
                    sortOrder = SortOrder.AlbumSortOrder.ALBUM_Z_A;
                    break;
                case R.id.action_album_sort_order_artist:
                    sortOrder = SortOrder.AlbumSortOrder.ALBUM_ARTIST;
                    break;
                case R.id.action_album_sort_order_year:
                    sortOrder = SortOrder.AlbumSortOrder.ALBUM_YEAR;
                    break;
            }
        } else if (fragment instanceof ArtistsFragment) {
            switch (item.getItemId()) {
                case R.id.action_artist_sort_order_asc:
                    sortOrder = SortOrder.ArtistSortOrder.ARTIST_A_Z;
                    break;
                case R.id.action_artist_sort_order_desc:
                    sortOrder = SortOrder.ArtistSortOrder.ARTIST_Z_A;
                    break;
            }
        } else if (fragment instanceof SongsFragment) {
            switch (item.getItemId()) {
                case R.id.action_song_sort_order_asc:
                    sortOrder = SortOrder.SongSortOrder.SONG_A_Z;
                    break;
                case R.id.action_song_sort_order_desc:
                    sortOrder = SortOrder.SongSortOrder.SONG_Z_A;
                    break;
                case R.id.action_song_sort_order_artist:
                    sortOrder = SortOrder.SongSortOrder.SONG_ARTIST;
                    break;
                case R.id.action_song_sort_order_album:
                    sortOrder = SortOrder.SongSortOrder.SONG_ALBUM;
                    break;
                case R.id.action_song_sort_order_year:
                    sortOrder = SortOrder.SongSortOrder.SONG_YEAR;
                    break;
                case R.id.action_song_sort_order_date:
                    sortOrder = SortOrder.SongSortOrder.SONG_DATE;
                    break;
            }
        }

        if (sortOrder != null) {
            item.setChecked(true);
            fragment.setAndSaveSortOrder(sortOrder);
            return true;
        }

        return false;
    }*/

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        ToolbarContentTintHelper.handleOnPrepareOptionsMenu(activity, toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //if (pager == null) return false;
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof AbsLibraryPagerRecyclerViewCustomGridSizeFragment) {
            AbsLibraryPagerRecyclerViewCustomGridSizeFragment fragment = (AbsLibraryPagerRecyclerViewCustomGridSizeFragment) currentFragment;
            if (handleGridSizeMenuItem(fragment, item)) {
                return true;
            }
           /* if (handleSortOrderMenuItem(fragment, item)) {
                return true;
            }*/
        }
        int id = item.getItemId();
        switch (id) {
            case R.id.action_new_playlist:
                CreatePlaylistDialog.create().show(getChildFragmentManager(), "CREATE_PLAYLIST");
                return true;
            case R.id.action_shuffle_all:
                //noinspection ConstantConditions
                MusicPlayerRemote.openAndShuffleQueue(SongLoader.getAllSongs(getActivity()), true);
                return true;
            case R.id.action_search:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                return true;
            case R.id.action_equalizer:
                //noinspection ConstantConditions
                NavigationUtil.openEqualizer(getActivity());
                return true;
            case R.id.action_sleep_timer:
                if (getFragmentManager() != null) {
                    new SleepTimerDialog().show(getFragmentManager(), TAG);
                }
                return true;
            /*case R.id.action_settings:
                startActivity(new Intent(getContext(), SettingsActivity.class));
                break;*/
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpGridSizeMenu(
            @NonNull AbsLibraryPagerRecyclerViewCustomGridSizeFragment fragment,
            @NonNull SubMenu gridSizeMenu) {
        switch (fragment.getGridSize()) {
            case 1:
                gridSizeMenu.findItem(R.id.action_grid_size_1).setChecked(true);
                break;
            case 2:
                gridSizeMenu.findItem(R.id.action_grid_size_2).setChecked(true);
                break;
            case 3:
                gridSizeMenu.findItem(R.id.action_grid_size_3).setChecked(true);
                break;
            case 4:
                gridSizeMenu.findItem(R.id.action_grid_size_4).setChecked(true);
                break;
            case 5:
                gridSizeMenu.findItem(R.id.action_grid_size_5).setChecked(true);
                break;
            case 6:
                gridSizeMenu.findItem(R.id.action_grid_size_6).setChecked(true);
                break;
            case 7:
                gridSizeMenu.findItem(R.id.action_grid_size_7).setChecked(true);
                break;
            case 8:
                gridSizeMenu.findItem(R.id.action_grid_size_8).setChecked(true);
                break;
        }
        int maxGridSize = fragment.getMaxGridSize();
        if (maxGridSize < 8) {
            gridSizeMenu.findItem(R.id.action_grid_size_8).setVisible(false);
        }
        if (maxGridSize < 7) {
            gridSizeMenu.findItem(R.id.action_grid_size_7).setVisible(false);
        }
        if (maxGridSize < 6) {
            gridSizeMenu.findItem(R.id.action_grid_size_6).setVisible(false);
        }
        if (maxGridSize < 5) {
            gridSizeMenu.findItem(R.id.action_grid_size_5).setVisible(false);
        }
        if (maxGridSize < 4) {
            gridSizeMenu.findItem(R.id.action_grid_size_4).setVisible(false);
        }
        if (maxGridSize < 3) {
            gridSizeMenu.findItem(R.id.action_grid_size_3).setVisible(false);
        }
    }


    private boolean handleGridSizeMenuItem(
            @NonNull AbsLibraryPagerRecyclerViewCustomGridSizeFragment fragment, @NonNull MenuItem item) {
        int gridSize = 0;
        switch (item.getItemId()) {
            case R.id.action_grid_size_1:
                gridSize = 1;
                break;
            case R.id.action_grid_size_2:
                gridSize = 2;
                break;
            case R.id.action_grid_size_3:
                gridSize = 3;
                break;
            case R.id.action_grid_size_4:
                gridSize = 4;
                break;
            case R.id.action_grid_size_5:
                gridSize = 5;
                break;
            case R.id.action_grid_size_6:
                gridSize = 6;
                break;
            case R.id.action_grid_size_7:
                gridSize = 7;
                break;
            case R.id.action_grid_size_8:
                gridSize = 8;
                break;
        }

        if (gridSize > 0) {
            item.setChecked(true);
            fragment.setAndSaveGridSize(gridSize);
            return true;
        }
        return false;
    }
}
