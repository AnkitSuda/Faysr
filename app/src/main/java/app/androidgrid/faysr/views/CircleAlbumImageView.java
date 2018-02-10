package app.androidgrid.faysr.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import app.androidgrid.faysr.R;
import app.androidgrid.faysr.helper.MusicPlayerRemote;
import app.androidgrid.faysr.interfaces.MusicServiceEventListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ankit on 5/1/18.
 */

public class CircleAlbumImageView extends CircleImageView {
    private static final int ROTATE_ANIMATION_DURATION = 15000;

    public CircleAlbumImageView(Context context) {
        super(context);
    }

    public CircleAlbumImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleAlbumImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // Start the animation
        //startAnimation();
    }

    /**
     * Starts the rotate animation.
     */
    public void startRotation() {
        clearAnimation();

        RotateAnimation rotate = new RotateAnimation(
                getRotation(), 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setDuration(ROTATE_ANIMATION_DURATION);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setFillAfter(true);
        rotate.setRepeatMode(Animation.RESTART);
        startAnimation(rotate);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if (visibility == View.VISIBLE) {
            startRotation();
        } else {
            clearAnimation();
        }
    }
}
