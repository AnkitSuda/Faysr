package app.androidgrid.faysr.ui.fragments.mainactivity.library.pager.creators;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import app.androidgrid.faysr.ui.fragments.AbsMusicServiceFragment;
import app.androidgrid.faysr.ui.fragments.mainactivity.library.LibraryFragment;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class AbsCreatorsPagerFragment extends AbsMusicServiceFragment {

    /* http://stackoverflow.com/a/2888433 */
    @Override
    public LoaderManager getLoaderManager() {
        return getParentFragment().getLoaderManager();
    }

    public CreatorsFragment getCreatorsFragment() {
        return (CreatorsFragment) getParentFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }
}
