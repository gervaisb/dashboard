package be.gervaisb.oss.dashboard;

import java.util.Set;
import java.util.TreeSet;

public class Module extends Artifact implements Reference, HasDependencies, HasModules {

    public static class Missing extends Module {
	public Missing(final Reference reference, final HasModules owner) {
	    super(reference.getGroupId()+":"+reference.getArtifactId()+":"+reference.getVersion(), owner);
	}
    }

    private final Set<Dependency> dependencies = new TreeSet<>();
    private final Set<Module> modules = new TreeSet<>();
    private final HasModules parent;

    protected Module(final String descriptor, final HasModules parent) {
	super(descriptor);
	this.parent = parent;
	this.parent.getModules().add(this);
    }

    protected Module(final Artifact descriptor, final HasModules parent) {
	super(descriptor);
	this.parent = parent;
	this.parent.getModules().add(this);
    }

    public Module(final Reference reference, final Packaging packaging, final HasModules parent) {
	this(reference.getGroupId(), reference.getArtifactId(), reference.getVersion(), packaging, parent);
    }

    public Module(final String groupId, final String artifactId, final Version version, final Packaging packaging, final HasModules parent) {
	super(groupId, artifactId, version, packaging);
	this.parent = parent;
	this.parent.getModules().add(this);
    }

    @SuppressWarnings("unchecked")
    public <T extends HasModules> T getParent() {
	return (T) parent;
    }

    @Override
    public Set<Dependency> getDependencies() {
	return dependencies;
    }

    public Reference reference() {
	return this;
    }

    @Override
    public Set<Module> getModules() {
	return modules;
    }

}
