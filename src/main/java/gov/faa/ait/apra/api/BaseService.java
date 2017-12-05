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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.faa.ait.apra.bootstrap.Config;
import gov.faa.ait.apra.bootstrap.ErrorCodes;
import gov.faa.ait.apra.jaxb.EditionCodeList;
import gov.faa.ait.apra.jaxb.FormatCodeList;
import gov.faa.ait.apra.jaxb.ObjectFactory;
import gov.faa.ait.apra.jaxb.ProductSet;
import gov.faa.ait.apra.jaxb.ProductSet.Edition;
import gov.faa.ait.apra.jaxb.ProductSet.Edition.Product;
import gov.faa.ait.apra.jaxb.ProductSet.Status;
import gov.faa.ait.apra.cycle.ChartCycleElementsJson;

import static gov.faa.ait.apra.bootstrap.ErrorCodes.DEPRECATED;

/**
 * This is the base public class for all APRA services. As an abstract class, 
 * all subsequent service endpoints should extend this class to provide required 
 * functionality to build an appropriate and correct response. 
 * 
 * @author FAA
 *
 */
public abstract class BaseService {
	public static final String CURRENT="current";
	public static final String NEXT="next";
	public static final String CHANGE_SET="changeset";
	public static final String PDF="pdf";
	public static final String ZIP="zip";
	public static final String TIFF="tiff";
	protected static final String EMPTY_STRING = "";
	private boolean changeFlag;
	private String format = EMPTY_STRING;
	private String edition = EMPTY_STRING;
	private String geoname = EMPTY_STRING;
	private static final Logger logger = LoggerFactory.getLogger(BaseService.class);

	/**
	 * Verify edition and format parameters
	 * @return true if parameters are valid and well formed
	 */
	public boolean verifyParameters () {  	
    	return verifyEdition() && verifyFormat();

	}
	
	/**
	 * verify that the format provided is one of the valid formats of tiff, pdf, or zip
	 * @return true if format is valid, false otherwise
	 */
	public boolean verifyFormat () {
		return TIFF.equalsIgnoreCase(format) || PDF.equalsIgnoreCase(format) || ZIP.equalsIgnoreCase(format);
	}
	
	/**
	 * verify the edition is one of either current or next
	 * @return true if the edition is current or next, false otherwise
	 */
	public boolean verifyEdition () {
		return CURRENT.equalsIgnoreCase(edition) || NEXT.equalsIgnoreCase(edition);
	}
	
	/**
	 * Produces an illegal argument response based upon a bad edition or format parameter being supplied
	 * @return
	 */
    public ProductSet getIllegalArgumentError () {
    	return getErrorResponse (400, "Illegal arguments provided to service. Format parameter must be one of tiff or pdf and edition parameter must be one of current or next");   	
    }
    
    /**
     * This method creates an error response using a code and string message 
     * that indicates the problem.
     * @param code an error code 
     * @param message the message to be returned in the response to the caller
     * @return a ProductSet response object that can be returned to the caller
     */
    public ProductSet getErrorResponse(int code, String message) {
    	ObjectFactory of = new ObjectFactory();
    	ProductSet response = of.createProductSet();
    	Status error = of.createProductSetStatus();
    	error.setCode(code);
    	error.setMessage(message);
    	response.setStatus(error);
    	return response;   	
    }
    
    /**
     * Set the format of the response type to either TIFF, PDF, or ZIP depending on the file type to be retrieved
     * @param format a file extension or type
     */
    public void setFormat(String fmt) {
    	this.format = fmt != null ? fmt.toUpperCase(Locale.ENGLISH) : EMPTY_STRING;
    	if (! verifyFormat()) {
    		this.format = EMPTY_STRING;
    	}
    }
    
    /**
     * Get the requested chart format. Typically PDF, ZIP, or TIFF
     * @return
     */
    public String getFormat() {
    	return format;
    }
    
    /**
     * Set the edition and should be one of changeset, current, or next. If changeset is specified, this is treated as a special case of the current release where we find the charts
     * that changed since the previous release. 
     * @param edition
     */
    public void setEdition (String edition) {
    	this.edition = edition != null ? edition.toUpperCase(Locale.ENGLISH) : EMPTY_STRING;
    	
    	if (CHANGE_SET.equalsIgnoreCase(edition)){
    		this.edition = CURRENT;
    		this.setChangeFlag(true);
    	}
    	
    	this.edition = edition == CHANGE_SET ? CURRENT : getEdition();
    	
    	if (! verifyEdition() ) {
    		this.edition = EMPTY_STRING;
    	}
    }
    
    /**
     * Get the edition name we are using
     * @return the edition name as a string either current or next
     */
    public String getEdition () {
    	return this.edition;
    }

    /**
     * Sets the supplied geoname and converts it to all upper case
     * @param geo
     */
    public void setGeoname (String geo) {
    	this.geoname  = geo != null ? geo.toUpperCase(Locale.ENGLISH) : EMPTY_STRING;
    }
    
    /**
     * Gets the geoname set on this service (if any)
     * @return the geoname being used for this instance of a service
     */
    public String getGeoname () {
    	return this.geoname;
    }
    
    /**
     * Abstract method used by sub-classes to build a response object
     * @param cycle the chart cycle information used to build the response
     * @return a ProductSet object that encapsulates the response streamed as XML or JSON
     */
	protected abstract ProductSet buildResponse (ChartCycleElementsJson cycle); 
	
