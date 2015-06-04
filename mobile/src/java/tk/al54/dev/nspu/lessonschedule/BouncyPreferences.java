package tk.al54.dev.nspu.lessonschedule;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import tk.al54.dev.nspu.lessonschedule.ParseXMLFaculty.Departments;
import tk.al54.dev.nspu.lessonschedule.ParseXMLGroups.Groups;
import tk.al54.dev.nspu.lessonschedule.ParseXMLTeachers.Teachers;

import java.util.List;

public class BouncyPreferences extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    private static final String FacultyURL = "http://schedule.nspu.ru/groups_xml.php?dep=-1";
    private static final String GroupURL = "http://schedule.nspu.ru/groups_xml.php?dep=";
    private static final String TeacherURL = "http://schedule.nspu.ru/preps_xml.php?";
    //    private static final String FacultyURL = "http://ws54.tk/rasp/index.html?type=groups&dep=-1";
//    private static final String GroupURL = "http://ws54.tk/rasp/index.html?type=groups&dep=";
//    private static final String TeacherURL = "http://ws54.tk/rasp/index.html?type=preps";
    String URL = "";

    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
    // Whether the display should be refreshed.
    public static boolean refreshDisplay = true;
    public static String sPref = null;

    ListPreference lp_teacher;

    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        GetFaculties();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // handle the preference change here
//        Toast.makeText(this, "Settings changed! ".concat(key), Toast.LENGTH_SHORT).show();
        if (key.equals("list_faculty")) {
            GetGroups();
        }
        if (key.equals("list_teacher_letters")) {
            GetTeachers();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Registers a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences();
    }

    protected void GetFaculties() {
        /* Network check */
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            ListPreference lp_faculty = (ListPreference) findPreference("list_faculty");
            Log.d(LOG_TAG, "Downloading faculties");
            URL = FacultyURL + "&amp;nc=" + System.nanoTime();
            new DownloadFacultyXml().doInBackground(URL);
            List<Departments> Faculties = DownloadFacultyXml._Faculties;

            CharSequence entries[] = new String[Faculties.size()];
            CharSequence entryValues[] = new String[Faculties.size()];
            int i = 0;
            for (Departments faculty : Faculties) {
//            Log.d(LOG_TAG, "Faculty["+i+"].id: "+faculty.depid+";");
                entryValues[i] = faculty.depid;
//            Log.d(LOG_TAG, "Faculty["+i+"].name: "+faculty.depname+";");
                entries[i] = faculty.depname;
                i++;
            }
            lp_faculty.setEntries(entries);
            lp_faculty.setEntryValues(entryValues);
//            Toast.makeText(this, "Network is available!", Toast.LENGTH_SHORT).show();
        } else {
            // display error
            Toast.makeText(this, R.string.network_is_unreached, Toast.LENGTH_LONG).show();
        }
    }

    protected void GetGroups() {
        ListPreference lp_group = (ListPreference) findPreference("list_groups");
        /* Network check */
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String fac_id = prefs.getString("list_faculty", "9");

            CharSequence groupsNames[];
            CharSequence groupsIds[];

            URL = GroupURL + fac_id;
//        Log.d(LOG_TAG, "Downloading groups for faculty: "+fac_id+" from url "+URL+";");
            new DownloadGroupsXml().doInBackground(URL);
            List<Groups> GroupsList = DownloadGroupsXml._Groups;
//        Log.d(LOG_TAG, "Groups loaded: "+GroupsList.size()+".");

            if (GroupsList.size() > 0) {
                groupsNames = new String[GroupsList.size()];
                groupsIds = new String[GroupsList.size()];

                int i = 0;
                for (Groups group : GroupsList) {
//                Log.d(LOG_TAG, "Group["+i+"].id: "+group.grid+";");
                    groupsIds[i] = group.grid;
//                Log.d(LOG_TAG, "Group["+i+"].name: "+group.grname+";");
                    groupsNames[i] = group.grname;
                    i++;
                }
//            Toast.makeText(this, "Groups downloaded.", Toast.LENGTH_SHORT).show();
            } else {
                groupsNames = new String[1];
                groupsIds = new String[1];
                groupsNames[0] = "Транзит";
                groupsIds[0] = "0";
//            Toast.makeText(this, "Groups download fail!.", Toast.LENGTH_SHORT).show();
            }
            lp_group.setEntries(groupsNames);
            lp_group.setEntryValues(groupsIds);
//        Log.d(LOG_TAG, "Groups list created.");
            if (!lp_group.isEnabled()) {
                lp_group.setEnabled(true);
            }
//            Toast.makeText(this, "Network is available!", Toast.LENGTH_SHORT).show();
        } else {
            // display error
            Toast.makeText(this, R.string.network_is_unreached, Toast.LENGTH_LONG).show();
        }
    }

    protected void GetTeachers() {
        ListPreference lp_teacher = (ListPreference) findPreference("list_teacher");
         /* Network check */
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String teacher_l = prefs.getString("list_teacher_letters", "Г");

            CharSequence teacherNames[];
            CharSequence teacherIds[];

            URL = TeacherURL+"letter="+ Uri.encode(teacher_l) + "&amp;nc=" + System.nanoTime();;
//        Log.d(LOG_TAG, "Downloading teachers for Letter: "+teacher_l+" from url "+URL+";");
            new DownloadTeachersXml().doInBackground(URL);
            List<Teachers> TeachersList = DownloadTeachersXml._Teachers;
//        Log.d(LOG_TAG, "Teachers loaded: "+TeachersList.size()+".");

            if (TeachersList.size() > 0) {
                teacherNames = new String[TeachersList.size()];
                teacherIds = new String[TeachersList.size()];

                int i = 0;
                for (Teachers teacher : TeachersList) {
                    teacherIds[i] = teacher.tid;
//                Log.d(LOG_TAG, "Teacher["+teacher_l+"]["+i+"].id: "+teacher.tid+";");
                    teacherNames[i] = teacher.tname;
//                Log.d(LOG_TAG, "Teacher["+teacher_l+"]["+i+"].name: "+teacher.tname+";");
                    i++;
                }
            } else {
                teacherNames = new String[1];
                teacherIds = new String[1];
                teacherNames[0] = "Герасёв Алексей Дмитриевич\n";
                teacherIds[0] = "99";
            }
            lp_teacher.setEntries(teacherNames);
            lp_teacher.setEntryValues(teacherIds);
//        Log.d(LOG_TAG, "Teachers list created.");
            if (!lp_teacher.isEnabled()) {
                lp_teacher.setEnabled(true);
            }
        } else {
            // display error
            Toast.makeText(this, R.string.network_is_unreached, Toast.LENGTH_LONG).show();
        }
    }
}
