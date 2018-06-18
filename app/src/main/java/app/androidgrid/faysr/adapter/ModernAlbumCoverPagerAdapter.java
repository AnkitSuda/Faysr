package app.androidgrid.faysr.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.kabouzeid.appthemehelper.util.MaterialValueHelper;

import java.util.ArrayList;

import app.androidgrid.faysr.R;
import app.androidgrid.faysr.glide.FaysrColoredTarget;
import app.androidgrid.faysr.glide.SongGlideRequest;
import app.androidgrid.faysr.helper.MusicPlayerRemote;
import app.androidgrid.faysr.loader.SongLoader;
import app.androidgrid.faysr.misc.CustomFragmentStatePagerAdapter;
import app.androidgrid.faysr.model.Song;
import app.androidgrid.faysr.util.MusicUtil;
import app.androidgrid.faysr.util.PreferenceUtil;
import app.androidgrid.faysr.visualizer.view.BarVisualizer;
import app.androidgrid.faysr.visualizer.view.CircleBarVisualizer;
import app.androidgrid.faysr.visualizer.view.CircleVisualizer;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ankit on 15/12/17.
 */

public class ModernAlbumCoverPagerAdapter extends CustomFragmentStatePagerAdapter {
    public static final String TAG = ModernAlbumCoverPagerAdapter.class.getSimpleName();

    private ArrayList<Song> dataSet;

    private ModernAlbumCoverPagerAdapter.ModernAlbumCoverFragment.ColorReceiver currentColorReceiver;
    private int currentColorReceiverPosition = -1;

    public ModernAlbumCoverPagerAdapter(FragmentManager fm, ArrayList<Song> dataSet) {
        super(fm);
        this.dataSet = dataSet;
    }

    @Override
    public Fragment getItem(final int position) {
        return ModernAlbumCoverPagerAdapter.ModernAlbumCoverFragment.newInstance(dataSet.get(position));
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
        }
        return o;
    }

    /**
     * Only the latest passed {@link ModernAlbumCoverPagerAdapter.ModernAlbumCoverFragment.ColorReceiver} is guaranteed to receive a response
     */
    public void receiveColor(ModernAlbumCoverPagerAdapter.ModernAlbumCoverFragment.ColorReceiver colorReceiver, int position) {
        ModernAlbumCoverPagerAdapter.ModernAlbumCoverFragment fragment = (ModernAlbumCoverPagerAdapter.ModernAlbumCoverFragment) getFragment(position);
        if (fragment != null) {
            currentColorReceiver = null;
            currentColorReceiverPosition = -1;
            fragment.receiveColor(colorReceiver, position);
      /*      if (MusicPlayerRemote.getCurrentSong().id == dataSet.get(position).id && ModernAlbumCoverFragment.visualizerView != null) {
                //fragment.toggleVisulizer(true);
            } else {
                //fragment.toggleVisulizer(false);
            }*/
        } else {
            currentColorReceiver = colorReceiver;
            currentColorReceiverPosition = position;
        }
    }

    public static class ModernAlbumCoverFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private static final String SONG_ARG = "song";

        private Unbinder unbinder;

        @BindView(R.id.modern_player_image)
        CircleImageView albumCover;
//        @BindView(R.id.modern_player_title)
//        TextView textTitle;
//        @BindView(R.id.modern_player_artist)
//        TextView textArtist;


        private boolean isColorReady;
        private int color;
        private Song song;
        private ModernAlbumCoverPagerAdapter.ModernAlbumCoverFragment.ColorReceiver colorReceiver;
        private int request;

        public static ModernAlbumCoverPagerAdapter.ModernAlbumCoverFragment newInstance(final Song song) {
            ModernAlbumCoverPagerAdapter.ModernAlbumCoverFragment frag = new ModernAlbumCoverPagerAdapter.ModernAlbumCoverFragment();
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
            View view = inflater.inflate(R.layout.fragment_modern_cover_album, container, false);
            unbinder = ButterKnife.bind(this, view);
            return view;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            // TODO
//            forceSquareAlbumCover(PreferenceUtil.getInstance(getContext()).forceSquareAlbumCover());
            PreferenceUtil.getInstance(getActivity()).registerOnSharedPreferenceChangedListener(this);
            loadAlbumCover();
            /*setTitle(song.title);
            setArtist(song.artistName);*/
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
/*
        private void setTitle(String str) {
            textTitle.setText(str);
        }

        private void setArtist(String str) {
            textArtist.setText(str);
        }*/

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case PreferenceUtil.FORCE_SQUARE_ALBUM_COVER:
                    // TODO
//                    forceSquareAlbumCover(PreferenceUtil.getInstance(getActivity()).forceSquareAlbumCover());
                    break;
            }
        }



        private void setColor(int color) {
            this.color = color;
            isColorReady = true;
            albumCover.setBorderColor(MaterialValueHelper.getPrimaryTextColor(getContext(), ColorUtil.isColorLight(color)));
            if (colorReceiver != null) {
                colorReceiver.onColorReady(color, request);
                colorReceiver = null;
            }
        }

        public void receiveColor(ModernAlbumCoverPagerAdapter.ModernAlbumCoverFragment.ColorReceiver colorReceiver, int request) {
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
