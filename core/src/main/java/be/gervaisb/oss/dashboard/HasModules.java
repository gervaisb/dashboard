package be.gervaisb.oss.dashboard;

import java.util.Set;

import be.gervaisb.oss.dashboard.Reference;

public interface HasModules extends Reference {

    public Set<Module> getModules();
    
}
