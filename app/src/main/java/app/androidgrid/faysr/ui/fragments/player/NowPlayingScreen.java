package app.androidgrid.faysr.ui.fragments.player;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import app.androidgrid.faysr.R;

public enum NowPlayingScreen {
   /* EXPLORE(R.string.explore, R.drawable.np_modern, 0),*/
    MODERN(R.string.modern, R.drawable.np_modern, 0),
    FLAT(R.string.flat, R.drawable.np_flat, 1),
    Minimal(R.string.minimal, R.drawable.np_minimal, 2),
    OLD_CARD(R.string.old_card, R.drawable.np_old_card, 3),
    OLD_FLAT(R.string.old_flat, R.drawable.np_old_flat, 4);

    @StringRes
    public final int titleRes;
    @DrawableRes
    public final int drawableResId;
    public final int id;

    NowPlayingScreen(@StringRes int titleRes, @DrawableRes int drawableResId, int id) {
        this.titleRes = titleRes;
        this.drawableResId = drawableResId;
        this.id = id;
    }
}
