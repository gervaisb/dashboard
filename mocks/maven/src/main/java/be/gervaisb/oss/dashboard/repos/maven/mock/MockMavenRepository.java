package be.gervaisb.oss.dashboard.repos.maven.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import be.gervaisb.oss.dashboard.Application;
import be.gervaisb.oss.dashboard.Artifact;
import be.gervaisb.oss.dashboard.Dependency;
import be.gervaisb.oss.dashboard.Module;
import be.gervaisb.oss.dashboard.Packaging;
import be.gervaisb.oss.dashboard.Project;
import be.gervaisb.oss.dashboard.Reference;
import be.gervaisb.oss.dashboard.Version;
import be.gervaisb.oss.dashboard.repos.maven.ArtifactNotFoundException;
import be.gervaisb.oss.dashboard.repos.maven.MvnRepository;

public class MockMavenRepository implements MvnRepository {

    private static final List<Artifact> ARTIFACTS = new ArrayList<Artifact>();
    static {
	Project dashboard = new Project("be.gervaisb.oss", "dashboard", new Version(1, 0, 0, true));
	dashboard.setDescription("Applications Dashboard<br />"+
		"<blockquote>A GUI who display metrics for each projects from the enterprise catalog.</blockquote>");

	Module  core  = new Module(new Reference.To("be.gervaisb.oss.dashboard", "core", new Version(1, 0, 0, true)), Packaging.Jar, dashboard);
	core.getDependencies().add(new Dependency("org.slf4j", "slf4j-api", new Version(1, 7, 5)));
	core.getDependencies().add(new Dependency("com.google.guava", "guava", new Version("13.0")));

	Module impls = new Module("be.gervaisb.oss.dashboard", "impls", new Version(1, 0, 0, true), Packaging.Pom, dashboard);
	impls.getDependencies().add(new Dependency(core));
	Module nexus = new Module("be.gervaisb.oss.dashboard.impls", "maven-nexus", new Version(1, 0, 0, true), Packaging.Jar, impls);
	nexus.getDependencies().add(new Dependency("org.slf4j", "slf4j-api", new Version(1, 7, 5)));

	Module mocks = new Module("be.gervaisb.oss.dashboard", "mocks", new Version(1, 0, 0, true), Packaging.Pom, dashboard);
	Module maven = new Module("be.gervaisb.oss.dashboard.mocks", "maven", new Version(1, 0, 0, true), Packaging.Jar, impls);

	Application ui = new Application("be.gervaisb.oss.dashboard", "ui", new Version(1, 0, 0, true), "/", dashboard);

	ARTIFACTS.add(dashboard);
	ARTIFACTS.add(core);
	ARTIFACTS.add(impls);
	ARTIFACTS.add(nexus);
	ARTIFACTS.add(mocks);
	ARTIFACTS.add(maven);
	ARTIFACTS.add(ui);
    }

    @Override
    public Collection<Reference> all() {
	return new ArrayList<Reference>(ARTIFACTS);
    }

    @Override
    public Collection<Reference> find(final Class<? extends Artifact> type) {
	final List<Reference> founds = new ArrayList<Reference>();
	for (final Artifact candidate : ARTIFACTS) {
	    if ( candidate.is(type) ) {
		founds.add(candidate);
	    }
	}
	return founds;
    }

    @Override
    public Collection<Reference> find(final String groupId) {
	final List<Reference> founds = new ArrayList<Reference>();
	for (final Artifact candidate : ARTIFACTS) {
	    if ( candidate.getGroupId().equals(groupId) ) {
		founds.add(candidate);
	    }
	}
	return founds;
    }

    @Override
    public Collection<Reference> find(final String groupId, final String artifactId) {
	final List<Reference> founds = new ArrayList<Reference>();
	for (final Reference candidate : find(groupId)) {
	    if ( candidate.getArtifactId().equals(artifactId) ) {
		founds.add(candidate);
	    }
	}
	return founds;
    }

    @Override
    public ArtifactBuilder get(final String groupId, final String artifactId, final Version version) throws ArtifactNotFoundException {
	final List<Reference> candidates = new ArrayList<>(find(groupId, artifactId));

	if ( candidates.isEmpty() ) {
	    throw new ArtifactNotFoundException(groupId, artifactId, version);
	}

	if ( Version.LATEST.equals(version) ) {
	    Collections.sort(candidates, new Comparator<Reference>() {
		@Override
		public int compare(final Reference o1, final Reference o2) {
		    int diff = o1.compareTo(o2);
		    if ( diff==0 ) {
			if ( Version.LATEST.equals(o1.getVersion()) ) {
			    return 1;
			}
			return o1.getVersion().compareTo(o2.getVersion());
		    }
		    return 0;
		}
	    });
	    return new ArtifactBuilder() {
		@Override
		public <A extends Artifact> A as(final Class<A> expected) {
		    return expected.cast(candidates.get(0));
		}
	    };
	} else {
	    for (final Reference candidate : candidates) {
		if (candidate.getVersion().equals(version)) {
		    return new ArtifactBuilder() {
			@Override
			public <A extends Artifact> A as(final Class<A> expected) {
			    return expected.cast(candidate);
			}
		    };
		}
	    }
	}
	throw new ArtifactNotFoundException(groupId, artifactId, version);
    }

    @Override
    public ArtifactBuilder get(final Reference reference) throws ArtifactNotFoundException {
	return get(reference.getGroupId(), reference.getArtifactId(), reference.getVersion());
    }

}
