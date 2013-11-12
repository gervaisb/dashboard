package be.gervaisb.oss.dashboard.repos.status;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class Status {

    public static class Attribute implements Comparable<Attribute> {

	private final String name;
	private final Object value;

	public Attribute(final String name, final Object value) {
	    if ( name==null || name.trim().isEmpty() ) {
		throw new IllegalArgumentException("Attribute key cannot be null or empty");
	    }
	    this.name = name;
	    this.value = value;
	}

	public String getName() {
	    return name;
	}

	public Object getValue() {
	    return value;
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
	    if ( !(obj instanceof Attribute) ) {
		return false;
	    }
	    Attribute that = (Attribute) obj;
	    return this.getName().equals(that.getName());
	}

	@Override
	public int compareTo(final Attribute o) {
	    return getName().compareTo(o.getName());
	}
    }

    private Set<Attribute> attributes = new TreeSet<>();

    public Status(final Map<String, Object> attributes) {
	if ( attributes!=null && !attributes.isEmpty() ) {
	    for (final Map.Entry<String, Object> attribute : attributes.entrySet()) {
		this.attributes.add(new Attribute(attribute.getKey(), attribute.getValue()));
	    }
	}
    }

    public Status(final Attribute... attributes) {
	if ( attributes!=null && attributes.length>0 ) {
	    this.attributes.addAll(Arrays.asList(attributes));
	}
    }

    public Status(final Iterable<Attribute> attributes) {
	if ( attributes!=null ) {
	    for (final Attribute attribute : attributes) {
		this.attributes.add(attribute);
	    }
	}
    }

    public Collection<Attribute> getAttributes() {
	return Collections.unmodifiableCollection(attributes);
    }

}
