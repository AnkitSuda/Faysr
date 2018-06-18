package app.androidgrid.faysr.adapter.song;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.kabouzeid.appthemehelper.ThemeStore;
import app.androidgrid.faysr.R;
import app.androidgrid.faysr.helper.MusicPlayerRemote;
import app.androidgrid.faysr.interfaces.CabHolder;
import app.androidgrid.faysr.loader.LastAddedLoader;
import app.androidgrid.faysr.loader.TopAndRecentlyPlayedTracksLoader;
import app.androidgrid.faysr.model.Song;

import java.util.ArrayList;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class QuickActionSongAdapter extends AbsOffsetSongAdapter {
    private Activity activity;

    public QuickActionSongAdapter(AppCompatActivity activity, ArrayList<Song> dataSet, @LayoutRes int itemLayoutRes, boolean usePalette, @Nullable CabHolder cabHolder) {
        super(true, activity, dataSet, itemLayoutRes, usePalette, cabHolder);
        this.activity = activity;
    }

    @Override
    protected SongAdapter.ViewHolder createViewHolder(View view) {

        return new QuickActionSongAdapter.ViewHolder(view);
    }
/*

    @NonNull
    @Override
    public SongAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //return super.onCreateViewHolder(parent, viewType);
        if (viewType == OFFSET_ITEM) {
            return new QuickViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.songs_quick_item_list, parent, false));
        } else {
            return new ShuffleButtonSongAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false));
        }
    }
*/

    @Override
    public void onBindViewHolder(@NonNull final SongAdapter.ViewHolder holder, int position) {
        if (holder.getItemViewType() == OFFSET_ITEM) {
            int accentColor = ThemeStore.accentColor(activity);
            holder.q0.setColorFilter(accentColor, PorterDuff.Mode.SRC_IN);
            holder.q1.setColorFilter(accentColor, PorterDuff.Mode.SRC_IN);
            holder.q2.setColorFilter(accentColor, PorterDuff.Mode.SRC_IN);

            holder.q0.setOnClickListener(v -> MusicPlayerRemote.openAndShuffleQueue(dataSet, true));
            holder.q1.setOnClickListener(v -> MusicPlayerRemote.openQueue(LastAddedLoader.getLastAddedSongs(activity), 0, true));
            holder.q2.setOnClickListener(v -> MusicPlayerRemote.openQueue(TopAndRecentlyPlayedTracksLoader.getTopTracks(activity), 0, true));

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                holder.q0.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Toast.makeText(activity, R.string.action_shuffle_all, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                holder.q0.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Toast.makeText(activity, R.string.play_last_added, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                holder.q0.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Toast.makeText(activity, R.string.play_top_tracks, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
            }
        } else {
            super.onBindViewHolder(holder, position - 1);
        }
    }


    public class ViewHolder extends AbsOffsetSongAdapter.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

        }

    }

}
