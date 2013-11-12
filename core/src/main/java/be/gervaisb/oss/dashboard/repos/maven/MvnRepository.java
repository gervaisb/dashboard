package be.gervaisb.oss.dashboard.repos.maven;

import java.util.Collection;

import be.gervaisb.oss.dashboard.Artifact;
import be.gervaisb.oss.dashboard.Reference;
import be.gervaisb.oss.dashboard.Version;

/**
 * An artifact catalog is used to list, query and retrieve {@link Artifact}s 
 * produced for it.
 */
public interface MvnRepository {

    /**
     * The artifact builder is used to convert a {@link Reference} to a concrete 
     * {@link Artifact}. The way the {@link Reference} is passed to the builder 
     * is dependent to the {@link MvnRepository}.
     * <p>This is a required aspect to enforce coupling between the {@link MvnRepository} 
     * implementation and the {@link ArtifactBuilder}.
     */
    public interface ArtifactBuilder {
	<A extends Artifact> A as(Class<A> expected);
    }

        
    /**
     * Retrieve all artifacts produced for this catalog.
     * 
     * @return A {@link Collection} of {@link Reference} who allow the user to 
     * 	retrieve the complete {@link Artifact} via the method {@link MvnRepository#get(Reference)}
     */
    Collection<Reference> all();
    
    /**
     * Retrieve all artifacts of a given <tt>type</tt> produced for this catalog.
     * 
     * @param  type An {@link Artifact} subclass used to filter the artifacts on
     * 	they type.
     * 
     * @return A {@link Collection} of {@link Reference} who allow the user to 
     * 	retrieve the complete {@link Artifact} via the method {@link MvnRepository#get(Reference)}
     */
    Collection<Reference> find(Class<? extends Artifact> type);
    
    /**
     * Retrieve all artifacts produced for this catalog within a given <tt>groupId</tt>.
     * 
     * @param  groupId The Maven group Id used to filter the artifacts.
     * 
     * @return A {@link Collection} of {@link Reference} who allow the user to 
     * 	retrieve the complete {@link Artifact} via the method {@link MvnRepository#get(Reference)}
     */
    Collection<Reference> find(String groupId);
    
    /**
     * Retrieve all artifacts produced for this catalog within a given <tt>groupId</tt> 
     * and <tt>artifactId</tt>.
     * 
     * @param  groupId The Maven group Id used to filter the artifacts.
     * @param  groupId The Maven artifact Id used to filter the artifacts.
     * 
     * @return A {@link Collection} of {@link Reference} who allow the user to 
     * 	retrieve the complete {@link Artifact} via the method {@link MvnRepository#get(Reference)}
     */
    Collection<Reference> find(String groupId, String artifactId);
    
    /**
     * Produce an {@link ArtifactBuilder} who can be used to retrieve all 
     * informations about an artifact produced for this catalog within a given 
     * <tt>groupId</tt>, <tt>artifactId</tt> and <tt>version</tt>.
     * 
     * @param  groupId The Maven group Id used to identify the artifact.
     * @param  groupId The Maven artifact Id used to identify the artifact.
     * @param  version The Maven version used to identify the artifact.
     * 
     * @return An {@link ArtifactBuilder} who can the be used to produce the 
     * required artifact.
     */
    ArtifactBuilder get(String groupId, String artifactId, Version version) throws ArtifactNotFoundException;
    
    /**
     * Produce an {@link ArtifactBuilder} who can be used to retrieve all 
     * informations about the artifact referenced by the given {@link Reference}.
     * 
     * @param  reference The reference to the required {@link Artifact} subclass.
     * 
     * @return An {@link ArtifactBuilder} who can the be used to produce the 
     * required artifact.
     */
    ArtifactBuilder get(Reference reference) throws ArtifactNotFoundException;

}
