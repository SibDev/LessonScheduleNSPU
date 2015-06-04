package tk.al54.dev.nspu.lessonschedule;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ParseXMLFaculty {
	private static final String ns = null;

	public List parseFaculty(InputStream in) throws XmlPullParserException, IOException {
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

		parser.require(XmlPullParser.START_TAG, ns, "nspu");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String department = parser.getName();
			// Starts by looking for the department tag
			if (department.equals("department")) {
				entries.add(readEntry(parser));
			} else {
				skip(parser);
			}
		}
		return entries;
	}
	public static class Departments {
		public final String depid;
		public final String depname;

		private Departments(String depid, String depname) {
			this.depid = depid;
			this.depname = depname;
		}
	}
	private Departments readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "department");
		String depid = null;
		String depname = null;
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String tagname = parser.getName();
			if (tagname.equals("depid")) {
				depid = readDepId(parser);
			} else if (tagname.equals("depname")) {
				depname = readDepName(parser);
			} else {
				skip(parser);
			}
		}
		return new Departments(depid, depname);
	}
	// Processes depid tags in the feed.
	private String readDepId(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "depid");
		String depid = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "depid");
		return depid;
	}
	// Processes depname tags in the feed.
	private String readDepName(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "depname");
		String depname = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "depname");
		return depname;
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
