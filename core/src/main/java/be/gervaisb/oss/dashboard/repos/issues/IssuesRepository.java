package be.gervaisb.oss.dashboard.repos.issues;

import be.gervaisb.oss.dashboard.Reference;

public interface IssuesRepository {

    Statistics getStatistics(Reference reference);
}
