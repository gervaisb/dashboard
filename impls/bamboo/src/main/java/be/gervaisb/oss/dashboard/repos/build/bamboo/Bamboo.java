package be.gervaisb.oss.dashboard.repos.build.bamboo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import be.gervaisb.oss.dashboard.Reference;
import be.gervaisb.oss.dashboard.repos.build.BuildsRepository;
import be.gervaisb.oss.dashboard.repos.build.Status;

public abstract class Bamboo implements BuildsRepository {

    private static final Logger LOG = LoggerFactory.getLogger(Bamboo.class);

    /** Relative URI who get the latest plan result */
    private static final String PLAN_INTERFACE = "/rest/api/latest/result/%1$s?expand=results[0:0].result";

    private final URL location;

    public Bamboo(final URL location) {
	this.location = location;
    }

    @Override
    public Status getStatus(final Reference reference) {
	LOG.debug("Getting latest build status for [{}].", reference);
	InputStream response = null;
	try {
	    URL request = new URL(location, String.format(PLAN_INTERFACE, resolve(reference)));
	    response = request.openStream();

	    BambooResponseParser parser = new BambooResponseParser();
	    return parser.parse(response);
	} catch (IOException e) {
	    LOG.error("Cannot get build status for [{}]. (Resolved to \"{}\").", new Object[]{
		    reference, resolve(reference), e});
	} catch (ParserConfigurationException e) {
	    LOG.error("Unable to create Bamboo response parser : {}. Returning Unknow status.", new Object[]{
		    e.getMessage(), e});
	} catch (SAXException e) {
	    LOG.error("Unable to parse Bamboo response for reference [{}] : {}. Returning Unknow status.", new Object[]{
		    reference, e.getMessage(), e});
	} finally {
	    if ( response!=null ) { try { response.close(); } catch (IOException ioe) { /* Ignore */ } }
	}
	return new Status(States.Unknow, new Date(), "Unable to retrieve status");
    }


    protected abstract String resolve(final Reference reference);

}
