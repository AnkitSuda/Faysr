package app.androidgrid.faysr.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import app.androidgrid.faysr.R;
import app.androidgrid.faysr.ui.fragments.mainactivity.library.pager.creators.AlbumsFragment;
import app.androidgrid.faysr.ui.fragments.mainactivity.library.pager.creators.ArtistsFragment;
import app.androidgrid.faysr.ui.fragments.mainactivity.library.pager.PlaylistsFragment;
import app.androidgrid.faysr.ui.fragments.mainactivity.library.pager.SongsFragment;
import app.androidgrid.faysr.util.PreferenceUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MusicLibraryPagerAdapter extends FragmentPagerAdapter {

    private final SparseArray<WeakReference<Fragment>> mFragmentArray = new SparseArray<>();

    private final List<Holder> mHolderList = new ArrayList<>();

    @NonNull
    private final Context mContext;

    @NonNull
    public final String[] titles;

    @NonNull
    private final int[] icons;

    public MusicLibraryPagerAdapter(@NonNull final Context context, final FragmentManager fragmentManager) {
        super(fragmentManager);
        mContext = context;
        titles = new String[]{
                context.getResources().getString(R.string.songs),
                context.getResources().getString(R.string.albums),
                context.getResources().getString(R.string.artists),
                context.getResources().getString(R.string.playlists)
        };

        icons = new int[]{
                R.drawable.ic_music_note_white_24dp,
                R.drawable.ic_album_white_24dp,
                R.drawable.ic_person_white_24dp,
                R.drawable.ic_queue_music_white_24dp};

        final MusicFragments[] fragments = MusicFragments.values();
        for (final MusicFragments fragment : fragments) {
            add(fragment.getFragmentClass(), null);
        }
    }

    @SuppressWarnings("synthetic-access")
    public void add(@NonNull final Class<? extends Fragment> className, final Bundle params) {
        final Holder mHolder = new Holder();
        mHolder.mClassName = className.getName();
        mHolder.mParams = params;

        final int mPosition = mHolderList.size();
        mHolderList.add(mPosition, mHolder);
        notifyDataSetChanged();
    }

    public Fragment getFragment(final int position) {
        final WeakReference<Fragment> mWeakFragment = mFragmentArray.get(position);
        if (mWeakFragment != null && mWeakFragment.get() != null) {
            return mWeakFragment.get();
        }
        return getItem(position);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        final Fragment mFragment = (Fragment) super.instantiateItem(container, position);
        final WeakReference<Fragment> mWeakFragment = mFragmentArray.get(position);
        if (mWeakFragment != null) {
            mWeakFragment.clear();
        }
        mFragmentArray.put(position, new WeakReference<>(mFragment));
        return mFragment;
    }

    @Override
    public Fragment getItem(final int position) {
        final Holder mCurrentHolder = mHolderList.get(position);
        return Fragment.instantiate(mContext,
                mCurrentHolder.mClassName, mCurrentHolder.mParams);
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        super.destroyItem(container, position, object);
        final WeakReference<Fragment> mWeakFragment = mFragmentArray.get(position);
        if (mWeakFragment != null) {
            mWeakFragment.clear();
        }
    }

    @Override
    public int getCount() {
        return mHolderList.size();
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        if (PreferenceUtil.getInstance(mContext).getTabIcons()) return null;
        return titles[position]
                .toUpperCase(Locale.getDefault());
    }

    @NonNull
    public int[] getIcons() {
        return icons;
    }


    public enum MusicFragments {
        SONG(SongsFragment.class),
        ALBUM(AlbumsFragment.class),
        ARTIST(ArtistsFragment.class),
        PLAYLIST(PlaylistsFragment.class);

        private final Class<? extends Fragment> mFragmentClass;

        MusicFragments(final Class<? extends Fragment> fragmentClass) {
            mFragmentClass = fragmentClass;
        }

        public Class<? extends Fragment> getFragmentClass() {
            return mFragmentClass;
        }

    }

    private final static class Holder {
        String mClassName;

        Bundle mParams;
    }
}
