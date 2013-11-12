package be.gervaisb.oss.dashboard.repos.build;

import java.util.Date;

import com.google.common.base.Optional;

public class Status {

    public interface State {

	String getLabel();

	boolean isSuccessful();

    }
    // ~ ------------------------------------------------------------------------------------- ~ //

    private final State state;
    private final Date when;
    private final Optional<String> message;

    public Status(final State state, final Date when) {
	this(state, when, null);
    }

    public Status(final State state, final Date when, final String message) {
	this.state = state;
	this.when = when;
	this.message = Optional.fromNullable(message);
    }

    public State getState() {
	return state;
    }

    public Date getWhen() {
	return when;
    }

    public Optional<String> getMessage() {
	return message;
    }

    @Override
    public String toString() {
	final StringBuilder string = new StringBuilder().append(String.format(
		"Build %1$s, %2$tT %2$tD", state.getLabel(), when));
	if (message.isPresent()) {
	    string.append(". ").append(message.get());
	}
	return string.toString();
    }

}
