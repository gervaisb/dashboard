package be.gervaisb.oss.dashboard.repos.quality;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Optional;

public class Quality {

	private final List<Violations> allViolations;
	private final Compliance compliance;

	public Quality(final Compliance compliance,
			final Collection<Violations> allViolations) {
		this.compliance = compliance;
		this.allViolations = new ArrayList<Violations>(allViolations);
	}

	public Compliance getCompliance() {
		return compliance;
	}

	public List<Violations> getAllViolations() {
		return Collections.unmodifiableList(allViolations);
	}

	public int getTotalViolations() {
		int total = 0;
		for (final Violations violations : getAllViolations()) {
			total += violations.getTotal();
		}
		return total;
	}

	public Optional<Integer> getTotalViolationsEvolution() {
		int evolution = 0;
		for (final Violations violations : getAllViolations()) {
			if (violations.getEvolution().isPresent()) {
				evolution += violations.getEvolution().get();
			} else
			    return Optional.absent();
		}
		return Optional.of(evolution);
	}
}
