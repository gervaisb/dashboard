package be.gervaisb.oss.dashboard.repos.maven.nexus;

import java.io.IOException;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

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

	A artifact = null;
	if ( Application.class.equals(expected) ) {
	    artifact = (A) new Application(descriptor, model.getBuild().getFinalName());
	} else if ( Project.class.equals(expected) ) {
	    Parent parent = model.getParent();
	    Reference parentReference = parent!=null
		    ?new Reference.To(parent.getGroupId(), parent.getArtifactId(), new Version(parent.getVersion()))
	    :Reference.UNKNOW;
		    StringBuilder description = new StringBuilder();
		    if ( !model.getName().isEmpty() ) {
			description.append(model.getName());
		    }
		    if ( !model.getDescription().isEmpty() ) {
			description.append(description.length()>0?"<br />":"")
			.append(model.getDescription());
		    }
		    artifact = (A) new Project(reference.getGroupId(), reference.getArtifactId(), reference.getVersion(), parentReference);
		    ((Project) artifact).setDescription(description.toString());
	};

	populate(artifact);
	return artifact;
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
	    Packaging packaging = Packaging.valueOfIgnoreCaseOrElse(model.getPackaging(), Packaging.Unknow);
	    try {
		module = new Module(nexus.get(reference.getGroupId(), reference.getArtifactId(), reference.getVersion()), packaging, (HasModules) owner);
	    } catch (ArtifactNotFoundException e) {
		module = new Module.Missing(reference, (HasModules) owner);
	    }
	    ((HasModules) owner).getModules().add(module);
	}
    }
}