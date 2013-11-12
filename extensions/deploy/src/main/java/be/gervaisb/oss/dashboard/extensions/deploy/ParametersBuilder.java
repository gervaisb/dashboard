package be.gervaisb.oss.dashboard.extensions.deploy;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

class ParametersBuilder {

    private final StringBuilder parameters = new StringBuilder("?");

    public ParametersBuilder add(final String name, final Object value) {
	parameters.append(name).append('=').append(encode(value)).append('&');
	return this;
    }

    private String encode(final Object value) {
	final String string = value==null?"":String.valueOf(value);
	try {
	    return URLEncoder.encode(string, "UTF-8");
	} catch (UnsupportedEncodingException e) {
	    return string;
	}
    }

    @Override
    public String toString() {
	return parameters.deleteCharAt(parameters.length()-1).toString();
    }
}
