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

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static gov.faa.ait.apra.bootstrap.ErrorCodes.ERROR_400;
import static gov.faa.ait.apra.bootstrap.ErrorCodes.ERROR_404;
import static gov.faa.ait.apra.bootstrap.ErrorCodes.ERROR_500;
import static gov.faa.ait.apra.bootstrap.ErrorCodes.RESPONSE_200;

import gov.faa.ait.apra.cycle.ChartCycleClient;
import gov.faa.ait.apra.cycle.ChartCycleElementsJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import gov.faa.ait.apra.jaxb.ProductSet;
import gov.faa.ait.apra.jaxb.ProductSet.Edition;

import gov.faa.ait.apra.bootstrap.Config;
import gov.faa.ait.apra.jaxb.ProductCodeList;


@Path("/cifp")
@Api(value="Coded Instrument Flight Procedures (CIFP)")
/**
 * This class returns the CIFP release of information or the edition information. CIFP is a product set within the FAA superset of aeronautic chart products
 * @author FAA
 *
 */
public class CIFP extends BaseService {

	private URL downloadURL = null;
	private static final Logger logger = LoggerFactory.getLogger(CIFP.class);
	
	/**
	 * This is the base chart download URL. A single parameter is provided to retrieve the URL for either the current or the next edition
	 * @param ed the edition for which you want a URL
	 * @return
	 */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Path("/chart")
    @ApiOperation(value="Get CIFP chart download link by edition type of current or next. If edition is left blank or null, the default edition of current is used.", 
    	notes="The CIFP release is distributed as a zip file containing charts and verification software.",
    	response=ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})
    
    public Response getCIFPRelease (
    		@ApiParam(name="edition", value="Requested product edition. If omitted, current edition is returned.", allowableValues="current, next", defaultValue="current", allowMultiple=false, required=false) @QueryParam("edition") String ed) {
    	ChartCycleElementsJson cycle;
    	
    	logger.info("Received call to retrieve current CIFP product release for edition '"+ed+"'.");
    	
    	cycle = initParameters(ed);
    	   	
    	if (!verifyEdition()) {
    		logger.error("Expected edition 'current' or 'next' and received '"+ed+"' instead. Error response being generated and returned.");
    		return Response.status(400).entity(getIllegalArgumentError()).build();    		
    	}
    	
    	ProductSet ps = getRelease(cycle);
    	return Response.status(ps.getStatus().getCode()).entity(ps).build();
	
    }
   
    /**
     * This is the edition info URL. Calls to this method return data about the edition date and edition number. 
     * @param ed
     * @return
     */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Path("/info")
    @ApiOperation(value="Get CIFP edition date and edition number by edition type of current or next. If the edition is left blank or null, the default edition of current is used.", response=ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})
    public Response getCIFPEdition (@ApiParam(name="edition", value="Requested product edition. If omitted, current edition is returned.", allowableValues="current, next", defaultValue="current", allowMultiple=false, required=false) @QueryParam("edition") String ed) {
    	ChartCycleElementsJson cycle;
    	
    	cycle = initParameters(ed);
    	
    	if (!verifyEdition()) {
    		logger.error("Expected edition 'next' and received '"+ed+"' instead. Error response being generated and returned.");
    		return Response.status(400).entity(getIllegalArgumentError()).build();    		
    	}
    	ProductSet ps = getEdition(cycle);
    	return Response.status(ps.getStatus().getCode()).entity(ps).build();		 	
    }    
    
    private ChartCycleElementsJson initParameters (String ed) {
    	ChartCycleElementsJson cycle;
    	
    	setEdition(ed != null ? ed : CURRENT);
    	setFormat(ZIP);
    	setGeoname(EMPTY_STRING);
    	
    	if ("current".equalsIgnoreCase(this.getEdition())) {   		
    		cycle = new ChartCycleClient().getCurrent28DayCycle();
    	}
    	else {
    		cycle = new ChartCycleClient().getNext28DayCycle();
    	}    	
    	
    	return cycle;
    }
    
    /**
     * Get the product release information for the specified release cycle
     * @param cycle a publication release cycle
     * @return the product set object representing the charts in the specified release
     */
    public ProductSet getRelease (ChartCycleElementsJson cycle) {   
    	StringBuilder cifpPath = new StringBuilder();
    	cifpPath = cifpPath.append(Config.getCIFPPath()).append("/").append(Config.getCIFPFilePrefix());
    	// the path looks like this: /Upload_313-d/cifp/cifp_<year><cycle>.zip
    	
    	GregorianCalendar cal = new GregorianCalendar();
    	cal.setTime(cycle.getChart_effective_date());
    	cifpPath = cifpPath.append(cal.get(Calendar.YEAR)).append(cycle.getChart_cycle_number()).append(".zip");
    	
    	try {
    		downloadURL = new URL (Config.getAeronavHost()+cifpPath.toString());
    	}
    	catch (MalformedURLException emalformed) {
    		logger.warn("The download URL is not valid", emalformed);
    		return getErrorResponse (500, "Unable to construct a valid URL for the CIFP product release.");
    	}
    	
    	return buildResponse(cycle);
    	 	
    }
    
    @Override
    protected ProductSet buildResponse (ChartCycleElementsJson cycle) {
    	ProductSet response = getEdition(cycle);
    	Edition.Product product = new Edition.Product();
    	product.setProductName(ProductCodeList.CIFP);
    	
    	validateAndSetUrl(downloadURL.toExternalForm(), response, product);

    	response.getEdition().get(0).setProduct(product);

    	return response;
    }
    
    /**
     * Build a ProductSet response for the edition information. This does not include URLs to products and only includes the edition date 
     * and edition number. 
     * @param cycle - this is the edition cycle for which the edition was requested
     * @return
     */
    public ProductSet getEdition (ChartCycleElementsJson cycle) {
    	ProductSet response = initPositiveResponse();
    	ProductSet.Edition ed = initEdition(cycle);
    	response.getEdition().add(ed);
    	return response;   	
    }
}
