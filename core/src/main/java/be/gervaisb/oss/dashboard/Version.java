package be.gervaisb.oss.dashboard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version implements Comparable<Version> {

    public static final Version UNKNOW = new Version(0, 0, 0) {
	@Override
	public String getValue() {
	    return "Unknow";
	}
	@Override
	public boolean isFormated() {
	    return false;
	}
    };

    public static final Version LATEST = new Version(0, 0, 0) {
	@Override
	public String getValue() {
	    return "[0)";
	}
	@Override
	public boolean isFormated() {
	    return false;
	}
    };

    public static Version valueOf(final String string) throws IllegalArgumentException {
	return new Version(string);
    }


    public final static String REGEX = "([0-9]*)\\.([0-9]*)(?:\\.([0-9]*))?(?:-(\\w*))?";

    private final static String SNAPSHOT = "SNAPSHOT";
    private final static Pattern PATTERN = Pattern.compile(REGEX);
    private final static int MAJOR 	= 1;
    private final static int MINOR 	= 2;
    private final static int CORRECTIVE = 3;
    private final static int QUALIFIER	= 4;



    private final int major;
    private final int minor;
    private final Integer corrective;
    private final String qualifier;
    private final String value;

    public Version(final String version) throws IllegalArgumentException {
	Matcher matcher = null;
	if ( version!=null && (matcher = PATTERN.matcher(version)).matches() ) {
	    this.major = Integer.parseInt(matcher.group(MAJOR));
	    this.minor = Integer.parseInt(matcher.group(MINOR));
	    this.corrective = matcher.group(CORRECTIVE)!=null?Integer.parseInt(matcher.group(CORRECTIVE)):null;
	    this.qualifier = matcher.group(QUALIFIER);
	    this.value = null;
	} else {
	    this.major = 0;
	    this.minor = 0;
	    this.corrective = null;
	    this.qualifier = null;
	    this.value = version;
	    //throw new IllegalArgumentException("Invalid version number ["+version+"]. Pattern not matched : \""+REGEX+"\"");
	}
    }

    public Version(final int major, final int minor, final int corrective) {
	this(major, minor, corrective, false);
    }

    public Version(final int major, final int minor, final int corrective, final boolean snapshot) {
	this(major, minor, corrective, snapshot?SNAPSHOT:null);
    }

    public Version(final int major, final int minor, final int corrective, final String qualifier) {
	this.major = major;
	this.minor = minor;
	this.corrective = corrective;
	this.qualifier = qualifier;
	this.value = null;
    }

    public int getMajor() {
	return major;
    }

    public int getMinor() {
	return minor;
    }

    public int getCorrective() {
	return corrective!=null?corrective:0;
    }

    public String getQualifier() {
	return qualifier!=null?qualifier:"";
    }

    public boolean isSnapshot() {
	return SNAPSHOT.equalsIgnoreCase(qualifier);
    }

    public String getValue() {
	return value==null?toString():value;
    }

    public boolean isFormated() {
	return value==null;
    }

    @Override
    public int compareTo(final Version other) {
	int diff = other.getMajor()-getMajor();
	if ( diff==0 ) {
	    diff = other.getMinor()-getMinor();
	}
	if ( diff==0) {
	    diff = other.getCorrective()-getCorrective();
	}
	if ( diff==0 ) {
	    diff = other.getQualifier().compareToIgnoreCase(getQualifier());
	}

	return diff;
    }

    @Override
    public boolean equals(final Object obj) {
	if ( this==obj ) {
	    return true;
	}
	if ( !(obj instanceof Version) ) {
	    return false;
	}
	Version that = (Version) obj;
	if ( isFormated() && that.isFormated() ) {
	    return	this.getMajor()==that.getMajor() &&
		    this.getMinor()==that.getMinor() &&
		    this.getCorrective()==that.getCorrective() &&
		    this.getQualifier()==that.getQualifier();
	} else {
	    return getValue().equals(that.getValue());
	}
    }

    @Override
    public String toString() {
	if ( !isFormated() ) {
	    return getValue();
	}

	StringBuilder string = new StringBuilder()
	.append(major).append('.').append(minor);
	if ( corrective!=null ) {
	    string.append('.').append(corrective);
	}
	if ( qualifier!=null ) {
	    string.append('-').append(qualifier);
	}
	return string.toString();
    }

}