	/**
	 * Given a URL, this method attempts to execute an HTTP HEAD check against the URL. If a 200 response is returned, the URL is valid
	 * @param url the url to be checked
	 * @return true if the url response is 200 when issuing a HTTP HEAD check; false otherwise
	 */
	public boolean verifyURL (URL url) {
		boolean ok = false; 
		HttpURLConnection connection = null;
		Proxy proxy = null;
		
		logger.info("Verifying URL "+url.toExternalForm()+" before responding to call");
		try {
			if (Config.getFAADMZProxyHost() != null && (! EMPTY_STRING.equals(Config.getFAADMZProxyHost()))) {
				int port = Integer.parseInt(Config.getFAADMZProxyPort());
				InetSocketAddress proxyAddress = new InetSocketAddress(Config.getFAADMZProxyHost(), port);
				proxy = new Proxy(Proxy.Type.HTTP, proxyAddress);			
			}
			
			/*
			 * Determine if we are going to use a proxy server to check the validity
			 * of the URL we return
			 */
			if (proxy != null) {
				logger.info("Using proxy server "+proxy.toString());
				connection = (HttpURLConnection) url.openConnection(proxy);
			}
			else {
				logger.info("Direct connection. No proxy server defined.");
				connection = (HttpURLConnection) url.openConnection();
			}
			
			/*
			 * This is the actual HTTP HEAD check to determine if the URL is valid
			 * and exists on the FAA web server
			 */
			connection.setRequestMethod("HEAD");
			int responseCode = connection.getResponseCode();
			if (responseCode == 200 || responseCode == 302) {
				logger.info("URL HEAD check returned response code "+responseCode+" for url "+url.toExternalForm());
			    ok = true;
			}
			else {
				logger.warn("URL HEAD check returned response code "+responseCode+" for url "+url.toExternalForm());
			}
		}
		catch (IllegalArgumentException eillegal) {
			logger.error("HEAD heck failed for url: "+url.toExternalForm(), eillegal);
			ok = false;
		}
		catch (IOException eio) {
			logger.error("HEAD heck failed for url: "+url.toExternalForm(), eio);
			ok = false;
		}
		

		/*
		 * Close down the connection as cleanup action
		 */
		if (connection != null)
			connection.disconnect();

		return ok;
	}
	
	/**
	 * Capitalize the geoname using either a space, underscore, or hyphe character as the delimeters. The geoname is converted to proper capitalization rules
	 * As an example, the string neW YorK would be transformed to New York
	 * @return a properly formatted geoname
	 */
	public String capitalizeGeoname () {
		char[] separators = {'-', '_', ' '};
		return WordUtils.capitalize(getGeoname().toLowerCase(Locale.ENGLISH), separators);
	}

	/**
	 * This method indicates if the user has requested only those charts that have changed
	 * @return
	 */
	public boolean isChangeFlag() {
		return changeFlag;
	}

	/**
	 * Set the change flag for the service to return only changed charts and not all charts. Equivalent to asking for the changeset edition rather than current or next
	 * @param changeFlag
	 */
	public void setChangeFlag(boolean changeFlag) {
		this.changeFlag = changeFlag;
	}
	
	/*
	 * A generic utility method to generate a template response
	 */
    protected ProductSet initPositiveResponse () {
    	ObjectFactory of = new ObjectFactory();
    	Status status = of.createProductSetStatus();
    	status.setCode(200);
    	status.setMessage("OK");
    	ProductSet response = of.createProductSet();
    	response.setStatus(status);
    	   	
    	return response;
    }   
 
    protected ProductSet initDeprecatedResponse () {
    	ObjectFactory of = new ObjectFactory();
    	Status status = of.createProductSetStatus();
    	status.setCode(404);
    	status.setMessage(DEPRECATED);
    	ProductSet response = of.createProductSet();
    	response.setStatus(status);
    	   	
    	return response;
    }
    
    /*
     * This initializes a template edition response that doesn't include the product URL
     */
    protected Edition initEdition (ChartCycleElementsJson cycle) {
    	ObjectFactory of = new ObjectFactory();

    	ProductSet.Edition ed = of.createProductSetEdition();
	   	
    	SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
    	ed.setEditionDate(formatter.format(cycle.getChart_effective_date()));
    	ed.setEditionNumber(Integer.valueOf(cycle.getChart_cycle_number()));
    	ed.setEditionName(EditionCodeList.fromValue(cycle.getChart_cycle_period_code()));
    	ed.setFormat(FormatCodeList.fromValue(getFormat()));
    	if (! EMPTY_STRING.equals(getGeoname())) {
    		ed.setGeoname(getGeoname());
    	}
    	   	
    	return ed;
    }
    
    /*
     * A general method to validate a URL and set the value in the response object
     */
	protected void validateAndSetUrl(String url, ProductSet ps, Product p) {
		try {
			URL downloadURL = new URL(url);
			if (!verifyURL(downloadURL)) {
				logger.warn(downloadURL.toExternalForm()
						+ " returned a non 200 response code when completing a HTTP HEAD check.");
				p.setUrl("");
				ps.getStatus().setCode(404);
				ps.getStatus()
						.setMessage(
								ErrorCodes.ERROR_404);
			} else {
				p.setUrl(downloadURL.toExternalForm());
			}
		} catch (MalformedURLException emalformed) {
			logger.warn("The download URL is not valid", emalformed);
			p.setUrl("");
		}
	}

}
