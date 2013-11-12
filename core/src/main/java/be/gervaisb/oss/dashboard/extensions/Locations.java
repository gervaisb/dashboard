package be.gervaisb.oss.dashboard.extensions;

public enum Locations implements Location {

    Navigation		("layout#navbar"),
    ProjectActions	("views.project/show#actions");

    private  final String PREFIX = "urn:be.gervaisb.oss.dashboard:ui:";

    private final String urn;

    private Locations(final String urn) {
	this.urn = urn;
    }

    @Override
    public String getUrn() {
	return PREFIX+urn;
    }

}
