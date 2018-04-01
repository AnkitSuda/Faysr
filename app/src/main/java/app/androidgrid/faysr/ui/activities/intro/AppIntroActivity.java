package app.androidgrid.faysr.ui.activities.intro;

import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import app.androidgrid.faysr.R;
import app.androidgrid.faysr.ui.fragments.player.NowPlayingScreen;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class AppIntroActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setButtonCtaVisible(true);
        setButtonNextVisible(false);
        setButtonBackVisible(false);

        setButtonCtaTintMode(BUTTON_CTA_TINT_MODE_TEXT);

        addSlide(new SimpleSlide.Builder()
                .title(R.string.app_name)
                .description(R.string.welcome_to_faysr)
                .image(R.drawable.icon_web)
                .background(R.color.md_blue_grey_100)
                .backgroundDark(R.color.md_blue_grey_200)
                .layout(R.layout.fragment_simple_slide_large_image)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.label_playing_queue)
                .description(R.string.open_playing_queue_instruction)
                .image(R.drawable.tutorial_open_queue)
                .background(R.color.md_deep_orange_500)
                .backgroundDark(R.color.md_deep_orange_600)
                .layout(R.layout.fragment_simple_slide_large_image)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.label_playing_queue)
                .description(R.string.open_page_instruction)
                .image(R.drawable.tutorial_change_page)
                .background(R.color.md_indigo_500)
                .backgroundDark(R.color.md_indigo_600)
                .layout(R.layout.fragment_simple_slide_large_image)
                .build());
    }
}
