package be.gervaisb.oss.dashboard.repos.status;

public interface Environment extends Comparable<Environment> {

    String getLabel();

    String getId();
}
