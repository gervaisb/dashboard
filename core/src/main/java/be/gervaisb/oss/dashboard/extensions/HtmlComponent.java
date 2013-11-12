package be.gervaisb.oss.dashboard.extensions;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

abstract class HtmlComponent implements Component {

    private final Map<String, String> attributes = new TreeMap<>();
    private final String tag;
    private String text = null;

    public HtmlComponent(final String tag) {
	this(tag, null);
    }

    public HtmlComponent(final String tag, final String text) {
	this.tag = tag;
	this.text = text;
    }

    public HtmlComponent text(final String text) {
	this.text = text;
	return this;
    }

    public HtmlComponent attr(final String name, final String value) {
	attributes.put(name, value);
	return this;
    }

    public String attr(final String name) {
	return attributes.containsKey(name)
		?attributes.get(name):"";
    }

    public HtmlComponent id(final String id) {
	return attr("id", "id");
    }

    public HtmlComponent css(final String css) {
	final StringBuffer current = new StringBuffer(attr("class"));
	if ( current.length()>0 ) {
	    current.append(' ');
	}
	current.append(css);
	return attr("class", current.toString());
    }

    @Override
    public String render(final Map<String, Object> model) {
	final StringBuilder html = new StringBuilder()
	.append('<').append(tag);

	for (final Entry<String, String> attribute : attributes.entrySet()) {
	    html.append(' ').append(attribute.getKey()).append("=\"")
	    .append(attribute.getValue()).append("\"");
	}

	if ( text!=null ) {
	    html.append('>').append(text).append("</").append(tag).append('>');
	} else {
	    html.append("/>");
	}
	return html.toString();
    }

}
