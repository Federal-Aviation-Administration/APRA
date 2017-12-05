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
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.faa.ait.apra.bootstrap.Config;
import gov.faa.ait.apra.cycle.ChartCycleElementsJson;
import gov.faa.ait.apra.jaxb.ChangeCodeList;
import gov.faa.ait.apra.jaxb.EditionCodeList;
import gov.faa.ait.apra.jaxb.FormatCodeList;
import gov.faa.ait.apra.jaxb.ObjectFactory;
import gov.faa.ait.apra.jaxb.ProductCodeList;
import gov.faa.ait.apra.jaxb.ProductSet;
import gov.faa.ait.apra.jaxb.ProductSet.Edition;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/ddof")
@Api(value="Daily Digital Obstacle File (DDOF)")

/**
 * This service provides the Daily Digital Obstacle File (DDOF) download link and edition information. There is only one release at any given time and it is the
 * daily DOF data file. 
 * @author FAA
 *
 */
public class DailyDigitalObstacleFile extends BaseService {
	private static final Logger logger = LoggerFactory.getLogger(DailyDigitalObstacleFile.class);
	
	/**
	 * This is the base chart download URL. A single parameter is provided to retrieve the URL for the current day's edition
	 * @return
	 */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Path("/chart")
    @ApiOperation(value="Get Daily Digital Obstacle File download link.", 
    	notes="The Daily Digital Obstacle File release is distributed as a zip file containing the latest obstacle information from the FAA database.",
    	response=ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})
    
    public Response getDDOFRelease () {
    	
    	logger.info("Received call to retrieve current DDOF product release for edition.");
    	setFormat(ZIP);
    	ProductSet ps = getRelease();
    	return Response.status(ps.getStatus().getCode()).entity(ps).build();
	
    }

	/**
	 * This is the base chart download URL. A single parameter is provided to retrieve the URL for the current day's edition
	 * @return
	 */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Path("/info")
    @ApiOperation(value="Get Daily Digital Obstacle File edition information.", 
    	notes="The Daily Digital Obstacle File is released by the FAA on a daily basis.",
    	response=ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})
    
    public Response getDDOFEdition () {
    	
    	logger.info("Received call to retrieve current DDOF edition information.");
    	setFormat("ZIP");
    	ProductSet response = initPositiveResponse();
    	ProductSet.Edition ed = initEdition(null);
    	response.getEdition().add(ed);
    	
       	ed = initEdition(null);
    	response.getEdition().add(ed);
    	
    	return Response.status(response.getStatus().getCode()).entity(response).build();
	
    }    
    
	private ProductSet getRelease() {
		return buildResponse(null);
	}

	// http://tod.faa.gov/tod/DOF_DAILY_CHANGE_UPDATE.ZIP
	
	@Override
	protected ProductSet buildResponse(ChartCycleElementsJson cycle) {
    	ProductSet response = initPositiveResponse();
    	
    	addProduct(Config.getDDOFFile(), response);
    	addProduct(Config.getDDOFDailyChangeFile(), response);
    	
    	return response;
	}
	
	private void addProduct (String name, ProductSet ps) {
		StringBuilder downloadURL = new StringBuilder();
		ProductSet.Edition ed = initEdition(null);
		Edition.Product product = new Edition.Product();
		product.setProductName(ProductCodeList.DDOF);
		product.setChange(ChangeCodeList.CHANGED);
    	downloadURL.append(Config.getDDOFHost()).append(Config.getDDOFPath()).append("/").append(name);
    	try {
    		product.setChartName(name);
    		URL url = new URL (downloadURL.toString());
    		product.setUrl(url.toString());
        }
       	catch (MalformedURLException emalformed) {
    		logger.warn("The DDOF download URL is not valid", emalformed);
    		product.setUrl("");
     	}		
        ed.setProduct(product);
        ps.getEdition().add(ed);   
        
    	return;
	}
	
	@Override
    protected Edition initEdition (ChartCycleElementsJson cycle) {
		Date today = new Date (System.currentTimeMillis());
    	ObjectFactory of = new ObjectFactory();

    	ProductSet.Edition ed = of.createProductSetEdition();
	   	
    	SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
    	ed.setEditionDate(formatter.format(today));
    	ed.setEditionNumber(1);
    	ed.setEditionName(EditionCodeList.DAILY);
    	ed.setFormat(FormatCodeList.fromValue(getFormat()));
    	ed.setGeoname("US");
    	   	
    	return ed;
    }

}
