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


import static gov.faa.ait.apra.bootstrap.ErrorCodes.DEPRECATED;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.faa.ait.apra.cycle.ChartCycleElementsJson;
import gov.faa.ait.apra.jaxb.ProductSet;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@Path("/ders")
@Api(value="Digital Enroute Supplement (DERS)")

/**
 * As of June 2017, the DERS chart set has been discontinued. 
 * This chart set is no longer available.
 * @author FAA
 *
 */
public class DigitalEnrouteSupplementCharts extends BaseService {

	private static final Logger logger = LoggerFactory.getLogger(DigitalEnrouteSupplementCharts.class);
	
	/**
	 * This is the base chart download URL. A single parameter is provided to retrieve the URL for the current day's edition
	 * @return 404 not found as this has been deprecated
	 */
    @GET
    @Deprecated
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Path("/chart")
    @ApiOperation(value="Get Digital Enroute Supplement download link.", 
    	notes="The Digital Enroute Supplement release is deprecated and publication has been discontinued as of June 2017.",
    	response=ProductSet.class)
	@ApiResponses(value = {@ApiResponse(code = 404, message = DEPRECATED)})
    
    public Response getDERSRelease (
    		@ApiParam(name="edition", value="Requested product edition. If omitted, current edition is returned.", allowableValues="current, next", defaultValue="current", allowMultiple=false, required=false) @QueryParam("edition") String ed) {
    	
    	logger.info("Received call to retrieve current DERS product release for edition.");
     	ProductSet ps = buildResponse(null);
    	return Response.status(ps.getStatus().getCode()).entity(ps).build();
    }

	/**
	 * This is the base chart download URL. A single parameter is provided to retrieve the URL for the current day's edition
	 * @return 404 not found as this has been deprecated
	 */
    @GET
    @Deprecated
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Path("/info")
    @ApiOperation(value="Get Digital Enroute Supplement edition information.", 
    	notes="The Digital Enroute Supplement release is deprecated and publication has been discontinued as of June 2017.",
    	response=ProductSet.class)
	@ApiResponses(value = {@ApiResponse(code = 404, message = DEPRECATED)})
    
    public Response getDERSEdition (
    		@ApiParam(name="edition", value="Requested product edition. If omitted, current edition is returned.", allowableValues="current, next", defaultValue="current", allowMultiple=false, required=false) @QueryParam("edition") String ed) {
    	
     	ProductSet ps = buildResponse(null);
    	return Response.status(ps.getStatus().getCode()).entity(ps).build();
    } 
    
    
	@Override
	protected ProductSet buildResponse(ChartCycleElementsJson cycle) {
		return initDeprecatedResponse();
	}

}
