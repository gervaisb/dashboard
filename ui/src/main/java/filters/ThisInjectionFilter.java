package filters;

import java.util.Map;

import javax.inject.Inject;

import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import conf.NinjaDashboard;

public class ThisInjectionFilter implements Filter {

    private static final String THIS = "this";

    private final NinjaDashboard dashboard;

    @Inject
    public ThisInjectionFilter(final NinjaDashboard reference) {
	this.dashboard = reference;
    }

    @Override @SuppressWarnings("rawtypes")
    public Result filter(final FilterChain chain, final Context context) {
	final Result result = chain.next(context);
	if ( result.getRenderable() instanceof Map && !((Map) result.getRenderable()).containsKey(THIS) ) {
	    result.render(THIS, dashboard);
	}
	return result;
    }

}
