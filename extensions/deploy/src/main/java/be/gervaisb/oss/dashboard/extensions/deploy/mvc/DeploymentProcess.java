package be.gervaisb.oss.dashboard.extensions.deploy.mvc;

import be.gervaisb.oss.dashboard.extensions.deploy.mvc.Deployment.State;

public class DeploymentProcess implements Runnable {

    private final Deployment deployment;

    public DeploymentProcess(final Deployment deployment) {
	this.deployment = deployment;
    }

    @Override
    public void run() {
	while ( isRunnable() ) {
	    deployment.setState(State.STARTED);
	    try {
		deployment.setState(State.FINISHED);

		//	} catch (InterruptedException interuption) {
		//	    deployment.setState(State.INTERRUPTED);
	    } catch (Throwable failure) {
		deployment.setState(State.FAILED);
	    }
	}
    }

}
