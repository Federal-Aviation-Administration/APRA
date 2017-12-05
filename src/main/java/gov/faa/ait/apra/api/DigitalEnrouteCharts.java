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

import java.net.URL;
import java.text.SimpleDateFormat;

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
import gov.faa.ait.apra.cycle.ChartCycleClient;
import gov.faa.ait.apra.cycle.ChartCycleElementsJson;

import gov.faa.ait.apra.jaxb.ProductCodeList;
import gov.faa.ait.apra.jaxb.ProductSet;
import gov.faa.ait.apra.jaxb.ProductSet.Edition;
import gov.faa.ait.apra.util.CycleDateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/dec")
@Api(value="Digital Enroute Charts US (DDECUS)")

/**
 * This is the service to return the URL for the Digital Enroute Charts (DDECUS). The chart set is part of the IFR and DERS chart group
 * published by the FAA.
 * 
 * @author FAA
 *
 */
public class DigitalEnrouteCharts extends BaseService {

	private static final Logger logger = LoggerFactory.getLogger(DigitalEnrouteCharts.class);

	/**
	 * This is the base chart download URL. A single parameter is provided to retrieve the URL for either the current or the next edition
	 * @param ed the edition for which you want a URL
	 * @return
	 */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Path("/chart")
    @ApiOperation(value="Get Digital Enroute Chart download link by edition type of current or next. If edition is left blank or null, the default edition of current is used.", 
    	notes="The DEC US release is distributed as a zip file containing charts.",
    	response=ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})
	// http://aeronav.faa.gov/Upload_313-d/enroute/DDECUS_32.zip 
    
    public Response getDECRelease (
    		@ApiParam(name="edition", value="Requested product edition. If omitted, current edition is returned.", allowableValues="current, next", defaultValue="current", allowMultiple=false, required=false) @QueryParam("edition") String ed) {
    	logger.info("Received call to retrieve current CIFP product release for edition '"+ed+"'.");

    	ChartCycleElementsJson cycle = initParameters (ed);

    	if (! verifyEdition() ) {
    		logger.error("Expected edition 'current' or 'next' and received '"+ed+"' instead. Error response being generated and returned.");
    		return Response.status(400).entity(getIllegalArgumentError()).build();    		
    	}

    	ProductSet ps = buildResponse(cycle);
    	return Response.status(ps.getStatus().getCode()).entity(ps).build();
    }
   
    /**
     * This is the Digital Enroute Charts (DEC) edition info URL. Calls to this method return data about the edition date and edition number. 
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
    public Response getDECEdition (@ApiParam(name="edition", value="Requested product edition. If omitted, current edition is returned.", allowableValues="current, next", defaultValue="current", allowMultiple=false, required=false) @QueryParam("edition") String ed) {
    	ChartCycleElementsJson cycle = initParameters(ed);
    	
    	if (! verifyEdition() ) {
    		logger.error("Expected edition 'current' or 'next' and received '"+ed+"' instead. Error response being generated and returned.");
    		return Response.status(400).entity(getIllegalArgumentError()).build();    		
    	}
    	
    	ProductSet ps = initPositiveResponse();
    	Edition edition = initEdition(cycle);
    	setCycleNumber(edition);
    	ps.getEdition().add(edition);
    	return Response.status(ps.getStatus().getCode()).entity(ps).build();		 	
    }    
	
	@Override
	protected ProductSet buildResponse(ChartCycleElementsJson cycle) {
		StringBuilder path = new StringBuilder();
		StringBuilder file = new StringBuilder();
    	ProductSet response = initPositiveResponse();
    	ProductSet.Edition ed = initEdition(cycle);
    	setCycleNumber(ed);
    	Edition.Product product = new Edition.Product();
    	product.setProductName(ProductCodeList.DEC);
    	
    	path.append(Config.getAeronavHost()).append(Config.getDECPath()).append("/");
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		path.append(sdf.format(cycle.getChart_effective_date())).append("/");
		file.append(Config.getDECFilePrefix()).append(".").append(ZIP);
    	path.append(file);
    	product.setChartName(file.toString());
    	product.setUrl(path.toString());
    	
    	try {
    		URL url = new URL(path.toString());
    		if (verifyURL(url)) {
    			product.setUrl(path.toString());
    		}
    	}
    	catch (Exception emalformed) {
    		logger.warn("The DEC url "+path+" is invalid or malformed.", emalformed);
    		response.getStatus().setCode(404);
    		response.getStatus().setMessage(ErrorCodes.ERROR_404);
    		product.setUrl("");
    	}

    	ed.setProduct(product);
    	response.getEdition().add(ed);
    	return response;
	}
	
    private ChartCycleElementsJson initParameters (String ed) {
    	ChartCycleElementsJson cycle;
    	
    	setEdition(ed != null ? ed : CURRENT);
    	setFormat(ZIP);
    	setGeoname("US");
    	
    	if (CURRENT.equalsIgnoreCase(this.getEdition())) {   		
    		cycle = new ChartCycleClient().getCurrent56DayCycle();
    	}
    	else {
    		cycle = new ChartCycleClient().getNext56DayCycle();
    	}    	
    	
    	return cycle;
    }
    
    protected Edition setCycleNumber (Edition edition) {
     	if (NEXT.equalsIgnoreCase(getEdition())) {
    		edition.setEditionNumber(CycleDateUtil.getNextDECCycleNumber());
    	}
    	else {
    		edition.setEditionNumber(CycleDateUtil.getCurrentDECCycleNumber());
    	}
    	   	
    	return edition;
    }
	
}
