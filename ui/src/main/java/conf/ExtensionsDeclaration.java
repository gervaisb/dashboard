package conf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.gervaisb.oss.dashboard.extensions.Extension;

class ExtensionsDeclaration implements Iterable<Class<Extension>> {

    private static final Logger LOG = LoggerFactory.getLogger(ExtensionsDeclaration.class);
    private static final String PATH = "META-INF/services/"+Extension.class.getName();

    private final ClassPathRessources implementations;
    private final ClassLoader loader;

    public ExtensionsDeclaration() {
	LOG.info("Loading extensions. ([{}]).", PATH);
	this.loader = Thread.currentThread().getContextClassLoader();
	this.implementations = new ClassPathRessources(PATH);
    }

    @Override
    public Iterator<Class<Extension>> iterator() {
	return new Iterator<Class<Extension>>() {
	    Iterator<String> extensions = Collections.emptyIterator();
	    Iterator<URL> declarations = implementations.iterator();

	    @Override
	    public boolean hasNext() {
		return extensions.hasNext() || declarations.hasNext();
	    }

	    @Override
	    public Class<Extension> next() {
		if ( !extensions.hasNext() && declarations.hasNext() ) {
		    extensions = read(declarations.next()).iterator();
		}
		return load(extensions.next());
	    }

	    @Override
	    public void remove() {
		throw new UnsupportedOperationException("Cannot remove extension declaration");
	    }
	};
    }

    private Collection<String> read(final URL url) {
	final Set<String> lines = new HashSet<>(5);
	try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
	    String line = null;
	    while ( (line = reader.readLine())!=null && !line.startsWith("#") ) {
		lines.add(line);
	    }
	} catch (final IOException ioe) {
	    throw new ServiceConfigurationError("Failed to read service description from \""+url+"\".", ioe);
	}
	return lines;
    }

    @SuppressWarnings("unchecked")
    private Class<Extension> load(final String className) {
	try {
	    return (Class<Extension>) Class.forName(className, false, loader);
	} catch (final ClassNotFoundException cnfe) {
	    throw new ServiceConfigurationError("Invalid extension ["+className+"]. Class "+className+" not found.", cnfe);
	}
    }

}
