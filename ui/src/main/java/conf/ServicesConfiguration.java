package conf;

import java.util.ServiceConfigurationError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.gervaisb.oss.dashboard.repos.Absence;
import be.gervaisb.oss.dashboard.repos.build.BuildsRepository;
import be.gervaisb.oss.dashboard.repos.issues.IssuesRepository;
import be.gervaisb.oss.dashboard.repos.maven.MvnRepository;
import be.gervaisb.oss.dashboard.repos.quality.QualityRepository;
import be.gervaisb.oss.dashboard.repos.status.StatusRepository;

import com.google.inject.AbstractModule;

class ServicesConfiguration extends AbstractModule {

    private static final Logger LOG = LoggerFactory.getLogger(NinjaDashboard.class);

    @Override
    protected void configure() {
	bind(MvnRepository.class).to(required(MvnRepository.class));
	bind(StatusRepository.class).to(required(StatusRepository.class));

	bind(BuildsRepository.class).to(optional(BuildsRepository.class));
	bind(IssuesRepository.class).to(optional(IssuesRepository.class));
	bind(QualityRepository.class).to(optional(QualityRepository.class));
    }

    private <T> Class<T> required(final Class<T> service) {
	final Class<T> implementation = ServiceDeclaration.get(service);
	if ( implementation==null ) {
	    LOG.error("No declaration found for the required service \"{}\".\n\t" +
		    "Please verify that you have a \"/META_INF/services/{}\" file in this application \"lib\" directory.",
		    service.getName(), service.getName());
	    throw new ServiceConfigurationError("No declaration found for the required \""+service.getName()+"\". "+
		    "Please verify that you have a \"/META_INF/services/"+service.getName()+"\" file in this application \"lib\" directory.");
	}
	return implementation;
    }

    private <T> Class<T> optional(final Class<T> service) {
	final Class<T> implementation = ServiceDeclaration.get(service);
	if ( implementation==null ) {
	    LOG.info("Returning absence for \"{}\".", service.getName());
	    return Absence.ofClass(service);
	}
	return implementation;
    }

}