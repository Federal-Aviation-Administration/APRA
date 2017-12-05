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

/**
 * 
 * @author FAA
 *
 */
public class WallPlanningChartCycleClient extends DenodoClient {
	private static final Logger logger = LoggerFactory
			.getLogger(WallPlanningChartCycleClient.class);
	private static ChartCycleData wpCycle;
	private static Date wpLastUpdate;
	private Date today;

	public WallPlanningChartCycleClient() {
		this.today = new Date(System.currentTimeMillis());

		if (this.isUpdateRequired()) {
			setLastUpdate();
			setChartCycle(getChartCycle(true));
		}
	}

	@Override
	public ChartCycleData getChartCycle() {
		if (! isUpdateRequired()) {
			return WallPlanningChartCycleClient.wpCycle;
		}
		
		return forceUpdate();
	}

	/**
	 * 
	 * @param targetDate
	 * @param forceUpdate
	 * @return
	 */
	
	public ChartCycleData getChartCycle(Date targetDate, boolean forceUpdate) {
		ChartCycleData cycleData;
		StringBuilder url = new StringBuilder();

		if (forceUpdate) {
			setLastUpdate();
			setChartCycle(null);
		}
		url = url.append(Config.getDenodoHost()+Config.getDenodoVFRCycleResource());
		SimpleDateFormat formatter = new SimpleDateFormat ("MM/dd/yyyy");
		
		String dateString = formatter.format(targetDate);

		StringBuilder queryString = new StringBuilder();
		queryString = queryString.append("?query_date=" + dateString);
		queryString = queryString
				.append("&chart_cycle_type_code=Wall_Planning");
		queryString = queryString.append("&%24format=json");

		url = url.append(queryString);
		logger.info("Calling denodo for vfr chart cycle at " + url.toString());

		if (wpCycle != null && wpLastUpdate != null) {
			return wpCycle;
		}

		setLastUpdate();

		String unbound = "";

		try {
			Client client = ClientBuilder.newClient();

			WebTarget webTarget = client.target(url.toString());
			long now = System.currentTimeMillis();
			unbound = webTarget.request(MediaType.APPLICATION_XML_TYPE).get(
					String.class);
			long duration = System.currentTimeMillis() - now;
			logger.info("Call for WallPlan chart cycle took " + duration
					+ " ms");
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
					false);
			mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
			cycleData = mapper.readValue(unbound.getBytes(Charsets.UTF_16), ChartCycleData.class);
			setChartCycle(cycleData);
		} catch (Exception ex) {
			logger.error("getChartCycle", ex);
			setChartCycle(null);
			return null;
		}
		return WallPlanningChartCycleClient.wpCycle;
	}
	
	
	@Override
	public boolean isUpdateRequired() {
		if (getLastUpdate() == null || WallPlanningChartCycleClient.wpCycle == null) {
			return true;
		}
		
		long diff = today.getTime() - getLastUpdate().getTime();
		long hours = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
		
		return hours >= Config.getCycleAgeLimit();
	}

	public ChartCycleElementsJson getCycle(String periodCode) {
		if (this.isUpdateRequired()) {
			getChartCycle(true);
		}

		boolean found;

		ChartCycleElementsJson[] elements = getChartCycle().getElements();
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

	/**
	 * 
	 * @return
	 */
	public ChartCycleElementsJson getNextCycle() {

		return getCycle("NEXT");
	}

	public ChartCycleElementsJson getCurrentCycle() {
		return getCycle("CURRENT");
	}

	@Override
	public String getWebTarget (Date targetDate) {
		StringBuilder url = new StringBuilder();

		url = url.append(Config.getDenodoHost()+Config.getDenodoVFRCycleResource());
		SimpleDateFormat formatter = new SimpleDateFormat ("MM/dd/yyyy");
		
		String dateString = formatter.format(targetDate);

		StringBuilder queryString = new StringBuilder();
		queryString = queryString.append("?query_date=" + dateString);
		queryString = queryString
				.append("&chart_cycle_type_code=Wall_Planning");
		queryString = queryString.append("&%24format=json");

		url = url.append(queryString);		
		return url.toString();
	}

	@Override
	public Date getLastUpdate() {
		return WallPlanningChartCycleClient.wpLastUpdate;
	}

	@Override
	protected void setLastUpdate() {
		WallPlanningChartCycleClient.wpLastUpdate = new Date(System.currentTimeMillis());
		
	}

	@Override
	protected void setChartCycle(ChartCycleData value) {
		WallPlanningChartCycleClient.wpCycle = value;
	}


}
