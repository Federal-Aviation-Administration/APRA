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

import static gov.faa.ait.apra.bootstrap.ErrorCodes.ERROR_400;
import static gov.faa.ait.apra.bootstrap.ErrorCodes.ERROR_404;
import static gov.faa.ait.apra.bootstrap.ErrorCodes.ERROR_500;
import static gov.faa.ait.apra.bootstrap.ErrorCodes.RESPONSE_200;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.faa.ait.apra.bootstrap.Config;
import gov.faa.ait.apra.bootstrap.ErrorCodes;
import gov.faa.ait.apra.bootstrap.USStateReferenceData;
import gov.faa.ait.apra.jaxb.ChangeCodeList;
import gov.faa.ait.apra.jaxb.FormatCodeList;
import gov.faa.ait.apra.jaxb.ObjectFactory;
import gov.faa.ait.apra.jaxb.ProductCodeList;
import gov.faa.ait.apra.jaxb.ProductSet;
import gov.faa.ait.apra.jaxb.ProductSet.Edition;
import gov.faa.ait.apra.jaxb.ProductSet.Edition.Product;
import gov.faa.ait.apra.json.TPPMetadata;

import gov.faa.ait.apra.cycle.ChartCycleClient;
import gov.faa.ait.apra.cycle.ChartCycleElementsJson;

import gov.faa.ait.apra.util.TPPMetadataClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/dtpp")
@Api(value="US Terminal Procedures Publication (TPP)")
/**
 * This class services requests for the digital terminal procedures publication. Currently, the allowed publication sets are US complete set and state complete set. If a changeset parameter is specified,
 * the service responds with only charts that have changed since the previous release of dTPP
 * @author FAA
 *
 */
