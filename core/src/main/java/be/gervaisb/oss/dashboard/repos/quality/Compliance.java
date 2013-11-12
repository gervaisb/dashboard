package be.gervaisb.oss.dashboard.repos.quality;

import com.google.common.base.Optional;

public class Compliance {

    private final Optional<Integer> evolution;
    private final double value;

    public Compliance(final double value) {
	this(value, null);
    }

    public Compliance(final double value, final Integer evolution) {
	this.evolution = Optional.fromNullable(evolution);
	this.value = value;
    }

    public double getValue() {
	return value;
    }

    public Optional<Integer> getEvolution() {
	return evolution;
    }

}
