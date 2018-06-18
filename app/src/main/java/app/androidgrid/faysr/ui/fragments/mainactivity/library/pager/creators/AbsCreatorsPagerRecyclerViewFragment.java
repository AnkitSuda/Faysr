package app.androidgrid.faysr.ui.fragments.mainactivity.library.pager.creators;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kabouzeid.appthemehelper.ThemeStore;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import app.androidgrid.faysr.R;
import app.androidgrid.faysr.ui.fragments.mainactivity.library.pager.AbsLibraryPagerFragment;
import app.androidgrid.faysr.util.ViewUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public abstract class AbsCreatorsPagerRecyclerViewFragment<A extends RecyclerView.Adapter, LM extends RecyclerView.LayoutManager> extends AbsCreatorsPagerFragment implements OnOffsetChangedListener {

    public static final String TAG = AbsCreatorsPagerRecyclerViewFragment.class.getSimpleName();

    private Unbinder unbinder;

    @BindView(R.id.container)
    View container;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @Nullable
    @BindView(android.R.id.empty)
    TextView empty;

    private A adapter;
    private LM layoutManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getCreatorsFragment().addOnAppBarOffsetChangedListener(this);

        initLayoutManager();
        initAdapter();
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        if (recyclerView instanceof FastScrollRecyclerView) {
            ViewUtil.setUpFastScrollRecyclerViewColor(getActivity(), ((FastScrollRecyclerView) recyclerView), ThemeStore.accentColor(getActivity()));
        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    protected void invalidateLayoutManager() {
        initLayoutManager();
        recyclerView.setLayoutManager(layoutManager);
    }

    protected void invalidateAdapter() {
        initAdapter();
        checkIsEmpty();
        recyclerView.setAdapter(adapter);
    }

    private void initAdapter() {
        adapter = createAdapter();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkIsEmpty();
            }
        });
    }

    private void initLayoutManager() {
        layoutManager = createLayoutManager();
    }

    protected A getAdapter() {
        return adapter;
    }

    protected LM getLayoutManager() {
        return layoutManager;
    }

    protected RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        container.setPadding(container.getPaddingLeft(), container.getPaddingTop(), container.getPaddingRight(), getCreatorsFragment().getTotalAppBarScrollingRange() + i);
    }

    private void checkIsEmpty() {
        if (empty != null) {
            empty.setText(getEmptyMessage());
            empty.setVisibility(adapter == null || adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    @StringRes
    protected int getEmptyMessage() {
        return R.string.empty;
    }

    @LayoutRes
    protected int getLayoutRes() {
        return R.layout.fragment_main_activity_recycler_view;
    }

    protected abstract LM createLayoutManager();

    @NonNull
    protected abstract A createAdapter();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getCreatorsFragment().removeOnAppBarOffsetChangedListener(this);
        unbinder.unbind();
    }
}
