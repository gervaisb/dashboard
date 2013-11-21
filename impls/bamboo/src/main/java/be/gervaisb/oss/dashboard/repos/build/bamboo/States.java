package be.gervaisb.oss.dashboard.repos.build.bamboo;

import be.gervaisb.oss.dashboard.repos.build.Status;

public enum States implements Status.State {

    Successful 	(true),
    Failed	(false),
    Unknow	(false);

    private final boolean isSuccessful;

    private States(final boolean isSuccessful) {
	this.isSuccessful = isSuccessful;
    }

    @Override
    public String getLabel() {
	return name();
    }

    @Override
    public boolean isSuccessful() {
	return isSuccessful;
    }

}
