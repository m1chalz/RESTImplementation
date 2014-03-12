package com.example.rest.app.parsers;

import android.content.ContentValues;
import android.text.format.Time;
import android.util.Xml;
import com.example.rest.app.provider.AppContract;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EntitiesParser extends Parser {

	// Constants indicting XML element names that we're interested in
	private static final int TAG_ID = 1;
	private static final int TAG_TITLE = 2;
	private static final int TAG_PUBLISHED = 3;
	private static final int TAG_LINK = 4;

	// We don't use XML namespaces
	private static final String ns = null;

	/** Parse an Atom feed, returning a collection of Entry objects.
	 *
	 * @param is Atom feed, as a stream.
	 * @return List of ContentValues objects.
	 * @throws org.xmlpull.v1.XmlPullParserException on error parsing feed.
	 * @throws java.io.IOException on I/O error.
	 */
	@Override
	public Collection<ContentValues> parse(InputStream is) throws XmlPullParserException, IOException, ParseException {

		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(is, null);
			parser.nextTag();
			return readFeed(parser);
		} finally {
			is.close();
		}
	}

	/**
	 * Decode a feed attached to an XmlPullParser.
	 *
	 * @param parser Incoming XMl
	 * @return List of ContentValues objects.
	 * @throws org.xmlpull.v1.XmlPullParserException on error parsing feed.
	 * @throws java.io.IOException on I/O error.
	 */
	private List<ContentValues> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
		List<ContentValues> entries = new ArrayList<>();

		// Search for <feed> tags. These wrap the beginning/end of an Atom document.
		//
		// Example:
		// <?xml version="1.0" encoding="utf-8"?>
		// <feed xmlns="http://www.w3.org/2005/Atom">
		// ...
		// </feed>
		parser.require(XmlPullParser.START_TAG, ns, "feed");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the <entry> tag. This tag repeates inside of <feed> for each
			// article in the feed.
			//
			// Example:
			// <entry>
			//   <title>Article title</title>
			//   <link rel="alternate" type="text/html" href="http://example.com/article/1234"/>
			//   <link rel="edit" href="http://example.com/admin/article/1234"/>
			//   <id>urn:uuid:218AC159-7F68-4CC6-873F-22AE6017390D</id>
			//   <published>2003-06-27T12:00:00Z</published>
			//   <updated>2003-06-28T12:00:00Z</updated>
			//   <summary>Article summary goes here.</summary>
			//   <author>
			//     <name>Rick Deckard</name>
			//     <email>deckard@example.com</email>
			//   </author>
			// </entry>
			if (name.equals("entry")) {
				entries.add(readEntry(parser));
			} else {
				skip(parser);
			}
		}
		return entries;
	}

	/**
	 * Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them
	 * off to their respective "read" methods for processing. Otherwise, skips the tag.
	 */
	private ContentValues readEntry(XmlPullParser parser)
			throws XmlPullParserException, IOException, ParseException {
		parser.require(XmlPullParser.START_TAG, ns, "entry");
		String id = null;
		String title = null;
		String link = null;
		long publishedOn = 0;

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("id")){
				// Example: <id>urn:uuid:218AC159-7F68-4CC6-873F-22AE6017390D</id>
				id = readTag(parser, TAG_ID);
			} else if (name.equals("title")) {
				// Example: <title>Article title</title>
				title = readTag(parser, TAG_TITLE);
			} else if (name.equals("link")) {
				// Example: <link rel="alternate" type="text/html" href="http://example.com/article/1234"/>
				//
				// Multiple link types can be included. readAlternateLink() will only return
				// non-null when reading an "alternate"-type link. Ignore other responses.
				String tempLink = readTag(parser, TAG_LINK);
				if (tempLink != null) {
					link = tempLink;
				}
			} else if (name.equals("published")) {
				// Example: <published>2003-06-27T12:00:00Z</published>
				Time t = new Time();
				t.parse3339(readTag(parser, TAG_PUBLISHED));
				publishedOn = t.toMillis(false);
			} else {
				skip(parser);
			}
		}

		ContentValues cv = new ContentValues();
		cv.put(AppContract.Entities.ENTRY_ID, id);
		cv.put(AppContract.Entities.TITLE, title);
		cv.put(AppContract.Entities.LINK, link);
		cv.put(AppContract.Entities.PUBLISHED, publishedOn);
		return cv;
	}

	/**
	 * Process an incoming tag and read the selected value from it.
	 */
	private String readTag(XmlPullParser parser, int tagType)
			throws IOException, XmlPullParserException {
		String tag = null;
		String endTag = null;

		switch (tagType) {
		case TAG_ID:
			return readBasicTag(parser, "id");
		case TAG_TITLE:
			return readBasicTag(parser, "title");
		case TAG_PUBLISHED:
			return readBasicTag(parser, "published");
		case TAG_LINK:
			return readAlternateLink(parser);
		default:
			throw new IllegalArgumentException("Unknown tag type: " + tagType);
		}
	}

	/**
	 * Reads the body of a basic XML tag, which is guaranteed not to contain any nested elements.
	 *
	 * <p>You probably want to call readTag().
	 *
	 * @param parser Current parser object
	 * @param tag XML element tag name to parse
	 * @return Body of the specified tag
	 * @throws java.io.IOException
	 * @throws org.xmlpull.v1.XmlPullParserException
	 */
	private String readBasicTag(XmlPullParser parser, String tag)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, tag);
		String result = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, tag);
		return result;
	}

	/**
	 * Processes link tags in the feed.
	 */
	private String readAlternateLink(XmlPullParser parser)
			throws IOException, XmlPullParserException {
		String link = null;
		parser.require(XmlPullParser.START_TAG, ns, "link");
		String tag = parser.getName();
		String relType = parser.getAttributeValue(null, "rel");
		if (relType.equals("alternate")) {
			link = parser.getAttributeValue(null, "href");
		}
		while (true) {
			if (parser.nextTag() == XmlPullParser.END_TAG) break;
			// Intentionally break; consumes any remaining sub-tags.
		}
		return link;
	}

	/**
	 * For the tags title and summary, extracts their text values.
	 */
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		String result = null;
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	/**
	 * Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
	 * if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
	 * finds the matching END_TAG (as indicated by the value of "depth" being 0).
	 */
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
