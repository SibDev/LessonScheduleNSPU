package tk.al54.dev.nspu.lessonschedule;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;

public class BouncyBrowser extends Activity implements OnClickListener {

    WebView canvasFieldView;
    String facultyId;
    String groupId;
    String teacherId;
    char kind;
    Calendar calendar = Calendar.getInstance();
    int curDay = calendar.get(Calendar.DAY_OF_WEEK)-1;
    Button prevDayButton, backButton, nextDayButton;

    final static int ACTIVITY_PREFERENCES = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_LEFT_ICON);
        setContentView(R.layout.browser);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        Intent bouncyBrowser = getIntent();
        kind = bouncyBrowser.getCharExtra("kind_key", 'g');

        prevDayButton = (Button) findViewById(R.id.prevDayButton);
        prevDayButton.setOnClickListener(this);

        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(this);

        nextDayButton = (Button) findViewById(R.id.nextDayButton);
        nextDayButton.setOnClickListener(this);
        final GestureDetector gestureDetector = new GestureDetector(new DetectGesture());
        canvasFieldView = (WebView) findViewById(R.id.canvasFieldView);
        canvasFieldView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        goSchedule();

    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        setContentView(R.layout.browser);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.browser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_action_settings) {
            gotoPreferences();
        } else if (id == R.id.menu_action_back) {
            gotoBack();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prevDayButton:
                gotoDay('p');
                break;
            case R.id.backButton:
                gotoBack();
                break;
            case R.id.nextDayButton:
                gotoDay('n');
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch(requestCode) {
            case ACTIVITY_PREFERENCES:
                updateFromPreferences();
                break;
        }
    }

    void gotoDay(char direction) {
        switch (direction) {
            case 'p':
                curDay--;
                break;
            case 'n':
                curDay++;
                break;
        }
        if (curDay == 7) curDay = 0;
        if (curDay == -1) curDay = 6;
        goSchedule();
    }

    void goSchedule() {
        updateFromPreferences();
        canvasFieldView = (WebView) findViewById(R.id.canvasFieldView);

        String url = "http://schedule.nspu.ru/";
        switch (kind) {
            case 'g':
                url = url.concat("group_shedule_oneday.php?id=").concat(groupId).concat("&day=");
                break;
            case 't':
                url = url.concat("teacher_shedule_oneday.php?id=").concat(teacherId).concat("&day=");
                break;
        }
        url = url+Integer.toString(curDay) + "&amp;nc=" + System.nanoTime();
//        Log.d("myLogs", "Load: "+url);
        canvasFieldView.loadUrl(url);
    }

    void gotoBack() {
        super.setResult(Activity.RESULT_OK);
        super.finish();

//        Intent bouncyActivity = new Intent(getBaseContext(), BouncyActivity.class);
//        startActivityIfNeeded(bouncyActivity, ACTIVITY_PREFERENCES);
//        //startActivityForResult(bouncyActivity, ACTIVITY_PREFERENCES);
    }
    void gotoPreferences() {
        Intent settingsActivity = new Intent(getBaseContext(), BouncyPreferences.class);
        startActivityForResult(settingsActivity, ACTIVITY_PREFERENCES);
    }
    void updateFromPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        facultyId = prefs.getString("list_faculty", "9");
        groupId = prefs.getString("list_groups", "82");
        teacherId = prefs.getString("list_teacher", "504");
//        Toast.makeText(this, "Settings loaded", Toast.LENGTH_SHORT).show();
    }

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private class DetectGesture extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                gotoDay('n');
//                Toast.makeText(getApplicationContext(), "Right to left", Toast.LENGTH_SHORT).show();
                System.out.println("Right to left");
                return true;
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                gotoDay('p');
//                Toast.makeText(getApplicationContext(), "Left to right", Toast.LENGTH_SHORT).show();
                System.out.println("Left to right");
                return true;
            }
            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
////                i++;
////                changeColor();
                canvasFieldView.flingScroll(0, Math.round(velocityY)*(-1));
////                Toast.makeText(getApplicationContext(), "Bottom to top", Toast.LENGTH_SHORT).show();
                System.out.println("Bottom to top: "+velocityY);
                return true;
            }  else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
////                i--;
////                changeColor();
                canvasFieldView.flingScroll(0, Math.round(velocityY)*(-1));
////                Toast.makeText(getApplicationContext(), "Top to bottom", Toast.LENGTH_SHORT).show();
                System.out.println("Top to bottom: "+velocityY);
                return true;
            }
            return false;
        }
    }

}
