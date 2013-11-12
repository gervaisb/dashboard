package be.gervaisb.oss.dashboard.extensions.deploy.mvc;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.gervaisb.oss.dashboard.Application;
import be.gervaisb.oss.dashboard.Artifact;
import be.gervaisb.oss.dashboard.Reference;
import be.gervaisb.oss.dashboard.Version;
import be.gervaisb.oss.dashboard.repos.maven.MvnRepository;
import be.gervaisb.oss.dashboard.repos.status.StatusRepository;

public class DeploymentsController {

    private static final Logger LOG = LoggerFactory.getLogger(DeploymentsController.class);

    private final StatusRepository environments;
    private final MvnRepository repository;

    @Inject
    public DeploymentsController(final MvnRepository repository, final StatusRepository environments) {
	this.environments = environments;
	this.repository = repository;
    }

    public Result form(@Param("groupId") final String groupId, @Param("artifactId") final String artifactId, final Context context) {
	LOG.info("Rendering form for deploying {}", (groupId!=null?(groupId+":"+artifactId):"any artifact"));
	final Collection<Reference> availableArtifacts = repository.find(Application.class);
	final Result result = Results.html().template(locate("form"))
		.render("environments", environments.getEnvironments())
		.render("artifacts", getArtifacts(availableArtifacts))
		.render("versions", getVersions(availableArtifacts));

	if ( groupId!=null && artifactId!=null ) {
	    String selected = new StringBuilder(groupId.length()+artifactId.length()+1)
	    .append(groupId).append(':').append(artifactId).toString();
	    result
	    .render("selected", selected);
	}

	return result;
    }

    private Collection<Version> getVersions(final Collection<Reference> availableArtifacts) {
	final Set<Version> versions = new TreeSet<>();
	for (final Reference reference : availableArtifacts) {
	    versions.add(reference.getVersion());
	}
	return versions;
    }

    private Collection<Artifact> getArtifacts(final Collection<Reference> availableArtifacts) {
	final Set<Artifact> artifacts = new TreeSet<>();
	for (final Reference reference : availableArtifacts) {
	    artifacts.add(new Artifact(reference.getGroupId(), reference.getArtifactId(), null){/*Just a DTO*/});
	}
	return artifacts;
    }

    private static final String locate(final String template) {
	return new StringBuilder(200)
	.append(DeploymentsController.class.getPackage().getName().replace('.', '/'))
	.append("/mvc/").append(template).append(".ftl.html").toString();
    }

}
