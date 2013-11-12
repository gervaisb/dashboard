package be.gervaisb.oss.dashboard.repos.quality.mocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import be.gervaisb.oss.dashboard.Application;
import be.gervaisb.oss.dashboard.repos.status.Environment;
import be.gervaisb.oss.dashboard.repos.status.Status;
import be.gervaisb.oss.dashboard.repos.status.Status.Attribute;
import be.gervaisb.oss.dashboard.repos.status.StatusRepository;

public class MockStatusRepository implements StatusRepository {

    private static final Set<Environment> ENVIRONMENTS = new HashSet<>(3);
    static {
	ENVIRONMENTS.add(new MockEnvironment(1, "Development"));
	ENVIRONMENTS.add(new MockEnvironment(2, "Test"));
	ENVIRONMENTS.add(new MockEnvironment(3, "Acceptance"));
    }

    @Override
    public Environment getEnvironment(final String name) throws IllegalArgumentException {
	for (final Environment environment : ENVIRONMENTS) {
	    if ( environment.getLabel().equals(name) ||
		    environment.getId().equals(name)   ) {
		return environment;
	    }
	}
	throw new IllegalArgumentException("Unknow environment for name ["+name+"].");
    }

    @Override
    public Collection<Environment> getEnvironments() {
	return Collections.unmodifiableCollection(ENVIRONMENTS);
    }

    @Override
    public Status getStatus(final Application application, final Environment environment) {
	List<Attribute> attributes = new ArrayList<>(3);
	attributes.add(new Attribute("Url", "http://localhost:8080/"+environment.getId()+"/"+application.getContext().replaceFirst("[\\\\|/]", "")));
	attributes.add(new Attribute("Version", application.getVersion()));
	if ( environment.getId().equalsIgnoreCase("Development") ) {
	    attributes.add(new Attribute("Scm", "https://github.com/gervaisb/"+application.getArtifactId()));
	}
	return new Status(attributes);
    }

}
