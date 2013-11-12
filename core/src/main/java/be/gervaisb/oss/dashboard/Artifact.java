package be.gervaisb.oss.dashboard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simplest dependendable element. An {@link Artifact} is any dependency who can
 * be added to a {@link Project}. It represent a Maven artifact.
 */
public abstract class Artifact implements Reference {

    public final static String REGEX = "([\\w\\.-]*):([\\w\\./-]*):((?:[0-9]*)\\.(?:[0-9]*)(?:\\.(?:[0-9]*))?(?:-(?:\\w*))?)(?:\\:(jar|war|pom|zip))?";

    private final static String MAVEN_GAV_SEPARATOR = ":";
    private final static Pattern PATTERN = Pattern.compile(REGEX);
    private final static int HAS_GROUP 	= 1;
    private final static int HAS_ARTIFACT 	= 2;
    private final static int HAS_VERSION 	= 3;
    private final static int HAS_PACKAGING 	= 4;

    private String groupId;
    private String artifactId;
    private Version version;
    private Packaging packaging = Packaging.Unknow;

    public Artifact(final String descriptor) {
	Matcher matcher = null;
	if ( descriptor!=null && (matcher = PATTERN.matcher(descriptor)).matches() ) {
	    this.groupId = matcher.group(HAS_GROUP);
	    this.artifactId = matcher.group(HAS_ARTIFACT);
	    this.version = new Version(matcher.group(HAS_VERSION));
	    if ( matcher.group(HAS_PACKAGING)!=null ) {
		this.packaging = Packaging.valueOfIgnoreCaseOrElse(matcher.group(HAS_PACKAGING), packaging);
	    }
	} else if ( descriptor!=null ) {
	    String[] gav = descriptor.split(MAVEN_GAV_SEPARATOR);
	    switch (gav.length) {
	    case HAS_PACKAGING:
		packaging = Packaging.valueOfIgnoreCaseOrElse(gav[HAS_PACKAGING-1], packaging);
	    case HAS_VERSION:
		version = new Version(gav[HAS_VERSION-1]);
	    case HAS_ARTIFACT:
		artifactId = gav[HAS_ARTIFACT-1];
	    case HAS_GROUP:
		groupId = gav[HAS_GROUP-1];
		break;

	    default:
		groupId = descriptor;
		break;
	    }
	} else {
	    throw new IllegalArgumentException("Invalid artifact descriptor ["+descriptor+"]. Pattern not matched : \""+REGEX+"\"");
	}
    }

    public Artifact(final String group, final String artifact, final Version version) {
	this(group, artifact, version, Packaging.Unknow);
    }

    public Artifact(final String group, final String artifact, final Version version, final Packaging packaging) {
	this.groupId = group;
	this.artifactId = artifact;
	this.version = version;
	this.packaging = packaging;
    }

    protected Artifact(final Artifact artifact) {
	this(artifact.groupId, artifact.artifactId, artifact.version, artifact.packaging);
    }

    @Override
    public String getGroupId() {
	return groupId;
    }

    @Override
    public String getArtifactId() {
	return artifactId;
    }

    @Override
    public Version getVersion() {
	return version;
    }

    public Packaging getPackaging() {
	return packaging;
    }

    public <O extends Reference> boolean is(final Class<O> type) {
	return type!=null && type.isAssignableFrom(getClass());
    }

    @Override
    public int compareTo(final Reference other) {
	int diff = getGroupId().compareToIgnoreCase(other.getGroupId());
	if ( diff==0 ) {
	    diff = getArtifactId().compareToIgnoreCase(other.getArtifactId());
	}
	if ( diff==0 && getVersion()!=null ) {
	    diff = getVersion().compareTo(other.getVersion());
	}
	if ( diff==0 && (other instanceof Artifact) ) {
	    diff = getPackaging().compareTo(((Artifact) other).getPackaging());
	}
	return diff;
    }

    @Override
    public boolean equals(final Object obj) {
	if ( this==obj ) {
	    return true;
	}
	if ( !(obj instanceof Artifact) ) {
	    return false;
	}
	Artifact that = (Artifact) obj;
	return	getGroupId().equalsIgnoreCase(that.getGroupId()) &&
		getArtifactId().equalsIgnoreCase(that.getArtifactId()) &&
		getVersion().equals(that.getVersion()) &&
		getPackaging().equals(that.getPackaging());
    }

    @Override
    public String toString() {
	final StringBuilder string = new StringBuilder()
	.append(getGroupId()).append(':')
	.append(getArtifactId()).append(':')
	.append(getVersion());
	if ( getPackaging()!=Packaging.Unknow ) {
	    string.append(':').append(getPackaging().name().toLowerCase());
	}
	return string.toString();
    }

}
