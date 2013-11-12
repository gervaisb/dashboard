package conf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

import ninja.Router;
import ninja.utils.NinjaProperties;
import be.gervaisb.oss.dashboard.extensions.Component;
import be.gervaisb.oss.dashboard.extensions.Dashboard;
import be.gervaisb.oss.dashboard.extensions.HttpVerb;
import be.gervaisb.oss.dashboard.extensions.Location;
import be.gervaisb.oss.dashboard.repos.Absence;
import be.gervaisb.oss.dashboard.repos.build.BuildsRepository;
import be.gervaisb.oss.dashboard.repos.issues.IssuesRepository;
import be.gervaisb.oss.dashboard.repos.quality.QualityRepository;

import com.google.inject.Injector;

public class NinjaDashboard implements Dashboard {

    private final Map<String, List<Component>> inclusions = new Hashtable<>();
    private final NinjaProperties properties;
    private final Injector module;
    private final Router router;

    @Inject
    public NinjaDashboard(final NinjaProperties properties, final Injector module, final Router router) {
	this.properties = properties;
	this.module = module;
	this.router = router;

	new ExtensionsManager(module).extend(this);
    }

    @Override
    public boolean hasIssuesTracker() {
	return has(IssuesRepository.class);
    }

    @Override
    public boolean hasQualityManager() {
	return has(QualityRepository.class);
    }

    @Override
    public boolean hasContinuousBuilder() {
	return has(BuildsRepository.class);
    }

    private boolean has(final Class<?> what) {
	return !(module.getInstance(what) instanceof Absence);
    }

    @Override
    public boolean isDevelopment() {
	return properties.isDev();
    }

    /** Workaround for freemarker who search for a getter */
    public boolean getIsDevelopment() {
	return isDevelopment();
    }

    @Override
    public Properties getProperties() {
	return properties.getAllCurrentNinjaProperties();
    }

    @Override
    public IncludeBuilder include(final Component component) {
	return new IncludeBuilder() {
	    @Override
	    public void into(final Location location) {
		final String urn = location.getUrn();
		if ( !NinjaDashboard.this.inclusions.containsKey(urn) ) {
		    NinjaDashboard.this.inclusions.put(urn, new ArrayList<Component>(2));
		}
		NinjaDashboard.this.inclusions.get(urn).add(component);
	    }
	};
    }

    public boolean hasInclusionsFor(final String location) {
	return inclusions.containsKey(location);
    }

    public Collection<Component> getInclusionsFor(final String location) {
	return inclusions.get(location);
    }

    @Override
    public RouteBuilder route(final HttpVerb verb, final String resource) {
	final ninja.RouteBuilder builder = getBuilder(verb);
	return new RouteBuilder() {
	    @Override
	    public void to(final Class<?> handler, final String method) {
		builder.route(resource).with(handler, method);
	    }
	};
    }

    private ninja.RouteBuilder getBuilder(final HttpVerb verb) {
	switch (verb) {
	case GET:
	    return router.GET();
	case PUT:
	    return router.PUT();
	case POST:
	    return router.POST();
	case DELETE:
	    return router.DELETE();
	default:
	    throw new IllegalArgumentException("Unknow http verb ["+verb+"] cannot create route.");
	}
    }

}