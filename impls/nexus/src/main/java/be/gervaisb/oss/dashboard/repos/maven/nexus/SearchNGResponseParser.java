package be.gervaisb.oss.dashboard.repos.maven.nexus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import be.gervaisb.oss.dashboard.Version;

class SearchNGResponseParser extends DefaultHandler {
    
    private final static Logger LOG = LoggerFactory.getLogger(SearchNGResponseParser.class);
    
    private final static String TAG_ARTIFACT = "artifact";
    private final static String TAG_ARTIFACT_ID = "artifactId";
    private final static String TAG_GROUP_ID = "groupId";    
    private final static String TAG_VERSION = "version";
    private final static String TAG_REPOSITORY_ID = "repositoryId";

    private final static int PROPERTY_ARTIFACT_ID = 0;
    private final static int PROPERTY_GROUP_ID = 1;
    private final static int PROPERTY_VERSION = 2;
    private final static int PROPERTY_REPOSITORY_ID = 3;
    
    private final static Map<String, Integer> DESTINATIONS = new HashMap<String, Integer>();
    static {
	DESTINATIONS.put(TAG_ARTIFACT_ID, PROPERTY_ARTIFACT_ID);
	DESTINATIONS.put(TAG_GROUP_ID, PROPERTY_GROUP_ID);
	DESTINATIONS.put(TAG_VERSION, PROPERTY_VERSION);
	DESTINATIONS.put(TAG_REPOSITORY_ID, PROPERTY_REPOSITORY_ID);
    }
        
    private final SAXParser saxParser;
    
    private Integer currentProperty;
    private String[] properties;
    
    private boolean isArtifact;    
    
    private Set<NexusReference> artifacts;
    
    public SearchNGResponseParser() throws ParserConfigurationException, SAXException {
	this.saxParser = SAXParserFactory.newInstance().newSAXParser();
    }
    
    public Set<NexusReference> parse(InputStream response) throws SAXException, IOException {
	artifacts = new TreeSet<NexusReference>();
	properties = new String[DESTINATIONS.size()];
	currentProperty = null;
	isArtifact = false;	
	
	saxParser.parse(response, this);
	
	return artifacts;
    }
    
    @Override
    public void startElement(String uri, String localName, String tag, Attributes attributes) throws SAXException {
	if ( !isArtifact && TAG_ARTIFACT.equals(tag) ) {
	    isArtifact = true;
	} else if ( isArtifact ) {
	    currentProperty = DESTINATIONS.get(tag);
	}
    }
    
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
	if ( currentProperty!=null ) {
	    properties[currentProperty.intValue()] = new String(ch, start, length);
	    currentProperty = null;
	}
    }
    
    @Override
    public void endElement(String uri, String localName, String tag) throws SAXException {
	if ( isArtifact && TAG_ARTIFACT.equals(tag) ) {	
	    try {
		LOG.trace("Creating NexusReference from \"{}\".", Arrays.toString(properties));
		artifacts.add(new NexusReference(
		    properties[PROPERTY_GROUP_ID], 
		    properties[PROPERTY_ARTIFACT_ID], 
		    new Version(properties[PROPERTY_VERSION]),
		    properties[PROPERTY_REPOSITORY_ID]));
	    } catch (IllegalArgumentException iae) {
		LOG.error("Cannot create reference from \"{}\" : {}.", new Object[]{
			Arrays.toString(properties), iae.getMessage(), iae});
	    } finally {
    	    	Arrays.fill(properties, null);
    	    	isArtifact = false;
	    }
	}
    }

}
