package be.gervaisb.oss.dashboard.repos.issues.mocks;

import java.util.HashMap;
import java.util.Map;

import be.gervaisb.oss.dashboard.Reference;
import be.gervaisb.oss.dashboard.repos.issues.IssuesRepository;
import be.gervaisb.oss.dashboard.repos.issues.Statistics;
import be.gervaisb.oss.dashboard.repos.issues.Statistics.Severity;

public class MockIssuesRepository implements IssuesRepository {

    public enum MockSeverity implements Severity {
	Error,
	Warning,
	Info;

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
    public Statistics getStatistics(final Reference reference) {
	final Map<Severity, Integer> numberOfIssuesBySeverity = new HashMap<Statistics.Severity, Integer>();
	for (final Severity severity : MockSeverity.values()) {
	    numberOfIssuesBySeverity.put(severity, (int)(Math.random()*severity.getLevel()*10)+2);
	}
	return new Statistics(numberOfIssuesBySeverity);
    }

}
