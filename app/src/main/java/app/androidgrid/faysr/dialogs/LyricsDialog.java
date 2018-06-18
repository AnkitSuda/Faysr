package app.androidgrid.faysr.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kabouzeid.appthemehelper.color.MaterialColor;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.kabouzeid.appthemehelper.util.MaterialValueHelper;

import app.androidgrid.faysr.R;
import app.androidgrid.faysr.dialogs.base.AbsRoundedBottomSheetDialogFragment;
import app.androidgrid.faysr.model.lyrics.Lyrics;
import app.androidgrid.faysr.ui.activities.base.AbsThemeActivity;
import app.androidgrid.faysr.util.Util;
import app.androidgrid.faysr.util.ViewUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class LyricsDialog extends AbsRoundedBottomSheetDialogFragment {
    @BindView(R.id.title)
    TextView titleTxt;
    @BindView(R.id.line)
    TextView lineTxt;

    public static String name;
    public static String ly;

    public static LyricsDialog create(@NonNull Lyrics lyrics) {
        LyricsDialog dialog = new LyricsDialog();
        /*Bundle args = new Bundle();
        args.putString("title", lyrics.song.title);
        args.putString("lyrics", lyrics.getText());*/
        name = lyrics.song.title;
        ly = lyrics.getText();
        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialogView = View.inflate(getActivity(), R.layout.sheet_lyric, null);
        ButterKnife.bind(this, dialogView);

        titleTxt.setText(name);
        lineTxt.setText(ly);

        return dialogView;
    }

    /*
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //noinspection ConstantConditions
        return new MaterialDialog.Builder(getActivity())
                .title(getArguments().getString("title"))
                .content(getArguments().getString("lyrics"))
                .build();
    }*/
}
