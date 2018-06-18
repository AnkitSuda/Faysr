package app.androidgrid.faysr.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.kabouzeid.appthemehelper.ThemeStore;
import app.androidgrid.faysr.App;
import app.androidgrid.faysr.EmptyActivity;
import app.androidgrid.faysr.R;
import app.androidgrid.faysr.dialogs.ChangelogDialog;
import app.androidgrid.faysr.dialogs.DonationsDialog;
import app.androidgrid.faysr.ui.activities.base.AbsBaseActivity;
import app.androidgrid.faysr.ui.activities.bugreport.BugReportActivity;
import app.androidgrid.faysr.ui.activities.intro.AppIntroActivity;

import app.androidgrid.faysr.util.NavigationUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.psdev.licensesdialog.LicensesDialog;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
@SuppressWarnings("FieldCanBeLocal")
public class AboutActivity extends AbsBaseActivity implements View.OnClickListener {

    private static String GITHUB = "https://github.com/kabouzeid/Phonograph";

    private static String GOOGLE_PLUS = "https://plus.google.com/104050651697995833326";
    private static String TWITTER = "https://twitter.com/SudaAnkit";
    private static String WEBSITE = "https://kabouzeid.com/";

    private static String GOOGLE_PLUS_COMMUNITY = "https://plus.google.com/u/0/communities/106227738496107108513";
    private static String TRANSLATE = "https://phonograph.oneskyapp.com/collaboration/project?id=26521";
    private static String RATE_ON_GOOGLE_PLAY = "https://play.google.com/store/apps/details?id=app.androidgrid.faysr";

    private static String KARIM_ABOU_ZEID_GOOGLE_PLUS = "https://google.com/+AidanFollestad";
    private static String AIDAN_FOLLESTAD_GITHUB = "https://github.com/afollestad";

    private static String MICHAEL_COOK_GOOGLE_PLUS = "https://plus.google.com/102718493746376292361";
    private static String MICHAEL_COOK_WEBSITE = "https://cookicons.co/";

    private static String MAARTEN_CORPEL_GOOGLE_PLUS = "https://google.com/+MaartenCorpel";

    private static String ALEKSANDAR_TESIC_GOOGLE_PLUS = "https://google.com/+aleksandartešić";

    private static String EUGENE_CHEUNG_GITHUB = "https://github.com/arkon";
    private static String EUGENE_CHEUNG_WEBSITE = "https://echeung.me/";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_version)
    TextView appVersion;
    @BindView(R.id.changelog)
    LinearLayout changelog;
    @BindView(R.id.intro)
    LinearLayout intro;
    @BindView(R.id.licenses)
    LinearLayout licenses;
    @BindView(R.id.write_an_email)
    LinearLayout writeAnEmail;
    @BindView(R.id.add_to_google_plus_circles)
    LinearLayout addToGooglePlusCircles;
    @BindView(R.id.follow_on_twitter)
    LinearLayout followOnTwitter;
    @BindView(R.id.fork_on_github)
    LinearLayout forkOnGitHub;

    @BindView(R.id.report_bugs)
    LinearLayout reportBugs;
    @BindView(R.id.join_google_plus_community)
    LinearLayout joinGooglePlusCommunity;
    @BindView(R.id.translate)
    LinearLayout translate;
    @BindView(R.id.donate)
    LinearLayout donate;
    @BindView(R.id.rate_on_google_play)
    LinearLayout rateOnGooglePlay;

    @BindView(R.id.logo)
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setDrawUnderStatusbar(true);
        ButterKnife.bind(this);

        setStatusbarColorAuto();
        setNavigationbarColorAuto();
        setTaskDescriptionColorAuto();

        setUpViews();

        logo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(AboutActivity.this, EmptyActivity.class);
                startActivity(intent);
                return true;
            }
        });
    }

    private void setUpViews() {
        setUpToolbar();
        setUpAppVersion();
        setUpOnClickListeners();
    }

    private void setUpToolbar() {
        toolbar.setBackgroundColor(ThemeStore.primaryColor(this));
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpAppVersion() {
        appVersion.setText(getCurrentVersionName(this));
    }

    private void setUpOnClickListeners() {
        changelog.setOnClickListener(this);
        intro.setOnClickListener(this);
        licenses.setOnClickListener(this);
        addToGooglePlusCircles.setOnClickListener(this);
        followOnTwitter.setOnClickListener(this);
        forkOnGitHub.setOnClickListener(this);
        reportBugs.setOnClickListener(this);
        writeAnEmail.setOnClickListener(this);
        joinGooglePlusCommunity.setOnClickListener(this);
        translate.setOnClickListener(this);
        rateOnGooglePlay.setOnClickListener(this);
        donate.setOnClickListener(this);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static String getCurrentVersionName(@NonNull final Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName + (App.isProVersion() ? " Pro" : "");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "Unkown";
    }

    @Override
    public void onClick(View v) {
        if (v == changelog) {
            ChangelogDialog.create().show(getSupportFragmentManager(), "CHANGELOG_DIALOG");
        } else if (v == licenses) {
            showLicenseDialog();
        } else if (v == intro) {
            startActivity(new Intent(this, AppIntroActivity.class));
        } else if (v == addToGooglePlusCircles) {
            openUrl(GOOGLE_PLUS);
        } else if (v == followOnTwitter) {
            openUrl(TWITTER);
        } else if (v == forkOnGitHub) {
            openUrl(GITHUB);
        } else if (v == reportBugs) {
            startActivity(new Intent(this, BugReportActivity.class));
        } else if (v == writeAnEmail) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:ankitsuda123@gmail.com"));
            intent.putExtra(Intent.EXTRA_EMAIL, "ankitsuda123@gmail.com");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Faysr");
            startActivity(Intent.createChooser(intent, "Choose E-Mail app"));
        } else if (v == joinGooglePlusCommunity) {
            openUrl(GOOGLE_PLUS_COMMUNITY);
        } else if (v == translate) {
            openUrl(TRANSLATE);
        } else if (v == rateOnGooglePlay) {
            openUrl(RATE_ON_GOOGLE_PLAY);
        } else if (v == donate) {
            //Toast.makeText(this, R.string.donation_system_down, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SupportDevelopmentActivity.class));
        }
    }

    private void openUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void showLicenseDialog() {
        new LicensesDialog.Builder(this)
                .setNotices(R.raw.notices)
                .setTitle(R.string.licenses)
                .setNoticesCssStyle(getString(R.string.license_dialog_style)
                        .replace("{bg-color}", ThemeSingleton.get().darkTheme ? "424242" : "ffffff")
                        .replace("{text-color}", ThemeSingleton.get().darkTheme ? "ffffff" : "000000")
                        .replace("{license-bg-color}", ThemeSingleton.get().darkTheme ? "535353" : "eeeeee")
                )
                .setIncludeOwnLicense(true)
                .build()
                .showAppCompat();
    }
}
