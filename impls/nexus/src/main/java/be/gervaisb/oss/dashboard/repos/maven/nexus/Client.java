package be.gervaisb.oss.dashboard.repos.maven.nexus;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import be.gervaisb.oss.dashboard.Packaging;
import be.gervaisb.oss.dashboard.Reference;
import be.gervaisb.oss.dashboard.Version;
import be.gervaisb.oss.dashboard.repos.maven.ArtifactNotFoundException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

class Client {
        
    private final static Logger LOG = LoggerFactory.getLogger(Client.class);
    private final static String QUERY_INTERFACE	= "service/local/lucene/search";
    private final static String READ_INTERFACE = "service/local/artifact/maven/content?r={0}&g={1}&a={2}&v={3}&e=pom";
    
    private final Set<NexusReference> loadArtifacts(final URL query) throws MalformedURLException, IOException {	
	InputStream response = null;	
	try {
	    SearchNGResponseParser parser = new SearchNGResponseParser();	    	
	    response = query.openStream(); 
	    return parser.parse(response);	    
	} catch (SAXException e) {
	    throw new Error("Cannot parse response. [GET "+query+"]", e);
	} catch (ParserConfigurationException e) {
	    throw new Error("Cannot create response parser.", e);
	} finally {
	    if ( response!=null ) { try { response.close(); } catch (IOException ioe) { /* Ignore */ } }
	}
    }
           
    // ~ ------------------------------------------------------------------ ~ //
    
    private final LoadingCache<URL, Set<NexusReference>> nexus = CacheBuilder.newBuilder()
	    .expireAfterAccess(30, TimeUnit.MINUTES)
	    .build(new CacheLoader<URL, Set<NexusReference>>(){
		@Override
		public Set<NexusReference> load(URL query) throws Exception {
		    return Client.this.loadArtifacts(query);
		}
	    });
    
    private final URL location;
    
    public Client(final URL location) {
	this.location = location;
    }  
    
    public Set<NexusReference> find(final String groupId, final String artifactId, final Version version, final Packaging packaging) {
	try {
	    final URL query = query(
		    	    groupId!=null?"g="+groupId:null, 
			    artifactId!=null?"a="+artifactId:null, 
			    version!=null?"v="+version.toString():null,
		            packaging!=null?"p="+packaging.toString().toLowerCase():null);
	    LOG.debug("Querying nexus with GET {}.", query);
	    return nexus.get(query);
	} catch (ExecutionException e) {
	    throw new Error("Failed to select artifacts from "+location+".", e.getCause());
	}
    }

//    public ArtifactBuilder get(final Artifact artifact) {
//	return new ArtifactBuilder() {	    
//	    @Override
//	    public <A extends Artifact> A as(Class<A> type) {
//		return null;
//	    }
//	};
//    }

    public NexusReference get(String groupId, String artifactId, Version version) throws IllegalArgumentException, ArtifactNotFoundException {
	if ( groupId==null || artifactId==null || version==null ) {
	    throw new IllegalArgumentException("The parameters \"groupId\", \"artifactId\", \"version\" are requireds.");
	}
	
	NexusReference reference = null;
	try {
	    final URL query = query("g="+groupId, "a="+artifactId, "v="+version);
	    LOG.debug("Getting nexus artifact with GET {}.", query);
	    Set<NexusReference> results = nexus.get(query);
	    if ( results.size()==1 ) {
		reference = results.iterator().next();
	    } else if ( results.size()>1 ) {
		throw new IllegalArgumentException("More than one artifact was found for \""+groupId+":"+artifactId+":"+version+"\".");
	    } else if ( results.isEmpty() ) {
		throw new ArtifactNotFoundException(groupId, artifactId, version);
	    }
	} catch (ExecutionException e) {
	    throw new Error("Failed to select artifacts from "+location+".", e.getCause());
	}        
     	return reference;
    }
    
    private URL query(final String... parameters) {
	final StringBuilder query = new StringBuilder(QUERY_INTERFACE)
		.append('?');
	for (int index=0; parameters!=null && index<parameters.length; index++) {
	    if ( parameters[index]!=null ) {
    	    	query.append(parameters[index]).append('&');
	    }
	}
	query.append("collapseresults=true");
	
	try {
	    return new URL(location, query.toString());
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	    return null;
	}
    }

    public InputStream read(final Reference ref) {
	LOG.debug("Reading repository content for [{}].", ref);
	if ( !(ref instanceof NexusReference) ) {
	    throw new IllegalArgumentException("Only "+NexusReference.class+" can be read from "+this);
	}
	
	final NexusReference reference = (NexusReference) ref;
	final String query = MessageFormat.format(READ_INTERFACE,
		reference.repositoryId, reference.getGroupId(), reference.getArtifactId(), reference.getVersion());
	try {
	    return new URL(location, query).openStream();
	} catch (final IOException ioe) {
	    LOG.error("Cannot read \"pom.xml\" for reference [{}] : {}.", new Object[]{
		ref, ioe.getMessage(), ioe});
	    throw new Error("Cannot read \"pom.xml\" for reference ["+ref+"] : "+ioe.getMessage()+".", ioe);
	}
    }

}
