package app.androidgrid.faysr.adapter.song;

import android.graphics.PorterDuff;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kabouzeid.appthemehelper.ThemeStore;

import app.androidgrid.faysr.R;
import app.androidgrid.faysr.helper.MusicPlayerRemote;
import app.androidgrid.faysr.interfaces.CabHolder;
import app.androidgrid.faysr.loader.TopAndRecentlyPlayedTracksLoader;
import app.androidgrid.faysr.model.Song;

import java.util.ArrayList;

/**
 * @author Eugene Cheung (arkon)
 */
public abstract class AbsOffsetSongAdapter extends SongAdapter {

    protected static final int OFFSET_ITEM = 0;
    protected static final int SONG = 1;

    public boolean isQuickActions;

    public AbsOffsetSongAdapter(boolean isQuickActions, AppCompatActivity activity, ArrayList<Song> dataSet, @LayoutRes int itemLayoutRes, boolean usePalette, @Nullable CabHolder cabHolder) {
        super(activity, dataSet, itemLayoutRes, usePalette, cabHolder);
        this.isQuickActions = isQuickActions;
    }

    public AbsOffsetSongAdapter(boolean isQuickActions, AppCompatActivity activity, ArrayList<Song> dataSet, @LayoutRes int itemLayoutRes, boolean usePalette, @Nullable CabHolder cabHolder, boolean showSectionName) {
        super(activity, dataSet, itemLayoutRes, usePalette, cabHolder, showSectionName);
        this.isQuickActions = isQuickActions;
    }



    @NonNull
    @Override
    public SongAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == OFFSET_ITEM) {
            View view = LayoutInflater.from(activity).inflate(isQuickActions ? R.layout.songs_quick_item_list : R.layout.item_list, parent, false);
            return createViewHolder(view);
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    protected SongAdapter.ViewHolder createViewHolder(View view) {
        return new AbsOffsetSongAdapter.ViewHolder(view);
    }

    @Override
    public long getItemId(int position) {
        position--;
        if (position < 0) return -2;
        return super.getItemId(position);
    }

    @Override
    protected Song getIdentifier(int position) {
        position--;
        if (position < 0) return Song.EMPTY_SONG;
        return super.getIdentifier(position);
    }

    @Override
    public int getItemCount() {
        int superItemCount = super.getItemCount();
        return superItemCount == 0 ? 0 : superItemCount + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? OFFSET_ITEM : SONG;
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        position--;
        if (position < 0) return "";
        return super.getSectionName(position);
    }

    public class ViewHolder extends SongAdapter.ViewHolder {
        public AppCompatImageButton m0;
        public AppCompatImageButton m1;
        public AppCompatImageButton m2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


        }

        @Override
        protected Song getSong() {
            if (getItemViewType() == OFFSET_ITEM) return Song.EMPTY_SONG;
            return dataSet.get(getAdapterPosition() - 1);
        }

        @Override
        public void onClick(View v) {
            if (isInQuickSelectMode() && getItemViewType() != OFFSET_ITEM) {
                toggleChecked(getAdapterPosition());

            } else {
                MusicPlayerRemote.openQueue(dataSet, getAdapterPosition() - 1, true);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (getItemViewType() == OFFSET_ITEM) return false;
            toggleChecked(getAdapterPosition());
            return true;
        }
    }
}
