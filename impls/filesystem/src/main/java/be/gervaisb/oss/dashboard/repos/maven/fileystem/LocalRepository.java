package be.gervaisb.oss.dashboard.repos.maven.fileystem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import be.gervaisb.oss.dashboard.Artifact;
import be.gervaisb.oss.dashboard.HasModules;
import be.gervaisb.oss.dashboard.Module;
import be.gervaisb.oss.dashboard.Packaging;
import be.gervaisb.oss.dashboard.Project;
import be.gervaisb.oss.dashboard.Reference;
import be.gervaisb.oss.dashboard.Version;
import be.gervaisb.oss.dashboard.repos.maven.ArtifactNotFoundException;
import be.gervaisb.oss.dashboard.repos.maven.MvnRepository;

public class LocalRepository implements MvnRepository {

    public static void main(final String[] args) {
	//File root = new File(new File(".").getAbsolutePath()).getParentFile().getParentFile().getParentFile();
	File root = new File("C:\\maven-home\\repository\\be\\credoc");
	System.out.println(root.getAbsolutePath());
	MvnRepository repository = new LocalRepository(root);

	//	System.out.println("Projects :");
	//	for (final Reference reference : repository.find(Project.class)) {
	//	    Project project = repository.get(reference).as(Project.class);
	//	    print(project, 0);
	//	}

	for (final Reference reference : repository.find("be.credoc.interactions", "asf")) {
	    System.out.println(reference.getGroupId()+':'+reference.getArtifactId()+':'+reference.getVersion());
	    printChildsOf(repository, reference, 0);
	}
    }

    private static void printChildsOf(final MvnRepository repository, final Reference parent, final int level) {
	for (final Reference child : repository.find(parent.getGroupId()+'.'+parent.getArtifactId())) {
	    if ( child.getVersion().equals(parent.getVersion()) ) {
		System.out.println(indent(child.getGroupId()+':'+child.getArtifactId()+':'+child.getVersion(), level));
		printChildsOf(repository, child, level+1);
	    }
	}
    }

    private static String indent(final String string, final int level) {
	final StringBuilder indented = new StringBuilder(string.length()+(level*3));
	for (int remaining=(level*3); remaining>-1; remaining--) {
	    indented.append(' ');
	}
	return indented.append(string).toString();
    }

    private static void print(final Artifact artifact, final int deep) {
	String prefix = "";
	if( deep==0 ) {
	    prefix = " ";
	} else {
	    for(int rest=deep; rest>=0; rest--) {
		prefix+= "   ";
	    }
	    prefix+="|";
	}
	System.out.println(prefix+"- "+artifact);

	//	if ( artifact instanceof HasDependencies ) {
	//	    for (final Dependency dependency : ((HasDependencies) artifact).getDependencies()) {
	//		System.out.println(prefix+"   |: "+dependency);
	//	    }
	//	}
	if ( artifact instanceof HasModules) {
	    for (final Module module : ((HasModules) artifact).getModules()) {
		print(module, deep+1);
	    }
	}
    }

    private final File root;
    private Set<LocalReference> content = null;

    public LocalRepository(final File root) {
	this.root = root;
    }

    @Override
    public Collection<Reference> all() {
	if ( content==null ) {
	    content = new TreeSet<>();
	    collectReferences(root, content);
	}
	return new TreeSet<Reference>(content);
    }

    private void collectReferences(final File from, final Collection<LocalReference> into) {
	for (final File file : from.listFiles()) {
	    if ( file.getName().equals("pom.xml") || file.getName().endsWith(".pom") ) {
		into.add(new LocalReference(file));
	    } else if ( file.isDirectory() ) {
		collectReferences(file, into);
	    }
	}
    }

    @Override
    public Collection<Reference> find(final Class<? extends Artifact> type) {
	final Set<Reference> founds = new TreeSet<>();
	for(final Reference candidate : all()) {
	    Packaging packaging = Packaging.valueOfOrNew(((LocalReference) candidate).model.getPackaging());
	    if ( type.equals(Project.class) && packaging.equals(Project.EXPECTED_PACKAGING) ) {
		founds.add(candidate);
	    }
	}
	return founds;
    }


    @Override
    public Collection<Reference> find(final String groupId) {
	final Set<Reference> founds = new TreeSet<>();
	for(final Reference candidate : all()) {
	    if ( candidate.getGroupId().equals(groupId) ) {
		founds.add(candidate);
	    }
	}
	return founds;
    }

    @Override
    public Collection<Reference> find(final String groupId, final String artifactId) {
	final Set<Reference> founds = new TreeSet<>();
	for(final Reference candidate : find(groupId)) {
	    if ( candidate.getArtifactId().equals(artifactId) ) {
		founds.add(candidate);
	    }
	}
	return founds;
    }

    @Override
    public ArtifactBuilder get(final String groupId, final String artifactId, final Version version) throws ArtifactNotFoundException {
	final List<LocalReference> founds = new ArrayList<>();
	for(final Reference candidate : find(groupId, artifactId)) {
	    if ( candidate.getVersion().equals(version) ) {
		founds.add((LocalReference) candidate);
	    }
	}
	if ( founds.size()==1 ) {
	    return new LocalArtifactBuilder(founds.get(0), this);
	} else {
	    throw new ArtifactNotFoundException(groupId, artifactId, version);
	}
    }

    @Override
    public ArtifactBuilder get(final Reference reference) throws ArtifactNotFoundException {
	return get(reference.getGroupId(), reference.getArtifactId(), reference.getVersion());
    }

}
