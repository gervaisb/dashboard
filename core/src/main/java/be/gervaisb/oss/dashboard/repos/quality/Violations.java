package be.gervaisb.oss.dashboard.repos.quality;

import com.google.common.base.Optional;

public class Violations implements Comparable<Violations> {

    private final Optional<Integer> evolution;
    private final Severity severity;
    private final int total;

    public Violations(final int total, final Severity severity) {
	this(total, severity, null);
    }

    public Violations(final int total, final Severity severity, final Integer evolution) {
	this.evolution = Optional.fromNullable(evolution);
	this.severity = severity;
	this.total = total;
    }

    public int getTotal() {
	return total;
    }

    public Severity getSeverity() {
	return severity;
    }

    public Optional<Integer> getEvolution() {
	return evolution;
    }

    @Override
    public int compareTo(final Violations o) {
        return getSeverity().getLevel()-o.getSeverity().getLevel();
    }
}
