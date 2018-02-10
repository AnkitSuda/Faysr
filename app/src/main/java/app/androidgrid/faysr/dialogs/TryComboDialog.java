package app.androidgrid.faysr.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEColorPreference;
import com.kabouzeid.appthemehelper.util.ColorUtil;

import app.androidgrid.faysr.R;
import app.androidgrid.faysr.ui.activities.SettingsActivity;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by ankit on 18/12/17.
 */

public class TryComboDialog extends BottomSheetDialogFragment {
    @BindView(R.id.try_wb_combo_close)
    AppCompatButton btnClose;
    @BindView(R.id.try_wb_combo_done)
    AppCompatButton btnTry;

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = View.inflate(getContext(), R.layout.try_white_theme_layout, null);
        dialog.setContentView(view);
        ButterKnife.bind(this, view);
        setUpButtons();
    }

    private void setUpButtons() {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        btnTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpColors();
            }
        });
    }

    private void setUpColors() {


    }

}
