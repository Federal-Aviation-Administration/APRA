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
package gov.faa.ait.apra.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.faa.ait.apra.bootstrap.Config;
import gov.faa.ait.apra.cycle.ChartCycleData;

public class TableChartClient {
	
	private static Logger logger = LoggerFactory.getLogger(TableChartClient.class);
	private static ChartInfoTable sectionalTable;
	private static Date lastUpdate;
	private Date today;
	
	/**
	 * Default constructor
	 * Initializes today to current time
	 */
	public TableChartClient() {
		this.today = new Date(System.currentTimeMillis());
		if(updateRequired()) {
			updateTable();
		}
	}
	
	/**
	 * Initializes client with a specified date 
	 * @param date date used for query date
	 */
	public TableChartClient(Date date) {
		this.today = new Date (date.getTime());
	}
	
	private static void updateTable() {
		// initiate call to REST 
		TableChartClient.lastUpdate = new Date(System.currentTimeMillis());
		ChartCycleData chartJson = callResource(TableChartClient.lastUpdate); 
		if(chartJson!=null) {
			TableChartClient.sectionalTable = new ChartInfoTable(chartJson);
		} 
	}
	
	public static ChartCycleData callResource(Date targetDate) {
		/* example:
		 * https://soadev.sm.faa.gov/denodo/apra/server/ifpa/edai/views/vfr_chart_cycle?query_date=6/15/2016&%24format=json
		 */
		String unbound = "";

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		StringBuilder url = new StringBuilder();
		url.append(Config.getDenodoHost()).append(Config.getDenodoVFRCycleResource()).append("?query_date=")
										.append(sdf.format(targetDate))
										.append("&%24format=json");
		logger.info("Calling denodo for sectional at "+url.toString());
		TableChartClient.lastUpdate = new Date(System.currentTimeMillis());
		
		try {
			Client client = ClientBuilder.newClient();	
			
			WebTarget webTarget = client.target(url.toString());
			
			long now = System.currentTimeMillis();
			unbound = webTarget.request(MediaType.APPLICATION_JSON_TYPE).get(String.class);
			long duration = System.currentTimeMillis() - now;
			logger.info("Call for sectional chart cycle took "+duration+" ms");
			
			if (logger.isDebugEnabled())
				logger.debug("JSON return value for sectional chart cycle = "+unbound);
			
			ChartCycleJsonUnmarshaller converter = new ChartCycleJsonUnmarshaller();
			logger.debug("Got the unmarshaller ChartCycleJsonUnmarshaller");
			ChartCycleData sectionJson = converter.unmarshalJson(unbound);
			logger.debug("Got the converter for ChartCycleData and unmarshalled the unbound string. Returning sectionJson");
			return sectionJson;
		}
		catch (Exception ex) {
			logger.error("Error calling service",ex);
			TableChartClient.sectionalTable = null;
			return null;
		}
		
	}


	private boolean updateRequired() {
		boolean update;
		if(TableChartClient.lastUpdate == null || TableChartClient.sectionalTable == null ) {				
			update = true;
		} else {
			long diff = this.today.getTime() - TableChartClient.lastUpdate.getTime();
			long hours = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
			
			update = (hours >= Config.getCycleAgeLimit());
		}
		return update;
	}
	
	public static ChartInfoTable getTable(TableChartClient client) {
		if(client.updateRequired()) {
			TableChartClient.updateTable();
		}
		return TableChartClient.sectionalTable;
	}
}
