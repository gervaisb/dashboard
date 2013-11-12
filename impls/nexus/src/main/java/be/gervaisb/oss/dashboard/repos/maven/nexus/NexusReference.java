package be.gervaisb.oss.dashboard.repos.maven.nexus;

import org.apache.maven.model.Model;

import be.gervaisb.oss.dashboard.Reference;
import be.gervaisb.oss.dashboard.Version;

class NexusReference implements Reference {

    protected Model model;
    private final String groupId;
    private final String artifactId;
    private final Version version;
    
    final String repositoryId;
        
    public NexusReference(String group, String artifact, Version version, String repositoryId) {
	this.groupId = group;
	this.artifactId = artifact;
	this.version = version;
	this.repositoryId = repositoryId;
    }
    
    public String getArtifactId() {
        return artifactId;
    }
    
    public String getGroupId() {
        return groupId;
    }
    
    public Version getVersion() {
	return version;
    }
    
    @Override
    public String toString() {
        return new StringBuilder("->")
        	.append(getGroupId()).append(':')
        	.append(getArtifactId()).append(':')
        	.append(getVersion())
        	.append("@Nexus\\").append(repositoryId).toString();
    }
    
    public int compareTo(Reference other) {
        return (toString()+repositoryId).compareTo(other.toString()+repositoryId);
    }
        
}