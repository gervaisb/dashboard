package be.gervaisb.oss.dashboard.repos.maven;

import be.gervaisb.oss.dashboard.Reference;
import be.gervaisb.oss.dashboard.Version;

public class ArtifactNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 7966816016283967486L;

    private final String artifact;

    public ArtifactNotFoundException(final Reference reference, final MvnRepository catalog) {
	this(reference, catalog, null);
    }

    public ArtifactNotFoundException(final Reference reference, final MvnRepository catalog,
	    final Throwable cause) {
	super(reference.getGroupId() + ":" + reference.getArtifactId() + ":"
		+ reference.getVersion(), cause);
	artifact = reference.getGroupId() + ":" + reference.getArtifactId() + ":"
		+ reference.getVersion();
    }

    public ArtifactNotFoundException(final String groupId, final String artifactId,
	    final Version version) {
	super(groupId + ":" + artifactId + ":" + version);
	artifact = groupId + ":" + artifactId + ":" + version;
    }

    public String getArtifact() {
	return artifact;
    }

}
