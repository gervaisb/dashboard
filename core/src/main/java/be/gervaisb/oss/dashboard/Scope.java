package be.gervaisb.oss.dashboard;

public enum Scope {

    Compile, Provided, Runtime, System, Test, Unknown;
    
    public final static Scope valueOfIgnoreCase(final String string) {
	for (final Scope scope : values()) {
	    if ( scope.name().equalsIgnoreCase(string) ) {
		return scope;
	    }
	}
	throw new IllegalArgumentException("No enum constant found for name ["+string+"]");
    }
    
    public final static Scope valueOfIgnoreCaseOrElse(final String string, final Scope def) {
	try {
	    return valueOfIgnoreCase(string);
	} catch (final IllegalArgumentException iae) {
	    return def;
	}
    }
        
}
