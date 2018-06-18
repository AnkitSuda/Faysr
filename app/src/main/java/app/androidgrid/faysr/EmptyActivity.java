package app.androidgrid.faysr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kabouzeid.appthemehelper.ThemeStore;

import app.androidgrid.faysr.visualizer.view.CircleBarVisualizer;

public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        CircleBarVisualizer cb = findViewById(R.id.cb);

        cb.setColor(ThemeStore.accentColor(this));

        cb.setPlayer();
    }
}
