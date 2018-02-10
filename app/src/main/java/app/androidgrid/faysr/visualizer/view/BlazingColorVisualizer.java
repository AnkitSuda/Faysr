package app.androidgrid.faysr.visualizer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import app.androidgrid.faysr.visualizer.BaseVisualizer;


/**
 * TODO
 *
 * Created by gautam chibde on 29/10/17.
 */

public class BlazingColorVisualizer extends BaseVisualizer {
    private Shader shader;

    public BlazingColorVisualizer(Context context) {
        super(context);
    }

    public BlazingColorVisualizer(Context context,
                                  @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BlazingColorVisualizer(Context context,
                                  @Nullable AttributeSet attrs,
                                  int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        shader = new LinearGradient(0,
                0,
                0,
                getHeight(),
                Color.BLUE,
                Color.GREEN,
                Shader.TileMode.MIRROR /*or REPEAT*/);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bytes != null) {
            paint.setShader(shader);
            for (int i = 0, k = 0; i < (bytes.length - 1) && k < bytes.length; i++, k++) {
                int top = canvas.getHeight() +
                        ((byte) (Math.abs(bytes[k]) + 128)) * canvas.getHeight() / 128;
                canvas.drawLine(i, getHeight(), i, top, paint);
            }
            super.onDraw(canvas);
        }
    }
}