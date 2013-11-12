package be.gervaisb.oss.dashboard.repos;

import java.util.Collections;
import java.util.Date;

import be.gervaisb.oss.dashboard.Reference;
import be.gervaisb.oss.dashboard.repos.build.BuildsRepository;
import be.gervaisb.oss.dashboard.repos.build.Status;
import be.gervaisb.oss.dashboard.repos.issues.IssuesRepository;
import be.gervaisb.oss.dashboard.repos.issues.Statistics;
import be.gervaisb.oss.dashboard.repos.quality.Compliance;
import be.gervaisb.oss.dashboard.repos.quality.Quality;
import be.gervaisb.oss.dashboard.repos.quality.QualityRepository;

/**
 * Factory and marker class for absent repository.
 */
public class Absence {

    public static final class AbsenceOfBuildRepository extends Absence implements BuildsRepository {
	private class AbsenceOfState extends Absence implements Status.State {
	    @Override
	    public String getLabel() {
		return "";
	    }
	    @Override
	    public boolean isSuccessful() {
		return false;
	    }
	}

	private class AbsenceOfStatus extends Status {
	    public AbsenceOfStatus() {
		super(new AbsenceOfState(), new Date());
	    }
	}

	@Override
	public Status getStatus(final Reference reference) {
	    return new AbsenceOfStatus();
	}
    }

    public static final class AbsenceOfIssuesRepository extends Absence implements IssuesRepository {
	private class AbsenceOfStatistics extends Statistics {
	    @SuppressWarnings("unchecked")
	    public AbsenceOfStatistics() {
		super(Collections.EMPTY_MAP);
	    }
	}

	@Override
	public Statistics getStatistics(final Reference reference) {
	    return new AbsenceOfStatistics();
	}
    }

    public static final class AbsenceOfQualityRepository extends Absence implements QualityRepository {
	private class AbsenceOfQuality extends Quality {
	    @SuppressWarnings("unchecked")
	    public AbsenceOfQuality() {
		super(new Compliance(0, 0), Collections.EMPTY_LIST);
	    }
	}
	@Override
	public Quality getQuality(final Reference reference) {
	    return new AbsenceOfQuality();
	}

    }

    @SuppressWarnings("unchecked")
    public static <S> Class<S> ofClass(final Class<S> service) {
	if ( service.isAssignableFrom(BuildsRepository.class) ) {
	    return (Class<S>) AbsenceOfBuildRepository.class;
	} else if ( service.isAssignableFrom(IssuesRepository.class) ) {
	    return (Class<S>) AbsenceOfIssuesRepository.class;
	} else if ( service.isAssignableFrom(QualityRepository.class) ) {
	    return (Class<S>) AbsenceOfQualityRepository.class;
	} else {
	    throw new UnsupportedOperationException("Cannot create absence of "+service);
	}
    }

    private Absence() {}

}
