package tk.al54.dev.nspu.lessonschedule;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class BouncyActivity extends ActionBarActivity implements OnClickListener {

    Button groupScheduleButton, teacherScheduleButton, preferencesButton, aboutButton;

    final static int ACTIVITY_PREFERENCES = 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bouncy);
        getSupportActionBar().setLogo(R.drawable.ic_menu);
        getSupportActionBar().setIcon(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DownloadFacultyXml.setContext(this);
        DownloadGroupsXml.setContext(this);
        DownloadTeachersXml.setContext(this);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        groupScheduleButton = (Button) findViewById(R.id.groupScheduleButton);
        groupScheduleButton.setOnClickListener(this);

        teacherScheduleButton = (Button) findViewById(R.id.teacherScheduleButton);
        teacherScheduleButton.setOnClickListener(this);

        preferencesButton = (Button) findViewById(R.id.preferencesButton);
        preferencesButton.setOnClickListener(this);

        aboutButton = (Button) findViewById(R.id.aboutButton);
        aboutButton.setOnClickListener(this);

//        lp_faculty = ListPreference.class.cast(lp_faculty);
//        ListPreference lp_faculty = (ListPreference) findPreference("list_faculty");
        /* Network check */
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            Toast.makeText(this, R.string.network_is_available, Toast.LENGTH_SHORT).show();
        } else {
            // display error
            Toast.makeText(this, R.string.network_is_unreached, Toast.LENGTH_LONG).show();
        }
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        setContentView(R.layout.bouncy);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bouncy, menu);
        // It is also possible add items here. Use a generated id from
        // resources (ids.xml) to ensure that all menu ids are distinct.
        MenuItem settingsItem = menu.add(0, R.id.menu_action_settings, 0, R.string.action_settings);
        settingsItem.setIcon(R.drawable.ic_action_settings);

        // Need to use MenuItemCompat methods to call any action item related methods
//        MenuItemCompat.setShowAsAction(settingsItem, MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_action_group) {
            gotoSchedule('g');
            return true;
        } else if (id == R.id.menu_action_teacher) {
            gotoSchedule('t');
            return true;
        } else if (id == R.id.menu_action_settings) {
            gotoPreferences();
            return true;
        } else if (id == R.id.menu_action_about) {
            gotoAbout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.groupScheduleButton:
                gotoSchedule('g');
                break;
            case R.id.teacherScheduleButton:
                gotoSchedule('t');
                break;
            case R.id.preferencesButton:
                gotoPreferences();
                break;
            case R.id.aboutButton:
                gotoAbout();
                break;
            default:
                break;
        }
    }

//    @Override
//    protected void onDestroy() {
//        saveSettings();
//        super.onDestroy();
//    }
    void gotoSchedule(char kind) {
        Intent bouncyBrowser = new Intent(getBaseContext(), BouncyBrowser.class);
        bouncyBrowser.putExtra("kind_key", kind);
        startActivity(bouncyBrowser);
    }

    void gotoPreferences() {
        Intent settingsActivity = new Intent(getBaseContext(), BouncyPreferences.class);
        startActivityForResult(settingsActivity, ACTIVITY_PREFERENCES);
    }

    void gotoAbout() {
        Intent aboutActivity = new Intent(getBaseContext(), AboutActivity.class);
        startActivity(aboutActivity);
    }

//   public void saveSettings() {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        SharedPreferences.Editor ed = prefs.edit();
//        ed.putString("list_faculty", ListPreference.class.cast(lp_faculty).getValue());
//        ed.putString("list_group", ListPreference.class.cast(lp_group).getValue());
//        ed.putString("list_teacher", ListPreference.class.cast(lp_teacher).getValue());
//        ed.commit();
////        Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show();
//
//    }
}
