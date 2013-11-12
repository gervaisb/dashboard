package be.gervaisb.oss.dashboard;


/**
 * An {@link Application} is part of a {@link Project}. An application is
 * deployable so it can only be packaged as {@link Packaging#War}
 * @author bgervais
 *
 */
public class Application extends Module {

    public static class Missing extends Application {
	public Missing(final Reference reference) {
	    super(
		    reference.getGroupId()+":"+reference.getArtifactId()+":"+reference.getVersion(),
		    reference.getArtifactId()+"-"+reference.getVersion());
	}
    }

    public static final Packaging EXPECTED_PACKAGING = Packaging.War;

    private final String context;

    public Application(final String descriptor, final String context) {
	super(descriptor, null);
	if (!EXPECTED_PACKAGING.equals(super.getPackaging())) {
	    throw new IllegalArgumentException("Cannot create Application from a "+super.getPackaging()
		    + " artifact");
	}
	this.context = context;
    }

    protected Application(final Artifact artifact, final String context) {
	super(artifact, null);
	if ( !EXPECTED_PACKAGING.equals(super.getPackaging()) ) {
	    throw new IllegalArgumentException("Cannot create Application from a "+super.getPackaging()+" artifact");
	}
	this.context = context;
    }

    public Application(final String group, final String artifact, final Version version, final String context, final HasModules parent) {
	super(group, artifact, version, EXPECTED_PACKAGING, parent);
	this.context = context;
    }

    public String getContext() {
	return context;
    }

    @Override
    public Packaging getPackaging() {
	return EXPECTED_PACKAGING;
    }

}
