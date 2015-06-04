package tk.al54.dev.nspu.lessonschedule;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ParseXMLTeachers {
    private static final String ns = null;

    public List parseTeachers(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List entries = new ArrayList();

//        Log.d("myLogs", "Tag: "+parser.getName());
        parser.require(XmlPullParser.START_TAG, ns, "nspu");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            // Starts by looking for the prepods tag
//            Log.d("myLogs", "_Tag: "+parser.getName());
            if (parser.getName().equals("prepods")) {
                parser.require(XmlPullParser.START_TAG, ns, "prepods");
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String prepods = parser.getName();
//                    Log.d("myLogs", "__Tag: " + parser.getName());
                    // Starts by looking for the group tag
                    if (prepods.equals("prepod")) {
                        entries.add(readEntry(parser));
                    } else {
                        skip(parser);
                    }
                }
            } else {
                skip(parser);
            }
        }
        return entries;
    }
    public static class Teachers {
        public final String tid;
        public final String tname;

        private Teachers(String tid, String tname) {
            this.tid = tid;
            this.tname = tname;
        }
    }
    private Teachers readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "prepod");
        String tid = null;
        String tname = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagname = parser.getName();
            if (tagname.equals("id")) {
                tid = readTId(parser);
            } else if (tagname.equals("name")) {
                tname = readTName(parser);
            } else {
                skip(parser);
            }
        }
        return new Teachers(tid, tname);
    }
    // Processes id tags in the feed.
    private String readTId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "id");
        String tid = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "id");
        return tid;
    }
    // Processes name tags in the feed.
    private String readTName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "name");
        String tname = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "name");
        return tname;
    }
    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
