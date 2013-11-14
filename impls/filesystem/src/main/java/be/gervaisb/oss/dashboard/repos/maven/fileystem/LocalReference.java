package be.gervaisb.oss.dashboard.repos.maven.fileystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import be.gervaisb.oss.dashboard.Reference;
import be.gervaisb.oss.dashboard.Version;

class LocalReference implements Reference {

    private MavenXpp3Reader reader = new MavenXpp3Reader();
    private String groupId;
    private String artifactId;
    private Version version;
    private File pom;
    protected Model model;

    public LocalReference(final File pom) {
	this.pom = pom;
	read();
    }

    protected Model read() {
	try (InputStream in = new FileInputStream(pom)) {
	    model = reader.read(in);
	    groupId = model.getGroupId();
	    if ( groupId==null && model.getParent()!=null ) {
		groupId = model.getParent().getGroupId();
	    }
	    artifactId = model.getArtifactId();
	    version = new Version(model.getVersion());
	} catch (IOException | XmlPullParserException e) {
	    e.printStackTrace();
	}
	return model;
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

    @Override
    public int compareTo(final Reference o) {
	int diff = getGroupId().compareTo(o.getGroupId());
	if ( diff==0 ) {
	    diff = getArtifactId().compareTo(o.getArtifactId());
	}
	if ( diff==0 ) {
	    diff = -getVersion().compareTo(o.getVersion());
	}
	return diff;
    }

    @Override
    public int hashCode() {
	return 12 * getGroupId().hashCode() +
		getArtifactId().hashCode() +
		getVersion().hashCode() *3;
    }

    @Override
    public boolean equals(final Object obj) {
	if ( this==obj ) {
	    return true;
	}
	if ( !(obj instanceof Reference) ) {
	    return false;
	}
	Reference that = (Reference) obj;
	return	this.getGroupId().equals(that.getGroupId()) &&
		this.getArtifactId().equals(that.getArtifactId()) &&
		this.getVersion().equals(that.getVersion());
    }

    @Override
    public String toString() {
	return new StringBuilder()
	.append(getGroupId()).append(':')
	.append(getArtifactId()).append(':')
	.append(getVersion()).toString();
    }
}
