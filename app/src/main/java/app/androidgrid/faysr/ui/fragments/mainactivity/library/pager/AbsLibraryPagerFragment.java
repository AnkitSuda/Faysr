package app.androidgrid.faysr.ui.fragments.mainactivity.library.pager;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.util.ColorUtil;

import app.androidgrid.faysr.R;
import app.androidgrid.faysr.ui.activities.MainActivity;
import app.androidgrid.faysr.ui.fragments.AbsMusicServiceFragment;
import app.androidgrid.faysr.ui.fragments.mainactivity.library.LibraryFragment;
import app.androidgrid.faysr.ui.fragments.mainactivity.library.pager.creators.CreatorsFragment;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class AbsLibraryPagerFragment extends AbsMusicServiceFragment {

    /* http://stackoverflow.com/a/2888433 */
    @Override
    public LoaderManager getLoaderManager() {
        return getParentFragment().getLoaderManager();
    }

    public LibraryFragment getLibraryFragment() {
        return (LibraryFragment) getParentFragment();
    }

    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public Toolbar getToolbar() {
        return getLibraryFragment().getToolbar();
    }

    // WORKAROUND
    public void setStatusbarColor(View view, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final View statusBar = view.findViewById(R.id.status_bar);
            if (statusBar != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    statusBar.setBackgroundColor(ColorUtil.darkenColor(color));
                    getMainActivity().setLightStatusbarAuto(color);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    statusBar.setBackgroundColor(color);
                    getMainActivity().setLightStatusbarAuto(color);
                }
            }
        }
    }

    public void setStatusbarColorAuto(View view) {
        // we don't want to use statusbar color because we are doing the color darkening on our own to support KitKat
        setStatusbarColor(view, ThemeStore.primaryColor(getContext()));
    }
}
