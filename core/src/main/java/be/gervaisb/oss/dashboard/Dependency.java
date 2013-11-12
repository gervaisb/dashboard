package be.gervaisb.oss.dashboard;

public class Dependency extends Artifact {

    private final Scope scope;

    /**
     * Create a new dependency from a given reference.
     * The reference is not remembered. This is just a shortcut to create a
     * dependency on an existing artifact.
     */
    public Dependency(final Reference reference) {
	this(reference.getGroupId(), reference.getArtifactId(), reference.getVersion());
    }

    public Dependency(final String groupId, final String artifactId, final Version version) {
	this(groupId, artifactId, version, Scope.Compile);
    }

    public Dependency(final String groupId, final String artifactId, final Version version, final Scope scope) {
	super(groupId, artifactId, version);
	this.scope = scope;
    }

    protected Dependency(final Dependency dependency) {
	super(dependency);
	this.scope = dependency.scope;
    }

    public Scope getScope() {
	return scope;
    }

}
