package be.gervaisb.oss.dashboard.repos.quality;

public interface Severity {

    /**
     * Return this severity level. Bigger is better.
     */
    int getLevel();

    String getLabel();

}
