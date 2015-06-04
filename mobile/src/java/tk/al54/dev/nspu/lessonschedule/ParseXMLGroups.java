package tk.al54.dev.nspu.lessonschedule;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ParseXMLGroups {
    private static final String ns = null;

    public List parseGroups(InputStream in) throws XmlPullParserException, IOException {
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
        parser.require(XmlPullParser.START_TAG, ns, "department");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            // Starts by looking for the groups tag
//            Log.d("myLogs", "_Tag: "+parser.getName());
            if (parser.getName().equals("groups")) {
                parser.require(XmlPullParser.START_TAG, ns, "groups");
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String groups = parser.getName();
//                    Log.d("myLogs", "__Tag: " + parser.getName());
                    // Starts by looking for the group tag
                    if (groups.equals("group")) {
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
    public static class Groups {
        public final String grid;
        public final String grname;

        private Groups(String grid, String grname) {
            this.grid = grid;
            this.grname = grname;
        }
    }
    private Groups readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "group");
        String grid = null;
        String grname = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagname = parser.getName();
            if (tagname.equals("id")) {
                grid = readGrId(parser);
            } else if (tagname.equals("name")) {
                grname = readGrName(parser);
            } else {
                skip(parser);
            }
        }
        return new Groups(grid, grname);
    }
    // Processes id tags in the feed.
    private String readGrId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "id");
        String grid = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "id");
        return grid;
    }
    // Processes name tags in the feed.
    private String readGrName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "name");
        String grname = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "name");
        return grname;
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
