package be.gervaisb.oss.dashboard.extensions;

import java.util.Properties;


public interface Dashboard {

    RouteBuilder route(final HttpVerb method, final String resource);

    public interface RouteBuilder {
	void to(final Class<?> hnadler, final String method);
    }


    IncludeBuilder include(final Component component);

    public interface IncludeBuilder {
	void into(Location location);
    }


    boolean hasIssuesTracker();

    boolean hasQualityManager();

    boolean hasContinuousBuilder();

    boolean isDevelopment();


    Properties getProperties();

}
