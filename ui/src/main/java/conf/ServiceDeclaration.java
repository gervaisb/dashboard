package conf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ServiceConfigurationError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ServiceDeclaration<S> {

    public static <S> Class<S> get(final Class<S> service) {
	return new ServiceDeclaration<>(service).get();
    }

    private static final Logger LOG = LoggerFactory.getLogger(ServiceDeclaration.class);
    private static final String PREFIX = "META-INF/services/";

    private final ClassPathRessources implementations;
    private final ClassLoader loader;
    private final Class<S> service;
    private Class<S> implementationClass;

    public ServiceDeclaration(final Class<S> service) {
	LOG.info("Loading declaration for [{}] from \"{}{}\".", service, PREFIX, service.getName());
	this.service = service;
	this.loader = Thread.currentThread().getContextClassLoader();
	this.implementations = new ClassPathRessources(PREFIX+service.getName());
    }

    /**
     * Get the declared implementation.
     * @return The declared implementation for <tt>S</tt> or <tt>null</tt> when
     * 	no implemntation has been declared.
     * @throws ServiceConfigurationError when more than one declartion is found
     * 	on the classpath.
     */
    public Class<S> get() {
	if ( implementationClass==null ) {
	    if ( implementations.isEmpty() ) {
		implementationClass = null;
	    } else if ( implementations.size()>1 ) {
		throw new ServiceConfigurationError("Too much implementations found for \""+service.getName()+"\" ("+implementations.size()+").");
	    } else {
		String implementationName = read(implementations.iterator().next());
		implementationClass = load(implementationName);
	    }
	}
	LOG.info("Returning [{}] as implementation for \"{}\".", (implementationClass!=null?implementationClass.getName():"Null"), service);
	return implementationClass;
    }

    private String read(final URL url) {
	try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
	    String line = null;
	    String className = null;
	    while ( (line = reader.readLine())!=null && !line.startsWith("#") ) {
		if ( className!=null ) {
		    throw new ServiceConfigurationError("Invalid service description for \""+service.getName()+"\". Too much lines found in ("+url+").");
		} else {
		    className = line;
		}
	    }
	    if ( className==null ) {
		throw new ServiceConfigurationError("Invalid service description for \""+service.getName()+"\". No lines found in ("+url+").");
	    }
	    return className;
	} catch (final IOException ioe) {
	    throw new ServiceConfigurationError("Failed to read service description from \""+url+"\".", ioe);
	}
    }

    @SuppressWarnings("unchecked")
    private Class<S> load(final String className) {
	try {
	    return (Class<S>) Class.forName(className, false, loader);
	} catch (final ClassNotFoundException cnfe) {
	    throw new ServiceConfigurationError("Invalid service description for \""+service.getName()+"\". Class "+className+" not found.", cnfe);
	}
    }

}
