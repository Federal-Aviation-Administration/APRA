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
import java.util.Arrays;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.faa.ait.apra.bootstrap.Config;
import gov.faa.ait.apra.jaxb.ProductCodeList;
import gov.faa.ait.apra.jaxb.ProductSet;
import gov.faa.ait.apra.jaxb.ProductSet.Edition.Product;
import gov.faa.ait.apra.cycle.ChartCycleElementsJson;
import gov.faa.ait.apra.util.TableChartClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ExternalDocs;
/**
 * This class is used to implement VFR Sectional service.
 * It extends the TableDataService class, implementing the edition and product methods.
 * @author FAA
 *
 */
@Api(value="Sectional Charts")
@Path("/vfr/sectional")
public class SectionalCharts extends AbstractTableDataService {

	private static final String CHART_TYPE_SECTIONAL = "SECTIONAL";

	private static final Logger logger = LoggerFactory.getLogger(SectionalCharts.class);
	

	/**
	 * Default constructor
	 */
	public SectionalCharts() {
		setClient(new TableChartClient());
	}
	
	/**
	 * This constructor allows a specific chart client to be used.  Mainly for test purposes.
	 * @param client
	 */
	public SectionalCharts(TableChartClient client) {
		setClient(client);
	}
	
	/**
	 * API method to get product download links + edition information
	 * @param cityRegion geoname to use
	 * @param edition current (default) or next
	 * @param format pdf (default) or tiff
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
	@Path("/chart")
	@ExternalDocs(value="FAA Sectional Charts", url = "http://www.faa.gov/air_traffic/flight_info/aeronav/digital_products/vfr/")
    @ApiOperation(value="Get Sectional Chart download link by edition, format, and geoname", 
    		notes="TIFF formatted files are geo-referenced while PDF format is not geo-referenced. Geoname is a city "
    				+ "for which the chart is requested. Valid cities can be found on the FAA public web site "
    				+ "at FAA Home > Air Traffic > Flight Information > Aeronautical Information Services > Digital Products > VFR Charts > Sectional Chart tab",
    				
    		response=ProductSet.class)
	
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})
	public Response getSectionalChart(@ApiParam (name="geoname", value="Geoname which is a city for which the chart is requested. Valid cities can be found on the FAA public web site.", 
				allowableValues="Albuquerque, Anchorage, Atlanta, Bethel, Billings, Brownsville, Cape Lisburne, Charlotte, Cheyenne, Chicago, Cincinnati, Cold Bay, "
				+"Dallas-Ft Worth, Dawson, Denver, Detroit, Dutch Harbor, El Paso, Fairbanks, Great Falls, Green Bay, Halifax, Hawaiian Islands, Houston, "
				+"Jacksonville, Juneau, Kansas City, Ketchikan, Klamath Falls, Kodiak, Lake Huron, Las Vegas, Los Angeles, McGrath, Memphis, Miami, Montreal, "
				+"New Orleans, New York, Nome, Omaha, Phoenix, Point Barrow, Salt Lake City, San Antonio, San Francisco, Seattle, Seward, St Louis, Twin Cities, "
				+"Washington, Western Aleutian Islands, Whitehorse, Wichita", required=true) @QueryParam("geoname") String cityRegion,
			@ApiParam(name="edition", value="Requested product edition. If omitted, the default current edition is returned.", allowableValues="current, next", defaultValue="current", allowMultiple=false, required=false)  @QueryParam("edition") String edition, 
			@ApiParam (name="format", value="Format of the requested chart. TIFF is georeferenced and PDF is not georeferenced. If omitted, the default format of PDF is returned.", allowableValues="tiff, pdf", defaultValue="pdf", allowMultiple=false, required=false) @QueryParam("format") String format) {

		this.setCity(cityRegion);
		ProductSet ps = super.buildChart(format, edition, CHART_TYPE_SECTIONAL);
    	return Response.status(ps.getStatus().getCode()).entity(ps).build();

	}
	
	/**
	 * API method to get edition information, such as chart date, edition number
	 * @param cityRegion geoname to use
	 * @param edition next or current
	 * 
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
	@Path("/info")
    @ApiOperation(value="Get Sectional Chart edition date and edition number by edition type and geoname", 
    		notes="Geoname is a city "
    				+ "for which the chart is requested. Valid cities can be found on the FAA public web site "
    				+ "at FAA Home > Air Traffic > Flight Information > Aeronautical Information Services > Digital Products > VFR Charts > Sectional Chart tab",     
    	response=ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})

	
	public Response getSectionalInfo(@ApiParam (name="geoname", value="Geoname which is a city for which the chart is requested. Valid cities can be found on the FAA public web site.", 
			allowableValues="Albuquerque, Anchorage, Atlanta, Bethel, Billings, Brownsville, Cape Lisburne, Charlotte, Cheyenne, Chicago, Cincinnati, Cold Bay, "
			+"Dallas-Ft Worth, Dawson, Denver, Detroit, Dutch Harbor, El Paso, Fairbanks, Great Falls, Green Bay, Halifax, Hawaiian Islands, Houston, "
			+"Jacksonville, Juneau, Kansas City, Ketchikan, Klamath Falls, Kodiak, Lake Huron, Las Vegas, Los Angeles, McGrath, Memphis, Miami, Montreal, "
			+"New Orleans, New York, Nome, Omaha, Phoenix, Point Barrow, Salt Lake City, San Antonio, San Francisco, Seattle, Seward, St Louis, Twin Cities, "
			+"Washington, Western Aleutian Islands, Whitehorse, Wichita", required=true) @QueryParam("geoname") String cityRegion,
			@ApiParam(name="edition", value="Requested product edition. If omitted, the default current edition information is returned.", allowableValues="current, next", defaultValue="current", allowMultiple=false, required=false) @QueryParam("edition") String edition) {
		this.setCity(cityRegion);
		ProductSet ps = super.buildInfo(edition, CHART_TYPE_SECTIONAL);
    	return Response.status(ps.getStatus().getCode()).entity(ps).build();

	}
	
	protected Product createProduct(ChartCycleElementsJson element) {
		gov.faa.ait.apra.jaxb.ObjectFactory of = new gov.faa.ait.apra.jaxb.ObjectFactory();
		Product prod = of.createProductSetEditionProduct();
		prod.setProductName(ProductCodeList.SECTIONAL);
		StringBuilder productUrl = new StringBuilder();
		productUrl.append(Config.getAeronavHost()).append(Config.getAeronavSectionalFolder());
		
		logger.info("Starting call to create sectional product.");
		
		if("PDF".equalsIgnoreCase(this.getFormat())) {
			productUrl.append("/PDFs");
		}
		String cityFileName = element.getChart_city_name().replace(" ", "_");
		productUrl.append("/").append(cityFileName).append("_").append(element.getChart_cycle_number());
		if("PDF".equalsIgnoreCase(this.getFormat())) {
			productUrl.append("_P.pdf");
		} else if ("TIFF".equalsIgnoreCase(this.getFormat()) || "ZIP".equalsIgnoreCase(this.getFormat())) {
			productUrl.append(".zip");
		}
		try {
			logger.info("HEAD check flag is "+Config.getTPPCheckFlag());
			
			if (Config.getSectioanlCheckFlag()) {
				if (this.verifyURL(new URL(productUrl.toString()))) {
					
					if (logger.isInfoEnabled()) {
						logger.info("HEAD check succeeeded for Sectional product URL: "+productUrl.toString());
					}
					prod.setUrl(productUrl.toString());
				}
				else {
					if (logger.isWarnEnabled()) {
						logger.warn("HEAD check failed for Sectional product URL: "+productUrl.toString());
					}
					prod.setUrl("");
				}
			}
			else {
				if (logger.isDebugEnabled()) {
					logger.debug("HEAD check not executed for Sectional product URL: "+productUrl.toString());
				}
				prod.setUrl(productUrl.toString());
			}
		} catch (MalformedURLException emalformed) {
    		logger.warn("The download URL "+productUrl.toString()+" is not valid", emalformed);
		}	
		
		logger.info("Ending call to create sectional product.");
		
		return prod;
	}

	@Override
	protected boolean verifyGeoName() {
		String[] validCities = {"Albuquerque",
				"Anchorage",
				"Atlanta",
				"Bethel",
				"Billings",
				"Brownsville",
				"Cape Lisburne",
				"Charlotte",
				"Cheyenne",
				"Chicago",
				"Cincinnati",
				"Cold Bay",
				"Dallas-Ft Worth",
				"Dawson",
				"Denver",
				"Detroit",
				"Dutch Harbor",
				"El Paso",
				"Fairbanks",
				"Great Falls",
				"Green Bay",
				"Halifax",
				"Hawaiian Islands",
				"Houston",
				"Jacksonville",
				"Juneau",
				"Kansas City",
				"Ketchikan",
				"Klamath Falls",
				"Kodiak",
				"Lake Huron",
				"Las Vegas",
				"Los Angeles",
				"McGrath",
				"Memphis",
				"Miami",
				"Montreal",
				"New Orleans",
				"New York",
				"Nome",
				"Omaha",
				"Phoenix",
				"Point Barrow",
				"Salt Lake City",
				"San Antonio",
				"San Francisco",
				"Seattle",
				"Seward",
				"St Louis",
				"Twin Cities",
				"Washington",
				"Western Aleutian Islands",
				"Whitehorse",
				"Wichita"};
		/*
		if(Arrays.asList(validCities).stream().filter(value -> value.equalsIgnoreCase(this.getCity())).count()==1)
			return true;
		else 
			return false;
			*/
		
		return Arrays.asList(validCities).stream().filter(value -> value.equalsIgnoreCase(this.getCity())).count()==1;
	}

}
