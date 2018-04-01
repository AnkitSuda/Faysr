package app.androidgrid.faysr.ui.activities.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.view.View;
import android.view.Window;

import com.kabouzeid.appthemehelper.ATH;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.color.MaterialColor;
import com.kabouzeid.appthemehelper.common.ATHToolbarActivity;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.kabouzeid.appthemehelper.util.MaterialDialogsUtil;
import app.androidgrid.faysr.R;
import app.androidgrid.faysr.util.NavigationUtil;
import app.androidgrid.faysr.util.PreferenceUtil;
import app.androidgrid.faysr.util.Util;

import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */

public abstract class AbsThemeActivity extends ATHToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // default theme
        if (!ThemeStore.isConfigured(this, 1)) {
            ThemeStore.editTheme(this)
                    .activityTheme(R.style.Theme_Phonograph_Light)
                    .primaryColorRes(R.color.md_white_1000)
                    .accentColorRes(R.color.md_blue_600)
                    .commit();
        }

        String currentTheme = PreferenceUtil.getInstance(getApplicationContext()).getGeneralThemeNem();
        switch (currentTheme) {
            case "light":
                if (ThemeStore.primaryColor(getApplicationContext()) != getResources().getColor(R.color.md_white_1000)) {
                    ThemeStore.editTheme(getApplicationContext())
                            .primaryColorRes(R.color.md_white_1000)
                            .commit();
                }
                break;
            case "dark":
                if (ThemeStore.primaryColor(getApplicationContext()) != getResources().getColor(R.color.md_black_1000)) {
                    ThemeStore.editTheme(getApplicationContext())
                            .primaryColorRes(R.color.md_black_1000)
                            .commit();
                }
                break;
            case "black":
                if (ThemeStore.primaryColor(getApplicationContext()) != getResources().getColor(R.color.md_black_1000)) {
                    ThemeStore.editTheme(getApplicationContext())
                            .primaryColorRes(R.color.md_black_1000)
                            .commit();
                }
                break;
        }

        getSharedPreferences("[[kabouzeid_app-theme-helper]]", 0).edit().putInt("activity_theme", PreferenceUtil.getInstance(this).getGeneralTheme()).commit(); // TEMPORARY FIX
        super.onCreate(savedInstanceState);
        MaterialDialogsUtil.updateMaterialDialogsThemeSingleton(this);
    }

    protected void setDrawUnderStatusbar(boolean drawUnderStatusbar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            Util.setAllowDrawUnderStatusBar(getWindow());
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            Util.setStatusBarTranslucent(getWindow());
    }

    /**
     * This will set the color of the view with the id "status_bar" on KitKat and Lollipop.
     * On Lollipop if no such view is found it will set the statusbar color using the native method.
     *
     * @param color the new statusbar color (will be shifted down on Lollipop and above)
     */
    public void setStatusbarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final View statusBar = getWindow().getDecorView().getRootView().findViewById(R.id.status_bar);
            if (statusBar != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    statusBar.setBackgroundColor(ColorUtil.darkenColor(color));
                    setLightStatusbarAuto(color);
                } else {
                    statusBar.setBackgroundColor(color);
                }
            } else if (Build.VERSION.SDK_INT >= 21) {
                getWindow().setStatusBarColor(ColorUtil.darkenColor(color));
                setLightStatusbarAuto(color);
            }
        }
    }

    public void setStatusbarColorAuto() {
        // we don't want to use statusbar color because we are doing the color darkening on our own to support KitKat
        setStatusbarColor(ThemeStore.primaryColor(this));
    }

    public void setTaskDescriptionColor(@ColorInt int color) {
        ATH.setTaskDescriptionColor(this, color);
    }

    public void setTaskDescriptionColorAuto() {
        setTaskDescriptionColor(ThemeStore.primaryColor(this));
    }

    public void setNavigationbarColor(int color) {
        if (ThemeStore.coloredNavigationBar(this)) {
            if (ColorUtil.isColorLight(color)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    View view = new View(this);
                    /*Window window = getWindow();
                    window.requestFeature(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);*/
                    view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                } else {
                    ATH.setNavigationbarColor(this, ColorUtil.shiftColor(color, 0.8f));
                    return;
                }
                ATH.setNavigationbarColor(this, ColorUtil.shiftColor(color, 0.8f));
            } else {
                ATH.setNavigationbarColor(this, color);
            }
        } else {
            ATH.setNavigationbarColor(this, Color.BLACK);
        }
    }

    public void setNavigationbarColorAuto() {
        setNavigationbarColor(ThemeStore.navigationBarColor(this));
    }

    public void setLightStatusbar(boolean enabled) {
        ATH.setLightStatusbar(this, enabled);
    }

    public void setLightStatusbarAuto(int bgColor) {
        setLightStatusbar(ColorUtil.isColorLight(bgColor));
    }
}