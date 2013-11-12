package be.gervaisb.oss.dashboard.extensions.deploy.mvc;

import java.util.Date;

import be.gervaisb.oss.dashboard.Application;
import be.gervaisb.oss.dashboard.repos.status.Environment;

public class Deployment implements Comparable<Deployment> {

    public enum State {
	PENDING, STARTED, FINISHED, INTERRUPTED, FAILED
    }

    private final Application application;
    private final Environment environment;
    private final Date when;
    private State state = State.PENDING;

    public Deployment(final Application application, final Environment environment) {
	this.application = application;
	this.environment = environment;
	this.when = new Date();
    }

    public Application getApplication() {
	return application;
    }

    public Environment getEnvironment() {
	return environment;
    }

    public void setState(final State state) {
	this.state = state;
    }

    public State getState() {
	return state;
    }

    @Override
    public String toString() {
	return new StringBuilder("Deployment of ")
	.append(application).append(" on ").append(environment.getLabel())
	.toString();
    }

    @Override
    public int hashCode() {
	return	application.hashCode()+environment.hashCode()*when.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
	if ( obj == this ) {
	    return true;
	}
	if ( !(obj instanceof Deployment) ) {
	    return false;
	}
	Deployment that = (Deployment) obj;
	return 	this.getApplication().equals(that.getApplication()) &&
		this.getEnvironment().equals(that.getEnvironment()) &&
		this.when.equals(that.when);
    }

    @Override
    public int compareTo(final Deployment o) {
	int diff = when.compareTo(o.when);
	if ( diff==0 ) {
	    diff = getApplication().compareTo(o.getApplication());
	}
	if ( diff==0 ) {
	    diff = getEnvironment().compareTo(o.getEnvironment());
	}
	return diff;
    }

}
