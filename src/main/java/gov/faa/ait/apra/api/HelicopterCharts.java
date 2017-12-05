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

import gov.faa.ait.apra.bootstrap.Config;

import gov.faa.ait.apra.jaxb.ProductCodeList;
import gov.faa.ait.apra.jaxb.ProductSet;

import gov.faa.ait.apra.jaxb.ProductSet.Edition.Product;
import static gov.faa.ait.apra.bootstrap.ErrorCodes.ERROR_400;
import static gov.faa.ait.apra.bootstrap.ErrorCodes.ERROR_404;
import static gov.faa.ait.apra.bootstrap.ErrorCodes.ERROR_500;
import static gov.faa.ait.apra.bootstrap.ErrorCodes.RESPONSE_200;
import gov.faa.ait.apra.cycle.ChartCycleElementsJson;

import gov.faa.ait.apra.util.TableChartClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.HashMap;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author FAA
 *
 */

@Api(value = "VFR Helicopter Route Chart")
@Path("/vfr/helicopter")
public class HelicopterCharts extends AbstractTableDataService {

	private static final String CHART_TYPE_HELICOPTER_VFR = "HELICOPTER_VFR";
	private static HashMap<String, String> cityPathMap = new HashMap<>();
	private static String usGulfCoast = "U.S Gulf Coast";

	private static final Logger logger = LoggerFactory
			.getLogger(HelicopterCharts.class);
	/**
	 * To avoid loading city and paths for each instance of this class calling
	 * following static block and this block calls only once when server stars.
	 */
	static {
		populateCityPaths();
	}

	/**
	 * Default constructor
	 */
	public HelicopterCharts() {
		setClient(new TableChartClient());
	}

	/**
	 * This constructor allows a specific chart client to be used. Mainly for
	 * test purposes.
	 * 
	 * @param client
	 */
	public HelicopterCharts(TableChartClient client) {
		setClient(client);
	}

