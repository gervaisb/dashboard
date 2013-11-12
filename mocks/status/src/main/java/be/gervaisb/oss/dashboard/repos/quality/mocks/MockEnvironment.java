package be.gervaisb.oss.dashboard.repos.quality.mocks;

import be.gervaisb.oss.dashboard.repos.status.Environment;

class MockEnvironment implements Environment {

    private final int position;
    private final String name;

    public MockEnvironment(final int position, final String name) {
	this.position = position;
	this.name = name;
    }

    @Override
    public int compareTo(final Environment o) {
	if ( o instanceof MockEnvironment ) {
	    return (position-(((MockEnvironment) o).position))*-1;
	}
	return getLabel().compareToIgnoreCase(o.getLabel());
    }

    @Override
    public String getLabel() {
	return name;
    }

    @Override
    public String getId() {
	return name.toLowerCase().replaceAll("\\s", "_");
    }

}
