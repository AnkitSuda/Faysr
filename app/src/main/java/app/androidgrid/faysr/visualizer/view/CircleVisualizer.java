package app.androidgrid.faysr.visualizer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import app.androidgrid.faysr.visualizer.BaseVisualizer;


/**
 * Custom view that creates a circle visualizer effect for
 * the android {@link android.media.MediaPlayer}
 *
 * Created by gautam on 13/11/17.
 */
public class CircleVisualizer extends BaseVisualizer {
    private float[] points;
    private float radiusMultiplier = 1;
    private float strokeWidth = 0.005f;

    public CircleVisualizer(Context context) {
        super(context);
    }

    public CircleVisualizer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleVisualizer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
    }

    /**
     * set Stroke width for your visualizer takes input between 1-10
     *
     * @param strokeWidth stroke width between 1-10
     */
    public void setStrokeWidth(int strokeWidth) {
        if (strokeWidth > 10) {
            this.strokeWidth = 10 * 0.005f;
        } else if (strokeWidth < 1) {
            this.strokeWidth = 0.005f;
        }
        this.strokeWidth = strokeWidth * 0.005f;
    }

    /**
     * This method sets the multiplier to the circle, by default the
     * multiplier is set to 1. you can provide value more than 1 to
     * increase size of the circle visualizer.
     *
     * @param radiusMultiplier multiplies to the radius of the circle.
     */
    public void setRadiusMultiplier(float radiusMultiplier) {
        this.radiusMultiplier = radiusMultiplier;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bytes != null) {
            paint.setStrokeWidth(getHeight() * strokeWidth);
            if (points == null || points.length < bytes.length * 4) {
                points = new float[bytes.length * 4];
            }
            double angle = 0;

            try {
                for (int i = 0; i < 360; i++, angle++) {
                    points[i * 4] = (float) (getWidth() / 2
                            + Math.abs(bytes[i * 2])
                            * radiusMultiplier
                            * Math.cos(Math.toRadians(angle)));
                    points[i * 4 + 1] = (float) (getHeight() / 2
                            + Math.abs(bytes[i * 2])
                            * radiusMultiplier
                            * Math.sin(Math.toRadians(angle)));

                    points[i * 4 + 2] = (float) (getWidth() / 2
                            + Math.abs(bytes[i * 2 + 1])
                            * radiusMultiplier
                            * Math.cos(Math.toRadians(angle + 1)));

                    points[i * 4 + 3] = (float) (getHeight() / 2
                            + Math.abs(bytes[i * 2 + 1])
                            * radiusMultiplier
                            * Math.sin(Math.toRadians(angle + 1)));
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            canvas.drawLines(points, paint);
        }
        super.onDraw(canvas);
    }
}