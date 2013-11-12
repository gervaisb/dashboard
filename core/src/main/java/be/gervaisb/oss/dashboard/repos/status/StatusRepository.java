package be.gervaisb.oss.dashboard.repos.status;

import java.util.Collection;

import be.gervaisb.oss.dashboard.Application;


public interface StatusRepository {

    Collection<Environment> getEnvironments();

    Environment getEnvironment(String name) throws IllegalArgumentException;

    Status getStatus(Application application, Environment environment);

}
