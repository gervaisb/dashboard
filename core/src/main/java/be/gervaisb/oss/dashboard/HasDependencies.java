package be.gervaisb.oss.dashboard;

import java.util.Set;

public interface HasDependencies {

    public Set<Dependency> getDependencies();
    
}
