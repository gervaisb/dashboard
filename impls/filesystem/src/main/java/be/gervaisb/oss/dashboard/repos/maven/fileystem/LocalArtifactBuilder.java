package be.gervaisb.oss.dashboard.repos.maven.fileystem;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;

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
import be.gervaisb.oss.dashboard.repos.maven.MvnRepository;
import be.gervaisb.oss.dashboard.repos.maven.MvnRepository.ArtifactBuilder;

public class LocalArtifactBuilder implements ArtifactBuilder {

    private final LocalReference reference;
    private final MvnRepository repository;

    public LocalArtifactBuilder(final LocalReference reference, final MvnRepository repository) {
	this.repository = repository;
	this.reference = reference;
    }

    @Override
    public <A extends Artifact> A as(final Class<A> expected) {
	A artifact = null;
	Model model = reference.read();
	if ( Application.class.equals(expected) ) {
	    artifact = (A) new Application(reference.getGroupId(), reference.getArtifactId(), reference.getVersion(), model.getBuild().getFinalName(), null);
	} else if ( Project.class.equals(expected) ) {
	    Parent parent = model.getParent();
	    Reference parentReference = parent!=null
		    ?new Reference.To(parent.getGroupId(), parent.getArtifactId(), new Version(parent.getVersion()))
	    :Reference.UNKNOW;
		    StringBuilder description = new StringBuilder();
		    if ( !(model.getName()==null || model.getName().isEmpty()) ) {
			description.append(model.getName());
		    }
		    if ( !(model.getDescription()==null || model.getDescription().isEmpty()) ) {
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
	for (org.apache.maven.model.Dependency mvnDep : reference.model.getDependencies()) {
	    Version version = new Version(mvnDep.getVersion());
	    Scope scope = Scope.valueOfIgnoreCaseOrElse(mvnDep.getScope(), Scope.Unknown);

	    owner.getDependencies().add(new Dependency(mvnDep.getGroupId(), mvnDep.getArtifactId(),
		    version, scope));
	}
    }

    private void setModules(final Artifact owner) {
	Module module = null;
	for (String mvnmodule : reference.model.getModules()) {
	    Reference reference = new Reference.To(owner.getGroupId(), mvnmodule, owner.getVersion());
	    Packaging packaging = Packaging.valueOfIgnoreCaseOrElse(this.reference.model.getPackaging(), Packaging.Unknow);
	    try {
		module = repository.get(reference.getGroupId(), reference.getArtifactId(), reference.getVersion()).as(Module.class);
	    } catch (ArtifactNotFoundException e) {
		module = new Module.Missing(reference, (HasModules) owner);
	    }
	    ((HasModules) owner).getModules().add(module);
	}
    }

}
