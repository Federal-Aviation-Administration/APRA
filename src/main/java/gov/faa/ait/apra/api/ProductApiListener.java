/*
 * Federal Aviation Administration (FAA) public work 
 * 
 * As a work of the United States Government, this project is in the 
 * public domain within the United States. Additionally, we waive copyright 
 * and related rights in the work worldwide 
 * through the Creative Commons 0 (CC0) 1.0 Universal public domain dedication
 * 
 * APRA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package gov.faa.ait.apra.api;

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A product api listener class for all JAX-RS events
 * @author FAA
 *
 */
public class ProductApiListener implements ApplicationEventListener {
	private static final Logger logger = LoggerFactory.getLogger(ProductApiListener.class);
	private volatile int requestCount = 0;
	@Override
	public void onEvent (ApplicationEvent appEvent) {
		switch (appEvent.getType()) {
			case INITIALIZATION_FINISHED:
				logger.info("FAA aeronautical product release API (APRA) started. Ready to service requests.");
				break;
			default:
				break;
		}
	}
	
	@Override
	public RequestEventListener onRequest (RequestEvent requestEvent) {
		requestCount++;
		return new ProductApiEventListener(requestCount);
	}
	
	/**
	 * The API event listener for APRA. Current events which are captured and processed are the RESOURCE_START_METHOD and FINISHED events
	 * @author Martin Hile
	 *
	 */
	public static class ProductApiEventListener implements RequestEventListener {
		
		private final int requestNumber;
		private final long startTime;
		private static final Logger logger = LoggerFactory.getLogger(ProductApiListener.class);
		
		/**
		 * This event listener records the time spent processing a request to APRA
		 * @param requestNumber the number of the request which is output to the log file. Incremented by 1 for each request processed
		 */
		public ProductApiEventListener (int requestNumber) {
			this.requestNumber = requestNumber;
			this.startTime = System.currentTimeMillis();
		}
		
		@Override
		public void onEvent (RequestEvent event) {
			switch (event.getType()) {
			case RESOURCE_METHOD_START:
				// Output the start of a resource event so we get a request number logged
				ThreadContext.put("ID", "APRA-"+Integer.toString(requestNumber));
				logger.info("Resource method "+event.getUriInfo().getMatchedResourceMethod().getHttpMethod()+" started for request "+requestNumber);
				break;
			case FINISHED:
				// Mark the finish of the processing with the duration spent fulfilling the request. Useful for debugging performance problems.
				logger.info("Request "+requestNumber+" finished. Processing time "+ (System.currentTimeMillis() - startTime) + " ms.");
				ThreadContext.clearAll();
				break;
			default:
				break;
			}
		}
	
	}

}
