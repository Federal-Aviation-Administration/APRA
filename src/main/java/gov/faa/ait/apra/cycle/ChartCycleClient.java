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


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

import gov.faa.ait.apra.bootstrap.Config;

/**
 * Here we are getting the 28 or 56 day chart cycle from the APRA support services. 
 * This is a regular chart cycle publication and the dates plus edition numbers 
 * are maintained outside of APRA. Data sources for this information are managed 
 * by the FAA chart production team and processes. This client is a convenience 
 * class to pull the cycle edition dates and numbers from an external source. 
 * @author FAA
 *
 */
public class ChartCycleClient extends DenodoClient {
	private Date today;
	private static ChartCycleData chartCycle;
	private static Date lastCycleUpdate;
	private static final Logger logger = 
		LoggerFactory.getLogger(ChartCycleClient.class);

	/**
	 * Construct the default chart cycle client to obtain the 28 day or 56 day chart 
	 * cycle from denodo.
	 */
	public ChartCycleClient () {
		this.today = new Date (System.currentTimeMillis());
		
		if (this.isUpdateRequired()) {
			setLastUpdate();
			setChartCycle(getChartCycle(true));
		}
	}
	
	/**
	 * Retrieve the chart cycle using today's date and do not force a refresh.
	 * @return the current chart cycle using today's date
	 */
	public ChartCycleData getChartCycle() {
		if (! isUpdateRequired()) {
			return ChartCycleClient.chartCycle;
		}
		
		return forceUpdate();
	}
	
	/**
	 * The base method to retrieve a chart cycle given a specific date and specify 
	 * whether to force an update of the cache.
	 * 
	 * @param targetDate the date for which the chart cycle is required
	 * 
	 * @param forceUpdate specify whether to force an update of the chart 
	 * cycle cache
	 * 
	 * @return the chart cycle in Json format bound to the Json POJO 
	 */
	@Override
	public ChartCycleData getChartCycle (Date targetDate, boolean forceUpdate) {
		String url;
		String unbound = "";
		
		if (forceUpdate) {
			setLastUpdate();
			setChartCycle(null);
		}
		
		url = getWebTarget(targetDate);
		
		logger.info("Calling denodo for chart cycle at "+url);
		
		if (ChartCycleClient.chartCycle != null && ChartCycleClient.lastCycleUpdate != null) {
			return ChartCycleClient.chartCycle;
		}
		
		setLastUpdate();
		
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
		catch (IOException ex) {
			logger.warn("Error getting chart cycle information.", ex);
			setChartCycle(null);
			return null;
		}

		return ChartCycleClient.chartCycle;
	}
	
	@Override
	public Date getLastUpdate () {
		return ChartCycleClient.lastCycleUpdate;
	}
	
	@Override
	protected void setLastUpdate () {
		setLastUpdateCycle();
	}
	
	private static void setLastUpdateCycle () {
		ChartCycleClient.lastCycleUpdate = new Date (System.currentTimeMillis());
	}
	
	@Override
	protected void setChartCycle (ChartCycleData value) {
		ChartCycleClient.setChartCycleData(value);
	}
	
	private static void setChartCycleData (ChartCycleData value) {
		ChartCycleClient.chartCycle = value;
	}
	
	@Override
	public boolean isUpdateRequired () {
		if (ChartCycleClient.lastCycleUpdate == null || ChartCycleClient.chartCycle == null) {
			return true;
		}
		
		long diff = today.getTime() - ChartCycleClient.lastCycleUpdate.getTime();
		long hours = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
		
		return hours >= Config.getCycleAgeLimit();
	}
	
	/**
	 * Given a specific period and type, attempt to retrieve the cycle information from
	 * memory rather than making a call to the server. This is a convenience method to 
	 * cut down on round trip traffic to the denodo servers.
	 * 
	 * @param periodCode either current or next depending on the desired cycle
	 * @param typeCode 28 DAY or 56 DAY cycle
	 * @return
	 */
	public ChartCycleElementsJson getCycle (String periodCode, String typeCode) {
		boolean found; 
		
		if (isUpdateRequired()) {
			getChartCycle(true);
		}
		
		if(ChartCycleClient.chartCycle !=null) {
			ChartCycleElementsJson [] elements = ChartCycleClient.chartCycle.getElements();
			for (int i = 0; i < elements.length; i++) {
				ChartCycleElementsJson element = elements[i];
				
				found = element.getChart_cycle_period_code().equalsIgnoreCase(periodCode)
						& element.getChart_cycle_type_code().equalsIgnoreCase(typeCode);
				if (found) {
					return element;
				}
			}
		}
		return null;
	}
	
	/**
	 * A convenience method to obtain the next 56 day chart cycle.
	 * @return the next 56 day chart cycle
	 */
	public ChartCycleElementsJson getNext56DayCycle () {
		return getCycle ("NEXT", "56 DAY");
	}
	
	/**
	 * A convenience method to obtain the current 28 day chart cycle.
	 * @return the current 28 day chart cycle
	 */
	public ChartCycleElementsJson getCurrentCycle () {
		return getCycle ("CURRENT", "28 DAY");
	}
	
	/**
	 * A convenience method to obtain the next 28 day chart cycle.
	 * @return the next 28 day chart cycle
	 */
	public ChartCycleElementsJson getNextCycle () {
		return getCycle ("NEXT", "28 DAY");
	}
	
	/**
	 * A convenience method to obtain the current 56 day chart cycle.
	 * @return the current 56 day chart cycle
	 */
	public ChartCycleElementsJson getCurrent56DayCycle () {
		return getCycle ("CURRENT", "56 DAY");
	}
	
	/**
	 * A convenience method to obtain the current 28 day chart cycle.
	 * @return the current 28 day chart cycle
	 */
	public ChartCycleElementsJson getCurrent28DayCycle () {
		return getCycle ("CURRENT", "28 DAY");
	}
	
	/** 
	 * A convenience method to obtain the next 28 day chart cycle.
	 * @return the next 28 day chart cycle
	 */
	public ChartCycleElementsJson getNext28DayCycle () {
		return getCycle ("NEXT", "28 DAY");
	}
	
	/**
	 * Get the target URL for this denodo cycle resource. 
	 * This satisfies the interface contract for the cycle client service.
	 * 
	 * @return the URL to obtain cycle information
	 */
	public String getWebTarget (Date targetDate) {
		StringBuilder url = new StringBuilder();
		url = url.append(Config.getDenodoHost()+Config.getDenodoCycleResource());
		SimpleDateFormat formatter = new SimpleDateFormat ("MM/dd/yyyy");
		
		String dateString = formatter.format(targetDate);
		
		StringBuilder queryString = new StringBuilder();
		queryString = queryString.append("?query_date="+dateString);
		queryString = queryString.append("&%24format=json");
		
		url = url.append(queryString);
		
		return url.toString();
	}
}

