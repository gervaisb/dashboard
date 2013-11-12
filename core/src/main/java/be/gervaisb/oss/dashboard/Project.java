package be.gervaisb.oss.dashboard;

import java.util.Set;
import java.util.TreeSet;

/**
 * A {@link Project} is an artifact with dependencies and at least one module.
 * Since a project evolve, it has no specific version.
 *
 */
public class Project extends Artifact implements HasDependencies, HasModules {

    public static class Missing extends Project {
	public Missing(final Reference reference, final Reference parent) {
	    super(reference.getGroupId(), reference.getArtifactId(), reference.getVersion(), parent);
	}
    }

    public static final Packaging EXPECTED_PACKAGING = Packaging.Pom;

    private final Set<Dependency> dependencies = new TreeSet<Dependency>();
    private final Set<Module> modules = new TreeSet<Module>();
    private final Reference parent;
    private String description = "";

    public Project(final String group, final String artifact) {
	this(group, artifact, Version.LATEST);
    }
    public Project(final String group, final String artifact, final Version version) {
	super(group, artifact, version);
	this.parent = null;
    }

    public Project(final String group, final String artifact, final Version version, final Reference parent) {
	super(group, artifact, version);
	this.parent = parent;
    }

    @Override
    public Packaging getPackaging() {
	return EXPECTED_PACKAGING;
    }

    @Override
    public Set<Module> getModules() {
	return modules;
    }

    @Override
    public Set<Dependency> getDependencies() {
	return dependencies;
    }

    public String getName() {
	return getArtifactId();
    }

    public Reference getParent() {
	return parent;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(final String description) {
	this.description = description;
    }

}
