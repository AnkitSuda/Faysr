package app.androidgrid.faysr.ui.fragments.player;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import app.androidgrid.faysr.R;
import app.androidgrid.faysr.dialogs.AddToPlaylistDialog;
import app.androidgrid.faysr.dialogs.CreatePlaylistDialog;
import app.androidgrid.faysr.dialogs.DeleteSongsDialog;
import app.androidgrid.faysr.dialogs.LyricsDialog;
import app.androidgrid.faysr.dialogs.NowPlayingListDialogFragment;
import app.androidgrid.faysr.dialogs.SleepTimerDialog;
import app.androidgrid.faysr.dialogs.SongDetailDialog;
import app.androidgrid.faysr.dialogs.SongShareDialog;
import app.androidgrid.faysr.editor.RingdroidSelectActivity;
import app.androidgrid.faysr.helper.MusicPlayerRemote;
import app.androidgrid.faysr.interfaces.PaletteColorHolder;
import app.androidgrid.faysr.model.Song;
import app.androidgrid.faysr.model.lyrics.Lyrics;
import app.androidgrid.faysr.ui.activities.tageditor.AbsTagEditorActivity;
import app.androidgrid.faysr.ui.activities.tageditor.SongTagEditorActivity;
import app.androidgrid.faysr.ui.fragments.AbsMusicServiceFragment;
import app.androidgrid.faysr.util.MusicUtil;
import app.androidgrid.faysr.util.NavigationUtil;

public abstract class AbsPlayerFragment extends AbsMusicServiceFragment implements Toolbar.OnMenuItemClickListener, PaletteColorHolder, NowPlayingListDialogFragment.Listener {
    public static final String TAG = AbsPlayerFragment.class.getSimpleName();

    private Callbacks callbacks;
    private static boolean isToolbarShown = true;

    public Lyrics mLyrics;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callbacks = (Callbacks) context;
        } catch (ClassCastException e) {
            throw new RuntimeException(context.getClass().getSimpleName() + " must implement " + Callbacks.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        final Song song = MusicPlayerRemote.getCurrentSong();
        switch (item.getItemId()) {
            case R.id.action_delete_from_device:
                DeleteSongsDialog.create(song)
                        .show(getActivity().getSupportFragmentManager(), "DELETE_SONGS");
                return true;
            case R.id.now_playing:
                NavigationUtil.goToPlayingQueue(getActivity());
                return true;
            case R.id.action_set_as_ringtone:
                MusicUtil.setRingtone(getActivity(), song.id);
                return true;
            case R.id.action_sleep_timer:
                new SleepTimerDialog().show(getFragmentManager(), "SET_SLEEP_TIMER");
                return true;
            case R.id.action_toggle_favorite:
                toggleFavorite(song);
                return true;
            case R.id.action_share:
                SongShareDialog.create(song).show(getFragmentManager(), "SHARE_SONG");
                return true;
            case R.id.action_equalizer:
                NavigationUtil.openEqualizer(getActivity());
                return true;
            case R.id.action_add_to_playlist:
                AddToPlaylistDialog.create(song).show(getFragmentManager(), "ADD_PLAYLIST");
                return true;
            case R.id.action_clear_playing_queue:
                MusicPlayerRemote.clearQueue();
                return true;
            case R.id.action_save_playing_queue:
                CreatePlaylistDialog.create(MusicPlayerRemote.getPlayingQueue()).show(getActivity().getSupportFragmentManager(), "ADD_TO_PLAYLIST");
                return true;
            case R.id.action_tag_editor:
                Intent intent = new Intent(getActivity(), SongTagEditorActivity.class);
                intent.putExtra(AbsTagEditorActivity.EXTRA_ID, song.id);
                startActivity(intent);
                return true;
            case R.id.action_song_editor:
                Intent editorIntent = new Intent(getActivity(), RingdroidSelectActivity.class);
                startActivity(editorIntent);
                return true;
            case R.id.action_details:
                SongDetailDialog.create(song).show(getFragmentManager(), "SONG_DETAIL");
                return true;
            case R.id.action_go_to_album:
                NavigationUtil.goToAlbum(getActivity(), song.albumId);
                return true;
            case R.id.action_go_to_artist:
                NavigationUtil.goToArtist(getActivity(), song.artistId);
                return true;
            case R.id.action_open_playing_queue:
                NavigationUtil.goToPlayingQueue(getActivity());
                return true;
            case R.id.action_show_lyrics:
                if (mLyrics != null)
                    LyricsDialog.create(mLyrics).show(getFragmentManager(), "LYRICS");
                return true;
            case R.id.action_open_menu:
                NowPlayingListDialogFragment.create().show(getFragmentManager(), "MENU");
                return true;
        }
        return false;
    }

    @Override
    public void onActionClicked(int position) {
        Toast.makeText(getActivity(), String.valueOf(position) + " ", Toast.LENGTH_SHORT).show();
    }

    protected void toggleFavorite(Song song) {
        MusicUtil.toggleFavorite(getActivity(), song);
    }

    protected boolean isToolbarShown() {
        return isToolbarShown;
    }

    protected void setToolbarShown(boolean toolbarShown) {
        isToolbarShown = toolbarShown;
    }

    protected void showToolbar(@Nullable final View toolbar) {
        if (toolbar == null) return;

        setToolbarShown(true);

        toolbar.setVisibility(View.VISIBLE);
        //toolbar.animate().alpha(1f).setDuration(PlayerAlbumCoverFragment.VISIBILITY_ANIM_DURATION);
    }

    protected void hideToolbar(@Nullable final View toolbar) {
        if (toolbar == null) return;

        setToolbarShown(false);

        toolbar.setVisibility(View.GONE);
       /* toolbar.animate().alpha(0f).setDuration(PlayerAlbumCoverFragment.VISIBILITY_ANIM_DURATION).withEndAction(new Runnable() {
            @Override
            public void run() {
                toolbar.setVisibility(View.GONE);
            }
        });*/
    }

    protected void toggleToolbar(@Nullable final View toolbar) {
        if (isToolbarShown()) {
            hideToolbar(toolbar);
        } else {
            showToolbar(toolbar);
        }
    }

    protected void checkToggleToolbar(@Nullable final View toolbar) {
        if (toolbar != null && !isToolbarShown() && toolbar.getVisibility() != View.GONE) {
            hideToolbar(toolbar);
        } else if (toolbar != null && isToolbarShown() && toolbar.getVisibility() != View.VISIBLE) {
            showToolbar(toolbar);
        }
    }

    protected String getUpNextAndQueueTime() {
        return getResources().getString(R.string.up_next) + "  •  " + MusicUtil.getReadableDurationString(MusicPlayerRemote.getQueueDurationMillis(MusicPlayerRemote.getPosition()));
    }

    public abstract void onShow();

    public abstract void onHide();

    public abstract boolean onBackPressed();

    public Callbacks getCallbacks() {
        return callbacks;
    }

    public interface Callbacks {
        void onPaletteColorChanged();
    }
}
