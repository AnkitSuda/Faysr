package app.androidgrid.faysr.adapter.home;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import app.androidgrid.faysr.BuildConfig;
import app.androidgrid.faysr.R;
import app.androidgrid.faysr.adapter.base.MediaEntryViewHolder;
import app.androidgrid.faysr.glide.SongGlideRequest;
import app.androidgrid.faysr.helper.MusicPlayerRemote;
import app.androidgrid.faysr.model.Song;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ankit on 28/2/18.
 */

public class HorizontalItemAdapter extends RecyclerView.Adapter<HorizontalItemAdapter.ViewHolder> {
    private static final int SONG = 0;
    private static final int VIEW_ALL = 1;
    private static final String TAG = "HorizontalItemAdapter";
    private AppCompatActivity activity;
    private List<Song> dataSet = new ArrayList<>();

    public HorizontalItemAdapter(@NonNull AppCompatActivity activity) {
        this.activity = activity;
    }

    public void swapData(List<Song> songs) {
        dataSet = songs;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new ViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_image_1, parent, false));
        } else {
            return new ViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_image, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        Song song = (Song) dataSet.get(position);
        if (viewHolder.title != null) {
            viewHolder.title.setText(song.title);
        }
        if (viewHolder.image != null) {
            SongGlideRequest.Builder.from(Glide.with(activity), song).checkIgnoreMediaStore(activity).asBitmap().build().into(viewHolder.image);
        }

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    public class ViewHolder extends MediaEntryViewHolder {
        @Nullable
        @BindView(R.id.root)
        View mView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        @Override
        public void onClick(View view) {
            super.onClick(view);
            ArrayList<Song> songs = new ArrayList<>();
            songs.addAll(dataSet);
            MusicPlayerRemote.openQueue(songs, getAdapterPosition(), true);
        }
    }
}
