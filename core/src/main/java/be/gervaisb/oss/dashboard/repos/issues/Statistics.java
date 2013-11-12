package be.gervaisb.oss.dashboard.repos.issues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Statistics {

    public interface Severity {

	/**
	 * Return this severity level where low level means low risk.
	 */
	int getLevel();

	String getLabel();

    }

    // ~ ------------------------------------------------------------------------------------- ~ //

    private final Map<Severity, Integer> numberOfIssuesBySeverity;

    public Statistics(final Map<Severity, Integer> numberOfIssuesBySeverity) {
	this.numberOfIssuesBySeverity = new HashMap<Severity, Integer>(numberOfIssuesBySeverity);
    }

    public int getTotalIssues() {
	int sum = 0;
	for (final Integer value : numberOfIssuesBySeverity.values()) {
	    sum += value.intValue();
	}
	return sum;
    }

    public int getTotalIssuesBySeverity(final Severity severity) {
	return	numberOfIssuesBySeverity.containsKey(severity)
		?numberOfIssuesBySeverity.get(severity).intValue()
			:0;
    }

    public double getPctIssuesBySeverity(final Severity severity) {
	double totalBySeverity = getTotalIssuesBySeverity(severity);
	double totalIssues     = getTotalIssues();
	return Math.round((totalBySeverity*100.0)/totalIssues);
    }

    public List<Severity> getSeverities() {
	return Collections.unmodifiableList(new ArrayList<Severity>(
		numberOfIssuesBySeverity.keySet()));
    }

}
