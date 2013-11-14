package be.gervaisb.oss.dashboard.repos.maven.nexus;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.gervaisb.oss.dashboard.Artifact;
import be.gervaisb.oss.dashboard.Packaging;
import be.gervaisb.oss.dashboard.Reference;
import be.gervaisb.oss.dashboard.Version;
import be.gervaisb.oss.dashboard.repos.maven.ArtifactNotFoundException;
import be.gervaisb.oss.dashboard.repos.maven.MvnRepository;

/**
 * Implementation of <a href="http://www.sonatype.org/nexus/">Sonatype's Nexus
 * Maven Repository</a>.
 */
public class Nexus implements MvnRepository {

    private static final Logger LOG = LoggerFactory.getLogger(Nexus.class);

    private final Client nexus;
    private final String group;

    public Nexus(@Named("nexus.location")final URL location, @Named("nexus.group")final String group) {
	if (location == null) {
	    throw new IllegalArgumentException("'location' is required");
	}
	if (group == null || group.trim().isEmpty()) {
	    throw new IllegalArgumentException("'group' is required and cannot be empty");
	}
	this.nexus = new Client(location);
	this.group = group;
    }


    @Override
    public Collection<Reference> all() {
	return find(group + ".*", null);
    }

    @Override
    public Collection<Reference> find(final String groupId) {
	return find(groupId, null);
    }

    @Override
    public Collection<Reference> find(final String groupId, final String artifactId) {
	LOG.info("Finding artifacts with [{}:{}:*] on {}.", new Object[] { groupId, artifactId,
		nexus });
	final Version version = null;
	final Packaging packaging = null;

	Collection<Reference> references = null;
	if (!groupId.startsWith(group)) {
	    references = Collections.emptyList();
	} else if (groupId.charAt(0) == '.') { // Relative PATH
	    references = new ArrayList<Reference>(nexus.find(group + groupId, artifactId, version,
		    packaging));
	} else {
	    references = new ArrayList<Reference>(nexus.find(groupId, artifactId, version,
		    packaging));
	}
	LOG.debug("{} artifact(s) found for gav [{}:{}:{}:{}] on {}.",
		new Object[] { references.size(), groupId, artifactId, version, packaging, nexus });
	return references;
    }

    @Override
    public Collection<Reference> find(final Class<? extends Artifact> type) {
	LOG.info("Finding artifacts of type[{}] on {}.", new Object[] { type, nexus });
	final String artifactId = null;
	final Version version = null;

	List<Reference> references = new ArrayList<Reference>(nexus.find(group + ".*", artifactId, version, null));
	LOG.debug("{} artifact(s) found for type [{}] on {}.", new Object[] { references.size(),
		type, nexus });
	return references;
    }

    @Override
    public ArtifactBuilder get(final String groupId, final String artifactId, final Version version) throws ArtifactNotFoundException {
	LOG.info("Getting artifact [{}:{}:{}] on {}.", new Object[] { groupId, artifactId, version,
		nexus });
	return get(nexus.get(groupId, artifactId, version));
    }

    @Override
    public ArtifactBuilder get(final Reference reference) throws ArtifactNotFoundException {
	try {
	    return new NexusArtifactBuilder(nexus, reference);
	} catch (Throwable e) {
	    throw new ArtifactNotFoundException(reference, this, e);
	}
    }

}
