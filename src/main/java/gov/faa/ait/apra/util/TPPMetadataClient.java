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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.faa.ait.apra.bootstrap.Config;
import gov.faa.ait.apra.cycle.ChartCycleElementsJson;
import gov.faa.ait.apra.json.TPPChartMetadata;

/**
 * This is the class that retrieves all of the TPP metadata from the 
 * virtualized XML file in denodo.
 * @author FAA
 *
 */
public class TPPMetadataClient {
	private StringBuilder url; 
	private String edition;
	private static final Logger logger = LoggerFactory.getLogger(TPPMetadataClient.class);
	private static final String BASE_URI=Config.getDenodoHost()+Config.getDenodoViewPath()+"/dtpp_chart_metadata?";
	private static final String CHANGE_FLAG="&chart_updated=Y";
	private static final String EDITION_PARAM="edition=";
	private static final String STATE_PARAM="&state_fullname=";
	private static final String VOLUME_PARAM="&volume=";
	private static final String JSON_FORMAT="&%24format=json";
	
	/**
	 * Setup the base URI for the denodo endpoint and add parameters for edition.
	 * @param cycle the current or next 28 day cycle used for TPP charts
	 */
	public TPPMetadataClient (ChartCycleElementsJson cycle) {
		this.url = new StringBuilder(BASE_URI);
		
		GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
		cal.setTime(cycle.getChart_effective_date());
		String year = Integer.toString(cal.get(Calendar.YEAR));
		this.edition = year.substring(2, 4)+cycle.getChart_cycle_number();
		this.url = this.url.append(EDITION_PARAM).append(edition);
		
		logger.info("URL for TPP metadata query constructed as is currently "+url);
	}

	/**
	 * Get the TPP metadata information using the cycle and specific request to include the change flag in the query
	 * @param cycle the current or next 28 day cycle used for construction of the edition
	 * @param changeFlag if true, query only for the changed charts
	 */
	public TPPMetadataClient (ChartCycleElementsJson cycle, boolean changeFlag) {
		this(cycle);
		
		if (changeFlag)
			this.url = this.url.append(CHANGE_FLAG);
		
		logger.info("URL for TPP metadata query now using change flag "+url);
	}

	/**
	 * Get the TPP chart metadata for all charts. No additional query parameters for state or volume are added
	 * @return
	 */
	public TPPChartMetadata getTPPChartMetadata () {
		this.url.append(JSON_FORMAT);
		return getTPPChartMetadataQuery ();
	}
	
	/**
	 * Get the TPP metadata for the specified state
	 * @param stateName a full state name such as Alaska
	 * @return
	 */
	public TPPChartMetadata getChartMetadataByState (String stateName) {	
		String state = "";
		
		if (stateName == null) 
			state = "US";
		else
			state = stateName;
		
		if ("US".equalsIgnoreCase(state)) {
			this.url.append(JSON_FORMAT);
			return getTPPChartMetadataQuery();
		}
		else {
			try {
				String encodedState = URLEncoder.encode(state, "UTF-8");
				this.url = this.url.append(STATE_PARAM).append(encodedState).append(JSON_FORMAT);
			}
			catch (UnsupportedEncodingException ex) {
				logger.info("State encoding failed. Attempting to retrieve TPP metadata with unencoded state value of "+stateName, ex);
				this.url = this.url.append(STATE_PARAM).append(state).append(JSON_FORMAT);
			}
		}
		
		return getTPPChartMetadataQuery ();	
	}

	/**
	 * Get the TPP metadata for a specific volume
	 * @param volumeName the name of the volume for which metadata is requsted such as NE-1
	 * @return
	 */
	public TPPChartMetadata getChartMetadataByVolume (String volumeName) {
		String volume = "";
		
		if (volumeName != null)
			volume = volumeName;
		
		this.url = this.url.append(VOLUME_PARAM).append(volume).append(JSON_FORMAT);
		return getTPPChartMetadataQuery ();		
	}
	
	// This is the meat of the operation. Once we have the query string all setup based upon the calls for state, volume or others, we execute the request against denodo
	private TPPChartMetadata getTPPChartMetadataQuery () {			
		String unbound = "";
		
		try {
			logger.info("Calling denodo for TPP metadata at "+url.toString());
			
			Client client = ClientBuilder.newClient();	
			WebTarget webTarget = client.target(this.url.toString());

			long now = System.currentTimeMillis();
			unbound = webTarget.request(MediaType.APPLICATION_XML_TYPE).get(String.class);
			long duration = System.currentTimeMillis() - now;
			logger.info("Call for DTPP Metadata took "+duration+" ms");
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
			return mapper.readValue(unbound.getBytes("UTF-8"), TPPChartMetadata.class);
		}
		catch (IOException eio) {
			logger.warn("Error getting chart cycle information using url "+this.url.toString(), eio);
			return null;
		}
		
	}

	/**
	 * Get the calculated edition number. The TPP metadata does not follow the convention of a "current" or "next" edition. Instead, it uses
	 * a catenation of the year (yy) and 2-digit cycle number such as 1607 which represents cycle number 7 of year 2016
	 * @return
	 */
	public String getEdition() {
		return edition;
	}
}
