package app.androidgrid.faysr.misc;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.media.AudioManager;
import android.os.Handler;
import android.provider.MediaStore;

/**
 * Created by ankit on 28/11/17.
 */

public class VolumeCheck extends ContentObserver {
    int perviusV;
    Context context;

    public VolumeCheck(Context context, Handler handler) {
        super(handler);
        this.context = context;

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        perviusV = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);


    }

    public void processVolumeCheck(int pV, int cV) {

    }
}
