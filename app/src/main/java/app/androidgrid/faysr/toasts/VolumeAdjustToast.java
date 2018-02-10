package app.androidgrid.faysr.toasts;

import android.content.Context;
import android.media.AudioManager;
import android.renderscript.Sampler;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import app.androidgrid.faysr.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ankit on 28/11/17.
 */

public class VolumeAdjustToast extends Toast {
    private Context context;

    private AppCompatImageView[] vats;

    public VolumeAdjustToast(Context context) {
        super(context);
        this.context = context;
    }

    public void showToast() {
        View contentView = View.inflate(context, R.layout.volume_adjust_toast, null);
        setView(contentView);

        initViews(contentView);
        setVolume();
        show();
    }

    private void initViews(View view) {
        vats = new AppCompatImageView[15];
        vats[0] = (AppCompatImageView)view.findViewById(R.id.vat_1);
        vats[1] = ((AppCompatImageView)view.findViewById(R.id.vat_2));
        vats[2] = ((AppCompatImageView)view.findViewById(R.id.vat_3));
        vats[3] = ((AppCompatImageView)view.findViewById(R.id.vat_4));
        vats[4] = ((AppCompatImageView)view.findViewById(R.id.vat_5));
        vats[5] = ((AppCompatImageView)view.findViewById(R.id.vat_6));
        vats[6] = ((AppCompatImageView)view.findViewById(R.id.vat_7));
        vats[7] = ((AppCompatImageView)view.findViewById(R.id.vat_8));
        vats[8] = ((AppCompatImageView)view.findViewById(R.id.vat_9));
        vats[9] = ((AppCompatImageView)view.findViewById(R.id.vat_10));
        vats[10] = ((AppCompatImageView)view.findViewById(R.id.vat_11));
        vats[11] = ((AppCompatImageView)view.findViewById(R.id.vat_12));
        vats[12] = ((AppCompatImageView)view.findViewById(R.id.vat_13));
        vats[13] = ((AppCompatImageView)view.findViewById(R.id.vat_14));
        vats[14] = ((AppCompatImageView)view.findViewById(R.id.vat_15));
    }

    private void setVolume() {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int i = manager.getStreamVolume(AudioManager.STREAM_MUSIC);

        if (i > 0 && i < 6) {
            setLines(1);
        } else if (i > 6 && i < 12) {
            setLines(2);
        } else if (i > 12 && i < 18) {
            setLines(3);
        } else if (i > 18 && i < 24) {
            setLines(4);
        } else if (i > 24 && i < 30) {
            setLines(5);
        } else if (i > 30 && i < 36) {
            setLines(6);
        } else if (i > 36 && i < 42) {
            setLines(7);
        } else if (i > 42 && i < 48) {
            setLines(8);
        } else if (i > 48 && i < 54) {
            setLines(10);
        } else if (i > 60 && i < 72) {
            setLines(11);
        } else if (i > 72 && i < 78) {
            setLines(12);
        } else if (i > 78 && i < 84) {
            setLines(13);
        } else if (i > 84 && i < 90) {
            setLines(14);
        } else if (i > 90 && i < 101) {
            setLines(15);
        }
    }

    public void setLines(int i) {
        i = i - 1;
        for (int j = 0; j < i; j++) {
            vats[j].setImageResource(R.drawable.line);
        }
    }

}
