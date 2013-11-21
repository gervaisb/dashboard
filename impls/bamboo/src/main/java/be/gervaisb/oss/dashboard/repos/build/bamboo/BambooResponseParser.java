package be.gervaisb.oss.dashboard.repos.build.bamboo;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import be.gervaisb.oss.dashboard.repos.build.Status;
import be.gervaisb.oss.dashboard.repos.build.Status.State;


class BambooResponseParser extends DefaultHandler {

    private static final int NONE 	= -1;
    private static final int STATE 	= 0;
    private static final int DATE 	= 1;
    private static final int MESSAGE 	= 2;

    private final SAXParser saxParser;

    private String[] properties;
    private int currentProperty = NONE;
    private StringBuilder content;

    public BambooResponseParser() throws ParserConfigurationException, SAXException {
	this.saxParser = SAXParserFactory.newInstance().newSAXParser();
    }

    public Status parse(final InputStream response) throws SAXException, IOException {
	properties = new String[3];
	content = new StringBuilder();
	saxParser.parse(response, this);

	return export(properties);
    }

    @Override
    public void startElement(final String uri, final String localName, final String tag, final Attributes attributes) throws SAXException {
	if ("result".equals(tag)) {
	    properties[STATE] = attributes.getValue("state");
	} else if ("buildCompletedTime".equals(tag)) {
	    currentProperty = DATE;
	} else if ("buildReason".equals(tag)) {
	    currentProperty = MESSAGE;
	}
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
	if (currentProperty != NONE) {
	    content.append(ch, start, length);
	}
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
	if (currentProperty != NONE) {
	    properties[currentProperty] = content.toString();
	    content.delete(0, content.length());
	    currentProperty = NONE;
	}
    }

    private Status export(final String[] properties) {
	return new Status(
		parseState(properties[STATE]),
		parseDate(properties[DATE]),
		properties[MESSAGE]);
    }

    private Date parseDate(final String string) {
	try {
	    return new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.S", Locale.ENGLISH).parse(string);
	} catch (ParseException e) {
	    return null;
	}
    }

    private State parseState(final String string) {
	return States.valueOf(string);
    }
}
