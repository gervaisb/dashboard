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
	try {
	    Result result = future.call();
	    LOG.trace("Asynchronous call for \"{} {}\" completed.",
		    context.getMethod(), context.getRequestPath());
	    return result;
	} catch (Exception failure) {
	    LOG.error("Asynchronous call for \"{} {}\" failed : {}.",
		    context.getMethod(), context.getRequestPath(), failure.getMessage(), failure);
	    throw failure;
	}
	//	LOG.trace("Submitting asynchronous call for \"{} {}\".", context.getMethod(), context.getRequestPath());
	//	executor.submit(new Callable<Void>() {
	//	    @Override
	//	    public Void call() throws Exception {
	//		try {
	//		    context.returnResultAsync(future.call());
	//		    LOG.trace("Asynchronous call for \"{} {}\" completed.",
	//			    context.getMethod(), context.getRequestPath());
	//		    return null;
	//		} catch (final Exception failure) {
	//		    LOG.error("Asynchronous call for \"{} {}\" failed : {}.",
	//			    context.getMethod(), context.getRequestPath(), failure.getMessage(), failure);
	//		    throw failure;
	//		} finally {
	//		    context.asyncRequestComplete();
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