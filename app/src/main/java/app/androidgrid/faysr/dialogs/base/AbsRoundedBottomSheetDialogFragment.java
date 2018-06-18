package app.androidgrid.faysr.dialogs.base;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.View;

import com.kabouzeid.appthemehelper.ATH;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.util.ColorUtil;

import app.androidgrid.faysr.R;
import app.androidgrid.faysr.ui.activities.base.AbsThemeActivity;
import app.androidgrid.faysr.util.PreferenceUtil;
import app.androidgrid.faysr.util.Util;
import me.zhanghai.android.materialprogressbar.internal.ThemeUtils;

/**
 * [BottomSheetDialogFragment] that uses a custom
 * theme which sets a rounded background to the dialog
 * and doesn't dim the navigation bar
 */
public abstract class AbsRoundedBottomSheetDialogFragment extends BottomSheetDialogFragment {
    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //super.onCreateDialog(savedInstanceState);
        return new BottomSheetDialog(requireContext(), getTheme());
    }

    public void autoNavigationBarColor() {
        if (getActivity() == null) { Log.e("NavColorAuto", "null activity"); return; }
        AbsThemeActivity activity = (AbsThemeActivity) getActivity();


        if (ThemeStore.coloredNavigationBar(getActivity())) {
            if (Util.isLollipop()) {
                switch (PreferenceUtil.getInstance(activity).getGeneralTheme()) {
                    case R.style.Theme_Phonograph_Base:
                        ATH.setNavigationbarColor(activity, android.R.attr.windowBackground);
                        break;
                    case R.style.Theme_Phonograph_Light:
                        ATH.setNavigationbarColor(activity, ColorUtil.darkenColor(activity.getResources().getColor(R.color.md_white_1000)));
                        if (Util.isOreo()) {
                            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                            ATH.setNavigationbarColor(activity, R.color.md_white_1000);
                        }
                        break;
                    case R.style.Theme_Phonograph_Black:
                        ATH.setNavigationbarColor(activity, android.R.attr.windowBackground);
                        break;
                }
            }
        } else {
            ATH.setNavigationbarColor(activity, Color.BLACK);
        }
    }
}
