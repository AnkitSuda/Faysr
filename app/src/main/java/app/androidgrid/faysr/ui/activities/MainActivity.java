package app.androidgrid.faysr.ui.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.util.ATHUtil;
import com.kabouzeid.appthemehelper.util.NavigationViewUtil;

import app.androidgrid.faysr.BuildConfig;
import app.androidgrid.faysr.R;
import app.androidgrid.faysr.dialogs.ChangelogDialog;
import app.androidgrid.faysr.dialogs.ScanMediaFolderChooserDialog;
import app.androidgrid.faysr.glide.SongGlideRequest;
import app.androidgrid.faysr.helper.MusicPlayerRemote;
import app.androidgrid.faysr.helper.SearchQueryHelper;
import app.androidgrid.faysr.interfaces.LibraryTabSelectedItem;
import app.androidgrid.faysr.loader.AlbumLoader;
import app.androidgrid.faysr.loader.ArtistSongLoader;
import app.androidgrid.faysr.loader.PlaylistSongLoader;
import app.androidgrid.faysr.model.Song;
import app.androidgrid.faysr.service.MusicService;
import app.androidgrid.faysr.ui.activities.base.AbsSlidingMusicPanelActivity;
import app.androidgrid.faysr.ui.activities.under_development.UnderDevelopmentActivity;
import app.androidgrid.faysr.ui.fragments.mainactivity.folders.FoldersFragment;
import app.androidgrid.faysr.ui.fragments.mainactivity.library.LibraryFragment;
import app.androidgrid.faysr.ui.fragments.mainactivity.library.pager.creators.CreatorsFragment;
import app.androidgrid.faysr.ui.fragments.mainactivity.library.pager.PlaylistsFragment;
import app.androidgrid.faysr.ui.fragments.mainactivity.library.pager.SongsFragment;
import app.androidgrid.faysr.ui.fragments.mainactivity.home.HomeFragment;
import app.androidgrid.faysr.util.NavigationUtil;
import app.androidgrid.faysr.util.PreferenceUtil;
import app.androidgrid.faysr.util.Util;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AbsSlidingMusicPanelActivity {

    // Remote Config keys
    private static final String UNDER_DEVELOPMENT_KEY = "under_development_enabled";

    private FirebaseRemoteConfig mFirebaseRemoteConfig;


    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int APP_INTRO_REQUEST = 100;
    public static final int PURCHASE_REQUEST = 101;

    private static final int LIBRARY = 0;
    private static final int FOLDERS = 1;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Nullable
    MainActivityFragmentCallbacks currentFragment;

    @Nullable
    private View navigationDrawerHeader;

    private boolean blockRequestPermissions;
    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Util.setStatusBarTranslucent(getWindow());
            drawerLayout.setFitsSystemWindows(false);
            navigationView.setFitsSystemWindows(false);
            //noinspection ConstantConditions
            findViewById(R.id.drawer_content_container).setFitsSystemWindows(false);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawerLayout.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    navigationView.dispatchApplyWindowInsets(windowInsets);
                    return windowInsets.replaceSystemWindowInsets(0, 0, 0, 0);
                }
            });
        }

        setUpDrawerLayout();

        if (savedInstanceState == null) {
           /* int defaultPage = PreferenceUtil.getInstance(this).getDefaultStartPage();
            Log.i("DefaultPage", String.valueOf(defaultPage));

            if (String.valueOf(defaultPage).equals("9")) {
                setMusicChooser(PreferenceUtil.getInstance(this).getLastMusicChooser());
            } else {
                setMusicChooser(PreferenceUtil.getInstance(this).getDefaultStartPage());
            }*/
            int m = PreferenceUtil.getInstance(this).getLastMusicChooser();
            setMusicChooser(m);
            if (m == LIBRARY) {
                getBottomNavigationView().setSelectedItemId(PreferenceUtil.getInstance(this).getLastPage());
                setCurrentFragment(PreferenceUtil.getInstance(this).getLastPage());
            }
        } else {
            restoreCurrentFragment();
        }

        if (!checkShowIntro()) {
            checkShowChangelog();
        }

        getBottomNavigationView().setOnNavigationItemSelectedListener(this);
    }

    private void setMusicChooser(int key) {
        PreferenceUtil.getInstance(this).setLastMusicChooser(key);

        switch (key) {
            /*case HOME:
                navigationView.setCheckedItem(R.id.nav_home);
                setCurrentFragment(HomeFragment.newInstance(), false);
                break;*/
            case LIBRARY:
                navigationView.setCheckedItem(R.id.nav_library);
                setCurrentFragment(LibraryFragment.newInstance(key), false);
                break;
            case FOLDERS:
                navigationView.setCheckedItem(R.id.nav_folders);
                setCurrentFragment(FoldersFragment.newInstance(this), false);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }


    public void setCurrentFragment(@Nullable Fragment fragment, boolean isStackAdd) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment, TAG);
        if (isStackAdd) {
            fragmentTransaction.addToBackStack(TAG);
        }
        fragmentTransaction.commit();
        currentFragment = (MainActivityFragmentCallbacks) fragment;
    }

    private void restoreCurrentFragment() {
        currentFragment = (MainActivityFragmentCallbacks) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        PreferenceUtil.getInstance(this).setLastPage(item.getItemId());
        disposable.add(Observable.just(item.getItemId())
                .throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(this::setCurrentFragment));
        return true;
    }

    private void setCurrentFragment(int menuItem) {
        switch (menuItem) {
            default:
            case R.id.action_song:
            case R.id.action_creators:
            case R.id.action_playlist:
                setCurrentFragment(LibraryFragment.newInstance(menuItem), false);
                break;
            case R.id.action_home:
                setCurrentFragment(HomeFragment.newInstance(), false);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_INTRO_REQUEST) {
            blockRequestPermissions = false;
            if (!hasPermissions()) {
                requestPermissions();
            }
        } else if (requestCode == PURCHASE_REQUEST) {
            if (resultCode == RESULT_OK) {
            }
        }
    }

    @Override
    protected void requestPermissions() {
        if (!blockRequestPermissions) super.requestPermissions();
    }

    @Override
    protected View createContentView() {
        @SuppressLint("InflateParams")
        View contentView = getLayoutInflater().inflate(R.layout.activity_main_drawer_layout, null);
        ViewGroup drawerContent = ButterKnife.findById(contentView, R.id.drawer_content_container);
        drawerContent.addView(wrapSlidingMusicPanel(R.layout.activity_main_content));
        return contentView;
    }

    private void setUpNavigationView() {
        int accentColor = ThemeStore.accentColor(this);
        NavigationViewUtil.setItemIconColors(navigationView, ATHUtil.resolveColor(this, R.attr.iconColor, ThemeStore.textColorSecondary(this)), accentColor);
        NavigationViewUtil.setItemTextColors(navigationView, ThemeStore.textColorPrimary(this), accentColor);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.nav_library:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setMusicChooser(LIBRARY);
                            }
                        }, 200);
                        break;
                    case R.id.nav_folders:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setMusicChooser(FOLDERS);
                            }
                        }, 200);
                        break;
                    case R.id.nav_scan:
                        new Handler().postDelayed(() -> {
                            ScanMediaFolderChooserDialog dialog = ScanMediaFolderChooserDialog.create();
                            dialog.show(getSupportFragmentManager(), "SCAN_MEDIA_FOLDER_CHOOSER");
                        }, 200);
                        break;
                    case R.id.nav_settings:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                            }
                        }, 200);
                        break;
                    case R.id.nav_about:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                            }
                        }, 200);
                        break;
                    case R.id.sup_dev:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                NavigationUtil.goToDonation(MainActivity.this);
                            }
                        }, 200);
                        break;
                }
                return true;
            }
        });
    }


    private void setUpDrawerLayout() {
        setUpNavigationView();
    }

    private void updateNavigationDrawerHeader() {
        if (!MusicPlayerRemote.getPlayingQueue().isEmpty()) {
            Song song = MusicPlayerRemote.getCurrentSong();
            if (navigationDrawerHeader == null) {
                navigationDrawerHeader = navigationView.inflateHeaderView(R.layout.navigation_drawer_header);
                //noinspection ConstantConditions
                navigationDrawerHeader.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawerLayout.closeDrawers();
                        if (getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                            expandPanel();
                        }
                    }
                });
            }
            ((TextView) navigationDrawerHeader.findViewById(R.id.title)).setText(song.title);
            ((TextView) navigationDrawerHeader.findViewById(R.id.text)).setText(song.artistName);
            SongGlideRequest.Builder.from(Glide.with(this), song)
                    .checkIgnoreMediaStore(this).build()
                    .into(((ImageView) navigationDrawerHeader.findViewById(R.id.image)));
        } else {
            if (navigationDrawerHeader != null) {
                navigationView.removeHeaderView(navigationDrawerHeader);
                navigationDrawerHeader = null;
            }
        }
    }

    @Override
    public void onPlayingMetaChanged() {
        super.onPlayingMetaChanged();
        updateNavigationDrawerHeader();
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        updateNavigationDrawerHeader();
        handlePlaybackIntent(getIntent());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
            } else {
                drawerLayout.openDrawer(navigationView);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean handleBackPress() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
            return true;
        }
        return super.handleBackPress() || (currentFragment != null &&
                currentFragment.handleBackPress());
    }

    private void handlePlaybackIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }

        Uri uri = intent.getData();
        String mimeType = intent.getType();
        boolean handled = false;

        if (intent.getAction() != null && intent.getAction().equals(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH)) {
            final ArrayList<Song> songs = SearchQueryHelper.getSongs(this, intent.getExtras());
            if (MusicPlayerRemote.getShuffleMode() == MusicService.SHUFFLE_MODE_SHUFFLE) {
                MusicPlayerRemote.openAndShuffleQueue(songs, true);
            } else {
                MusicPlayerRemote.openQueue(songs, 0, true);
            }
            handled = true;
        }

        if (uri != null && uri.toString().length() > 0) {
            //MusicPlayerRemote.playFromUri(uri);
            handled = true;
        } else if (MediaStore.Audio.Playlists.CONTENT_TYPE.equals(mimeType)) {
            final int id = (int) parseIdFromIntent(intent, "playlistId", "playlist");
            if (id >= 0) {
                int position = intent.getIntExtra("position", 0);
                ArrayList<Song> songs = new ArrayList<>();
                songs.addAll(PlaylistSongLoader.getPlaylistSongList(this, id));
                MusicPlayerRemote.openQueue(songs, position, true);
                handled = true;
            }
        } else if (MediaStore.Audio.Albums.CONTENT_TYPE.equals(mimeType)) {
            final int id = (int) parseIdFromIntent(intent, "albumId", "album");
            if (id >= 0) {
                int position = intent.getIntExtra("position", 0);
                MusicPlayerRemote.openQueue(AlbumLoader.getAlbum(this, id).songs, position, true);
                handled = true;
            }
        } else if (MediaStore.Audio.Artists.CONTENT_TYPE.equals(mimeType)) {
            final int id = (int) parseIdFromIntent(intent, "artistId", "artist");
            if (id >= 0) {
                int position = intent.getIntExtra("position", 0);
                MusicPlayerRemote.openQueue(ArtistSongLoader.getArtistSongList(this, id), position, true);
                handled = true;
            }
        }
        if (handled) {
            setIntent(new Intent());
        }
    }

    private long parseIdFromIntent(@NonNull Intent intent, String longKey,
                                   String stringKey) {
        long id = intent.getLongExtra(longKey, -1);
        if (id < 0) {
            String idString = intent.getStringExtra(stringKey);
            if (idString != null) {
                try {
                    id = Long.parseLong(idString);
                } catch (NumberFormatException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return id;
    }

    @Override
    public void onPanelExpanded(View view) {
        super.onPanelExpanded(view);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onPanelCollapsed(View view) {
        super.onPanelCollapsed(view);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    private boolean checkShowIntro() {
        if (!PreferenceUtil.getInstance(this).introShown()) {
            PreferenceUtil.getInstance(this).setIntroShown();
            ChangelogDialog.setChangelogRead(this);
           /* blockRequestPermissions = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivityForResult(new Intent(MainActivity.this, AppIntroActivity.class), APP_INTRO_REQUEST);
                }
            }, 50);*/
            return true;
        }
        return false;
    }

    private boolean checkShowChangelog() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int currentVersion = pInfo.versionCode;
            if (currentVersion != PreferenceUtil.getInstance(this).getLastChangelogVersion()) {
                ChangelogDialog.create().show(getSupportFragmentManager(), "CHANGE_LOG_DIALOG");
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public interface MainActivityFragmentCallbacks {
        boolean handleBackPress();
    }

    private void setUpRemoteConfig() {
        // Get Remote Config instance.
        // [START get_remote_config_instance]
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        // [END get_remote_config_instance]

        // Create a Remote Config Setting to enable developer mode, which you can use to increase
        // the number of fetches available per hour during development. See Best Practices in the
        // README for more information.
        // [START enable_dev_mode]
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        // [END enable_dev_mode]

        // Set default Remote Config parameter values. An app uses the in-app default values, and
        // when you need to adjust those defaults, you set an updated value for only the values you
        // want to change in the Firebase console. See Best Practices in the README for more
        // information.
        // [START set_default_values]
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        // [END set_default_values]
        fetchRemoteConfigs();

    }

    public void fetchRemoteConfigs() {
        boolean is_development = mFirebaseRemoteConfig.getBoolean(UNDER_DEVELOPMENT_KEY);

        if (is_development) {
            finish();
            startActivity(new Intent(this, UnderDevelopmentActivity.class));
            finish();
        }

        long cacheExpiration = 43200; // 12 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        // [START fetch_config_with_callback]
        // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
        // will use fetch data from the Remote Config service, rather than cached parameter values,
        // if cached parameter values are more than cacheExpiration seconds old.
        // See Best Practices in the README for more information.
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            //Toast.makeText(MainActivity.this, "Fetch Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // [END fetch_config_with_callback]
    }

}