package app.androidgrid.faysr.glide;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.kabouzeid.appthemehelper.util.ATHUtil;
import app.androidgrid.faysr.R;
import app.androidgrid.faysr.glide.palette.BitmapPaletteTarget;
import app.androidgrid.faysr.glide.palette.BitmapPaletteWrapper;
import app.androidgrid.faysr.util.FaysrColorUtil;

import static app.androidgrid.faysr.util.FaysrColorUtil.getColor;

public abstract class FaysrColoredTarget extends BitmapPaletteTarget {
    public FaysrColoredTarget(ImageView view) {
        super(view);
    }

    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        super.onLoadFailed(e, errorDrawable);
        onColorReady(getDefaultFooterColor());
    }

    @Override
    public void onResourceReady(BitmapPaletteWrapper resource, GlideAnimation<? super BitmapPaletteWrapper> glideAnimation) {
        super.onResourceReady(resource, glideAnimation);
        int defaultColor = getDefaultFooterColor();
        int primaryColor = getColor(resource.getPalette(), defaultColor);

        onColorReady(getColor(resource.getPalette(), primaryColor));
    }

    protected int getDefaultFooterColor() {
        return ATHUtil.resolveColor(getView().getContext(), R.attr.defaultFooterColor);
    }

    protected int getAlbumArtistFooterColor() {
        return ATHUtil.resolveColor(getView().getContext(), R.attr.cardBackgroundColor);
    }

    public abstract void onColorReady(int color);
}
