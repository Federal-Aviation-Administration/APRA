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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.faa.ait.apra.bootstrap.Config;
import gov.faa.ait.apra.bootstrap.ErrorCodes;
import gov.faa.ait.apra.jaxb.ProductCodeList;
import gov.faa.ait.apra.jaxb.ProductSet;
import gov.faa.ait.apra.jaxb.ProductSet.Edition;
import gov.faa.ait.apra.cycle.ChartCycleElementsJson;
import gov.faa.ait.apra.cycle.TACCycleClient;
import gov.faa.ait.apra.util.TACSpecialCase;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path ("/vfr/tac")
@Api(value="Terminal Area Charts")
/** 
 * This class is used to retrieve the TAC charts
 * @author FAA 
 *
 */

public class TerminalAreaCharts extends BaseService {
	private String city = "";
	private URL downloadURL = null;
	private static final Logger logger = LoggerFactory.getLogger(TerminalAreaCharts.class);

	/**
	 * This is the base chart download URL. A single parameter is provided to retrieve the URL for either the current or the next edition
	 * @param ed the edition of the release that is requested
	 * @return
	 */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Path("/chart")
    @ApiOperation(value="Get Terminal Area Chart download link by edition, format, and geoname", 
    		notes="TIFF formatted files are geo-referenced while PDF format is not geo-referenced. Geoname is a city "
    				+ "for which the chart is requested. Valid cities can be found on the FAA public web site "
    				+ "at FAA Home > Air Traffic > Flight Information > Aeronautical Information "
    				+ "Services > Digital Products > VFR Charts > Terminal Area Chart tab", 
    		response=ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})
    
    /**
     * This method gets the TAC release information which includes both the edition information and the download url to retrieve the product
     * @param ed the edition of the release that is requested
     * @param fmt the format of the release that is requested
     * @param geo the geographic name of the chart that is requested
     * @return The TAC release in a serialized JSON or XML format
     */
    public Response getTACRelease (
    		@ApiParam(name="edition", value="Requested product edition. If omitted, the default current edition is returned.", allowableValues="current, next", defaultValue="current", allowMultiple=false, required=false) @QueryParam("edition") String ed, 
    		@ApiParam (name="format", value="Format of the requested chart. TIFF is georeferenced and PDF is not georeferenced. If omitted, the default format of PDF is returned.", allowableValues="tiff, pdf", defaultValue="pdf", allowMultiple=false, required=false) @QueryParam("format") String fmt, 
    		@ApiParam (name="geoname", value="A US city for which the chart is requested.", 
    			allowableValues="Anchorage-Fairbanks, Atlanta, Baltimore-Washington, Boston, Charlotte, Chicago, Cincinnati, Cleveland, Dallas-Ft Worth, Denver-Colorado Springs, "
    				+"Detroit, Houston, Kansas City, Las Vegas, Los Angeles, Memphis, Miami, Minneapolis-St Paul, New Orleans, New York, Philadelphia, Phoenix, "
    				+"Pittsburgh, Puerto Rico-VI, St Louis, Salt Lake City, San Diego, San Francisco, Seattle, Tampa-Orlando",    		
    				required=true) @QueryParam ("geoname") String geo) {
    	ChartCycleElementsJson cycle;
    	
    	logger.info("Received call to retrieve current TAC product release for '"+ed+"', '"+fmt+"', '"+geo+"'");
    	
    	setEdition(ed != null ? ed : CURRENT);
    	setFormat(fmt != null ? fmt : PDF);
    	setCity(geo != null ? geo: EMPTY_STRING);
    	
    	if (EMPTY_STRING.equals(geo)) {
    		return Response.status(400).entity(getErrorResponse(400, "A geographic name (city) must be specified for TAC charts. Received a null city")).build();    		
    	}
    	
    	if (! verifyEdition() || ! verifyFormat()) {
    		logger.error("Received edition "+ed+" and format "+fmt+". Error response being generated and returned.");
    		return Response.status(400).entity(getIllegalArgumentError()).build();    		
    	}
    	   	
     	cycle = initParameters();
    	
     	if (cycle == null) {
     		logger.warn("Unable to locate "+this.getEdition()+" edition chart for "+this.getCity());
     		return Response.status(404).entity(getErrorResponse(404, ErrorCodes.ERROR_404)).build();
     	}	
    	
    	return getRelease(cycle);
    }
   
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Path("/info")
    @ApiOperation(value="Get Terminal Area Chart edition date and edition number by edition type and geoname", 
    		notes="Geoname is a city for which the chart is requested. Valid cities can be found on the FAA public web site "
    				+ "at FAA Home > Air Traffic > Flight Information > Aeronautical Information "
    				+ "Services > Digital Products > VFR Charts > Terminal Area Chart tab",  
    				response=ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})

    /**
     * This method gets the TAC edition information which includes only the edition information for the chart product
     * @param ed the edition of the release that is requested
     * @param geo the geographic name of the chart that is requested
     * @return the edition information in a serialized JSON or XML format
     */    
    public Response getTACEdition (
    		@ApiParam(name="edition", value="Requested product edition. If omitted, the default current edition information is returned.", allowableValues="current, next", defaultValue="current", allowMultiple=false, required=false) @QueryParam("edition") String ed, 
    		@ApiParam (name="geoname", value="A US city for which the chart is requested.", 
			allowableValues="Anchorage-Fairbanks, Atlanta, Baltimore-Washington, Boston, Charlotte, Chicago, Cincinnati, Cleveland, Dallas-Ft Worth, Denver-Colorado Springs, "
				+"Detroit, Houston, Kansas City, Las Vegas, Los Angeles, Memphis, Miami, Minneapolis-St Paul, New Orleans, New York, Philadelphia, Phoenix, "
				+"Pittsburgh, Puerto Rico-VI, St Louis, Salt Lake City, San Diego, San Francisco, Seattle, Tampa-Orlando",    		
				required=true) @QueryParam ("geoname") String geo) {
    	ChartCycleElementsJson cycle;
    	
    	setEdition(ed != null ? ed : CURRENT);
    	setFormat(PDF);
    	setCity(geo != null ? geo: EMPTY_STRING);
    	   	
    	if (EMPTY_STRING.equals(geo)) {
    		return Response.status(400).entity(getErrorResponse(400, "A geographic name (city) must be specified for TAC charts. Received a null city")).build();    		
    	}
    	
    	if (!verifyEdition()) {
    		logger.error("Expected edition current or next and received '"+ed+"' instead. Error response being generated and returned.");
    		return Response.status(400).entity(getIllegalArgumentError()).build();    		
    	}
    	   	
     	if (CURRENT.equalsIgnoreCase(this.getEdition())) {   		
    		cycle = new TACCycleClient().getCurrentCycle(this.getCity());
    	}
    	else {
    		cycle = new TACCycleClient().getNextCycle(this.getCity());
    	}
    	
     	if (cycle == null) {
     		logger.warn("Unable to locate "+this.getEdition()+" edition chart for "+this.getCity());
    		return Response.status(404).entity(getErrorResponse(404, ErrorCodes.ERROR_404)).build();    		
     	}

    	ProductSet ps = getEditionInfo(cycle);
    	return Response.status(ps.getStatus().getCode()).entity(ps).build();

    }    
    
    /**
     * Build the TAC release based upon the given chart cycle passed to this method
     * @param cycle the chart cycle used to build the release information
     * @return a ProductSet object that can be serialized as XML or JSON which contains the requested product release information
     */
    public Response getRelease (ChartCycleElementsJson cycle) {   	
    	int specialCase;
    	StringBuilder tacPath = new StringBuilder(Config.getTACPath());
    	
    	if ("pdf".equalsIgnoreCase(getFormat())) {
    		tacPath = new StringBuilder(Config.getTACPdfPath());
    	}
    	   	
    	// the path looks like this http://www.aeronav.faa.gov/content/aeronav/tac_files/Anchorage-Fairbanks_TAC_77.zip
    	specialCase = TACSpecialCase.getSpecialCase(this.getCity());
    	
    	if (specialCase != -1) {
    		String fileName = TACSpecialCase.getTACFileName(specialCase, cycle.getChart_cycle_number(), this.getFormat());
    		tacPath = tacPath.append("/").append(fileName);
    	}
    	else { 
    		StringBuilder fileName = new StringBuilder(this.formatCity()+"_TAC_");
    		fileName.append(cycle.getChart_cycle_number());
    		if ("tiff".equalsIgnoreCase(getFormat())) { 
    			fileName.append(".zip");
    		}
    		else {
     			fileName.append("_P.pdf");
    		}
    		tacPath = tacPath.append("/").append(fileName.toString());
    	}
    	
    	try {
    		downloadURL = new URL (Config.getAeronavHost()+tacPath.toString());
    	}
    	catch (MalformedURLException emalformed) {
    		logger.warn("Unable to verify the download URL", emalformed);
    		return Response.status(500).entity(getErrorResponse (500, "Unable to construct a valid URL for the TAC product release.")).build();
    	}
    	
    	ProductSet ps = buildResponse(cycle);    	
    	return Response.status(ps.getStatus().getCode()).entity(ps).build();

    }
   
    @Override
    protected ProductSet buildResponse (ChartCycleElementsJson cycle) {
    	ProductSet response = getEditionInfo(cycle);
    	Edition.Product product = new Edition.Product();
    	product.setProductName(ProductCodeList.TAC);
    	validateAndSetUrl(downloadURL.toExternalForm(), response, product);
    	
    	response.getEdition().get(0).setProduct(product);
    	
    	return response;
    }
    
    
    /**
     * Build the TAC edition information based upon the given chart cycle passed to this method
     * @param cycle the chart cycle used to build the edition information
     * @return a ProductSet object that can be serialized as XML or JSON which contains the requested product edition information
     */    
    public ProductSet getEditionInfo (ChartCycleElementsJson cycle) {
    	ProductSet response = initPositiveResponse();
    	
    	ProductSet.Edition ed = initEdition(cycle);
    	ed.setGeoname(getCity());
    	
    	response.getEdition().add(ed);    	
    	return response;   	
    }
    
    /**
     * Set the city name and normalize it to follow punctuation and capitalization rules.
     * @param cityName the name of the city to be set and normalized
     */
    public void setCity (String cityName) {
    	
    	char[] separators = {'-', '_', ' '};
    	
    	if (cityName != null) {
    		this.city = cityName;
    		this.city = this.city.replace('_', ' ');
    		this.city = WordUtils.capitalizeFully(this.city, separators);
    	}
    	else {
    		this.city = "";
    	}
    	
    	
    	// This is a special case because the wordutils will change 'Puerto Rico-IV' into 'Puerto Rico-Iv' while all other strings work. Once again, the AJV5 file 
    	// naming convention strikes
    	if (this.city.startsWith("Puerto")) {
    		this.city = "Puerto Rico-VI";
    	}
    	setGeoname(this.city);
    }
    
    /**
     * Get the normalized city name for this specific request. The city should follow punctuation and capitalization rules if set correctly.
     * @return
     */
    public String getCity() {
    	return new String(this.city);
    }
    

    /**
     * This method exists because the TAC file names replace all space characters with the "_" character. I guess if someone sends in a city with underscores, that will be ok
     	There is also a special case where "Colorado Springs" is simply dropped from the filename on the URL. Finally, who knows what someone will send in, so I'm 
     	going to capitalize each word on the '_' delimeter. If I get junk, then I get junk. There is only so much cleaning I'm willing to do.
    	Strings like denver, baltimore-washington, and dallas-ft worth should work. 
     * @return a properly formatted city string capitalized as the web site produces it
     */
    public String formatCity() {
    	String scratch = this.getCity();
    	char[] separators = {'-', '_', ' '};
    	
    	String retVal = scratch.replace(" ",  "_");
    	retVal = WordUtils.capitalizeFully(retVal, separators);

		logger.info("Converted "+this.getCity()+" to "+retVal);

    	
    	if (logger.isDebugEnabled()) 
    		logger.debug("Converted "+this.getCity()+" to "+retVal);
    	
    	return retVal;
    }
    
    private ChartCycleElementsJson initParameters () {
    	ChartCycleElementsJson cycle;

     	if (CURRENT.equalsIgnoreCase(this.getEdition())) {   		
    		cycle = new TACCycleClient().getCurrentCycle(this.getCity());
    	}
    	else {
    		cycle = new TACCycleClient().getNextCycle(this.getCity());
    	}
    	
    	return cycle;
    }
	
}
