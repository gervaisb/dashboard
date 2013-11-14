package be.gervaisb.oss.dashboard;

public class Packaging implements Comparable<Packaging> {

    public static final Packaging Pom = new Packaging("pom");
    public static final Packaging War = new Packaging("war");
    public static final Packaging Jar = new Packaging("jar");
    public static final Packaging Unknow = new Packaging("unknow");

    private static final Packaging[] VALUES = new Packaging[]{
	Pom, War, Jar, Unknow };


    private static final Packaging[] values() {
	return VALUES;
    }

    public final static Packaging valueOf(final String name) {
	for (final Packaging packaging : values()) {
	    if ( packaging.getName().equalsIgnoreCase(name) ) {
		return packaging;
	    }
	}
	throw new IllegalArgumentException("No enum constant found for name ["+name+"]");
    }

    public final static Packaging valueOfOr(final String name, final Packaging fallback) {
	try {
	    return valueOf(name);
	} catch (final IllegalArgumentException iae) {
	    return fallback;
	}
    }

    public final static Packaging valueOfOrNew(final String name) {
	return valueOfOr(name, new Packaging(name));
    }


    private final String name;

    private Packaging(final String name) {
	this.name = name.toLowerCase().intern();
    }

    public String getName() {
	return name;
    }

    @Override
    public int hashCode() {
	return name.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
	if ( this==obj ) {
	    return true;
	}
	if ( !(obj instanceof Packaging) ) {
	    return false;
	}

	return ((Packaging) obj).getName().equals(this.getName());
    }

    @Override
    public int compareTo(final Packaging o) {
	return getName().compareTo(o.getName());
    }

    @Override
    public String toString() {
	return getName();
    }

}
