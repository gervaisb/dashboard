package be.gervaisb.oss.dashboard.extensions;

public class Button extends Link {

    public Button(final String text) {
	this(text, null, null);
    }

    public Button(final String text, final String href) {
	this(text, href, null);
    }

    public Button(final String text, final String href, final String title) {
	super(text, href, title);
	css("btn");
    }

}
