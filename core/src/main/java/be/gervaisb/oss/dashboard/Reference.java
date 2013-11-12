package be.gervaisb.oss.dashboard;

import be.gervaisb.oss.dashboard.Version;

/**
 * The {@link Reference} is the Dahsboard core element. It describe a Maven
 * Artifact but it is named "reference" to denote the lightweight aspect of this
 * object.
 */
public interface Reference extends Comparable<Reference> {

    Reference UNKNOW = new To("", "", Version.UNKNOW);
    
    public static class To implements Reference {
	private final String artifactId;
	private final String groupId;
	private final Version version;

	public To(final String groupId, final String artifactId, final Version version) {
	    this.artifactId = artifactId;
	    this.groupId = groupId;
	    this.version = version;
	}

	public String getGroupId() {
	    return groupId;
	}

	public String getArtifactId() {
	    return artifactId;
	}

	public Version getVersion() {
	    return version;
	}

	public int compareTo(Reference other) {
	    int diff = getGroupId().compareToIgnoreCase(other.getGroupId());
	    if (diff == 0) {
		diff = getArtifactId().compareToIgnoreCase(other.getArtifactId());
	    }
	    if (diff == 0) {
		diff = getVersion().compareTo(other.getVersion());
	    }
	    return diff;
	}

	public String toString() {
	    return new StringBuilder().append(getGroupId()).append(':').append(getArtifactId())
		    .append(':').append(getVersion()).toString();
	}
    }

    // ~ ------------------------------------------------------------------ ~ //

    public String getGroupId();

    public String getArtifactId();

    public Version getVersion();

    public String toString();

}
