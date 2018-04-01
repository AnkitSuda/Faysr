package app.androidgrid.faysr.adapter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import app.androidgrid.faysr.R;
import app.androidgrid.faysr.glide.FaysrColoredTarget;
import app.androidgrid.faysr.glide.SongGlideRequest;
import app.androidgrid.faysr.misc.CustomFragmentStatePagerAdapter;
import app.androidgrid.faysr.model.Song;
import app.androidgrid.faysr.ui.fragments.player.flat.FlatPlayerAlbumCoverFragment;
import app.androidgrid.faysr.util.PreferenceUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ankit on 21/2/18.
 */

public class FlatAlbumCoverPagerAdapter extends CustomFragmentStatePagerAdapter {
    public static final String TAG = FlatAlbumCoverPagerAdapter.class.getSimpleName();

    private ArrayList<Song> dataSet;

    private FlatAlbumCoverFragment.ColorReceiver currentColorReceiver;
    private int currentColorReceiverPosition = -1;

    public FlatAlbumCoverPagerAdapter(FragmentManager fm, ArrayList<Song> dataSet) {
        super(fm);
        this.dataSet = dataSet;
    }


    @Override
    public Fragment getItem(final int position) {
        return FlatAlbumCoverPagerAdapter.FlatAlbumCoverFragment.newInstance(dataSet.get(position));
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object o = super.instantiateItem(container, position);
        if (currentColorReceiver != null && currentColorReceiverPosition == position) {
            receiveColor(currentColorReceiver, currentColorReceiverPosition);
            //setUpRotation(position);
        }
        return o;
    }

    /**
     * Only the latest passed {@link AlbumCoverPagerAdapter.AlbumCoverFragment.ColorReceiver} is guaranteed to receive a response
     */
    public void receiveColor(FlatAlbumCoverFragment.ColorReceiver colorReceiver, int position) {
        FlatAlbumCoverFragment fragment = (FlatAlbumCoverFragment) getFragment(position);
        if (fragment != null) {
            currentColorReceiver = null;
            currentColorReceiverPosition = -1;
            fragment.receiveColor(colorReceiver, position);

        } else {
            currentColorReceiver = colorReceiver;
            currentColorReceiverPosition = position;
        }
    }

    public static class FlatAlbumCoverFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private static final String SONG_ARG = "song";

        private Unbinder unbinder;

        @BindView(R.id.flat_album_cover)
        public ImageView albumCover;

        private boolean isColorReady;
        private int color;
        private Song song;
        private ColorReceiver colorReceiver;
        private int request;

        public static FlatAlbumCoverFragment newInstance(final Song song) {
            FlatAlbumCoverFragment frag = new FlatAlbumCoverFragment();
            final Bundle args = new Bundle();
            args.putParcelable(SONG_ARG, song);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            song = getArguments().getParcelable(SONG_ARG);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_flat_album_cover, container, false);
            unbinder = ButterKnife.bind(this, view);
            //albumCover = view.findViewById(R.id.player_image);
            return view;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            //forceSquareAlbumCover(false);
            // TODO
//            forceSquareAlbumCover(PreferenceUtil.getInstance(getContext()).forceSquareAlbumCover());
            PreferenceUtil.getInstance(getActivity()).registerOnSharedPreferenceChangedListener(this);
            loadAlbumCover();
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            PreferenceUtil.getInstance(getActivity()).unregisterOnSharedPreferenceChangedListener(this);
            unbinder.unbind();
            colorReceiver = null;
        }

        private void loadAlbumCover() {
            SongGlideRequest.Builder.from(Glide.with(this), song)
                    .checkIgnoreMediaStore(getActivity())
                    .generatePalette(getActivity()).build()
                    .into(new FaysrColoredTarget(albumCover) {
                        @Override
                        public void onColorReady(int color) {
                            setColor(color);
                        }
                    });
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case PreferenceUtil.FORCE_SQUARE_ALBUM_COVER:
                    // TODO
//                    forceSquareAlbumCover(PreferenceUtil.getInstance(getActivity()).forceSquareAlbumCover());
                    break;
            }
        }

        public void forceSquareAlbumCover(boolean forceSquareAlbumCover) {
            albumCover.setScaleType(forceSquareAlbumCover ? ImageView.ScaleType.FIT_CENTER : ImageView.ScaleType.CENTER_CROP);
        }

        private void setColor(int color) {
            this.color = color;
            isColorReady = true;
            if (colorReceiver != null) {
                colorReceiver.onColorReady(color, request);
                colorReceiver = null;
            }
        }

        public void receiveColor(ColorReceiver colorReceiver, int request) {
            if (isColorReady) {
                colorReceiver.onColorReady(color, request);
            } else {
                this.colorReceiver = colorReceiver;
                this.request = request;
            }
        }

        public interface ColorReceiver {
            void onColorReady(int color, int request);
        }

    }
}