package be.gervaisb.oss.dashboard;

public enum Packaging {

    Zip, War, Jar, Pom, Unknow;
    
    public final static Packaging valueOfIgnoreCase(final String string) {
	for (final Packaging packaging : values()) {
	    if ( packaging.name().equalsIgnoreCase(string) ) {
		return packaging;
	    }
	}
	throw new IllegalArgumentException("No enum constant found for name ["+string+"]");
    }
    
    public final static Packaging valueOfIgnoreCaseOrElse(final String string, final Packaging def) {
	try {
	    return valueOfIgnoreCase(string);
	} catch (final IllegalArgumentException iae) {
	    return def;
	}
    }
}
