package be.gervaisb.oss.dashboard.extensions;

public class Link extends HtmlComponent {

    public Link(final String text) {
	this(text, null, null);
    }

    public Link(final String text, final String path) {
	this(text, path, null);
    }

    public Link(final String text, final String path, final String title) {
	super("a", text);
	to(path).titled(title);
    }

    public Link to(final String href) {
	attr("href", href);
	return this;
    }

    public Link titled(final String title) {
	attr("title", title);
	return this;
    }

}
