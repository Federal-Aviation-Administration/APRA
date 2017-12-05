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
import java.util.Date;

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
import gov.faa.ait.apra.jaxb.ObjectFactory;
import gov.faa.ait.apra.jaxb.ProductCodeList;
import gov.faa.ait.apra.jaxb.ProductSet;
import gov.faa.ait.apra.jaxb.ProductSet.Edition;
import gov.faa.ait.apra.cycle.ChartCycleClient;
import gov.faa.ait.apra.cycle.ChartCycleElementsJson;
import gov.faa.ait.apra.util.CycleDateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path ("/nfdc/nasr")
@Api(value="NASR 28 Day Subscription")
/** 
 * This class is used to retrieve the NASR 56 day subscription file
 *
 * @author FAA
 */
public class NASRSubscription extends BaseService {
	private static final Logger logger = LoggerFactory.getLogger(NASRSubscription.class);
	private static final String ERROR = " Error response being generated and returned.";
	private Date fromDate;
	private Date toDate;

	public NASRSubscription() { } 
	
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Path("/chart")
    @ApiOperation(value="Get the National Flight Data Center NASR 28 day subscription file", 
    		response=ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})
    
    /**
     * This method gets the NASR subscription release information which includes both the edition information and the download url to retrieve the product
     * @param ed the edition of the release that is requested
     * @param fmt the format of the release that is requested
     * @param geo the geographic name of the chart that is requested
     * @return The Oceanic Chart release in a serialized JSON or XML format
     */
	public Response getNASRSubscription (
    	@ApiParam(name="edition", value="Requested product edition. If omitted, the default current edition is returned.", allowableValues="current, next", defaultValue="current", allowMultiple=false, required=false)
    	@QueryParam("edition") String ed) {
	    logger.info("Received call to retrieve current NFDC NASR subscription release for "+ed);
	    
    	setEdition(ed != null ? ed : CURRENT);
    	setFormat("zip");
    	
    	if (!verifyEdition()) {
    		logger.error("Expected edition current or next and received "+ed+ERROR);
    		return Response.status(400).entity(getIllegalArgumentError()).build();    		
    	}

    	ChartCycleElementsJson cycle = initParameters();
    	
    	ProductSet ps = buildResponse(cycle);
    	return Response.status(ps.getStatus().getCode()).entity(ps).build();

    }
 
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Path("/info")
    @ApiOperation(value="Get the National Flight Data Center NASR 28 day subscription file edition information", 
    		response=ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})
    /**
     * This method gets the NASR subscription edition information
     * @param ed the edition of the release that is requested
     * @return The NASR subscription edition information in a serialized JSON or XML format
     */
	public Response getNASREdition (
    	@ApiParam(name="edition", value="Requested product edition. If omitted, the default current edition is returned.", allowableValues="current, next", defaultValue="current", allowMultiple=false, required=false)
    	@QueryParam("edition") String ed) {
	    logger.info("Received call to retrieve current NASR subscription Chart release for "+ed);
	    
    	setEdition(ed != null ? ed : CURRENT);
    	setFormat("zip");
    	
    	if (!verifyEdition()) {
    		logger.error("Expected edition current or next and received "+ed+ERROR);
    		return Response.status(400).entity(getIllegalArgumentError()).build();    		
    	}

    	ChartCycleElementsJson cycle = initParameters();
    	ProductSet response = initPositiveResponse();
    	response.getEdition().add(initEdition(cycle));
    	
    	return Response.status(response.getStatus().getCode()).entity(response).build();

    }
	
    // https://nfdc.faa.gov/webContent/28DaySub/28DaySubscription_Effective_2017-08-17.zip
    //
	// https://nfdc.faa.gov/webContent/56DaySub/56DySubscription_May_26__2016_-_July_21__2016.zip
    // To the extent possible, we parameterize this path so it can be adjusted without code changes
    // the nfdc host, context path, file prefix, and date formats are all in configuration properties
    // read from disk. The properties used are delivered by Ansible during deployment and read upon 
    // first use
	@Override
	protected ProductSet buildResponse(ChartCycleElementsJson cycle) {
    	ObjectFactory of = new ObjectFactory();
    	ProductSet response = initPositiveResponse();
    	
    	ProductSet.Edition ed = initEdition(cycle);
    	Edition.Product product = of.createProductSetEditionProduct();
    	product.setProductName(ProductCodeList.SUBSCRIBER);
    	
    	StringBuilder path = new StringBuilder(Config.getNFDCHost()).append(Config.getNfdcNasrPath());

    	StringBuilder fileName = new StringBuilder("/").append(Config.getNASRFilePrefix());
    	fileName = fileName.append(formatDate(fromDate, Config.getNASRDateFormat())).append(".zip");

    	path = path.append(fileName);
    	
    	if (logger.isInfoEnabled())
    		logger.info("NASR susbscriber file URL created: "+path.toString());
   	
    	try {
    		URL url = new URL(path.toString());
    		
    		if (Config.getNASRCheckFlag())
    			verifyURL(url);
    		
        	product.setUrl(path.toString());

        }
    	catch (Exception exurl) {
    		logger.error("Unable to verify the download url "+path.toString(), exurl);
    		product.setUrl("");
        	response.getStatus().setCode(404);
        	response.getStatus().setMessage(ErrorCodes.ERROR_404);
        }

    	ed.setProduct(product);
    	response.getEdition().add(ed);
    	
		return response;
	}
	
    private ChartCycleElementsJson initParameters () {
    	ChartCycleElementsJson cycle;
    	   	
    	if (CURRENT.equalsIgnoreCase(this.getEdition())) {   		
    		cycle = new ChartCycleClient().getCurrent28DayCycle();
    		fromDate = cycle.getChart_effective_date();
    		toDate = new CycleDateUtil().getNext28Day();
    	}
    	else {
    		cycle = new ChartCycleClient().getNext28DayCycle();
    		fromDate = cycle.getChart_effective_date();
    		toDate = new CycleDateUtil().get28DayCycleDate(2);
    	}    	
    	
    	return cycle;
    }

    private String formatDate (Date unformattedDate, String format) {
    	SimpleDateFormat formatter = new SimpleDateFormat(format);
    	return formatter.format(unformattedDate);
    }
    
}
