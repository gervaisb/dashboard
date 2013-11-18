package be.gervaisb.oss.dashboard.repos.maven.nexus;

import java.io.IOException;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.gervaisb.oss.dashboard.Application;
import be.gervaisb.oss.dashboard.Artifact;
import be.gervaisb.oss.dashboard.Dependency;
import be.gervaisb.oss.dashboard.HasDependencies;
import be.gervaisb.oss.dashboard.HasModules;
import be.gervaisb.oss.dashboard.Module;
import be.gervaisb.oss.dashboard.Packaging;
import be.gervaisb.oss.dashboard.Project;
import be.gervaisb.oss.dashboard.Reference;
import be.gervaisb.oss.dashboard.Scope;
import be.gervaisb.oss.dashboard.Version;
import be.gervaisb.oss.dashboard.repos.maven.ArtifactNotFoundException;
import be.gervaisb.oss.dashboard.repos.maven.MvnRepository.ArtifactBuilder;

class NexusArtifactBuilder implements ArtifactBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(NexusArtifactBuilder.class);

    private final MavenXpp3Reader reader;
    private final Client nexus;
    private final Reference reference;
    private final String descriptor;
    private final Model model;

    public NexusArtifactBuilder(final Client nexus, final Reference reference) throws Throwable {
	this.reference = reference;
	this.nexus = nexus;

	this.reader = new MavenXpp3Reader();
	this.model = read();
	this.descriptor = reference.getGroupId()+":"+reference.getArtifactId()+":"+reference.getVersion();
    }

    private final Model read() throws IOException, XmlPullParserException {
	final Reference trueReference = (reference instanceof Module)?((Module) reference).reference():reference;
	if ( trueReference instanceof NexusReference ) {
	    NexusReference nexusReference = (NexusReference) trueReference;
	    if ( nexusReference.model==null ) {
		nexusReference.model = reader.read(nexus.read(trueReference));
	    }
	    return nexusReference.model;
	} else {
	    return reader.read(nexus.read(trueReference));
	}
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Artifact> A as(final Class<A> expected) {
	if ( reference.getClass().equals(expected) ) {
	    return expected.cast(reference);
	}

	final String artifactId = reference.getArtifactId();
	final String groupId = reference.getGroupId();
	final Version version = reference.getVersion();

	A artifact = null;
	if (Application.class.equals(expected)) {
	    Project parent = loadParentProject(model);
	    String context = model.getBuild().getFinalName();

	    artifact = (A) new Application(groupId, artifactId, version, context, parent);

	} else if (Project.class.equals(expected)) {
	    Reference parent = parentReference(model);

	    String name = null;
	    StringBuilder description = new StringBuilder();
	    if (model.getName() != null && !model.getName().isEmpty()) {
		name = model.getName();
		description.append(model.getName());
	    }
	    if (model.getDescription() != null && !model.getDescription().isEmpty()) {
		description.append(description.length() > 0 ? "<br />" : "")
		.append(model.getDescription());
	    }

	    artifact = (A) new Project(groupId, artifactId, version, parent);
	    ((Project) artifact).setName(name);
	    ((Project) artifact).setDescription(description.toString());
	} else if (Module.class.equals(expected)) {
	    Project parent = loadParentProject(model);
	    artifact = (A) new Module(groupId, artifactId, version, Packaging.valueOfOrNew(model.getPackaging()), parent);
	}
	populate(artifact);
	LOG.debug("{} loaded as {} from {}.", artifact, expected.getSimpleName(), nexus);
	return artifact;
    }

    private Project loadParentProject(final Model model) {
	final Reference reference = parentReference(model);
	Project parent;
	try {
	    parent = new NexusArtifactBuilder(nexus, reference).as(Project.class);
	} catch (Throwable e) {
	    parent = new Project.Missing(reference, null);
	}
	return parent;
    }

    private Reference parentReference(final Model model) {
	Parent descriptor = model.getParent();
	if ( descriptor==null ) {
	    return Reference.UNKNOW;
	} else {
	    Version version = new Version(descriptor.getVersion());
	    return new Reference.To(
		    descriptor.getGroupId(), descriptor.getArtifactId(), version);
	}
    }

    private void populate(final Artifact artifact) {
	if ( artifact instanceof HasDependencies ) {
	    setDependencies((HasDependencies) artifact);
	}
	if ( artifact instanceof HasModules ) {
	    setModules(artifact);
	}
    }

    private void setDependencies(final HasDependencies owner) {
	for (org.apache.maven.model.Dependency mvnDep : model.getDependencies()) {
	    Version version = new Version(mvnDep.getVersion());
	    Scope scope = Scope.valueOfIgnoreCaseOrElse(mvnDep.getScope(), Scope.Unknown);

	    owner.getDependencies().add(new Dependency(mvnDep.getGroupId(), mvnDep.getArtifactId(),
		    version, scope));
	}
    }

    private void setModules(final Artifact owner) {
	Module module = null;
	for (String mvnmodule : model.getModules()) {
	    Reference reference = new Reference.To(owner.getGroupId(), mvnmodule, owner.getVersion());
	    Packaging packaging = Packaging.valueOfOr(model.getPackaging(), Packaging.Unknow);
	    try {
		module = new Module(nexus.get(reference.getGroupId(), reference.getArtifactId(), reference.getVersion()), packaging, (HasModules) owner);
	    } catch (ArtifactNotFoundException e) {
		module = new Module.Missing(reference, (HasModules) owner);
	    }
	    ((HasModules) owner).getModules().add(module);
	}
    }
}