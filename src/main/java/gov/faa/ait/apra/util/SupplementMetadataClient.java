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

import java.io.UnsupportedEncodingException;

import java.text.SimpleDateFormat;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.faa.ait.apra.bootstrap.Config;
import gov.faa.ait.apra.cycle.ChartCycleElementsJson;
import gov.faa.ait.apra.json.SupplementChartMetadata;

/**
 * This is the class that retrieves all of the Supplement metadata from the virtualized XML file in denodo.
 *
 */
public class SupplementMetadataClient {
	private StringBuilder url; 
	private String edition;
	private static final Logger logger = LoggerFactory.getLogger(SupplementMetadataClient.class);
	private static final String BASE_URI=Config.getDenodoHost()+Config.getDenodoViewPath()+"/afd_chart_metadata?";
	private static final String CHART_DATE="chart_date=";
	private static final String VOLUME_PARAM="&volume_name=";
	private static final String JSON_FORMAT="&%24format=json";
	
	/**
	 * Setup the base URI for the denodo endpoint and add parameters for edition.
	 * @param cycle the current or next 56 day cycle used for Supplement charts
	 */
	public SupplementMetadataClient (ChartCycleElementsJson cycle) {
		this.url = new StringBuilder(BASE_URI);

		logger.info("Chart effective date "+cycle.getChart_effective_date());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		this.url = this.url.append(CHART_DATE).append(formatter.format(cycle.getChart_effective_date()));
		
		logger.info("URL for Supplement metadata query constructed as is currently "+url);
	}

	/**
	 * Get the Supplement chart metadata for all charts. No additional query parameters for state or volume are added
	 * @return
	 */
	public SupplementChartMetadata getSupplementChartMetadata () {
		this.url.append(JSON_FORMAT);
		return getSupplementChartMetadataQuery ();
	}


	/**
	 * Get the Supplement metadata for a specific volume
	 * @param volumeName the name of the volume for which metadata is requsted such as NE-1
	 * @return
	 */
	public SupplementChartMetadata getChartMetadataByVolume (String vol) {
		String volumeName;
		
		if (vol == null) {
			volumeName = "";
		}
		else {
			volumeName = vol;
		}
		
		try {
			volumeName =  java.net.URLEncoder.encode(volumeName, "UTF-8").replace("+","%20");
		} catch (UnsupportedEncodingException e) {
			logger.error(" volumeName encode issue ", e);
		}

		this.url = this.url.append(VOLUME_PARAM).append(volumeName).append(JSON_FORMAT);
		return getSupplementChartMetadataQuery ();		
	}
	
	// This is the meat of the operation. Once we have the query string all setup based upon the calls for state, volume or others, we execute the request against denodo
	private SupplementChartMetadata getSupplementChartMetadataQuery () {			
		String unbound = "";
		
		try {
			logger.info("Calling denodo for Supplement metadata at "+url.toString());
			
			Client client = ClientBuilder.newClient();	
			WebTarget webTarget = client.target(this.url.toString());

			long now = System.currentTimeMillis();
			unbound = webTarget.request(MediaType.APPLICATION_XML_TYPE).get(String.class);
			long duration = System.currentTimeMillis() - now;
			logger.info("Call for Supplement Metadata took "+duration+" ms");
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
			return mapper.readValue(unbound.getBytes("UTF-8"), SupplementChartMetadata.class);
		}
		catch (Exception ex) {
			logger.warn("Error getting chart cycle information using url "+this.url.toString(), ex);
			return null;
		}
	}

}
