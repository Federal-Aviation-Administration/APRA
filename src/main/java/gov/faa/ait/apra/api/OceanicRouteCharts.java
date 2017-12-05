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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path ("/ifr/oceanic")
@Api(value="Oceanic Route Charts")
/** 
 * This class is used to retrieve the Oceanic Route charts
 * @author FAA
 *
 */
public class OceanicRouteCharts extends BaseService {
	private static final Logger logger = LoggerFactory.getLogger(OceanicRouteCharts.class);
	private static final String NARC = "NARC";
	private static final String PORC = "PORC";
	private static final String WATRS = "WATRS";
	private static final String ERROR = " Error response being generated and returned.";

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Path("/chart")
    @ApiOperation(value="Get Oceanic Route Chart download link by edition, format, and geoname", 
    		notes="TIFF formatted files are geo-referenced while PDF format is not geo-referenced. Geoname is a geographic area "
    				+ "for which the chart is requested. Valid geographic names are Pacific (PORC), North Atlantic (NARC), and Wester Atlantic (WATRS) ", 
    		response=ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})
    
    /**
     * This method gets the Oceanic Chart release information which includes both the edition information and the download url to retrieve the product
     * @param ed the edition of the release that is requested
     * @param fmt the format of the release that is requested
     * @param geo the geographic name of the chart that is requested
     * @return The Oceanic Chart release in a serialized JSON or XML format
     */
	public Response getOceanicRouteChart (
    		@ApiParam(name="edition", value="Requested product edition. If omitted, the default current edition is returned.", allowableValues="current, next", defaultValue="current", allowMultiple=false, required=false) @QueryParam("edition") String ed, 
    		@ApiParam (name="format", value="Format of the requested chart. TIFF is georeferenced and PDF is not georeferenced. If omitted, the default format of PDF is returned.", allowableValues="tiff, pdf", defaultValue="pdf", allowMultiple=false, required=false) @QueryParam("format") String fmt, 
    		@ApiParam (name="geoname", value="A geographic area for which the chart is requested", allowableValues="NARC, PORC, WATRS", defaultValue="PORC", required=true) @QueryParam ("geoname") String geo) {

	    	logger.info("Received call to retrieve current Oceanic Route Chart release for '"+ed+"', '"+fmt+"', '"+geo+"'");
			
	    	setEdition(ed != null ? ed : CURRENT);
	    	setGeoname(geo != null ? geo : PORC);
	    	setFormat (fmt != null ? fmt : PDF);
	    	
	    	if (!verifyEdition()) {
	    		logger.error("Expected edition 'next' and received '"+ed+ERROR);
	    		return Response.status(400).entity(getIllegalArgumentError()).build();    		
	    	}
	    	
	    	if (!verifyFormat()) {
	    		logger.error("Expected format of 'tiff' or 'pdf'. Received format '"+fmt+ERROR);
	    		return Response.status(400).entity(getIllegalArgumentError()).build();    		
	    	}
	    	
	    	if (!verifyGeoname()) {
	    		logger.error("Expected geographic area of NARC, PORC, or WATRS. Received geoname of '"+geo+ERROR);
	    		return Response.status(400).entity(getErrorResponse(400, "Expected geographic area of NARC, PORC, or WATRS. Received geoname of "+geo)).build();
	    	}
	    	
	    	ChartCycleElementsJson cycle = initParameters();
	    	
	    	ProductSet ps = buildResponse(cycle);
	    	return Response.status(ps.getStatus().getCode()).entity(ps).build();

		}

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Path("/info")
    @ApiOperation(value="Get Oceanic Route Chart edition information by edition type", 
    		notes="All oceanic charts are released on a regular 56 day cycle. "
    				+ "The format and geographic name are not necessary to obtain edition information.", 
    		response=ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})
    
    /**
     * This method gets the Oceanic Chart edition information
     * @param ed the edition of the release that is requested
     * @return The Oceanic Chart edition in a serialized JSON or XML format
     */
	public Response getOceanicRouteEdition (
    		@ApiParam(name="edition", value="Requested product edition. If omitted, the default current edition information is returned.", allowableValues="current, next", defaultValue="current", allowMultiple=false, required=false) @QueryParam("edition") String ed) {

    	setEdition(ed != null ? ed : CURRENT);    	
    	setGeoname("ALL");
    	setFormat("PDF");
    	ChartCycleElementsJson cycle = initParameters();
    	ProductSet ps = getEditionResponse(cycle);
    	return Response.status(ps.getStatus().getCode()).entity(ps).build();

    }
    
    
    // http://aeronav.faa.gov/enroute/05-26-2016/narc_tif.zip
    // http://aeronav.faa.gov/enroute/05-26-2016/narc_pdf.zip
	@Override
	protected ProductSet buildResponse(ChartCycleElementsJson cycle) {
    	ObjectFactory of = new ObjectFactory();
    	ProductSet response = initPositiveResponse();
    	
    	ProductSet.Edition ed = initEdition(cycle);
    	Edition.Product product = of.createProductSetEditionProduct();
    	product.setProductName(ProductCodeList.IFR_OCEANIC);
    	
    	StringBuilder path = new StringBuilder(Config.getAeronavHost()).append("/enroute")
    			.append("/").append(formatDate(cycle.getChart_effective_date(), "MM-dd-yyyy"));
    	StringBuilder fileName = new StringBuilder(getGeoname().toLowerCase());
    	
    	if (TIFF.equalsIgnoreCase(getFormat())) {
    		fileName.append("_tif.zip");
    	}
    	else {
    		fileName.append("_pdf.zip");
    	}
    		
    	product.setChartName(fileName.toString());
    	path = path.append("/").append(fileName);
    	try {
    		URL url = new URL(path.toString());
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
	
    /**
     * Get the edition information response using the specified chart cycle retrieved from the chart cycle resource
     * @param cycle the chart cycle of either current or next 56 day cycle
     * @return
     */
    public ProductSet getEditionResponse (ChartCycleElementsJson cycle) {
    	ProductSet response = initPositiveResponse();
    	response.getEdition().add(initEdition(cycle));
    	return response;   	
    }

	private boolean verifyGeoname () {
		if (NARC.equalsIgnoreCase(getGeoname()) || PORC.equalsIgnoreCase(getGeoname()) || WATRS.equalsIgnoreCase(getGeoname()) ) {
			return true;
		}
		
		return false;
	}
	
    private ChartCycleElementsJson initParameters () {
    	ChartCycleElementsJson cycle;
    	   	
    	if (CURRENT.equalsIgnoreCase(this.getEdition())) {   		
    		cycle = new ChartCycleClient().getCurrent56DayCycle();
    	}
    	else {
    		cycle = new ChartCycleClient().getNext56DayCycle();
    	}    	
    	
    	return cycle;
    }
    
    private String formatDate (Date unformattedDate, String format) {
    	SimpleDateFormat formatter = new SimpleDateFormat(format);
    	return formatter.format(unformattedDate);
    }
}
