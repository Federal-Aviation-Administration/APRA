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
package gov.faa.ait.apra.cycle;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

/**
 * Here we are getting the 28 or 56 day chart cycle from the APRA support services. This is a regular chart cycle publication and the dates
 * plus edition numbers are maintained outside of APRA. Data sources for this information are managed by the FAA chart production team and processes.
 * This client is a convenience class to pull the cycle edition dates and numbers from an external source. 
 * @author FAA
 *
 */
public abstract class DenodoClient {
	private Date today;
	private ChartCycleData cycle = null;
	private static final Logger logger = LoggerFactory.getLogger(ChartCycleClient.class);

	/**
	 * Construct the default chart cycle client to obtain the 28 day or 56 day chart cycle from denodo
	 */
	public DenodoClient () {
		this.today = new Date (System.currentTimeMillis());
	}
	
	/**
	 * Force an update of the chart cycle by reaching back to denodo and retrieving an update. 
	 * The update is based on the current system date
	 * @return the updated ChartCycleData from denodo
	 */
	public ChartCycleData forceUpdate () {
		return getChartCycle(today, true);
	}
	
	/**
	 * Get a chart cycle using todays date and specify whether to force an update by 
	 * retrieving the latest cycle from the denodo data source
	 * @param forceUpdate boolean to specify whether the cache should be updated
	 * @return the Json representation of the chart cycle
	 */
	public ChartCycleData getChartCycle (boolean forceUpdate) {
		return getChartCycle (today, forceUpdate);
	}
	
	/**
	 * The base method to retrieve a chart cycle given a specific date and specify whether to force
	 * an update of the cache
	 * @param targetDate the date for which the chart cycle is required
	 * @param forceUpdate specify whether to force an update of the chart cycle cache
	 * @return the chart cycle in Json format bound to the Json POJO 
	 */
	public ChartCycleData getChartCycle (Date targetDate, boolean forceUpdate) {
		String url;
		
		if (! forceUpdate) {
			return getChartCycle();
		}
		
		url = getWebTarget(targetDate);
		
		logger.info("Calling denodo for chart cycle at "+url);
			
		setLastUpdate();
		
		String unbound = "";
		
		try {
			Client client = ClientBuilder.newClient();	
			
			WebTarget webTarget = client.target(url);
			long now = System.currentTimeMillis();
			unbound = webTarget.request(MediaType.APPLICATION_XML_TYPE).get(String.class);
			long duration = System.currentTimeMillis() - now;
			logger.info("Call for 28/56 day chart cycle took "+duration+" ms");
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
			setChartCycle (mapper.readValue(unbound.getBytes(Charsets.UTF_16), ChartCycleData.class));
		}
		catch (Exception ex) {
			logger.warn("Error getting chart cycle information.", ex);
			setChartCycle(null);
			return null;
		}
		return cycle;
	}

	/**
	 * Retrieve the chart cycle using today's date and do not force a refresh
	 * @return
	 */
	public abstract ChartCycleData getChartCycle();
	
	/**
	 * Get the date that this client was last updated for cache aging purposes
	 * @return the date of the last update
	 */
	public abstract Date getLastUpdate(); 
	
	protected abstract void setLastUpdate ();
	
	protected abstract void setChartCycle (ChartCycleData value);
	
	/**
	 * Determine if the cycle client cache need to be updated
	 * @return true if the cache needs to be updated, false otherwise
	 */
	public abstract boolean isUpdateRequired ();
		
	/**
	 * Construct the URL to call the airspace cycle client denodo web service.
	 * 
	 * @param targetDate the date used in construction of the web target url
	 * @return
	 */
	public abstract String getWebTarget (Date targetDate);
	
}