public class TerminalProcedureCharts extends BaseService {
	private static final Logger logger = LoggerFactory.getLogger(TerminalProcedureCharts.class);
	private static final String US = "US";

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Path("/chart")
    @ApiOperation(value="Get Terminal Procedure Publication chart download information by requesting an edition with geographic area of United States or a valid US State Name.", 
    	notes="The complete United States Terminal Procedure Publication (TPP) release is distributed as a set of zip files containing charts and verification software. "
    			+ "Requests for charts by state returns a list of download URLs which can be quite extensive. "
    			+" All 50 US states are valid for requesting chart publication download URLs. The special 'changeset' edition operates against "
    			+ "the current release and returns the charts that were changed since the previous release. ",
    	response=ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})
 
	/**
	 * This is the base chart download URL. Parameters are provided for edition and geoname. The geoname can be US, US state, or publication volume
	 * 
	 * @param ed the edition for which you want a URL
	 * @param geo the geographic name for which a download URL is requested
	 * @return the product set
	 */
    public Response getTPPRelease (
    		@ApiParam(name="edition", value="Requested product edition. If omitted, the default current edition is returned.", allowableValues="current, next, changeset", defaultValue="current", allowMultiple=false, required=false) @QueryParam("edition") String ed,
    		@ApiParam(name="geoname", value="Requested geographic region of Terminal Procedures Publication chart set. Specify either US or a valid full state name such as Alaska. If omitted, the default US complete set is returned.", defaultValue="US", allowMultiple=false, required=false) @QueryParam("geoname") String geo) {
    	ChartCycleElementsJson cycle;
    	
    	logger.info("Received call to retrieve current TPP product release for edition '"+ed+"'.");
    	
    	// Default the geoname to US, but we also accept states at this time
		setGeoname(geo != null ? geo : US);
		setFormat(ZIP);
		
		if (!verifyGeoname()) {
    		logger.error("Expected a geographic name of a US state or just US, but received '"+geo+"' instead. Error response being generated and returned.");
    		return Response.status(400).entity(getErrorResponse (400, "Geographic name must be a full US state name or 'US'")).build();    					
		}
		// Check the base service class for what happens here. If someone specifies the "changeset" edition, the setEdition method 
		// actually sets the edition to CURRENT and adds a change flag. This is becasue we are looking for the changed charts
		// in the current edition as compared to the prior edition. This is really only valid for TPP at this time
		setEdition(ed != null ? ed : CURRENT);
    	cycle = initParameters();
		
    	if (!verifyEdition()) {
    		logger.error("Expected edition current, next, or changeset and received '"+ed+"' instead. Error response being generated and returned.");
    		return Response.status(400).entity(getErrorResponse (400, "Edition must be current, next, or changeset.")).build();    		
    	}
    	
    	// 2 cases where we want to get the charts and not the full zip set. If this is a state or if someone requests ALL US changed files, we go to get charts rather than the full set
    	// If someone specifies both US and the changeset edition, we return all changed files across entire US. Otherwise, we drop down to providing the zip full set
    	if ( (isChangeFlag() && isUnitedStates()) || (! isUnitedStates())) {
    		logger.info("Retrieving individual TPP charts rather than full US set. User asked for a state, volume, or US changes.");
    		setFormat(PDF);
    		ProductSet ps = getChartProductSet(cycle);
        	return Response.status(ps.getStatus().getCode()).entity(ps).build();

    	}
    	
    	// By default, we return the zippped US product set
    	ProductSet ps = buildResponse(cycle); 
    	return Response.status(ps.getStatus().getCode()).entity(ps).build();

    }	
	

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Path("/info")
    @ApiOperation(value="Get Terminal Procedure Publication chart edition information by requesting an edition with geographic area of United States or one of the 50 US states", 
    	notes="The US Terminal Procedure Publication is released on a 28 day airspace cycle. Edition information is identical regardless of the geographic area or format of the desired charts.",
    	response=ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})
    
	/**
	 * This is the base chart download URL. Parameters are provided for edition and geoname. The geoname can be US, US state, or publication volume
	 * 
	 * @param ed the edition for which you want a URL
	 * @param geo the geographic name for which edition information is requested
	 * @return
	 */    
    public Response getTPPEdition (
    		@ApiParam(name="edition", value="Requested product edition. If omitted, the default current edition information is returned.", allowableValues="current, next", defaultValue="current", allowMultiple=false, required=false) @QueryParam("edition") String ed,
    		@ApiParam(name="geoname", value="Requested geographic region of Terminal Procedures Publication chart set. Specify US or a valid full US state name such as Alaska. If omitted, edition information for the complete US set is returned.", defaultValue="US", allowMultiple=false, required=false) @QueryParam("geoname") String geo) {
    	ChartCycleElementsJson cycle;
    	
    	logger.info("Received call to retrieve current TPP product release for edition '"+ed+"'.");
    	
		setGeoname(geo != null ? geo : US);
		setEdition(ed != null ? ed : CURRENT);
		
		if (US.equalsIgnoreCase(getGeoname())) {
			setFormat(ZIP);
		}
		else {
			setFormat(PDF);
		}
		
    	cycle = initParameters();
    	
		if (!verifyGeoname()) {
    		logger.error("Expected a geographic name of a US state or just US, but received '"+geo+"' instead. Error response being generated and returned.");
    		return Response.status(400).entity(getErrorResponse (400, "Geographic name must be a full US state name or 'US'")).build();    					
		}
		
    	if (!verifyEdition()) {
    		logger.error("Expected edition 'current' or 'next' and received '"+ed+"' instead. Error response being generated and returned.");
    		return Response.status(400).entity(getErrorResponse (400, "Edition must be current, next, or changeset.")).build();    		
    	}
    	
    	ProductSet ps = getEditionResponse(cycle);  	
    	return Response.status(ps.getStatus().getCode()).entity(ps).build();

    }
    
	@Override
	protected ProductSet buildResponse(ChartCycleElementsJson cycle) {
    	if ("US".equalsIgnoreCase(getGeoname())) {
    		return getUSProductSet(cycle);
    	} 
    	
    	else {
    		return getChartProductSet(cycle);
    	}
	}
    
    private ProductSet getUSProductSet (ChartCycleElementsJson cycle) {
    	ObjectFactory of = new ObjectFactory();
    	String [] pathSet = getUSFilePaths(cycle);
    	ProductSet ps = initPositiveResponse();
    	
    	for (int i = 0; i < pathSet.length; i++) {
    		Edition ed = initEdition(cycle);
        	Product product = of.createProductSetEditionProduct();
        	product.setProductName(ProductCodeList.TPP);        	
        	validateAndSetUrl(Config.getAeronavHost()+pathSet[i], ps, product);	
        	ed.setProduct(product);
        	ps.getEdition().add(ed);
        }
    	
    	return ps;
    }
    
    // Chart paths follow this convention http://aeronav.faa.gov/d-tpp/1607/akto.pdf   
    
    private ProductSet getChartProductSet (ChartCycleElementsJson cycle) {
    	logger.info("Getting the chart product set for "+getEdition()+" "+capitalizeGeoname()+" with change flag = "+isChangeFlag());
    	ObjectFactory of = new ObjectFactory();
    	TPPMetadataClient tppClient = new TPPMetadataClient (cycle, isChangeFlag()); 
    	TPPMetadata [] elements = tppClient.getChartMetadataByState(capitalizeGeoname()).getElements();
    	HashSet <String> processedFiles = new HashSet <> ();
    	int processTotal = 0;
    	
    	if (elements == null || elements.length == 0) 
    		return getErrorResponse(404, ErrorCodes.ERROR_404);
    	
    	logger.info(elements.length+" total charts found for "+getEdition()+" "+capitalizeGeoname()+" with change flag = "+isChangeFlag());
    	
    	String edition = tppClient.getEdition();
    	ProductSet ps = initPositiveResponse();
    	
    	for (int i = 0; i < elements.length; i++) {
    		if (processedFiles.contains(elements[i].getChart_name())) {
    			//skip the chart if we've already processed it
    			continue;
    		}
    		else {
    			// add the chart to our processed list and we build the response
    			processedFiles.add(elements[i].getChart_name());
    			processTotal++;
    		}
    		
    		StringBuilder path = new StringBuilder(Config.getTPPChartPath());
    		Edition ed = initEdition(cycle);
    		ed.setFormat(FormatCodeList.PDF);
    		ed.setGeoname(elements[i].getState_fullname());
    		ed.setVolume(elements[i].getVolume());

        	Product product = of.createProductSetEditionProduct();      	
        	product.setProductName(ProductCodeList.TPP);
        	product.setChartName(elements[i].getChart_name());   
        	
        	if (! isNullValue(elements[i].getAirport_icao_identifier()))
        		product.setIcao(elements[i].getAirport_icao_identifier());
        	
        	if (! isNullValue(elements[i].getAirport_identifier()))
        		product.setAirportId(elements[i].getAirport_identifier());
        	
        	if (! isNullValue(elements[i].getCity_name()))
        		product.setCityName(elements[i].getCity_name());
        	
        	if (! isNullValue(elements[i].getAirport_name())) 
        		product.setAirportName(elements[i].getAirport_name());
        	
    		path.append("/").append(edition);
    		path.append("/").append(elements[i].getPdf_name());
    		
    		product.setUrl(Config.getAeronavHost()+path.toString());
    		
        	setChangeType(product, elements[i].getUseraction());
    		
    		// The HEAD check for TPP files can introduce a significant performance penalty. This is controlled by a flag in the Configuration. 
    		// Recommendation is to enable the flag in DEV only and leave disabled in TEST and PROD unless someone wants to check and verify in TEST
    		
    		if (Config.getTPPCheckFlag()) {
    			logger.warn("URL validation check is enabled for the DTPP product set. This can cause serious performance issues for the DTTP product responses."
    					+ " Consider changing the configuration parameter gov.faa.ait.tpp.check.flag = false and re-deploy.");
    			validateAndSetUrl(Config.getAeronavHost()+path.toString(), ps, product);
    		}
   		
           	ed.setProduct(product);
        	ps.getEdition().add(ed);
    	}
    	
    	processedFiles.clear();
    	logger.info("Processed a total of "+processTotal+" charts for "+this.getGeoname());
    	
       	return ps;
    }   
    
    // This is where we get the full US product set file path that is divided into 5 separate ZIP files for download. The files are named A through E
    private String [] getUSFilePaths (ChartCycleElementsJson cycle) {
    	String [] usPathSet = new String [5];
    	char [] filePart = { 'A', 'B', 'C', 'D', 'E' };
    	
    	GregorianCalendar cal = new GregorianCalendar();
    	cal.setTime(cycle.getChart_effective_date());
    	String year = Integer.toString(cal.get(Calendar.YEAR));
    	   	
    	for (int i = 0; i < filePart.length; i++) {	
    		StringBuilder path = new StringBuilder(Config.getTPPUSPath());
    		StringBuilder fileName = new StringBuilder(Config.getTPPUSPrefix()).append(filePart[i]).append("_").append(year).append(cycle.getChart_cycle_number()).append(".zip");
    		path.append("/").append(fileName);
    		usPathSet[i] = path.toString();
    	}
    	
    	return usPathSet;
    }
    
    private ChartCycleElementsJson initParameters () {
    	ChartCycleElementsJson cycle;
    	   	
    	if (CURRENT.equalsIgnoreCase(this.getEdition())) {   		
    		cycle = new ChartCycleClient().getCurrent28DayCycle();
    	}
    	else {
    		cycle = new ChartCycleClient().getNext28DayCycle();
    	}    	
    	
    	return cycle;
    }
    
    /**
     * Get the edition information response using the specified chart cycle retrieved from the chart cycle resource
     * @param cycle the chart cycle of either current or next 28 day cycle
     * @return
     */
    public ProductSet getEditionResponse (ChartCycleElementsJson cycle) {
    	ProductSet response = initPositiveResponse();
    	response.getEdition().add(initEdition(cycle));
    	return response;   	
    }
    
    private void setChangeType (Product p, String code) {
    	   	
    	if (code == null || "".equals(code)) {
    		// perhaps a "NONE" code should be added? For now, we'll leave it off entirely 
    		return;
    	}

    	if ("C".equalsIgnoreCase(code)) {
    		p.setChange(ChangeCodeList.CHANGED);
    	}
    	else if ("D".equalsIgnoreCase(code)) {
    		p.setChange(ChangeCodeList.DELETED);
    		p.setUrl("");
    	}
    	else if ("A".equalsIgnoreCase(code)) {
    		p.setChange(ChangeCodeList.ADDED);
    		return;
    	}
    }
    
    private boolean isUnitedStates () {
    	return US.equalsIgnoreCase(getGeoname());
    }
    
    private boolean verifyGeoname () {   	
    	if ("US".equalsIgnoreCase(getGeoname())) {
    		return true;
    	}
    		
    	return USStateReferenceData.stateNameExists(getGeoname());   	
    }

    private boolean isNullValue (String value) {
    	
    	return value == null || value.equals(EMPTY_STRING);

    }
    
    @Override
	protected void validateAndSetUrl(String url, ProductSet ps, Product p) {
		try {
			URL downloadURL = new URL(url);
			if (!verifyURL(downloadURL)) {
				logger.warn(downloadURL.toExternalForm()
						+ " returned a non 200 response code when completing a HTTP HEAD check.");
				p.setUrl("");
			} else {
				p.setUrl(downloadURL.toExternalForm());
			}
		} catch (MalformedURLException emalformed) {
			logger.warn("The download URL is not valid", emalformed);
			p.setUrl("");
		}
	}
}
