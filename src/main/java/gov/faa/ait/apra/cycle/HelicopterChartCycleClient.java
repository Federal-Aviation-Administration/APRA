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
import gov.faa.ait.apra.cycle.ChartCycleData;
import gov.faa.ait.apra.cycle.ChartCycleElementsJson;

/**
 * 
 * @author FAA
 *
 */

public class HelicopterChartCycleClient {
	private static final Logger logger = LoggerFactory
			.getLogger(HelicopterChartCycleClient.class);
	private ChartCycleData cycle = null;
	private Date lastUpdate = null;
	private Date today;
	private String chartCycleTypeCode;
	private String city;

	/**
	 * Helicopter Chart Cycle Client with city name.
	 * 
	 * @param cityName
	 */
	public HelicopterChartCycleClient(String cityName) {

		// default to the Grand Canyon type code to avoid breaking Grand Canyon
		// service
		chartCycleTypeCode = "Helicopter_VFR";
		try {
			city = java.net.URLEncoder.encode(cityName, "UTF-8").replace("+",
					"%20");
			this.today = new Date(System.currentTimeMillis());

			if (this.isUpdateRequired()) {
				lastUpdate = new Date(System.currentTimeMillis());
				cycle = getChartCycle();
			}
			logger.info("city  " + city);

		} catch (Exception e) {
			logger.error(" HelicopterChartCycleClient ", e);
		}
	}

	/**
	 * 
	 * @return
	 */

	public ChartCycleData getChartCycle() {
		return getChartCycle(today, false);
	}

	/**
	 * dd
	 */
	public void forceUpdate() {
		getChartCycle(today, true);
	}

	/**
	 * 
	 * @param forceUpdate
	 * @return
	 */
	public ChartCycleData getChartCycle(boolean forceUpdate) {
		return getChartCycle(today, forceUpdate);
	}

	/**
	 * 
	 * @param targetDate
	 * @param forceUpdate
	 * @return
	 */
	public ChartCycleData getChartCycle(Date targetDate, boolean forceUpdate) {
		StringBuilder url = new StringBuilder();

		if (forceUpdate) {
			lastUpdate = new Date(System.currentTimeMillis());
			cycle = null;
		}
		url = url.append(Config.getDenodoHost()
				+ Config.getDenodoVFRCycleResource());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

		String dateString = formatter.format(targetDate);

		StringBuilder queryString = new StringBuilder();
		queryString = queryString.append("?query_date=" + dateString);
		queryString = queryString.append("&chart_cycle_type_code="
				+ chartCycleTypeCode);
		queryString = queryString.append("&chart_city_name=" + this.city);
		queryString = queryString.append("&%24format=json");

		url = url.append(queryString);
		logger.info("Calling denodo for Helicopter chart cycle at "
				+ url.toString());

		if (cycle != null && lastUpdate != null) {
			return cycle;
		}

		lastUpdate = new Date(System.currentTimeMillis());

		String unbound = "";

		try {
			Client client = ClientBuilder.newClient();

			WebTarget webTarget = client.target(url.toString());
			long now = System.currentTimeMillis();
			unbound = webTarget.request(MediaType.APPLICATION_XML_TYPE).get(
					String.class);
			long duration = System.currentTimeMillis() - now;
			logger.info("Call for Helicopter chart cycle took " + duration
					+ " ms");
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
					false);
			mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
			cycle = mapper.readValue(unbound.getBytes(Charsets.UTF_16), ChartCycleData.class);
		} catch (Exception ex) {
			logger.error("getChartCycle", ex);
			cycle = null;
			return null;
		}
		return cycle;
	}

	private boolean isUpdateRequired() {
		if (lastUpdate == null || cycle == null) {
			return true;
		}

		long diff = today.getTime() - lastUpdate.getTime();
		long hours = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);

		return hours >= Config.getCycleAgeLimit();
	}

	private ChartCycleElementsJson getCycle(String periodCode) {
		if (this.isUpdateRequired()) {
			getChartCycle();
		}

		boolean found;

		ChartCycleElementsJson[] elements = cycle.getElements();
		for (int i = 0; i < elements.length; i++) {
			ChartCycleElementsJson element = elements[i];
			found = element.getChart_cycle_period_code().equalsIgnoreCase(
					periodCode);
			if (found) {
				return element;
			}
		}
		return null;
	}

	public ChartCycleElementsJson getNextCycle() {

		return getCycle("NEXT");
	}

	public ChartCycleElementsJson getCurrentCycle() {
		return getCycle("CURRENT");
	}

}