	/**
	 * This is the Helicopter Route Char download URL. Parameters edition and
	 * geoname will provided to retrieve the URL for either the current or the
	 * next edition.
	 * 
	 * @param ed the edition either current or next
	 * @param fmt the format of the response either a geo-tiff url or a pdf url
	 * @param cityRegion the city name or region for which the helicopter chart is requested
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
	@Path("/chart")
	@ApiOperation(value = "Get VFR Helicopter Route Chart download link by edition and geoname", nickname="getVFRHelicopterRelease", 
			notes = "Geoname is a city "
			+ "for which the chart is requested. Valid cities can be found on the FAA public web site "
			+ "under FAA Home > Air Traffic > Flight Information > Aeronautical Information Services "
			+ "> Digital Products > VFR Charts > Helicopter tab", response = ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})
	public Response getHelicopterRelease(
			@ApiParam(name = "edition", value = "Requested product edition. If omitted, the default current edition is returned.", allowableValues = "current, next", defaultValue = "current", allowMultiple = false, required = false) @QueryParam("edition") String ed,
    		@ApiParam (name="format", value="Format of the requested chart. TIFF is georeferenced and PDF is not georeferenced. If omitted, the default format of PDF is returned.", allowableValues="tiff, pdf", defaultValue="pdf", allowMultiple=false, required=false) @QueryParam("format") String fmt, 
			@ApiParam(name = "geoname", value = "Geoname which is a city for which the chart is requested. If omitted, charts for all cities are returned.", 
			allowableValues="Baltimore Washington Heli, Boston Heli, Chicago Heli, Dallas Ft. Worth Heli, Detroit Heli, Houston Heli, Los Angeles Heli, New York Heli, U.S Gulf Coast",
			allowMultiple = false, required = false) @QueryParam("geoname") String cityRegion) {

		logger.info("Received call to retrieve current VFR Helicopter Route Chart product release for edition '"
				+ ed + " City '" + cityRegion + "'.");
		this.setCity(cityRegion);
    	setEdition(ed != null ? ed : CURRENT);
    	setFormat(fmt != null ? fmt : PDF);

		ProductSet ps = super.buildChart(fmt, ed, CHART_TYPE_HELICOPTER_VFR);
    	return Response.status(ps.getStatus().getCode()).entity(ps).build();

	}

	/**
	 * This is the Helicopter Route Char download URL. Parameters edition and
	 * geoname will provided to retrieve the URL for either the current or the
	 * next edition.
	 * 
	 * @param ed
	 * @param cityRegion
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
	@Path("/info")
	@ApiOperation(value = "Get VFR Helicopter Route Chart edition date and edition number by edition type of current or next and geoname", 
			nickname="getVFRHelicopterEdition", response = ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})
	public Response getHelicopterEdition(
			@ApiParam(name = "edition", value = "Requested product edition. If omitted, the default current edition is returned.", allowableValues = "current, next", defaultValue = "current", allowMultiple = false, required = false) @QueryParam("edition") String ed,
			@ApiParam(name = "geoname", value = "Geoname which is a city for which the chart is requested. If omitted, charts for all cities are returned.", 
			allowableValues="Baltimore Washington Heli, Boston Heli, Chicago Heli, Dallas Ft. Worth Heli, Detroit Heli, Houston Heli, Los Angeles Heli, New York Heli, U.S Gulf Coast",
			allowMultiple = false, required = false) @QueryParam("geoname") String cityRegion) {

		logger.info("Received call to retrieve current VVFR Helicopter Route Chart product edition for edition '"
				+ ed + "'.");

		this.setCity(cityRegion);
		ProductSet ps = super.buildInfo(ed, CHART_TYPE_HELICOPTER_VFR);
    	return Response.status(ps.getStatus().getCode()).entity(ps).build();
	}

	/**
	 * This is the GulfCoast Route Char download URL. Parameters edition and
	 * format will provided to retrieve the URL for either the current or the
	 * next edition.
	 * 
	 * @param ed the edition of the chart either current or next
	 * @param fmt the format of the chart requested either geo-tiff or pdf
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
	@Path("/gulf/chart")
	@ApiOperation(value = "Get GulfCoast Route Chart download link by edition", 
			nickname="getVFRGulfCoastRelease", notes = "The geoname is absent from this "
					+ "operation and defaults to U.S Gulf Coast", response = ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})
	public Response getGulfCoastRelease(
			@ApiParam(name = "edition", value = "Requested product edition. If omitted, the default current edition is returned.", allowableValues = "current, next", defaultValue = "current", allowMultiple = false, required = false) @QueryParam("edition") String ed,
    		@ApiParam (name="format", value="Format of the requested chart. TIFF is georeferenced and PDF is not georeferenced. If omitted, the default format of PDF is returned.", allowableValues="tiff, pdf", defaultValue="pdf", allowMultiple=false, required=false) @QueryParam("format") String fmt) { 

		logger.info("Received call to retrieve current VFR GulfCoast Route Chart product release for edition '"
				+ ed + "'.");
    	setEdition(ed != null ? ed : CURRENT);
    	setFormat(fmt != null ? fmt : PDF);
    	
		return getHelicopterRelease(ed, fmt, usGulfCoast);
	}

	/**
	 * This is the GulfCoast Route Char download URL. Parameter edition 
	 * will provided to retrieve the URL for either the current or the
	 * next edition.
	 * 
	 * @param ed the edition of the chart either current or next
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
	@Path("/gulf/info")
	@ApiOperation(value = "Get VFR GulfCoast Route Chart edition date and edition number by edition type of 'current' or 'next' ", 
			nickname="getVFRGulfCoastEdition", response = ProductSet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = RESPONSE_200),
			@ApiResponse(code = 400, message = ERROR_400),
			@ApiResponse(code = 404, message = ERROR_404),
			@ApiResponse(code = 500, message = ERROR_500)})
	public Response getGulfCoastEdition(
			@ApiParam(name = "edition", value = "Requested product edition. If omitted, the default current edition is returned.", allowableValues = "current, next", defaultValue = "current", allowMultiple = false, required = false) @QueryParam("edition") String ed) {

		logger.info("Received call to retrieve current VVFR GulfCoast Route Chart product edition for edition '"
				+ ed + "'.");
		return getHelicopterEdition(ed, usGulfCoast);
	}

	private static void populateCityPaths() {
		cityPathMap.put("Baltimore Washington Heli".toLowerCase(Locale.ENGLISH),
				"Balt Wash Heli");
		cityPathMap.put("Dallas Ft. Worth Heli".toLowerCase(Locale.ENGLISH),
				"Dallas-Ft Worth Heli");
		cityPathMap.put(usGulfCoast.toLowerCase(Locale.ENGLISH), "US Gulf Coast Heli");

	}

	@Override
	protected Product createProduct(ChartCycleElementsJson element) {
		gov.faa.ait.apra.jaxb.ObjectFactory of = new gov.faa.ait.apra.jaxb.ObjectFactory();
		Product prod = of.createProductSetEditionProduct();
		prod.setProductName(ProductCodeList.VFR_HELICOPTER);
		StringBuilder productUrl = new StringBuilder();
		productUrl.append(Config.getAeronavHost());
		
		String cityFileName = cityPathMap.get(element.getChart_city_name()
				.toLowerCase());
		if (cityFileName == null) {
			cityFileName = element.getChart_city_name();
		}

		cityFileName = cityFileName.replace(" ", "_");
		
		if (TIFF.equalsIgnoreCase(getFormat())) {
			productUrl.append(Config.getHelicopterTIFFPath()).append("/").append(cityFileName).append("_");
			productUrl.append(element.getChart_cycle_number()).append(".zip");
		}
		else {
			productUrl.append(Config.getHelicopterPDFPath()).append("/").append(cityFileName).append("_");			
			productUrl.append(element.getChart_cycle_number()).append("_P").append(".pdf");
		}

		try {
			if (this.verifyURL(new URL(productUrl.toString()))) {
				prod.setUrl(productUrl.toString());
			}
		} catch (MalformedURLException emalformed) {
			logger.warn("The download URL is not valid", emalformed);
		}
		return prod;
	}

	@Override
	protected boolean verifyGeoName() {
		return true;
	}
	
	

}
