package be.gervaisb.oss.dashboard.repos.quality.mocks;

import java.util.ArrayList;
import java.util.List;

import be.gervaisb.oss.dashboard.Reference;
import be.gervaisb.oss.dashboard.repos.quality.Compliance;
import be.gervaisb.oss.dashboard.repos.quality.Quality;
import be.gervaisb.oss.dashboard.repos.quality.QualityRepository;
import be.gervaisb.oss.dashboard.repos.quality.Severity;
import be.gervaisb.oss.dashboard.repos.quality.Violations;

public class MockQualityRepository implements QualityRepository {

    public enum MockSeverity implements Severity {
	Blocker,
	Critical,
	Major,
	Minor;

	@Override
	public int getLevel() {
	    return values().length-ordinal();
	}

	@Override
	public String getLabel() {
	    return name();
	}

    }

    @Override
    public Quality getQuality(final Reference reference) {
	Compliance compliance = new Compliance(Math.random()*10, +3);
	List<Violations> violations = new ArrayList<>(MockSeverity.values().length);
	for (final be.gervaisb.oss.dashboard.repos.quality.Severity severity : MockSeverity.values()) {
	    int total = (int)((Math.random()*3)*(Math.random()*10));
	    int evolution = (int)(Math.random()*2)*(((Math.random()*10)%2)==0?-1:1);
	    violations.add(new Violations(total, severity, evolution));
	}
	return new Quality(compliance, violations);
    }


}
