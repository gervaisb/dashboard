package conf;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.gervaisb.oss.dashboard.extensions.Dashboard;
import be.gervaisb.oss.dashboard.extensions.Extension;

import com.google.inject.Injector;
import com.google.inject.Key;

class ExtensionsManager {

    private static final Logger LOG = LoggerFactory.getLogger(NinjaDashboard.class);

    private final Injector context;

    public ExtensionsManager(final Injector module) {
	this.context = module;
    }

    public void extend(final Dashboard dashboard) {
	for (final Extension extension : getExtensions()) {
	    LOG.info("Extending {} with [{}]", dashboard, extension);
	    extension.extend(dashboard);
	}
    }

    public Iterable<Extension> getExtensions() {
	final Set<Extension> extensions = new HashSet<>(10);
	for (final Key<?> binding : context.getBindings().keySet()) {
	    if ( isExtension(binding) ) {
		extensions.add((Extension) getInstance(binding));
	    }
	}
	return extensions;
    }

    private boolean isExtension(final Key<?> binding) {
	Class<?> type = binding.getTypeLiteral().getRawType();
	return Extension.class.isAssignableFrom(type);
    }

    private <T> T getInstance(final Key<T> binding) {
	return context.getInstance(binding);
    }

}
