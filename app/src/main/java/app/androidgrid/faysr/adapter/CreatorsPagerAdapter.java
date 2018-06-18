package app.androidgrid.faysr.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.androidgrid.faysr.R;
import app.androidgrid.faysr.ui.fragments.mainactivity.library.pager.creators.AlbumsFragment;
import app.androidgrid.faysr.ui.fragments.mainactivity.library.pager.creators.ArtistsFragment;

public class CreatorsPagerAdapter extends FragmentPagerAdapter {
    private final SparseArray<WeakReference<Fragment>> mFragmentArray = new SparseArray<>();

    private final List<Holder> mHolderList = new ArrayList<>();

    @NonNull
    private final Context mContext;

    @NonNull
    private final String[] titles;

    public CreatorsPagerAdapter(@NonNull final Context context, final FragmentManager fragmentManager) {
        super(fragmentManager);
        mContext = context;
        titles = new String[]{
                context.getResources().getString(R.string.albums),
                context.getResources().getString(R.string.artists),
        };
        final CreatorsFragments[] fragments = CreatorsFragments.values();
        for (final CreatorsFragments fragment : fragments) {
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

    @NonNull
    @Override
    public CharSequence getPageTitle(final int position) {
        return titles[position];
    }


    public enum CreatorsFragments {
        ALBUM(AlbumsFragment.class),
        ARTIST(ArtistsFragment.class);

        private final Class<? extends Fragment> mFragmentClass;

        CreatorsFragments(final Class<? extends Fragment> fragmentClass) {
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
