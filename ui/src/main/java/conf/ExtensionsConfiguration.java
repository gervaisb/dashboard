package conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.gervaisb.oss.dashboard.extensions.Extension;

import com.google.inject.AbstractModule;

class ExtensionsConfiguration extends AbstractModule {

    private static final Logger LOG = LoggerFactory.getLogger(NinjaDashboard.class);

    @Override
    protected void configure() {
	ExtensionsDeclaration declarations = new ExtensionsDeclaration();

	for (final Class<Extension> extension : declarations) {
	    LOG.debug("Registering extension [{}].", extension);
	    bind(extension); // Use Guice to inject dependencies
	}
    }

}
