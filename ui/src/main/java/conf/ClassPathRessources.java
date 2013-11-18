package conf;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.ServiceConfigurationError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ClassPathRessources implements Iterable<URL> {

    private static final Logger LOG = LoggerFactory.getLogger(ClassPathRessources.class);

    private final Collection<URL> founds;
    private final ClassLoader loader;

    public ClassPathRessources(final String what) {
	try {
	    LOG.info("Finding classpath ressources named ["+what+"].");
	    this.loader = Thread.currentThread().getContextClassLoader();
	    this.founds = collect(what);
	} catch (final IOException ioe) {
	    throw new ServiceConfigurationError("Cannot read classpath for \""+what+"\".", ioe);
	}
    }

    private Collection<URL> collect(final String what) throws IOException {
	final Collection<URL> collection = new ArrayList<>();
	final Enumeration<URL> founds = loader.getResources(what);
	while ( founds.hasMoreElements() ) {
	    URL found = founds.nextElement();
	    collection.add(found);
	    LOG.debug("Found {}.", found);
	}
	return collection;
    }

    @Override
    public Iterator<URL> iterator() {
	return founds.iterator();
    }

    public boolean isEmpty() {
	return founds.isEmpty();
    }

    public int size() {
	return founds.size();
    }
}
