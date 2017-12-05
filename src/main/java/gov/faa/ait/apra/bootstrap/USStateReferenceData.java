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
package gov.faa.ait.apra.bootstrap;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

import gov.faa.ait.apra.json.USState;
import gov.faa.ait.apra.json.USStateReference;

/**
 * The state reference data is loaded from an external REST service. 
 * The service provides state names and abbreviations.
 * @author FAA
 *
 */
public class USStateReferenceData {
	private static final Logger logger  = LoggerFactory.getLogger(USStateReferenceData.class);
	private static HashMap <String, String> stateByName = new HashMap <> ();
	private static HashMap <String, String> stateByAbbreviation = new HashMap <> ();
	
	private USStateReferenceData() { } 
	
	static {
		USStateReferenceData.loadStateData();
	}
	
	/**
	 * Load the state reference data from Denodo. 
	 */
	private static void loadStateData () {
		logger.info("Starting load of US state reference data from denodo");
		try {
			StringBuilder url = new StringBuilder();
			url = url.append(Config.getDenodoHost()).append(Config.getDenodoViewPath()).append("/state_reference?%24format=json");
			
			Client client = ClientBuilder.newClient();	
			
			WebTarget webTarget = client.target(url.toString());
			long now = System.currentTimeMillis();
			String unbound = webTarget.request(MediaType.APPLICATION_XML_TYPE).get(String.class);
			long duration = System.currentTimeMillis() - now;
			logger.info("Call US state reference data took "+duration+" ms");
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
			USStateReference data = mapper.readValue(unbound.getBytes(Charsets.UTF_16), USStateReference.class);
			USState [] states = data.getElements();
			
			for (int i = 0; i < states.length; i++) {
				stateByAbbreviation.put(states[i].getAbbreviation().toUpperCase(), states[i].getName().toUpperCase());
				stateByName.put(states[i].getName().toUpperCase(), states[i].getAbbreviation().toUpperCase());
			}
		}
		catch (Exception ex) {
			logger.warn("Unable to load the US state reference data from denodo", ex);
		}
	}
	
	/**
	 * Get the state abbreviation given a full state name
	 * @param stateName the full name of the state for which an abbreviation is required
	 * @return
	 */
	public static String getAbbreviation (String stateName) {
		return stateByName.get(stateName);
	}
	
	/**
	 * Get the state name given a 2-letter state abbreviation
	 * @param abbreviation the 2-letter abbreviation for which a state name is required
	 * @return
	 */
	public static String getStateName (String abbreviation) {
		return stateByAbbreviation.get(abbreviation);
	}
	
	/**
	 * Check to see if a state exists in the reference data
	 * @param stateName the name of the state to check
	 * @return true if the state exists in the reference data set; false otherwise
	 */
	public static boolean stateNameExists (String stateName) {
		return stateByName.containsKey(stateName.toUpperCase(Locale.ENGLISH));
	}
	
	/**
	 * Check to see if a state exists in the reference data using the state abbreviation
	 * @param abbr the 2-letter abbreviation of the state to check
	 * @return true if the state exists in the reference data set; false otherwise
	 */
	public static boolean stateAbbreviationExists (String abbr) {
		return stateByAbbreviation.containsKey(abbr.toUpperCase(Locale.ENGLISH));
	}
	
}
