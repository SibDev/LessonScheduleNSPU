package tk.al54.dev.nspu.lessonschedule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;

public class StartUp extends Activity {
    protected boolean _active = true;
    protected int _splashTime = 100; // time to display the splash screen in ms
    ProgressBar progressbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.splash);
        progressbar = (ProgressBar) findViewById(R.id.progressBar);
        progressbar.setProgress(0);
        setProgressBarVisibility(true);

        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    // place downloading code here
                    int waited = 0;
                    while (_active && (waited < _splashTime)) {
                        sleep(200);
                        if (_active) {
                            waited += Math.round(Math.random()*20);
                            progressbar.setProgress(waited);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    _active=false;
                    startActivity(new Intent(StartUp.this, BouncyActivity.class));
                    finish();
                }
            }
        };
        splashTread.start();
    }
}
