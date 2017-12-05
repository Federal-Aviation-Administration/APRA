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
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

import gov.faa.ait.apra.bootstrap.Config;

/**
 * The TAC chart cycle client to obtain the TAC chart cycle from the denodo data source
 * @author FAA
 *
 */
public class TACCycleClient extends DenodoClient {
	private static final Logger logger = LoggerFactory.getLogger(TACCycleClient.class);
	private static HashMap <String, ChartCycleElementsJson> current = null;
	private static HashMap <String, ChartCycleElementsJson> next = null;
	private static Date tacLastUpdate = null;
	private Date today;
	
	/**
	 * Default constructor to get a TAC cycle
	 */
	public TACCycleClient () {
		this.today = new Date (System.currentTimeMillis());
		
		if (this.isUpdateRequired()) {
			setLastUpdate();
			getChartCycle(today, false);
		}
	}	
	
	/**
	 * The default chart cycle method that retrieves the chart cycle information from cache or from the denodo data source
	 * @param targetDate the date for which a chart cycle is desired
	 * @param forceUpdate specifies whether or not to force an update by retrieving data from the cache or the denodo source. 
	 * @return 
	 */
	@Override
	public ChartCycleData getChartCycle(Date targetDate, boolean forceUpdate) {
		String url;
		
		if (forceUpdate) {
			setLastUpdate();
			TACCycleClient.setCyclesNull();
		}
		
		url = getWebTarget(targetDate);
		logger.info("Calling denodo for vfr chart cycle at "+url);
		
		if (TACCycleClient.current != null && TACCycleClient.next != null && TACCycleClient.tacLastUpdate != null) {
			if (logger.isDebugEnabled())
				logger.debug("Update not required for TAC Cycle. Aborting call to allow use of cache. Returning null.");
			return null;
		}
		
		setLastUpdate();
		
		String unbound = "";
		
		try {
			logger.info("Updating the TAC chart cycle cache.");
			Client client = ClientBuilder.newClient();	
			
			WebTarget webTarget = client.target(url);
			long now = System.currentTimeMillis();
			unbound = webTarget.request(MediaType.APPLICATION_XML_TYPE).get(String.class);
			long duration = System.currentTimeMillis() - now;
			logger.info("Call for TAC chart cycle took "+duration+" ms");
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
			
			ChartCycleData cycleObjects = mapper.readValue(unbound.getBytes(Charsets.UTF_16), ChartCycleData.class);
			TACCycleClient.initCycles();

			ChartCycleElementsJson [] cycles = cycleObjects.getElements();
			
			for (int i = 0; i < cycleObjects.getElements().length; i++) {
				String key = cycles[i].getChart_city_name();
				
				if ("CURRENT".equalsIgnoreCase(cycles[i].getChart_cycle_period_code())) {
					TACCycleClient.current.put(key,  cycles[i]);
				}
				else {
					TACCycleClient.next.put(key, cycles[i]);
				}
			}
		}
		catch (Exception ex) {
			logger.warn("Unable to get the TAC cycle information.", ex);
			TACCycleClient.setCyclesNull();
			return null;
		}
		return null;
	}

	@Override
	public boolean isUpdateRequired () {
		if (TACCycleClient.tacLastUpdate == null || TACCycleClient.current == null || TACCycleClient.next == null) {
			if (logger.isDebugEnabled()) 
				logger.debug("TAC chart cycles need to be updated. One of current, next, or lastupdate is null. Returning true to update the cycle cache.");
			return true;
		}
		
		long diff = today.getTime() - TACCycleClient.tacLastUpdate.getTime();
		long hours = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
		
		return hours >= Config.getCycleAgeLimit();
	}
	
	@Override
	public void setLastUpdate () {
		TACCycleClient.tacLastUpdate = new Date (System.currentTimeMillis());
	}
	
	private static void initCycles () {
		TACCycleClient.current = new HashMap <> ();
		TACCycleClient.next = new HashMap <> ();
	}
	
	private static void setCyclesNull () {
		TACCycleClient.current = null;
		TACCycleClient.next = null;
	}
	
	/**
	 * Get the current TAC cycle for a specific named city. TAC cycles vary by the geographic city 
	 * @param city the city for which the TAC cycle is desired
	 * @return the TAC chart cycle for the named city
	 */
	public ChartCycleElementsJson getCurrentCycle (String city) {
		if (this.isUpdateRequired()) {
			getChartCycle();
		}
		
		return TACCycleClient.current.get(city);
	}
	
	/**
	 * Get the next TAC cycle for a specific named city. TAC cycles vary by the geographic city 
	 * @param city the city for which the TAC cycle is desired
	 * @return the TAC chart cycle for the named city
	 */	
	public ChartCycleElementsJson getNextCycle (String city) {
		if (this.isUpdateRequired()) {
			getChartCycle();
		}
		
		return TACCycleClient.next.get(city);
	}

	/**
	 * Get the current TAC edition number for a specific named city. TAC cycles vary by the geographic city 
	 * @param city the city for which the TAC cycle is desired
	 * @return the current TAC chart edition number for the named city
	 */
	public String getCurrentEditionNumber (String city) {
		if (this.isUpdateRequired()) {
			getChartCycle();
		}
		ChartCycleElementsJson currentTacCycle = getCurrentCycle(city);
		
		if (currentTacCycle != null)			
			return currentTacCycle.getChart_cycle_number();
		
		return null;
	}

	/**
	 * Get the next TAC edition number for a specific named city. TAC cycles vary by the geographic city 
	 * @param city the city for which the TAC cycle is desired
	 * @return the next TAC chart edition number for the named city
	 */
	public String getNextEditionNumber (String city) {
		if (this.isUpdateRequired()) {
			getChartCycle();
		}
		ChartCycleElementsJson nextTacCycle = getNextCycle(city);
		
		if (nextTacCycle != null)			
			return nextTacCycle.getChart_cycle_number();
		
		return null;
	}

	@Override
	public String getWebTarget(Date targetDate) {
		StringBuilder url = new StringBuilder();
		
		url = url.append(Config.getDenodoHost()+Config.getDenodoVFRCycleResource());
		SimpleDateFormat formatter = new SimpleDateFormat ("MM/dd/yyyy");
		
		String dateString = formatter.format(targetDate);
		
		StringBuilder queryString = new StringBuilder();
		queryString = queryString.append("?query_date="+dateString);
		queryString = queryString.append("&%24format=json");
		queryString = queryString.append("&chart_cycle_type_code=TAC");
		
		url = url.append(queryString);
		
		return url.toString();
	}

	@Override
	public Date getLastUpdate() {
		return TACCycleClient.tacLastUpdate;
	}


	@Override
	protected void setChartCycle(ChartCycleData value) {
		return;
	}

	@Override
	public ChartCycleData getChartCycle() {
		getChartCycle(today, true);
		return null;
	}
	
}
