package app.androidgrid.faysr.ui.fragments.player;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import app.androidgrid.faysr.R;

public enum NowPlayingScreen {
    MODERN(R.string.modern, R.drawable.np_modern, 0),
    CARD(R.string.card, R.drawable.np_card, 1),
    FLAT(R.string.flat, R.drawable.np_flat, 2);

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
