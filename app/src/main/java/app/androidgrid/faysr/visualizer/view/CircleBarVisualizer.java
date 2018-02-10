package app.androidgrid.faysr.visualizer.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLSurfaceView;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.Toast;

import app.androidgrid.faysr.visualizer.BaseVisualizer;

public class CircleBarVisualizer extends BaseVisualizer {
    private float[] points;
    private Paint circlePaint;
    private int radius;

    public CircleBarVisualizer(Context context) {
        super(context);
    }

    public CircleBarVisualizer(Context context,
                               @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleBarVisualizer(Context context,
                               @Nullable AttributeSet attrs,
                               int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        paint.setStyle(Paint.Style.STROKE);
        circlePaint = new Paint();
        radius = -1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (radius == -1) {
            radius = getHeight() < getWidth() ? getHeight() : getWidth();
            radius = (int) (radius * 0.65 / 2);
            double circumference = 2 * Math.PI * radius;
            paint.setStrokeWidth((float) (circumference / 120));
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeWidth(4);
        }
        circlePaint.setColor(color);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, circlePaint);
        if (bytes != null) {
            if (points == null || points.length < bytes.length * 4) {
                points = new float[bytes.length/* * 4*/];
            }
            double angle = 0;

            try {
                for (int i = 0; i < 120; i++, angle += 3) {
                    int x = (int) Math.ceil(i * 8.5);
                    int t = ((byte) (-Math.abs(bytes[x]) + 128))
                            * (canvas.getHeight() / 4) / 128;

                    points[i * 4] = (float) (getWidth() / 2
                            + radius
                            * Math.cos(Math.toRadians(angle)));

                    points[i * 4 + 1] = (float) (getHeight() / 2
                            + radius
                            * Math.sin(Math.toRadians(angle)));

                    points[i * 4 + 2] = (float) (getWidth() / 2
                            + (radius + t)
                            * Math.cos(Math.toRadians(angle)));

                    points[i * 4 + 3] = (float) (getHeight() / 2
                            + (radius + t)
                            * Math.sin(Math.toRadians(angle)));
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            canvas.drawLines(points, paint);
        }
        super.onDraw(canvas);
    }
}
