package controllers;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ninja.Context;
import ninja.Result;
import ninja.lifecycle.Dispose;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AsyncController {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncController.class);

    private final ExecutorService executor;

    public AsyncController() {
	int numberOfThreads = Runtime.getRuntime().availableProcessors()-1;
	executor = Executors.newScheduledThreadPool(numberOfThreads<1?1:numberOfThreads);
	LOG.info("Initialized with a fixed pool of {} thread(s).", (numberOfThreads<1?1:numberOfThreads));
    }

    protected final Result async(final Context context, final Callable<Result> future) throws Exception {
	return future.call();
	//	LOG.trace("Submitting asynchronous call for \"{} {}\".", context.getMethod(), context.getRequestPath());
	//	executor.submit(new Runnable() {
	//	    @Override
	//	    public void run() {
	//		Result result = null;
	//		try {
	//		    result = future.call();
	//		    LOG.trace("Asynchronous call for \"{} {}\" completed.",
	//			    context.getMethod(), context.getRequestPath());
	//		} catch (final Exception failure) {
	//		    LOG.error("Asynchronous call for \"{} {}\" failed : {}.",
	//			    context.getMethod(), context.getRequestPath(), failure.getMessage(), failure);
	//		    result = Results.xml().render(future)
	//			    .status(SC_500_INTERNAL_SERVER_ERROR);
	//		} finally {
	//		    context.asyncRequestComplete();
	//		    context.returnResultAsync(result);
	//		}
	//	    }
	//	});
	//	context.handleAsync();
	//	return Results.async();
    }

    @Override @Dispose
    public void finalize() throws Throwable {
	LOG.trace("Finalizing.");
	executor.shutdownNow();
    }

}
