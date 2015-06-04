package tk.al54.dev.nspu.lessonschedule;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class AboutActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.about);
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        String baseText = getString(R.string.app_name) + "\n";
        baseText += getString(R.string.version) + " " + versionName +" ("+getString(R.string.build)+" "+versionCode+")\n";
        baseText += getString(R.string.about_text);
        TextView tv = (TextView)findViewById(R.id.aboutTextView);
        tv.setText(baseText);
    }
}
