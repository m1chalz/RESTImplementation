package com.example.rest.app.parsers;

import android.content.ContentValues;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Collection;

public abstract class Parser {
	public abstract Collection<ContentValues> parse(InputStream is) throws XmlPullParserException, IOException, ParseException;
}
