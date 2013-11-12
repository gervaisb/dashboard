package be.gervaisb.oss.dashboard.extensions.deploy.mvc;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import be.gervaisb.oss.dashboard.Application;
import be.gervaisb.oss.dashboard.extensions.deploy.mvc.Deployment.State;
import be.gervaisb.oss.dashboard.repos.status.Environment;

public class DeploymentsManager {

    private static final ExecutorService EXECUTORS = Executors.newFixedThreadPool(2);
    private static final ScheduledExecutorService CLEANERS  = Executors.newSingleThreadScheduledExecutor();
    private static final LinkedList<Deployment> QUEUE = new LinkedList<>();

    public DeploymentsManager() {

    }

    public synchronized Deployment push(final Application application, final Environment environment) {
	final Deployment deployment = new Deployment(application, environment);
	final DeploymentProcess process = new DeploymentProcess(deployment){
	    protected void inTerminated() {
		CLEANERS.schedule(new Runnable() {
		    @Override
		    public void run() {
			QUEUE.remove(deployment);
		    }
		}, 30, TimeUnit.SECONDS);
	    }
	};
	QUEUE.add(deployment);
	EXECUTORS.submit(process);
	return deployment;
    }

    public void abort(final Deployment deployment) {
	deployment.setState(State.INTERRUPTED);
    }


}
