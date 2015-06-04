package tk.al54.dev.nspu.lessonschedule;

import android.content.Context;
import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParserException;
import tk.al54.dev.nspu.lessonschedule.ParseXMLTeachers.Teachers;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DownloadTeachersXml extends AsyncTask<String, Void, String> {
    static Context _context;
    public static List<Teachers> _Teachers;

    public static void setContext(Context value) {
        _context = value;
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            loadXmlFromNetwork(urls[0]);
        } catch (IOException e) {
//            return getResources().getString(R.string.connection_error);
        } catch (XmlPullParserException e) {
//            return getResources().getString(R.string.xml_error);
        }
        return null;
    }

    private void loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        // Instantiate the parser
        ParseXMLTeachers XMLTeachersParser = new ParseXMLTeachers();
        List<Teachers> entries = null;

        try {
            stream = downloadUrl(urlString);
            entries = XMLTeachersParser.parseTeachers(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        _Teachers = entries;
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(20000 /* milliseconds */);
        conn.setConnectTimeout(25000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
}
