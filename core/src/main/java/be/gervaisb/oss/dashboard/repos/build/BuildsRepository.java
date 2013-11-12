package be.gervaisb.oss.dashboard.repos.build;

import be.gervaisb.oss.dashboard.Reference;

public interface BuildsRepository {

    Status getStatus(final Reference reference);
    
}
