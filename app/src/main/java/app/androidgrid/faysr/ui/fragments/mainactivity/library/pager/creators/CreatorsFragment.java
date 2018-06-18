package app.androidgrid.faysr.ui.fragments.mainactivity.library.pager.creators;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialcab.MaterialCab;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.util.TabLayoutUtil;
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper;

import app.androidgrid.faysr.R;
import app.androidgrid.faysr.adapter.CreatorsPagerAdapter;
import app.androidgrid.faysr.interfaces.CabHolder;
import app.androidgrid.faysr.ui.activities.MainActivity;
import app.androidgrid.faysr.ui.fragments.mainactivity.AbsMainActivityFragment;
import app.androidgrid.faysr.ui.fragments.mainactivity.library.LibraryFragment;
import app.androidgrid.faysr.ui.fragments.mainactivity.library.pager.AbsLibraryPagerFragment;
import app.androidgrid.faysr.util.FaysrColorUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static app.androidgrid.faysr.ui.fragments.mainactivity.library.LibraryFragment.appbar;
import static app.androidgrid.faysr.ui.fragments.mainactivity.library.LibraryFragment.refreshTabVisibility;
import static app.androidgrid.faysr.ui.fragments.mainactivity.library.LibraryFragment.tabs;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreatorsFragment extends AbsLibraryPagerFragment implements CabHolder, MainActivity.MainActivityFragmentCallbacks, ViewPager.OnPageChangeListener {

    public static final String TAG = CreatorsFragment.class.getSimpleName();

    @BindView(R.id.pager)
    ViewPager pager;

    private CreatorsPagerAdapter pagerAdapter;
    private MaterialCab cab;

    private Unbinder unbinder;

    public CreatorsFragment() {}

    public static CreatorsFragment newInstance() {
        Bundle args = new Bundle();
        CreatorsFragment fragment = new CreatorsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_creators, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void addOnAppBarOffsetChangedListener(AppBarLayout.OnOffsetChangedListener onOffsetChangedListener) {
        appbar.addOnOffsetChangedListener(onOffsetChangedListener);
    }

    public void removeOnAppBarOffsetChangedListener(AppBarLayout.OnOffsetChangedListener onOffsetChangedListener) {
        appbar.removeOnOffsetChangedListener(onOffsetChangedListener);
    }

    public int getTotalAppBarScrollingRange() {
        return appbar.getTotalScrollRange();
    }


    @Override
    public boolean handleBackPress() {
        if (cab != null && cab.isActive()) {
            cab.finish();
            return true;
        }
        return false;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setStatusbarColorAuto(view);
        getMainActivity().setNavigationbarColorAuto();
        refreshTabVisibility(new CreatorsFragment());
        getToolbar().setTitle(R.string.action_meta);
        setUpViewPager();
    }


    private void setUpViewPager() {
        if (tabs == null) return;

        pagerAdapter = new CreatorsPagerAdapter(getActivity(), getChildFragmentManager());
        pager.setAdapter(pagerAdapter);

        tabs.setupWithViewPager(pager);

        int primaryColor = ThemeStore.primaryColor(getActivity());
        int normalColor = ToolbarContentTintHelper.toolbarSubtitleColor(getActivity(), primaryColor);
        int selectedColor = ThemeStore.accentColor(getActivity()); /*ToolbarContentTintHelper.toolbarTitleColor(getActivity(), primaryColor);*/
        TabLayoutUtil.setTabIconColors(tabs, normalColor, selectedColor);
        tabs.setTabTextColors(normalColor, selectedColor);
        tabs.setSelectedTabIndicatorColor(ThemeStore.accentColor(getActivity()));

        pager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @NonNull
    @Override
    public MaterialCab openCab(int menuRes, MaterialCab.Callback callback) {
        if (cab != null && cab.isActive()) cab.finish();
        cab = new MaterialCab(getMainActivity(), R.id.cab_stub)
                .setMenu(menuRes)
                .setCloseDrawableRes(R.drawable.ic_close_white_24dp)
                .setBackgroundColor(FaysrColorUtil.shiftBackgroundColorForLightText(ThemeStore.primaryColor(getActivity())))
                .start(callback);
        return cab;
    }
}
